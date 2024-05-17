package com.revengemission.sso.oauth2.server.service.impl;

import com.revengemission.sso.oauth2.server.config.CachesEnum;
import com.revengemission.sso.oauth2.server.service.CaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * 保存到缓存中
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${captcha.max.times:5}")
    private int captchaMaxTimes;

    @Autowired
    CacheManager cacheManager;

    @Override
    public boolean saveCaptcha(CachesEnum cachesEnum, String key, Object value) {
        cacheManager.getCache(cachesEnum.name()).put(key, value);
        return true;
    }

    @Override
    public String getCaptcha(CachesEnum cachesEnum, String key) {
        if (!checkCaptchaTimes(cachesEnum, key)) {
            return null;
        }
        ValueWrapper valueWrapper = cacheManager.getCache(cachesEnum.name()).get(key);
        if (valueWrapper != null) {
            return String.valueOf(valueWrapper.get());
        } else {
            return null;
        }
    }

    @Override
    public void removeCaptcha(CachesEnum cachesEnum, String key) {
        cacheManager.getCache(cachesEnum.name()).evict(key);
    }


    @Override
    public boolean checkCaptchaTimes(CachesEnum cachesEnum, String key) {
        ValueWrapper valueWrapper = cacheManager.getCache(CachesEnum.CaptchaTimesCache.name()).get(key);
        if (valueWrapper != null) {
            int times = Integer.parseInt(String.valueOf(valueWrapper.get()));
            if (times < captchaMaxTimes) {
                cacheManager.getCache(CachesEnum.CaptchaTimesCache.name()).put(key, times + 1);
                return true;
            } else {
                log.debug("验证码达到最大尝试次数：" + cachesEnum + "_" + key);
                removeCaptcha(cachesEnum, key);
                removeCaptcha(CachesEnum.CaptchaTimesCache, key);
                return false;
            }
        } else {
            cacheManager.getCache(CachesEnum.CaptchaTimesCache.name()).put(key, 1);
            return true;
        }
    }

}
