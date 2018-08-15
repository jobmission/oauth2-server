package com.revengemission.sso.oauth2.server;

import com.revengemission.sso.oauth2.server.utils.JSONUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;


public class OAuth2Test {

    private String host = "http://localhost:33380";

    @Test
    @Ignore
    public void encodePassword() {
        System.out.println("password=" + new BCryptPasswordEncoder().encode("password"));
        System.out.println("secret=" + new BCryptPasswordEncoder().encode("secret"));
    }


    @Test
    @Ignore
    public void flowTest() throws IOException {
        String token = getToken();

        String result = checkToken(token);

        System.out.println("result = " + result);
    }


    public String getToken() throws IOException {

        String url = host + "/oauth/token";
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
//  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//  也支持中文
        params.add("grant_type", "password");
        params.add("scope", "read");
        params.add("client_id", "SampleClientId");
        params.add("client_secret", "secret");
        params.add("username", "zhangsan");
        params.add("password", "password");
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
//  执行HTTP请求
        ResponseEntity<String> response = client.exchange(url, HttpMethod.POST, requestEntity, String.class);

        String jsonString = response.getBody();
        Map<String, Object> result = JSONUtil.JSONStringToMap(jsonString);
//  输出结果
        System.out.println(result);

        return String.valueOf(result.get("access_token"));
    }


    public String checkToken(String token) throws IOException {

        String url = host + "/oauth/check_token";
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
//  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//  也支持中文
        params.add("token", token);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
//  执行HTTP请求
        ResponseEntity<String> response = client.exchange(url, HttpMethod.POST, requestEntity, String.class);
        Map<String, Object> result = JSONUtil.JSONStringToMap(response.getBody());
//  输出结果
        System.out.println(result);

        return String.valueOf(result.get("active"));
    }


}
