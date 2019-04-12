package com.revengemission.sso.oauth2.server.service;

public interface CaptchaService {
	public boolean saveGraphCaptcha(String key, Object value);

	public String getGraphCaptcha(String key);

	public void removeGraphCaptcha(String key);

	public boolean saveSmsCaptcha(String key, Object value);

	public String getSmsCaptcha(String key);

	public void removeSmsCaptcha(String key);
}
