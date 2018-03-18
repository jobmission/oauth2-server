package com.revengemission.sso.oauth2.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SignInController {

    @GetMapping("/signIn")
    public String signIn() throws Exception {
        return "login";
    }
}
