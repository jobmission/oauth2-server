package com.revengemission.sso.oauth2.server;

import com.revengemission.sso.oauth2.server.utils.CheckPasswordStrength;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by zhang wanchao on 18-9-15.
 */
public class SimpleTest {

    @Test
    @Ignore
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
}
