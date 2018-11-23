package com.revengemission.sso.oauth2.server;

import com.revengemission.sso.oauth2.server.utils.ClientIPUtils;
import com.revengemission.sso.oauth2.server.utils.JSONUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class WebRequestLogAspect {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(public * com.revengemission.sso.oauth2.server.controller.*.*(..))")
    public void wsLog() {
    }

    @Before("wsLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        if (log.isInfoEnabled()) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                // 记录下请求内容
                Map<String, String[]> parameters = request.getParameterMap();

                try {
                    String parametersString = null;
                    String requestBody = null;
                    if (parameters != null) {
                        parametersString = JSONUtil.multiValueMapToJSONString(parameters);
                    }
                    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                    Method method = signature.getMethod(); //获取被拦截的方法
                    Object object = getAnnotatedParameterValueRequestBody(method, joinPoint.getArgs());
                    if (object != null) {
                        requestBody = JSONUtil.objectToJSONString(object);
                    }
                    log.info("\nRequest from " + ClientIPUtils.getIpAddress(request) +
                            ";\nHeaders =" + JSONUtil.objectToJSONString(getHeadersInfo(request)) +
                            ";\nuri =" + request.getRequestURL().toString() +
                            "; \nrequest method=" + request.getMethod() +
                            "; \ncontent type=" + request.getContentType() +
                            ";\nrequest parameters=" + parametersString +
                            ";\nrequest body=" + requestBody);
                } catch (Exception e) {
                    log.info("Request Object To Json  Exception: " + e);
                }
            }
        }
    }

    @AfterReturning(returning = "ret", pointcut = "wsLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        if (log.isInfoEnabled()) {
            try {
                log.info("Response from server : \n" + JSONUtil.objectToJSONString(ret));
            } catch (Exception e) {
                log.info("Response Object To Json  Exception:\n " + e);
            }
        }
    }

    private Object getAnnotatedParameterValueRequestBody(Method method, Object[] args) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Parameter[] parameters = method.getParameters();

        int i = 0;
        for (Annotation[] annotations : parameterAnnotations) {
            Object arg = args[i];
            String name = parameters[i++].getDeclaringExecutable().getName();
            for (Annotation annotation : annotations) {
                if (annotation instanceof RequestBody) {
                    return arg;
                }
            }
        }
        return null;
    }

    //get request headers
    private Map<String, String> getHeadersInfo(HttpServletRequest request) {

        Map<String, String> map = new HashMap<String, String>();

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }

}