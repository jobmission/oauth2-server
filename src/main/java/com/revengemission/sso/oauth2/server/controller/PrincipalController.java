package com.revengemission.sso.oauth2.server.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Michael Lavelle
 *         <p>
 *         Added to provide an endpoint from which Spring Social can obtain authentication details
 */
@Controller
public class PrincipalController {

    @ResponseBody
    @RequestMapping("/user/me")
    public Principal getPhotoServiceUser(Principal principal) {
        return principal;
    }
}
