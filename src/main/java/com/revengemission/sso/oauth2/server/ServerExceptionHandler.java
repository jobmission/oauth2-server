package com.revengemission.sso.oauth2.server;

import com.revengemission.sso.oauth2.server.utils.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ServerExceptionHandler {

    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());


    @ExceptionHandler({
        NoHandlerFoundException.class
    })
    public ResponseEntity<Object> handleNoHandlerFoundException(Exception ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        logRequest(ex, httpStatus, request);
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> responseResult = new HashMap<>(16);
        responseResult.put("status", httpStatus.value());
        responseResult.put("message", ex.getMessage());
        responseResult.put("url", request.getRequestURL());
        return new ResponseEntity<>(responseResult, headers, httpStatus);
    }

    @ExceptionHandler({
        AccessDeniedException.class
    })
    @ResponseBody
    ResponseEntity<Object> handleDeniedException(Exception ex, HttpServletRequest request) {

        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        logRequest(ex, httpStatus, request);
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> responseResult = new HashMap<>(16);
        responseResult.put("status", httpStatus.value());
        responseResult.put("error", httpStatus.getReasonPhrase());
        responseResult.put("timestamp", new Date());
        responseResult.put("message", ex.getMessage());
        responseResult.put("path", request.getRequestURL());
        return new ResponseEntity<>(responseResult, headers, httpStatus);
    }


    /**
     * 捕获全局异常，处理所有不可知的异常
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    ResponseEntity<Object> handleException(Exception ex, HttpServletRequest request) {

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        logRequest(ex, httpStatus, request);
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> responseResult = new HashMap<>(16);
        responseResult.put("status", httpStatus.value());
        responseResult.put("error", httpStatus.getReasonPhrase());
        responseResult.put("timestamp", new Date());
        responseResult.put("message", ex.getMessage());
        responseResult.put("path", request.getRequestURL());
        return new ResponseEntity<>(responseResult, headers, httpStatus);
    }

    /**
     * 记录下请求内容
     *
     * @param ex
     * @param status
     * @param request
     */
    private void logRequest(Exception ex, HttpStatus status, HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        try {
            String uri = request.getRequestURI();
            log.error("User Agent =" + request.getHeader("User-Agent") +
                ";\nstatus =" + status.toString() + ",reason " + status.getReasonPhrase() +
                ";\nexception =" + ex.getMessage() +
                ";\nuri =" + uri +
                ";\ncontent Type =" + request.getHeader("content-type") +
                ";\nrequest parameters =" + JsonUtil.multiValueMapToJsonString(parameters), ex);
        } catch (Exception e) {
            log.error("ControllerAdvice log  Exception", e);
        }
    }

}
