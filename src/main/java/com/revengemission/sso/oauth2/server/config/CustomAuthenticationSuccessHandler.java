package com.revengemission.sso.oauth2.server.config;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revengemission.sso.oauth2.server.domain.GlobalConstant;
import com.revengemission.sso.oauth2.server.domain.LoginHistory;
import com.revengemission.sso.oauth2.server.domain.ResponseResult;
import com.revengemission.sso.oauth2.server.domain.RoleEnum;
import com.revengemission.sso.oauth2.server.service.LoginHistoryService;
import com.revengemission.sso.oauth2.server.service.UserAccountService;
import com.revengemission.sso.oauth2.server.utils.ClientIpUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    UserAccountService userAccountService;

    @Autowired
    LoginHistoryService loginHistoryService;

    RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException {

        String redirectUrl = "";
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null && StringUtils.isNotEmpty(savedRequest.getRedirectUrl())) {
            redirectUrl = savedRequest.getRedirectUrl();
        }

        boolean isAjax = "XMLHttpRequest".equals(request
            .getHeader("X-Requested-With")) || "apiLogin".equals(request
            .getHeader("api-login"));

        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setUsername(authentication.getName());
        loginHistory.setIp(ClientIpUtil.getIpAddress(request));
        loginHistory.setDevice(request.getHeader("User-Agent"));
        loginHistory.setRecordStatus(1);
        loginHistory.setRemarks("isAjax:" + isAjax + ",redirectUrl:" + redirectUrl);
        loginHistoryService.asyncCreate(loginHistory);

        userAccountService.loginSuccess(authentication.getName());


        if (isAjax) {
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            try {

                ResponseResult<Object> responseMessage = new ResponseResult<>();
                responseMessage.setStatus(GlobalConstant.SUCCESS);
                responseMessage.setAdditional(redirectUrl);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(response.getOutputStream(),
                    JsonEncoding.UTF8);
                objectMapper.writeValue(jsonGenerator, responseMessage);
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Could not write JSON:", ex);
                }
                throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
            }
        } else {
            //Call the parent method to manage the successful authentication
///            setDefaultTargetUrl("/");
            if (StringUtils.isNotEmpty(redirectUrl)) {
                super.onAuthenticationSuccess(request, response, authentication);
            } else {
                if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(RoleEnum.ROLE_USER.toString()))) {
                    response.sendRedirect("/");
                } else {
                    response.sendRedirect("/management/user");
                }
            }
        }

    }

}
