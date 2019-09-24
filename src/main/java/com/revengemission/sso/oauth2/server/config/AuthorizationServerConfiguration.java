package com.revengemission.sso.oauth2.server.config;

import com.revengemission.sso.oauth2.server.persistence.repository.RoleRepository;
import com.revengemission.sso.oauth2.server.persistence.repository.ThirdPartyAccountRepository;
import com.revengemission.sso.oauth2.server.service.CaptchaService;
import com.revengemission.sso.oauth2.server.service.impl.ClientDetailsServiceImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Import(AuthorizationServerEndpointsConfiguration.class)
@Configuration
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter implements InitializingBean {
    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    ClientDetailsServiceImpl clientDetailsService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    CaptchaService captchaService;

    @Autowired
    ThirdPartyAccountRepository thirdPartyAccountRepository;

    @Autowired
    RoleRepository roleRepository;

    @Value("${jwt.jks.keypass:keypass}")
    private String keypass;

    @Value("${thirdparty.weixin.mini.appid:1}")
    private String appId;

    @Value("${thirdparty.weixin.mini.secret:1}")
    private String secret;

    @Value("${oauth2.issuer-uri:http://localhost:10380}")
    private String issuerUri;

    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"),
            "keypass" .toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt");
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        accessTokenConverter.setKeyPair(keyPair());

        // 测试用,资源服务使用相同的字符达到一个对称加密的效果,生产时候使用RSA非对称加密方式
        /// accessTokenConverter.setSigningKey("123");
        return accessTokenConverter;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        // CustomTokenEnhancer 是我自定义一些数据放到token里用的
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(new CustomTokenEnhancer(issuerUri), accessTokenConverter()));
        return tokenEnhancerChain;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
    }

    /**
     * 可以用redis等存储
     *
     * @return ApprovalStore
     */
    @Bean
    public ApprovalStore approvalStore() {
        TokenApprovalStore approvalStore = new TokenApprovalStore();
        approvalStore.setTokenStore(tokenStore());
        return approvalStore;
    }

    @Bean
    public DefaultOAuth2RequestFactory oAuth2RequestFactory() {
        return new DefaultOAuth2RequestFactory(clientDetailsService);
    }

    @Bean
    public UserApprovalHandler userApprovalHandler() {
        ApprovalStoreUserApprovalHandler userApprovalHandler = new ApprovalStoreUserApprovalHandler();
        userApprovalHandler.setApprovalStore(approvalStore());
        userApprovalHandler.setClientDetailsService(clientDetailsService);
        userApprovalHandler.setRequestFactory(oAuth2RequestFactory());
        return userApprovalHandler;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        // 注入authenticationManager来支持password模式
        endpoints.authenticationManager(authenticationManager);
        endpoints.accessTokenConverter(accessTokenConverter());
        endpoints.tokenStore(tokenStore());
        // !!!要使用refresh_token的话，需要额外配置userDetailsService!!!
        endpoints.userDetailsService(userDetailsService);
        endpoints.reuseRefreshTokens(true);
        endpoints.tokenGranter(tokenGranter());
        endpoints.authorizationCodeServices(authorizationCodeServices());
        // 设了 tokenGranter 后该配制失效,需要在 tokenServices() 中设置
///        endpoints.tokenEnhancer(tokenEnhancerChain);
        endpoints.userApprovalHandler(userApprovalHandler());

    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer.tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED_CLIENT')")
            .checkTokenAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED_CLIENT')")
            .allowFormAuthenticationForClients();
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        // 使用默认
        return new InMemoryAuthorizationCodeServices();
    }

    @Bean
    public DefaultTokenServices authorizationServerTokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        //token 有效时间,默认12h
        defaultTokenServices.setClientDetailsService(clientDetailsService);
        // 如果没有设置它,JWT就失效了.
        defaultTokenServices.setTokenEnhancer(tokenEnhancer());
        return defaultTokenServices;
    }

    /**
     * 通过 tokenGranter 塞进去的就是它了
     */
    private TokenGranter tokenGranter() {
        return new TokenGranter() {
            private CompositeTokenGranter delegate;

            @Override
            public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                if (delegate == null) {
                    delegate = new CompositeTokenGranter(getDefaultTokenGranters());
                }
                return delegate.grant(grantType, tokenRequest);
            }
        };
    }

    private List<TokenGranter> getDefaultTokenGranters() {

        List<TokenGranter> tokenGranters = new ArrayList<>();
        tokenGranters.add(new AuthorizationCodeTokenGranter(authorizationServerTokenServices(),
            authorizationCodeServices(), clientDetailsService, oAuth2RequestFactory()));
        tokenGranters.add(new RefreshTokenGranter(authorizationServerTokenServices(), clientDetailsService,
            oAuth2RequestFactory()));
        ImplicitTokenGranter implicit = new ImplicitTokenGranter(authorizationServerTokenServices(),
            clientDetailsService, oAuth2RequestFactory());
        tokenGranters.add(implicit);
        tokenGranters.add(new ClientCredentialsTokenGranter(authorizationServerTokenServices(), clientDetailsService,
            oAuth2RequestFactory()));
        if (authenticationManager != null) {
            tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager,
                authorizationServerTokenServices(), clientDetailsService, oAuth2RequestFactory()));
        }

        tokenGranters.add(new SmsCodeTokenGranter(userDetailsService, authorizationServerTokenServices(),
            clientDetailsService, oAuth2RequestFactory(), captchaService));

        tokenGranters.add(new WeChatMiniProgramTokenGranter(thirdPartyAccountRepository, roleRepository, authorizationServerTokenServices(),
            clientDetailsService, oAuth2RequestFactory(), appId, secret));
        return tokenGranters;
    }

    @Override
    public void afterPropertiesSet() {

    }

}
