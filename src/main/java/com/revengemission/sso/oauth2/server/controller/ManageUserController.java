package com.revengemission.sso.oauth2.server.controller;

import com.revengemission.sso.oauth2.server.domain.*;
import com.revengemission.sso.oauth2.server.service.UserAccountService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping(value = "/management/user")
public class ManageUserController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserAccountService userAccountService;

    @GetMapping(value = {"/", "", "/master"})
    public String master(Principal principal) {

        return "users/master";
    }

    @PostMapping(value = "/list")
    @ResponseBody
    public JsonObjects<UserAccount> listObjects(@RequestParam(value = "searchValue", required = false) String searchValue,
                                                @RequestParam(value = "rows", defaultValue = "10") Integer pageSize,
                                                @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "sidx", defaultValue = "id") String sortField,
                                                @RequestParam(value = "sord", defaultValue = "desc") String sortOrder) {
        return userAccountService.listByRole(RoleEnum.ROLE_USER.name(), searchValue, pageNum, pageSize, sortField, sortOrder);
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
    public ResponseResult handlePost(@RequestParam(value = "id", required = false) Long id,
                                     @RequestParam(value = "deleteOperation", required = false, defaultValue = "1") int deleteOperation,
                                     @RequestParam(value = "nickName", required = false) String nickName,
                                     @RequestParam(value = "address", required = false) String address,
                                     @RequestParam(value = "password", required = false) String password) {

        ResponseResult responseResult = new ResponseResult();

        if (deleteOperation == -1 && id != null && id > 0) {
            userAccountService.updateRecordStatus(id, 0);
            responseResult.setStatus(GlobalConstant.SUCCESS);
        } else if (deleteOperation == 0 && id != null && id > 0) {
            userAccountService.updateRecordStatus(id, -1);
            responseResult.setStatus(GlobalConstant.SUCCESS);
        } else if (id != null && id > 0) {
            UserAccount object = userAccountService.retrieveById(id);
            if (StringUtils.isNotEmpty(password)) {
                object.setPassword(passwordEncoder.encode(StringUtils.trim(password)));
            }
            if (StringUtils.isNotEmpty(address)) {
                object.setAddress(address);
            }
            object.setNickName(nickName);
            userAccountService.updateById(object);
        } else {
            responseResult.setStatus(GlobalConstant.ERROR);
        }

        return responseResult;
    }

}
