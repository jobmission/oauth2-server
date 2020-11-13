package com.revengemission.sso.oauth2.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.revengemission.sso.oauth2.server.domain.UserAccount;
import com.revengemission.sso.oauth2.server.utils.CheckPasswordStrength;
import com.revengemission.sso.oauth2.server.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by zhang wanchao on 18-9-15.
 */
public class SimpleTest {

    @Test
    @Disabled
    public void checkPwd() {
        System.out.println(StringUtils.center("123", 30, "-") + CheckPasswordStrength.check("123"));
        System.out.println(StringUtils.center("123456", 30, "-") + CheckPasswordStrength.check("123456"));
        System.out.println(StringUtils.center("258369", 30, "-") + CheckPasswordStrength.check("258369"));
        System.out.println(StringUtils.center("123456a", 30, "-") + CheckPasswordStrength.check("123456a"));
        System.out.println(StringUtils.center("123456a!", 30, "-") + CheckPasswordStrength.check("123456a!"));
        System.out.println(StringUtils.center("123567aAa", 30, "-") + CheckPasswordStrength.check("123567aAa"));
        System.out.println(StringUtils.center("123789a!", 30, "-") + CheckPasswordStrength.check("123789a!"));
        System.out.println(StringUtils.center("123789A!", 30, "-") + CheckPasswordStrength.check("123789A!"));
        System.out.println(StringUtils.center("123789Aa!", 30, "-") + CheckPasswordStrength.check("123789Aa!"));
        System.out.println(StringUtils.center("!123789Aa!", 30, "-") + CheckPasswordStrength.check("!123789Aa!"));
    }

    @Test
    @Disabled
    public void localDateTest() throws JsonProcessingException {
        LocalDateTime nowDateTime = LocalDateTime.now();
        UserAccount userAccount = new UserAccount();
        userAccount.setDateCreated(nowDateTime);
        userAccount.setBirthday(LocalDate.now());
        System.out.println(JsonUtil.objectToJsonString(userAccount));
    }

}
