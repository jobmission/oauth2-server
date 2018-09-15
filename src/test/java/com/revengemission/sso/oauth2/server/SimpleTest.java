package com.revengemission.sso.oauth2.server;

import com.revengemission.sso.oauth2.server.utils.VerifyCodeUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhang wanchao on 18-9-15.
 */
public class SimpleTest {

    @Test
    @Ignore
    public static void outputFileImageTest(String[] args) throws IOException {
        File dir = new File("F:/tmp");
        int w = 200, h = 80;
        for (int i = 0; i < 50; i++) {
            String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
            File f = new File(dir, verifyCode + ".jpg");
            VerifyCodeUtils.outputFileImage(w, h, f, verifyCode);
        }
    }
}
