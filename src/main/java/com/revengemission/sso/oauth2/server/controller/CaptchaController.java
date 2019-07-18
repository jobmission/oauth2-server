package com.revengemission.sso.oauth2.server.controller;

import com.revengemission.commons.captcha.core.VerificationCodeUtil;
import com.revengemission.sso.oauth2.server.config.CachesEnum;
import com.revengemission.sso.oauth2.server.service.CaptchaService;
import com.revengemission.sso.oauth2.server.service.UserAccountService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class CaptchaController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private CaptchaService captchaService;

    private UserAccountService userAccountService;

    @Autowired
    public CaptchaController(CaptchaService captchaService, UserAccountService userAccountService) {
        this.captchaService = captchaService;
        this.userAccountService = userAccountService;
    }

    /**
     * 图形验证码
     */
    @ResponseBody
    @RequestMapping(value = "/captcha/graph")
    public Map<String, Object> captchaGraph() {

        Map<String, Object> resultMap = new HashMap<>(16);

        String uuid = UUID.randomUUID().toString();
        String captcha = VerificationCodeUtil.generateVerificationCode(4, null);

        resultMap.put("status", 1);
        resultMap.put("ttl", CachesEnum.GraphCaptchaCache.getTtl());
        resultMap.put("graphId", uuid);
        resultMap.put("graphUrl", "/captcha/graph/print?graphId=" + uuid);

        captchaService.saveCaptcha(CachesEnum.GraphCaptchaCache, uuid, captcha);

        log.debug("captcha=" + captcha);
        return resultMap;

    }

    /**
     * 短信证码
     *
     * @param phone   手机号
     * @param graphId 图形验证码id
     */
    @ResponseBody
    @RequestMapping(value = "/captcha/sms")
    public Map<String, Object> captchaSms(@RequestParam(value = "signType", required = false, defaultValue = "signIn") String signType,
                                          @RequestParam(value = "phone") String phone,
                                          @RequestParam(value = "captcha") String inputCaptcha, @RequestParam(value = "graphId") String graphId) {
        Map<String, Object> resultMap = new HashMap<>(16);

        String captcha = captchaService.getCaptcha(CachesEnum.GraphCaptchaCache, graphId);

        if (StringUtils.equalsIgnoreCase(inputCaptcha, captcha)) {

            if (StringUtils.equalsIgnoreCase(signType, "signIn") && !userAccountService.existsByUsername(phone)) {
                resultMap.put("status", 0);
                resultMap.put("message", "账号不存在");
                return resultMap;
            }

            String uuid = UUID.randomUUID().toString();
            String smsCaptcha = RandomStringUtils.randomNumeric(4);

            captchaService.saveCaptcha(CachesEnum.SmsCaptchaCache, uuid, phone + "_" + smsCaptcha);

            log.info("smsCaptcha=" + smsCaptcha);
            // TODO send sms smsCaptcha

            resultMap.put("status", 1);
            resultMap.put("smsId", uuid);
            resultMap.put("ttl", CachesEnum.SmsCaptchaCache.getTtl());
            captchaService.removeCaptcha(CachesEnum.GraphCaptchaCache, graphId);
        } else {
            resultMap.put("status", 0);
            resultMap.put("message", "验证码错误！");
        }

        return resultMap;
    }

    /**
     * 图形验证码打印
     *
     * @param graphId 验证码编号
     * @param width   图片宽度
     * @param height  图片高度
     */
    @RequestMapping(value = "/captcha/graph/print")
    public void captchaGraphPrint(HttpServletResponse response,
                                  @RequestParam(value = "graphId") String graphId,
                                  @RequestParam(value = "w", defaultValue = "150") int width,
                                  @RequestParam(value = "h", defaultValue = "38") int height) throws IOException {

        String captcha = captchaService.getCaptcha(CachesEnum.GraphCaptchaCache, graphId);
        if (StringUtils.isBlank(captcha)) {
            captcha = "0000";
        }
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        long time = System.currentTimeMillis();
        response.setDateHeader("Last-Modified", time);
        response.setDateHeader("Date", time);
        response.setDateHeader("Expires", time);
        ServletOutputStream stream = response.getOutputStream();
        VerificationCodeUtil.outputImage(width, height, stream, captcha);
        stream.flush();
        stream.close();

    }

    /**
     * 图形验证码Base64
     *
     * @param graphId 验证码编号
     * @param width   图片宽度
     * @param height  图片高度
     */
    @ResponseBody
    @RequestMapping(value = "/captcha/graph/base64")
    public Map<String, Object> captchaGraphBase64(@RequestParam(value = "graphId") String graphId, @RequestParam(value = "w", defaultValue = "150") int width,
                                                  @RequestParam(value = "h", defaultValue = "38") int height) throws IOException {

        Map<String, Object> resultMap = new HashMap<>(16);
        String captcha = captchaService.getCaptcha(CachesEnum.GraphCaptchaCache, graphId);
        if (captcha != null) {
            String base64EncodedGraph = VerificationCodeUtil.outputImage(width, height, captcha);
            resultMap.put("status", 1);
            resultMap.put("base64EncodedGraph", base64EncodedGraph);
        } else {
            resultMap.put("status", 0);
            resultMap.put("message", "验证码编号无效！");
        }
        return resultMap;

    }

}
