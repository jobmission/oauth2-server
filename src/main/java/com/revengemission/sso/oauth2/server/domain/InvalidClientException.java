package com.revengemission.sso.oauth2.server.domain;

import org.springframework.security.oauth2.provider.ClientRegistrationException;

public class InvalidClientException extends ClientRegistrationException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5999356261352232772L;

	public InvalidClientException(String msg) {
        super(msg);
    }

    public InvalidClientException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
