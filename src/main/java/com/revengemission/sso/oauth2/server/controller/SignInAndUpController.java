package com.revengemission.sso.oauth2.server.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import com.revengemission.sso.oauth2.server.config.CachesEnum;
import com.revengemission.sso.oauth2.server.service.CaptchaService;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.revengemission.sso.oauth2.server.domain.AlreadyExistsException;
import com.revengemission.sso.oauth2.server.domain.GlobalConstant;
import com.revengemission.sso.oauth2.server.domain.ResponseResult;
import com.revengemission.sso.oauth2.server.domain.RoleEnum;
import com.revengemission.sso.oauth2.server.domain.UserAccount;
import com.revengemission.sso.oauth2.server.service.UserAccountService;
import com.revengemission.sso.oauth2.server.utils.CheckPasswordStrength;

@Controller
public class SignInAndUpController {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserAccountService userAccountService;

    @Autowired
    ClientDetailsService clientDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CaptchaService captchaService;

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
    public ResponseResult<Object> handleSignUp(HttpServletRequest request,
                                               @RequestParam(value = GlobalConstant.VERIFICATION_CODE) String verificationCode,
                                               @RequestParam(value = "graphId") String graphId,
                                               @RequestParam(value = "username") String username,
                                               @RequestParam(value = "password") String password,
                                               @RequestParam(value = "confirmPassword") String confirmPassword) {

        ResponseResult<Object> responseResult = new ResponseResult<>();
        if (StringUtils.isAnyBlank(graphId, username, password, confirmPassword) || !StringUtils.equals(password, confirmPassword)) {
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
        userAccount.setRole(RoleEnum.ROLE_USER.name());
        userAccount.setUsername(StringEscapeUtils.escapeHtml4(username));
        userAccount.setPassword(passwordEncoder.encode(password));
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

    @CrossOrigin
    @ResponseBody
    @PostMapping("/oauth/signUp")
    public ResponseResult<Object> handleOauthSignUp(@RequestParam(value = "client_id") String clientId,
                                                    @RequestParam(value = "client_secret") String clientSecret,
                                                    @RequestParam(value = "username") String username,
                                                    @RequestParam(value = "password") String password) {

        ResponseResult<Object> responseResult = new ResponseResult<>();

        if (StringUtils.isAnyBlank(clientId, clientSecret, username, password)) {
            responseResult.setStatus(GlobalConstant.ERROR);
            responseResult.setMessage(GlobalConstant.ERROR_MESSAGE_ILLEGAL_PARAMETER);
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
