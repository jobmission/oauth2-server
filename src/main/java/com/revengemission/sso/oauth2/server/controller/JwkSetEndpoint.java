package com.revengemission.sso.oauth2.server.controller;


import com.revengemission.sso.oauth2.server.utils.BigIntegerUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
class JwkSetEndpoint implements InitializingBean {

    @Value("${jwt.jks.keypass:keypass}")
    String keypass;

    @Autowired
    KeyPair keyPair;

    @Value("${oauth2.issuer-uri:http://localhost:10380}")
    String issuerUri;


    @GetMapping("/.well-known/jwks.json")
    @ResponseBody
    public Map<String, List<Map<String, Object>>> getKey() {
        Map<String, List<Map<String, Object>>> jwksData = new HashMap<>(16);
        RSAPublicKey publicKey = (RSAPublicKey) this.keyPair.getPublic();

        String n = new String(Base64Utils.encode(BigIntegerUtils.toBytesUnsigned(publicKey.getModulus())));
        String e = new String(Base64Utils.encode(BigIntegerUtils.toBytesUnsigned(publicKey.getPublicExponent())));
        Map<String, Object> jwk = new HashMap<>(16);
        jwk.put("kty", publicKey.getAlgorithm());
        jwk.put("n", n);
        jwk.put("e", e);
        jwksData.put("keys", Arrays.asList(jwk));
        return jwksData;
    }

    /**
     * Authorization Server Metadata Request
     * https://tools.ietf.org/html/rfc8414#section-3.1
     *
     * @return
     */
    @GetMapping("/.well-known/oauth-authorization-server")
    @ResponseBody
    public Map<String, String> metadataRequest() {
        Map<String, String> metaData = new HashMap<>(16);
        metaData.put("issuer", issuerUri);
        metaData.put("authorization_endpoint", issuerUri + "/oauth/authorize");
        metaData.put("token_endpoint", issuerUri + "/oauth/token");
        metaData.put("check_token", issuerUri + "/oauth/check_token");
        metaData.put("jwks_uri", issuerUri + "/.well-known/jwks.json");
        metaData.put("userinfo_endpoint", issuerUri + "/user/me");
        return metaData;
    }

    @Override
    public void afterPropertiesSet() {
///        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), keypass.toCharArray());
///       this.keyPair = keyStoreKeyFactory.getKeyPair("jwt");

    }
}
