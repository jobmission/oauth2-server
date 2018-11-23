package com.revengemission.sso.oauth2.server.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.revengemission.sso.oauth2.server.domain.EntityNotFoundException;
import com.revengemission.sso.oauth2.server.domain.UserAccount;
import com.revengemission.sso.oauth2.server.service.UserAccountService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ProfileController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserAccountService userAccountService;

    @ResponseBody
    @GetMapping("/user/me")
    public Map<String, String> info(Principal principal) {
        Map<String, String> result = new HashMap<>();
        try {
            UserAccount userAccount = userAccountService.findByUsername(principal.getName());
            result.put("username", principal.getName());
            if (StringUtils.isNotEmpty(userAccount.getGender())) {
                result.put("gender", userAccount.getGender());
            }
            if (StringUtils.isNotEmpty(userAccount.getNickName())) {
                result.put("nickName", userAccount.getNickName());
            }
        } catch (EntityNotFoundException e) {
            if (log.isErrorEnabled()) {
                log.error("findByUsername exception", e);
            }
        }

        return result;
    }

    @GetMapping(value = {"", "/", "/user/profile"})
    public String profile(Principal principal,
                          Model model) {
        try {
            UserAccount userAccount = userAccountService.findByUsername(principal.getName());
            model.addAttribute("userAccount", userAccount);
        } catch (EntityNotFoundException e) {
            if (log.isErrorEnabled()) {
                log.error("findByUsername exception", e);
            }
        }

        return "profile";
    }

    @PostMapping("/user/profile")
    public String handleProfile(Principal principal,
                                @RequestParam(value = "nickName", required = false) String nickName,
                                @RequestParam(value = "avatarUrl", required = false) String avatarUrl,
                                @RequestParam(value = "email", required = false) String email,
                                @RequestParam(value = "mobile", required = false) String mobile,
                                @RequestParam(value = "province", required = false) String province,
                                @RequestParam(value = "city", required = false) String city,
                                @RequestParam(value = "address", required = false) String address,
                                @JsonFormat(pattern = "MM-dd-yyyy") @DateTimeFormat(pattern = "MM-dd-yyyy")
                                @RequestParam(value = "birthday", required = false) Date birthday,
                                Model model) {

        try {
            UserAccount userAccount = userAccountService.findByUsername(principal.getName());
            userAccount.setNickName(StringEscapeUtils.escapeHtml4(nickName));
            userAccount.setAvatarUrl(StringEscapeUtils.escapeHtml4(avatarUrl));
            userAccount.setEmail(StringEscapeUtils.escapeHtml4(email));
            userAccount.setMobile(StringEscapeUtils.escapeHtml4(mobile));
            userAccount.setProvince(StringEscapeUtils.escapeHtml4(province));
            userAccount.setCity(StringEscapeUtils.escapeHtml4(city));
            userAccount.setAddress(StringEscapeUtils.escapeHtml4(address));
            userAccount.setBirthday(birthday);
            userAccountService.updateById(userAccount);
        } catch (EntityNotFoundException e) {
            if (log.isErrorEnabled()) {
                log.error("findByUsername exception", e);
            }
        }

        return "redirect:/user/profile?success=update";
    }
}
