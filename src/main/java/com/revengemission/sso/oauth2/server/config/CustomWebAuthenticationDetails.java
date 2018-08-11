package com.revengemission.sso.oauth2.server.config;

import com.revengemission.sso.oauth2.server.domain.GlobalConstant;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {
    private static final long serialVersionUID = 6975601077710753878L;
    private final String inputVerificationCode;
    private final String sessionVerificationCode;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        inputVerificationCode = request.getParameter(GlobalConstant.VERIFICATION_CODE);
        sessionVerificationCode = String.valueOf(request.getSession().getAttribute(GlobalConstant.VERIFICATION_CODE));
    }

    public String getInputVerificationCode() {
        return inputVerificationCode;
    }

    public String getSessionVerificationCode() {
        return sessionVerificationCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("; inputVerificationCode: ").append(this.getInputVerificationCode()).append("; sessionVerificationCode: ").append(this.getSessionVerificationCode());
        return sb.toString();
    }
}
