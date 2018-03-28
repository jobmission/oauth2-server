package com.revengemission.sso.oauth2.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ProfileController {

    @ResponseBody
    @GetMapping("/user/me")
    public Map<String, String> info(Principal principal) {
        Map<String, String> result = new HashMap<>();
        result.put("username", principal.getName());
        return result;
    }

    @GetMapping("/user/profile")
    public String profile(Principal principal,
                          Model model) {
        return "profile";
    }

    @PostMapping("/user/profile")
    public String handleProfile(Principal principal,
                                Model model) {
        return "profile";
    }
}
