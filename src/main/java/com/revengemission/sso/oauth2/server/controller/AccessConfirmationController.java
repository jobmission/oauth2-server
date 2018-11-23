package com.revengemission.sso.oauth2.server.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/oauth")
@SessionAttributes("authorizationRequest")
public class AccessConfirmationController {

    @Autowired
    ClientDetailsService clientDetailsService;

    @RequestMapping("/confirm_access")
    public String getAccessConfirmation(Map<String, Object> model,
                                        Principal principal,
                                        HttpServletRequest request,
                                        @RequestParam(value = "redirect_uri", required = false) String redirectUri) throws Exception {
        AuthorizationRequest clientAuth = (AuthorizationRequest) model.remove("authorizationRequest");
        ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
        model.put("auth_request", clientAuth);
        model.put("client", client);
        if (StringUtils.isNotEmpty(redirectUri)) {
            model.put("from", getHost(redirectUri));
        }
        Map<String, String> scopes = new LinkedHashMap<>();
        for (String scope : clientAuth.getScope()) {
            scopes.put(OAuth2Utils.SCOPE_PREFIX + scope, "false");
        }
        model.put("scopes", scopes);
        return "accessConfirmation";
    }

    @RequestMapping("/error")
    public String handleError(Map<String, Object> model) throws Exception {
        // We can add more stuff to the model here for JSP rendering. If the client was a machine then
        // the JSON will already have been rendered.
        model.put("message", "There was a problem with the OAuth2 protocol");
        return "oauthError";
    }

    private URI getHost(String url) {
        URI uri = URI.create(url);
        URI effectiveURI = null;
        try {
            // URI(String scheme, String userInfo, String host, int port, String
            // path, String query,String fragment)
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        } catch (Throwable var4) {
            effectiveURI = null;
        }

        return effectiveURI;
    }
}
