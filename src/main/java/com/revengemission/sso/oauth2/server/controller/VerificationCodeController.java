package com.revengemission.sso.oauth2.server.controller;

import com.revengemission.sso.oauth2.server.domain.GlobalConstant;
import org.patchca.color.SingleColorFactory;
import org.patchca.filter.predefined.CurvesRippleFilterFactory;
import org.patchca.font.RandomFontFactory;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.utils.encoder.EncoderHelper;
import org.patchca.word.RandomWordFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;

@Controller
public class VerificationCodeController {
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
                        @RequestParam(value = "w", defaultValue = "160") int width,
                        @RequestParam(value = "h", defaultValue = "40") int height,
                        @RequestParam(value = "length", defaultValue = "4") int length) throws IOException {
        if (length < 4) {
            length = 4;
        }
        request.getSession(true);
        ConfigurableCaptchaService cs = new ConfigurableCaptchaService();
        cs.setColorFactory(new SingleColorFactory(new Color(25, 60, 170)));
        cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));
        RandomFontFactory ff = new RandomFontFactory();
        ff.setMinSize(30);
        ff.setMaxSize(30);
        RandomWordFactory rwf = new RandomWordFactory();
        rwf.setMinLength(length);
        rwf.setMaxLength(length);
        cs.setWordFactory(rwf);
        cs.setFontFactory(ff);
        cs.setHeight(height);
        cs.setWidth(width);
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        long time = System.currentTimeMillis();
        response.setDateHeader("Last-Modified", time);
        response.setDateHeader("Date", time);
        response.setDateHeader("Expires", time);
        ServletOutputStream stream = response.getOutputStream();
        String validateCode = EncoderHelper.getChallangeAndWriteImage(cs,
                "png", stream);
        request.getSession().setAttribute(GlobalConstant.VERIFICATION_CODE, validateCode);
        stream.flush();
        stream.close();
    }
}
