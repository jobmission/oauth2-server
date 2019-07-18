package com.revengemission.sso.oauth2.server.controller;

import com.revengemission.sso.oauth2.server.config.CachesEnum;
import com.revengemission.sso.oauth2.server.domain.*;
import com.revengemission.sso.oauth2.server.service.CaptchaService;
import com.revengemission.sso.oauth2.server.service.OauthClientService;
import com.revengemission.sso.oauth2.server.service.RoleService;
import com.revengemission.sso.oauth2.server.service.UserAccountService;
import com.revengemission.sso.oauth2.server.utils.CheckPasswordStrength;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@Controller
public class SignInAndUpController {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserAccountService userAccountService;

    @Autowired
    OauthClientService oauthClientService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CaptchaService captchaService;

    @Autowired
    RoleService roleService;

    @GetMapping("/signIn")
    public String signIn(@RequestParam(value = "error", required = false) String error,
                         Model model) {
        if (StringUtils.isNotEmpty(error)) {
            model.addAttribute("error", error);
        }
        return "signIn";
    }

    @GetMapping("/signUp")
    public String signUp(@RequestParam(value = "error", required = false) String error,
                         Model model) {
        if (StringUtils.isNotEmpty(error)) {
            model.addAttribute("error", error);
        }
        return "signUp";
    }

    @ResponseBody
    @PostMapping("/oauth/signUp")
    public ResponseResult<Object> handleOauthSignUp(@RequestParam(value = GlobalConstant.VERIFICATION_CODE) String verificationCode,
                                                    @RequestParam(value = "graphId") String graphId,
                                                    @RequestParam(value = "username") String username,
                                                    @RequestParam(value = "password") String password) {

        ResponseResult<Object> responseResult = new ResponseResult<>();
        if (StringUtils.isAnyBlank(graphId, username, password)) {
            responseResult.setStatus(GlobalConstant.ERROR);
            responseResult.setMessage("请检查输入");
            return responseResult;
        }

        username = StringUtils.trimToEmpty(username).toLowerCase();
        password = StringUtils.trimToEmpty(password);

        if (username.length() < 6) {
            responseResult.setStatus(GlobalConstant.ERROR);
            responseResult.setMessage("用户名至少6位");
            return responseResult;
        }

        if (password.length() < 6) {
            responseResult.setStatus(GlobalConstant.ERROR);
            responseResult.setMessage("密码至少6位");
            return responseResult;
        }

        if (CheckPasswordStrength.check(password) < 4) {
            responseResult.setStatus(GlobalConstant.ERROR);
            responseResult.setMessage("密码应包含字母、数字、符号");
            return responseResult;
        }

        String captcha = captchaService.getCaptcha(CachesEnum.GraphCaptchaCache, graphId);
        if (!StringUtils.equalsIgnoreCase(verificationCode, captcha)) {
            responseResult.setStatus(GlobalConstant.ERROR);
            responseResult.setMessage("验证码错误");
            return responseResult;
        }

        UserAccount userAccount = new UserAccount();
        Role userRole = roleService.findByRoleName(RoleEnum.ROLE_USER.name());
        userAccount.getRoles().add(userRole);
        userAccount.setUsername(StringEscapeUtils.escapeHtml4(username));
        userAccount.setPassword(passwordEncoder.encode(password));
        userAccount.setAccountOpenCode(UUID.randomUUID().toString());
        try {
            userAccountService.create(userAccount);
            //移除验证码
            captchaService.removeCaptcha(CachesEnum.GraphCaptchaCache, graphId);
        } catch (AlreadyExistsException e) {
            if (log.isErrorEnabled()) {
                log.error("create user exception", e);
            }
            responseResult.setStatus(GlobalConstant.ERROR);
            responseResult.setMessage("用户已经存在");
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("create user exception", e);
            }
            responseResult.setStatus(GlobalConstant.ERROR);
            responseResult.setMessage("错误，请重试");
        }
        return responseResult;
    }
}
