package com.revengemission.sso.oauth2.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.revengemission.sso.oauth2.server.utils.JsonUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;


public class OAuth2Test {

    private Logger log = LoggerFactory.getLogger(this.getClass());


    private String issuerUrl = "http://localhost:8080";

    @Test
    @Disabled
    public void encodePassword() {
        System.out.println("tgb.258------" + new BCryptPasswordEncoder().encode("tgb.258"));
    }


    @Test
    @Disabled
    public void flowTest() throws IOException {
        Map<String, String> result = getToken();

        log.info("token = " + result.get("access_token"));

        String newToken = refreshToken(result.get("refresh_token"));
        log.info("newToken = " + newToken);
    }


    public Map<String, String> getToken() throws IOException {

        String url = issuerUrl + "/oauth2/token";
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
//  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//  也支持中文
        params.add("grant_type", "password");
        params.add("scope", "user_info");
        params.add("client_id", "SampleClientId");
        params.add("client_secret", "tgb.258");
        params.add("username", "zhangsan");
        params.add("password", "tgb.258");
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
//  执行HTTP请求
        ResponseEntity<String> response = client.exchange(url, HttpMethod.POST, requestEntity, String.class);

        String jsonString = response.getBody();
        log.info("getToken:" + jsonString);
        Map<String, String> result = JsonUtil.jsonStringToObject(jsonString, new TypeReference<Map<String, String>>() {
        });
//  输出结果
        return result;
    }


    public String refreshToken(String refresh_token) throws IOException {

        String url = issuerUrl + "/oauth/token";
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
//  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//  也支持中文
        params.add("client_id", "SampleClientId");
        params.add("client_secret", "tgb.258");
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refresh_token);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
//  执行HTTP请求
        ResponseEntity<String> response = client.exchange(url, HttpMethod.POST, requestEntity, String.class);
        String jsonString = response.getBody();
        log.info("refreshToken:" + jsonString);
        Map<String, String> result = JsonUtil.jsonStringToObject(response.getBody(), new TypeReference<Map<String, String>>() {
        });
//  输出结果
        return result.get("access_token");
    }


}
