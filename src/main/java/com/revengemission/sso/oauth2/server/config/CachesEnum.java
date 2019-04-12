package com.revengemission.sso.oauth2.server.config;

public enum CachesEnum {
	DefaultCache, // 使用默认值
	SmsCaptchaCache(60 * 3), // 过期。最大容量使用默认值
	GraphCaptchaCache(60 * 5, 100000), // 指定过期时间和最大容量
	;
	CachesEnum() {
	}

	CachesEnum(int ttl) {
		this.ttl = ttl;
	}

	CachesEnum(int ttl, int maxSize) {
		this.ttl = ttl;
		this.maxSize = maxSize;
	}

	private int maxSize = 100000;
	private int ttl = 60 * 5;

	public int getMaxSize() {
		return maxSize;
	}

	public int getTtl() {
		return ttl;
	}
}
