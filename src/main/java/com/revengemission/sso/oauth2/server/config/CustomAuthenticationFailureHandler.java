package com.revengemission.sso.oauth2.server.config;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revengemission.sso.oauth2.server.domain.GlobalConstant;
import com.revengemission.sso.oauth2.server.domain.ResponseResult;
import com.revengemission.sso.oauth2.server.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private String failureUrl = "/signIn";

    @Autowired
    UserAccountService userAccountService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        String username = request.getParameter("username");
        log.debug(username + " try to login");

        boolean isAjax = "XMLHttpRequest".equals(request
                .getHeader("X-Requested-With")) || "apiLogin".equals(request
                .getHeader("api-login"));
        if (isAjax) {
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            try {
                ResponseResult responseMessage = new ResponseResult<>();
                responseMessage.setStatus(GlobalConstant.ERROR);
                responseMessage.setMessage(exception.getMessage());
                ObjectMapper objectMapper = new ObjectMapper();
                JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(response.getOutputStream(),
                        JsonEncoding.UTF8);
                objectMapper.writeValue(jsonGenerator, responseMessage);
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("Could not write JSON:", ex);
                }
                throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
            }
        } else {
            String encodedMessage = "";
            try {
                encodedMessage = URLEncoder.encode(exception.getMessage(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                if (log.isErrorEnabled()) {
                    log.error("encodedMessage", e);
                }
            }
            response.sendRedirect(failureUrl + "?authentication_error=true&loginMessage=" + encodedMessage);
            /*super.onAuthenticationFailure(request, response, exception);*/
        }
    }
}
