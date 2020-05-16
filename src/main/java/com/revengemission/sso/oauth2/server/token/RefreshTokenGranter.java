package com.revengemission.sso.oauth2.server.token;

import com.revengemission.sso.oauth2.server.domain.OauthClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;

import java.security.KeyPair;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RefreshTokenGranter implements TokenGranter {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String GRANT_TYPE = "refresh_token";
    private final AuthenticationManager authenticationManager;
    KeyPair keyPair;
    String issuer;

    public RefreshTokenGranter(AuthenticationManager authenticationManager, KeyPair keyPair, String issuer) {
        this.authenticationManager = authenticationManager;
        this.keyPair = keyPair;
        this.issuer = issuer;
    }

    @Override
    public Map<String, Object> grant(OauthClient client, String grantType, Map<String, String> parameters) {

        Map<String, Object> result = new HashMap<>();
        result.put("status", 0);

        String refreshToken = parameters.get("refresh_token");

        if (!GRANT_TYPE.equals(grantType)) {
            return result;
        }

        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(refreshToken).getBody();
            Date now = new Date();
            Date tokenExpiration = Date.from(LocalDateTime.now().plusSeconds(client.getAccessTokenValidity()).atZone(ZoneId.systemDefault()).toInstant());
            Date refreshTokenExpiration = Date.from(LocalDateTime.now().plusSeconds(client.getRefreshTokenValidity()).atZone(ZoneId.systemDefault()).toInstant());
            String tokenId = UUID.randomUUID().toString();
            claims.setId(tokenId);
            claims.setIssuedAt(now);
            claims.setExpiration(tokenExpiration);
            claims.setNotBefore(now);

            claims.put("jti", tokenId);
            String newRefreshToken = Jwts.builder()
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .signWith(keyPair.getPrivate())
                .compact();

            claims.setId(tokenId);
            claims.setIssuedAt(now);
            claims.setExpiration(tokenExpiration);
            claims.setNotBefore(now);
            claims.remove("jti");
            String accessToken = Jwts.builder()
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .signWith(keyPair.getPrivate())
                .compact();

            result.put("access_token", accessToken);
            result.put("token_type", "bearer");
            result.put("refresh_token", newRefreshToken);
            result.put("expires_in", client.getAccessTokenValidity() - 1);
            result.put("accountOpenCode", claims.get("accountOpenCode"));
            result.put("scope", "user_info");
            result.put("jti", tokenId);
            result.put("status", 1);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("exception", e);
            }
            throw e;
        }


        return result;
    }
}
