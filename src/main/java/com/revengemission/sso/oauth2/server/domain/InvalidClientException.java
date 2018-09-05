package com.revengemission.sso.oauth2.server.domain;

import org.springframework.security.oauth2.provider.ClientRegistrationException;

public class InvalidClientException extends ClientRegistrationException {
    public InvalidClientException(String msg) {
        super(msg);
    }

    public InvalidClientException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
