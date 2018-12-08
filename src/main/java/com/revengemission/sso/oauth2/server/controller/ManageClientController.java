package com.revengemission.sso.oauth2.server.controller;

import com.revengemission.sso.oauth2.server.domain.GlobalConstant;
import com.revengemission.sso.oauth2.server.domain.JsonObjects;
import com.revengemission.sso.oauth2.server.domain.OauthClient;
import com.revengemission.sso.oauth2.server.domain.ResponseResult;
import com.revengemission.sso.oauth2.server.service.OauthClientService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping(value = "/management/client")
public class ManageClientController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OauthClientService oauthClientService;

    @GetMapping(value = {"/", "", "/master"})
    public String master(Principal principal) {

        return "client/master";
    }

    @GetMapping(value = "/list")
    @ResponseBody
    public JsonObjects<OauthClient> listObjects(@RequestParam(value = "search[value]", required = false, defaultValue = "") String searchValue,
                                                @RequestParam(value = "draw", defaultValue = "0") int draw,
                                                @RequestParam(value = "length", defaultValue = "10") Integer pageSize,
                                                @RequestParam(value = "start", defaultValue = "0") Integer start,
                                                @RequestParam(value = "sidx", defaultValue = "id") String sortField,
                                                @RequestParam(value = "sord", defaultValue = "desc") String sortOrder) {
        int pageNum = start / 10 + 1;
        JsonObjects<OauthClient> result = oauthClientService.list(pageNum, pageSize, sortField, sortOrder);
        result.setDraw(draw + 1);
        return result;
    }

    @GetMapping(value = "/details")
    @ResponseBody
    public OauthClient setupDetails(@RequestParam(value = "id") Long id,
                                    @RequestParam(value = "additionalData", required = false) String additionalData) {
        OauthClient object = oauthClientService.retrieveById(id);
        object.setAdditionalData(additionalData);
        return object;
    }

    @PostMapping(value = "/details")
    @ResponseBody
    public ResponseResult handlePost(@RequestParam(value = "id", required = false) long id,
                                     @RequestParam(value = "deleteOperation", required = false, defaultValue = "1") int deleteOperation,
                                     @RequestParam(value = "clientId", required = false) String clientId,
                                     @RequestParam(value = "clientSecret", required = false) String clientSecret,
                                     @RequestParam(value = "authorities", required = false) String authorities,
                                     @RequestParam(value = "scope", required = false) String scope,
                                     @RequestParam(value = "authorizedGrantTypes", required = false) String authorizedGrantTypes,
                                     @RequestParam(value = "webServerRedirectUri", required = false) String webServerRedirectUri,
                                     @RequestParam(value = "remarks", required = false) String remarks) {

        ResponseResult responseResult = new ResponseResult();

        if (deleteOperation == -1 && id > 0) {
            oauthClientService.updateRecordStatus(id, 0);
            responseResult.setStatus(GlobalConstant.SUCCESS);
        } else if (deleteOperation == 0 && id > 0) {
            oauthClientService.updateRecordStatus(id, -1);
            responseResult.setStatus(GlobalConstant.SUCCESS);
        } else if (id > 0) {
            OauthClient object = oauthClientService.retrieveById(id);
            if (StringUtils.isNotEmpty(clientSecret)) {
                object.setClientSecret(passwordEncoder.encode(StringUtils.trim(clientSecret)));
            }
            if (StringUtils.isNotEmpty(authorities)) {
                object.setAuthorities(authorities);
            }
            if (StringUtils.isNotEmpty(scope)) {
                object.setScope(scope);
            }
            if (StringUtils.isNotEmpty(authorizedGrantTypes)) {
                object.setAuthorizedGrantTypes(authorizedGrantTypes);
            }
            if (StringUtils.isNotEmpty(webServerRedirectUri)) {
                object.setWebServerRedirectUri(webServerRedirectUri);
            }
            if (StringUtils.isNotEmpty(remarks)) {
                object.setRemarks(remarks);
            }
            oauthClientService.updateById(object);
        } else {
            if (StringUtils.isAnyEmpty(clientId, clientSecret, authorities, scope, authorizedGrantTypes, webServerRedirectUri)) {
                responseResult.setStatus(GlobalConstant.ERROR);
            } else {
                OauthClient object = new OauthClient();
                object.setClientId(clientId);
                object.setClientSecret(passwordEncoder.encode(StringUtils.trim(clientSecret)));
                object.setAuthorities(authorities);
                object.setScope(scope);
                object.setAuthorizedGrantTypes(authorizedGrantTypes);
                object.setWebServerRedirectUri(webServerRedirectUri);
                object.setRemarks(remarks);
                oauthClientService.create(object);
            }

        }

        return responseResult;
    }

}
