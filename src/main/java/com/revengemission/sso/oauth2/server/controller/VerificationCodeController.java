package com.revengemission.sso.oauth2.server.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.revengemission.commons.captcha.core.VerificationCodeUtil;
import com.revengemission.sso.oauth2.server.domain.GlobalConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class VerificationCodeController {


    //每秒只发出100个令牌
    RateLimiter rateLimiter = RateLimiter.create(100.0);

    /**
     * 验证码
     *
     * @param width  图片宽度
     * @param height 图片高度
     * @param length 验证码数量
     */
    @RequestMapping(value = "/captcha")
    public void captcha(HttpServletRequest request,
                        HttpServletResponse response,
                        @RequestParam(value = "w", defaultValue = "150") int width,
                        @RequestParam(value = "h", defaultValue = "38") int height,
                        @RequestParam(value = "length", defaultValue = "4") int length) throws IOException {

        if (!rateLimiter.tryAcquire()) {
            return;
        }
        if (length < 4) {
            length = 4;
        }
        request.getSession(true);
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        long time = System.currentTimeMillis();
        response.setDateHeader("Last-Modified", time);
        response.setDateHeader("Date", time);
        response.setDateHeader("Expires", time);
        ServletOutputStream stream = response.getOutputStream();
        String validateCode = VerificationCodeUtil.generateVerificationCode(4, null);
        request.getSession().setAttribute(GlobalConstant.VERIFICATION_CODE, validateCode);
        VerificationCodeUtil.outputImage(width, height, stream, validateCode);
        stream.flush();
        stream.close();
    }
}
