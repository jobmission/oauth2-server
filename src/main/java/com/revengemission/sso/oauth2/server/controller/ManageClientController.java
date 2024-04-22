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

@Controller
@RequestMapping(value = "/management/client")
public class ManageClientController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OauthClientService oauthClientService;

    @GetMapping(value = {"/", "", "/master"})
    public String master() {

        return "client/master";
    }

    @GetMapping(value = "/list")
    @ResponseBody
    public JsonObjects<OauthClient> listObjects(@RequestParam(value = "search", required = false) String searchValue,
                                                @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                                                @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder) {
        int pageNum = offset / limit + 1;
        return oauthClientService.list(pageNum, limit, sortField, sortOrder);
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
    public ResponseResult<Object> handlePost(@RequestParam(value = "id", required = false) long id,
                                             @RequestParam(value = "deleteOperation", required = false, defaultValue = "1") int deleteOperation,
                                             @RequestParam(value = "clientId", required = false) String clientId,
                                             @RequestParam(value = "clientSecret", required = false) String clientSecret,
                                             @RequestParam(value = "authorities", required = false) String authorities,
                                             @RequestParam(value = "scope", required = false) String scope,
                                             @RequestParam(value = "authorizedGrantTypes", required = false) String authorizedGrantTypes,
                                             @RequestParam(value = "webServerRedirectUri", required = false) String webServerRedirectUri,
                                             @RequestParam(value = "remarks", required = false) String remarks) {

        ResponseResult<Object> responseResult = new ResponseResult<>();

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
            responseResult.setStatus(GlobalConstant.SUCCESS);
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
                responseResult.setStatus(GlobalConstant.SUCCESS);
            }

        }

        return responseResult;
    }

}
