package com.revengemission.sso.oauth2.server.domain;

public class GlobalConstant {

    /**
     * 返回成功
     */
    public final static int SUCCESS = 1;
    /**
     * 返回失败
     */
    public final static int ERROR = -1;
    /**
     * 参数类型错误
     */
    public final static int ERROR_PARAMETER_TYPE = 400;
    /**
     * 没有找到
     */
    public final static int ERROR_NOT_FOUNT = 404;
    /**
     * 未登录
     */
    public final static int ERROR_NO_LOGIN = 401;
    /**
     * 权限不足
     */
    public final static int ERROR_DENIED = 403;

    /**
     * 验证码key
     */
    public final static String VERIFICATION_CODE = "verificationCode";
    /**
     * 验证码Id
     */
    public final static String GRAPH_ID = "graphId";
}
