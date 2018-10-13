package com.revengemission.sso.oauth2.server.controller;

import com.revengemission.sso.oauth2.server.domain.*;
import com.revengemission.sso.oauth2.server.service.UserAccountService;
import com.revengemission.sso.oauth2.server.utils.CheckPasswordStrength;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class SignInAndUpController {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserAccountService userAccountService;

    @Autowired
    ClientDetailsService clientDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/signIn")
    public String signIn(@RequestParam(value = "error", required = false) String error,
                         Model model) {
        if (StringUtils.isNotEmpty(error)) {
            model.addAttribute("error", error);
        }
        return "signIn";
    }

    @GetMapping("/signUp")
    public String signUp(HttpServletRequest request,
                         @RequestParam(value = "error", required = false) String error,
                         Model model) {
        if (StringUtils.isNotEmpty(error)) {
            model.addAttribute("error", error);
        }
        return "signUp";
    }

    @ResponseBody
    @PostMapping("/signUp")
    public ResponseResult handleSignUp(HttpServletRequest request,
                                       @RequestParam(value = "verificationCode") String verificationCode,
                                       @RequestParam(value = "username") String username,
                                       @RequestParam(value = "password") String password,
                                       @RequestParam(value = "confirmPassword") String confirmPassword) {

        ResponseResult responseResult = new ResponseResult();
        if (StringUtils.isAnyBlank(verificationCode, username, password, confirmPassword) || !StringUtils.equals(password, confirmPassword)) {
            responseResult.setStatus(GlobalConstant.ERROR);
            responseResult.setMessage("请检查输入");
            return responseResult;
        }

        username = StringUtils.trimToEmpty(username);
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

        String sessionVerificationCode = String.valueOf(request.getSession().getAttribute(GlobalConstant.VERIFICATION_CODE));
        if (!StringUtils.equalsIgnoreCase(verificationCode, sessionVerificationCode)) {
            responseResult.setStatus(GlobalConstant.ERROR);
            responseResult.setMessage("验证码错误");
            return responseResult;
        }
        UserAccount userAccount = new UserAccount();
        userAccount.setRole(RoleEnum.ROLE_USER.name());
        userAccount.setUsername(StringEscapeUtils.escapeHtml4(username));
        userAccount.setPassword(passwordEncoder.encode(password));
        try {
            userAccountService.create(userAccount);
            //移除验证码
            request.getSession().removeAttribute(GlobalConstant.VERIFICATION_CODE);
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

    @CrossOrigin
    @ResponseBody
    @PostMapping("/oauth/signUp")
    public ResponseResult handleOauthSignUp(HttpServletRequest request,
                                            Principal principal,
                                            @RequestParam(value = "client_id") String clientId,
                                            @RequestParam(value = "client_secret") String clientSecret,
                                            @RequestParam(value = "username") String username,
                                            @RequestParam(value = "password") String password) {

        ResponseResult responseResult = new ResponseResult();

        if (StringUtils.isAnyBlank(clientId, clientSecret, username, password)) {
            responseResult.setStatus(GlobalConstant.ERROR);
            responseResult.setMessage(GlobalConstant.ERROR_MESSAGE_ILLEGAL_PARAMETER);
            return responseResult;
        }
        username = StringUtils.trimToEmpty(username);
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

        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (clientDetails == null || passwordEncoder.matches(password, clientDetails.getClientSecret())) {
            responseResult.setStatus(GlobalConstant.ERROR_DENIED);
            responseResult.setMessage(GlobalConstant.ERROR_MESSAGE_DENIED);
            return responseResult;
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setClientId(clientId);
        userAccount.setRole(RoleEnum.ROLE_USER.name());
        userAccount.setUsername(username);
        userAccount.setPassword(passwordEncoder.encode(password));
        try {
            userAccountService.create(userAccount);
        } catch (AlreadyExistsException e) {
            if (log.isDebugEnabled()) {
                log.error("create user exception", e);
            }
            responseResult.setStatus(GlobalConstant.ERROR_ALREADY_EXIST);
            responseResult.setMessage(GlobalConstant.ERROR_MESSAGE_ALREADY_EXIST);
        }
        return responseResult;
    }
}
