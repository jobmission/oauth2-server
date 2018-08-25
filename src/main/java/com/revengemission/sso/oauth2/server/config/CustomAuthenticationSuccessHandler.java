package com.revengemission.sso.oauth2.server.config;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revengemission.sso.oauth2.server.domain.GlobalConstant;
import com.revengemission.sso.oauth2.server.domain.ResponseResult;
import com.revengemission.sso.oauth2.server.domain.RoleEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private Logger log = LoggerFactory.getLogger(this.getClass());

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

        //移除验证码
        request.getSession().removeAttribute(GlobalConstant.VERIFICATION_CODE);

        boolean isAjax = "XMLHttpRequest".equals(request
                .getHeader("X-Requested-With")) || "apiLogin".equals(request
                .getHeader("api-login"));

        if (isAjax) {
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            try {

                ResponseResult responseMessage = new ResponseResult<>();
                responseMessage.setStatus(GlobalConstant.SUCCESS);
                responseMessage.setReserve2(redirectUrl);
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
            //setDefaultTargetUrl("/");
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
