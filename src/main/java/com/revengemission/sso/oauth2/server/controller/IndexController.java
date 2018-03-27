package com.revengemission.sso.oauth2.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @GetMapping(value = {"/", "/index"})
    @ResponseBody
    public String index() throws Exception {
        return "index";
    }
}
