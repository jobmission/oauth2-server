package com.revengemission.sso.oauth2.server.controller;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.TACBuilder;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

@RequestMapping(value = "/captcha")
@Controller
public class CaptchaController implements InitializingBean {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private ImageCaptchaApplication imageCaptchaApplication;

    @RequestMapping("/gen")
    @ResponseBody
    public ApiResponse<ImageCaptchaVO> genCaptcha(HttpServletRequest request,
                                                  @RequestParam(value = "type", required = false) String type) {
        if (StringUtils.isBlank(type)) {
            type = CaptchaTypeConstant.SLIDER;
        }
        if ("RANDOM".equals(type)) {
            int i = ThreadLocalRandom.current().nextInt(0, 4);
            if (i == 0) {
                type = CaptchaTypeConstant.SLIDER;
            } else if (i == 1) {
                type = CaptchaTypeConstant.CONCAT;
            } else if (i == 2) {
                type = CaptchaTypeConstant.ROTATE;
            } else {
                type = CaptchaTypeConstant.WORD_IMAGE_CLICK;
            }

        }
        ApiResponse<ImageCaptchaVO> response = imageCaptchaApplication.generateCaptcha(type);
        return response;
    }

    @PostMapping("/check")
    @ResponseBody
    public ApiResponse<?> checkCaptcha(@RequestBody Data data,
                                       HttpServletRequest request) {
        ApiResponse<?> response = imageCaptchaApplication.matching(data.getId(), data.getData());
        if (response.isSuccess()) {
            return ApiResponse.ofSuccess(Collections.singletonMap("id", data.getId()));
        }
        return response;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        imageCaptchaApplication = TACBuilder.builder()
            .addDefaultTemplate() // 添加默认模板
            // 给滑块验证码 添加背景图片，宽高为600*360, Resource 参数1为 classpath/file/url , 参数2 为具体url
            .addResource("SLIDER", new Resource("classpath", "META-INF/cut-image/resource/1.jpg")) // 滑块验证的背景图
            .addResource("WORD_IMAGE_CLICK", new Resource("classpath", "META-INF/cut-image/resource/1.jpg")) // 文字点选的背景图
            .addResource("ROTATE", new Resource("classpath", "META-INF/cut-image/resource/1.jpg")) // 旋转验证的背景图
            .build();
        // 2. 添加自定义背景图片
    }

    @lombok.Data
    public static class Data {
        private String id;
        private ImageCaptchaTrack data;
    }

    /**
     * 二次验证，一般用于机器内部调用，这里为了方便测试
     *
     * @param id id
     * @return boolean
     */
    @GetMapping("/check2")
    @ResponseBody
    public boolean check2Captcha(@RequestParam("id") String id) {
        // 如果开启了二次验证
        return false;
    }


}
