package com.revengemission.sso.oauth2.server.controller;

import com.revengemission.sso.oauth2.server.domain.GlobalConstant;
import com.revengemission.sso.oauth2.server.domain.JsonObjects;
import com.revengemission.sso.oauth2.server.domain.ResponseResult;
import com.revengemission.sso.oauth2.server.domain.Role;
import com.revengemission.sso.oauth2.server.domain.UserAccount;
import com.revengemission.sso.oauth2.server.service.UserAccountService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/management/user")
public class ManageUserController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserAccountService userAccountService;

    @GetMapping(value = {"/", "", "/master"})
    public String master() {

        return "user/master";
    }

    @GetMapping(value = "/list")
    @ResponseBody
    public JsonObjects<UserAccount> listObjects(@RequestParam(value = "search", required = false) String searchValue,
                                                @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                                                @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder) {
        int pageNum = offset / limit + 1;
        return userAccountService.listByUsername(searchValue, pageNum, limit, sortField, sortOrder);
    }

    @GetMapping(value = "/details")
    @ResponseBody
    public UserAccount setupDetails(@RequestParam(value = "id") Long id,
                                    @RequestParam(value = "additionalData", required = false) String additionalData) {
        UserAccount object = userAccountService.retrieveById(id);
        object.setAdditionalData(additionalData);
        return object;
    }

    @PostMapping(value = "/details")
    @ResponseBody
    public ResponseResult<Object> handlePost(@RequestParam(value = "id", required = false) long id,
                                             @RequestParam(value = "deleteOperation", required = false, defaultValue = "1") int deleteOperation,
                                             @RequestParam(value = "username", required = false) String username,
                                             @RequestParam(value = "nickName", required = false) String nickName,
                                             @RequestParam(value = "address", required = false) String address,
                                             @RequestParam(value = "password", required = false) String password,
                                             @RequestParam(value = "roles", required = false) String roles) {

        ResponseResult<Object> responseResult = new ResponseResult<>();

        if (deleteOperation == -2 && id > 0) {
            userAccountService.updateRecordStatus(id, 0);
            responseResult.setStatus(GlobalConstant.SUCCESS);
        } else if (deleteOperation == 0 && id > 0) {
            userAccountService.updateRecordStatus(id, -2);
            responseResult.setStatus(GlobalConstant.SUCCESS);
        } else if (id > 0) {
            UserAccount object = userAccountService.retrieveById(id);
            if (StringUtils.isNotEmpty(password)) {
                object.setPassword(passwordEncoder.encode(StringUtils.trim(password)));
            }
            if (StringUtils.isNotEmpty(address)) {
                object.setAddress(address);
            }
            object.getRoles().clear();
            if (StringUtils.isNotEmpty(roles)) {
                String[] roleArray = roles.split(",");
                for (String s : roleArray) {
                    Role role = new Role();
                    role.setRoleName(s);
                    object.getRoles().add(role);
                }
            }
            object.setUsername(username);
            object.setNickName(nickName);
            userAccountService.updateById(object);
        } else if (id == 0) {
            if (StringUtils.isAnyEmpty(username, password)) {
                responseResult.setStatus(GlobalConstant.ERROR);
            } else {
                UserAccount object = new UserAccount();
                object.setUsername(username);
                object.setPassword(passwordEncoder.encode(StringUtils.trim(password)));
                object.setAddress(address);
                object.setNickName(nickName);
                object.getRoles().clear();
                if (StringUtils.isNotEmpty(roles)) {
                    String[] roleArray = roles.split(",");
                    for (String s : roleArray) {
                        Role role = new Role();
                        role.setRoleName(s);
                        object.getRoles().add(role);
                    }
                }
                userAccountService.create(object);
            }

        } else {
            log.info("invalid request!");
            responseResult.setStatus(GlobalConstant.ERROR);
        }

        return responseResult;
    }

}
