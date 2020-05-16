//package com.revengemission.sso.oauth2.server.config;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.revengemission.sso.oauth2.server.domain.RoleEnum;
//import com.revengemission.sso.oauth2.server.domain.UserInfo;
//import com.revengemission.sso.oauth2.server.persistence.entity.RoleEntity;
//import com.revengemission.sso.oauth2.server.persistence.entity.ThirdPartyAccountEntity;
//import com.revengemission.sso.oauth2.server.persistence.repository.RoleRepository;
//import com.revengemission.sso.oauth2.server.persistence.repository.ThirdPartyAccountRepository;
//import com.revengemission.sso.oauth2.server.utils.JsonUtil;
//import org.springframework.security.authentication.AbstractAuthenticationToken;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
//import org.springframework.security.oauth2.provider.*;
//import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
//import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.IOException;
//import java.util.*;
//
///**
// * 微信小程序登陆，返回JWT
// */
//public class WeChatMiniProgramTokenGranter extends AbstractTokenGranter {
//
//    private static final String GRANT_TYPE = "wechat_mini";
//    private String weChatCodeUrl = "https://api.weixin.qq.com/sns/jscode2session?appid={appId}&secret={secret}&js_code={code}&grant_type=authorization_code";
//    private String appId;
//    private String secret;
//
//    private ThirdPartyAccountRepository thirdPartyAccountRepository;
//
//    private RoleRepository roleRepository;
//
//    private RestTemplate restTemplate = new RestTemplate();
//
//    public WeChatMiniProgramTokenGranter(ThirdPartyAccountRepository thirdPartyAccountRepository,
//                                         RoleRepository roleRepository,
//                                         AuthorizationServerTokenServices authorizationServerTokenServices,
//                                         ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory,
//                                         String appId,
//                                         String secret) {
//        super(authorizationServerTokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
//        this.thirdPartyAccountRepository = thirdPartyAccountRepository;
//        this.roleRepository = roleRepository;
//        this.appId = appId;
//        this.secret = secret;
//    }
//
//    @Override
//    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
//
//        Map<String, String> parameters = tokenRequest.getRequestParameters();
//        //客户端提交的用户名
//        String code = parameters.get("code");
//
//        Map<String, Object> params = new HashMap<>(16);
//        params.put("appId", appId);
//        params.put("secret", secret);
//        params.put("code", code);
//        String result = restTemplate.getForObject(weChatCodeUrl, String.class, params);
//
//        try {
//            Map<String, String> openIdMap = JsonUtil.jsonStringToObject(result, new TypeReference<Map<String, String>>() {
//            });
//            if (openIdMap.containsKey("openid")) {
//                String openId = openIdMap.get("openid");
//                ThirdPartyAccountEntity thirdPartyAccountEntity = thirdPartyAccountRepository.findByThirdPartyAndThirdPartyAccountId(GRANT_TYPE, openId);
//                if (thirdPartyAccountEntity == null) {
//                    thirdPartyAccountEntity = new ThirdPartyAccountEntity();
//                    thirdPartyAccountEntity.setThirdParty(GRANT_TYPE);
//                    thirdPartyAccountEntity.setThirdPartyAccountId(openId);
//                    thirdPartyAccountEntity.setAccountOpenCode(UUID.randomUUID().toString());
//
//                    RoleEntity roleEntity = roleRepository.findByRoleName(RoleEnum.ROLE_USER.name());
//                    thirdPartyAccountEntity.getRoles().add(roleEntity);
//                    thirdPartyAccountRepository.save(thirdPartyAccountEntity);
//                }
//
//                UserInfo user = new UserInfo(thirdPartyAccountEntity.getAccountOpenCode(), openId, "", getAuthorities(thirdPartyAccountEntity.getRoles()));
//                AbstractAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//                // 关于user.getAuthorities(): 我们的自定义用户实体是实现了
//                // org.springframework.security.core.userdetails.UserDetails 接口的, 所以有
//                // user.getAuthorities()
//                // 当然该参数传null也行
//                userAuth.setDetails(parameters);
//
//                OAuth2Request auth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
//                return new OAuth2Authentication(auth2Request, userAuth);
//            } else {
//                throw new InvalidGrantException("获取openid失败");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new InvalidGrantException("获取openid失败");
//        }
//
//
//    }
//
//    private Collection<? extends GrantedAuthority> getAuthorities(List<RoleEntity> roles) {
//        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        if (roles != null && roles.size() > 0) {
//            for (RoleEntity temp : roles) {
//                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(temp.getRoleName());
//                grantedAuthorities.add(grantedAuthority);
//            }
//        }
//        return grantedAuthorities;
//    }
//
//
//}
