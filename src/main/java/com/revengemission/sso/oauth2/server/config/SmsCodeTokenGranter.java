//package com.revengemission.sso.oauth2.server.config;
//
//import com.revengemission.sso.oauth2.server.service.CaptchaService;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.security.authentication.AbstractAuthenticationToken;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
//import org.springframework.security.oauth2.provider.*;
//import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
//import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
//
//import java.util.Map;
//
//public class SmsCodeTokenGranter extends AbstractTokenGranter {
//
//    private static final String GRANT_TYPE = "sms_code";
//
//    private UserDetailsService userDetailsService;
//
//    private CaptchaService captchaService;
//
//    public SmsCodeTokenGranter(UserDetailsService userDetailsService,
//                               AuthorizationServerTokenServices authorizationServerTokenServices,
//                               ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory,
//                               CaptchaService captchaService) {
//        super(authorizationServerTokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
//        this.userDetailsService = userDetailsService;
//        this.captchaService = captchaService;
//    }
//
//    @Override
//    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
//
//        Map<String, String> parameters = tokenRequest.getRequestParameters();
//        // 客户端提交的用户名
//        String userMobileNo = parameters.get("username");
//        // 客户端提交的验证码
//        String smsCodeInput = parameters.get("smsCode");
//        // 客户端提交的验证码编号
//        String smsId = parameters.get("smsId");
//
//        // 从库里查用户
//        UserDetails user = userDetailsService.loadUserByUsername(userMobileNo);
//        if (user == null) {
//            throw new InvalidGrantException("用户不存在");
//        }
//
//        // 验证用户状态(是否禁用等),代码略
//        // 验证验证码
//        String smsCodeCached = captchaService.getCaptcha(CachesEnum.SmsCaptchaCache, smsId);
//
//        if (!StringUtils.equalsIgnoreCase(smsCodeCached, userMobileNo + "_" + smsCodeInput)) {
//            throw new InvalidGrantException("验证码不正确");
//        } else {
//            captchaService.removeCaptcha(CachesEnum.SmsCaptchaCache, smsId);
//        }
//
//        AbstractAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//        // 关于user.getAuthorities(): 我们的自定义用户实体是实现了
//        // org.springframework.security.core.userdetails.UserDetails 接口的, 所以有
//        // user.getAuthorities()
//        // 当然该参数传null也行
//        userAuth.setDetails(parameters);
//
//        OAuth2Request auth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
//        return new OAuth2Authentication(auth2Request, userAuth);
//    }
//
//}
