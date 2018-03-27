package com.revengemission.sso.oauth2.server;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class PasswordTest {
    @Test
    @Ignore
    public void password() throws Exception {
        System.out.println("password=" + new BCryptPasswordEncoder().encode("password"));
        System.out.println("secret=" + new BCryptPasswordEncoder().encode("secret"));
    }
}
