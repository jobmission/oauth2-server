package com.revengemission.sso.oauth2.server.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

@FrameworkEndpoint
class JwkSetEndpoint implements InitializingBean {

    @Value("${jwt.jks.keypass:keypass}")
    private String keypass;

    @Autowired
    KeyPair keyPair;

    @Value("${oauth2.issuer-uri:http://localhost:10380}")
    private String issuerUri;


    @GetMapping("/.well-known/jwks.json")
    @ResponseBody
    public Map<String, Object> getKey() {
        RSAPublicKey publicKey = (RSAPublicKey) this.keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toPublicJWKSet().toJSONObject();
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
        HashMap<String, String> metaData = new HashMap<>(16);
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
