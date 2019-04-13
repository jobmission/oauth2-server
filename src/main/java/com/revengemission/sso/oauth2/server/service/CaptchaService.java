package com.revengemission.sso.oauth2.server.service;

import com.revengemission.sso.oauth2.server.config.CachesEnum;

public interface CaptchaService {
    boolean saveCaptcha(CachesEnum cachesEnum, String key, Object value);

    String getCaptcha(CachesEnum cachesEnum, String key);

    void removeCaptcha(CachesEnum cachesEnum, String key);

    boolean checkCaptchaTimes(CachesEnum cachesEnum, String key);
}
