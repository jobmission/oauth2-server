package com.revengemission.sso.oauth2.server.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.revengemission.sso.oauth2.server.domain.EntityNotFoundException;
import com.revengemission.sso.oauth2.server.domain.UserAccount;
import com.revengemission.sso.oauth2.server.service.UserAccountService;
import com.revengemission.sso.oauth2.server.utils.CheckPasswordStrength;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.regex.Pattern;

@Controller
public class ProfileController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static final Pattern AUTHORIZATION_PATTERN = Pattern.compile("^[B|b]]earer (?<token>[a-zA-Z0-9-._~+/]+)=*$");

    @Autowired
    UserAccountService userAccountService;

    @Autowired
    PasswordEncoder passwordEncoder;

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
                                @JsonFormat(pattern = "yyyy-MM-dd") @DateTimeFormat(pattern = "yyyy-MM-dd")
                                @RequestParam(value = "birthday", required = false) LocalDate birthday,
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
            userAccount = userAccountService.updateById(userAccount);
            model.addAttribute("userAccount", userAccount);
            model.addAttribute("updated", true);
        } catch (EntityNotFoundException e) {
            if (log.isErrorEnabled()) {
                log.error("findByUsername exception", e);
            }
            model.addAttribute("updated", false);
            model.addAttribute("error", e.getMessage());
        }
        return "profile";
    }

    @GetMapping(value = "/user/changePwd")
    public String changePwd(Principal principal) {
        return "changePwd";
    }

    @PostMapping("/user/changePwd")
    public String handleChangePwd(Principal principal,
                                  @RequestParam(value = "oldPassword") String oldPassword,
                                  @RequestParam(value = "newPassword") String newPassword,
                                  Model model) {

        if (newPassword.length() < 6) {
            model.addAttribute("updated", false);
            model.addAttribute("error", "密码至少6位");
            return "changePwd";
        }

        if (CheckPasswordStrength.check(newPassword) < 4) {
            model.addAttribute("updated", false);
            model.addAttribute("error", "密码安全等级较低，应包含字母、数字、符号");
            return "changePwd";
        }

        try {
            UserAccount userAccount = userAccountService.findByUsername(principal.getName());
            if (!passwordEncoder.matches(oldPassword, userAccount.getPassword())) {
                model.addAttribute("updated", false);
                model.addAttribute("error", "原密码错误");
            } else {
                userAccount.setPassword(passwordEncoder.encode(newPassword));
                userAccount = userAccountService.updateById(userAccount);
                model.addAttribute("updated", true);
            }
        } catch (EntityNotFoundException e) {
            if (log.isErrorEnabled()) {
                log.error("findByUsername exception", e);
            }
            model.addAttribute("updated", false);
            model.addAttribute("error", e.getMessage());
        }
        return "changePwd";
    }
}
