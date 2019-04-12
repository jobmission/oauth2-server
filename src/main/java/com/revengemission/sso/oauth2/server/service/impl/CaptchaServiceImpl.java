package com.revengemission.sso.oauth2.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.revengemission.sso.oauth2.server.config.CachesEnum;
import com.revengemission.sso.oauth2.server.service.CaptchaService;

/*
 * 保存到缓存中
 * 
 * */
@Service
public class CaptchaServiceImpl implements CaptchaService {

	@Autowired
	CacheManager cacheManager;

	@Override
	public boolean saveGraphCaptcha(String key, Object value) {
		cacheManager.getCache(CachesEnum.GraphCaptchaCache.name()).put(key, value);
		return true;
	}

	@Override
	public String getGraphCaptcha(String key) {
		ValueWrapper valueWrapper = cacheManager.getCache(CachesEnum.GraphCaptchaCache.name()).get(key);
		if (valueWrapper != null) {
			return String.valueOf(valueWrapper.get());
		} else {
			return null;
		}
	}

	@Override
	public void removeGraphCaptcha(String key) {
		cacheManager.getCache(CachesEnum.GraphCaptchaCache.name()).evict(key);
	}

	@Override
	public boolean saveSmsCaptcha(String key, Object value) {
		cacheManager.getCache(CachesEnum.SmsCaptchaCache.name()).put(key, value);
		return true;
	}

	@Override
	public String getSmsCaptcha(String key) {
		ValueWrapper valueWrapper = cacheManager.getCache(CachesEnum.SmsCaptchaCache.name()).get(key);
		if (valueWrapper != null) {
			return String.valueOf(valueWrapper.get());
		} else {
			return null;
		}
	}

	@Override
	public void removeSmsCaptcha(String key) {
		cacheManager.getCache(CachesEnum.SmsCaptchaCache.name()).evict(key);
	}

}
