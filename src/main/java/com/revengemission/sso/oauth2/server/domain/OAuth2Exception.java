package com.revengemission.sso.oauth2.server.domain;

import org.springframework.http.HttpStatus;

public class OAuth2Exception extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private String errorCode = "invalid_request";

    public OAuth2Exception(String msg) {
        super(msg);
    }

    public OAuth2Exception(String msg, Throwable t) {
        super(msg, t);
    }

    public OAuth2Exception(String msg, HttpStatus httpStatus, String errorCode) {
        super(msg);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public OAuth2Exception(String msg, Throwable t, HttpStatus httpStatus, String errorCode) {
        super(msg, t);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
