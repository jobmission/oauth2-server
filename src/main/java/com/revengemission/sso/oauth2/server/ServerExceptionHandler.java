package com.revengemission.sso.oauth2.server;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ServerExceptionHandler {

    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());


    @ExceptionHandler({
            NoHandlerFoundException.class
    })
    public ResponseEntity<Object> handleNoHandlerFoundException(Exception ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        logRequest(ex, httpStatus, request);
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("status", httpStatus.value());
        responseResult.put("message", ex.getMessage());
        return new ResponseEntity<Object>(responseResult, headers, httpStatus);
    }

    private void logRequest(Exception ex, HttpStatus status, WebRequest webRequest) {
        // 记录下请求内容
        Map<String, String[]> parameters = webRequest.getParameterMap();
        try {
            String uri = "";
            if (webRequest instanceof ServletWebRequest) {
                uri = ((ServletWebRequest) webRequest).getRequest().getRequestURI();
            }
            log.info("User Agent =" + webRequest.getHeader("User-Agent") +
                    ";\nstatus =" + status.toString() + ",reason " + status.getReasonPhrase() +
                    ";\nexception =" + ex.getMessage() +
                    ";\nuri =" + uri +
                    ";\ncontent Type =" + webRequest.getHeader("content-type") +
                    ";\nrequest parameters =" + parameters);
        } catch (Exception e) {
            log.info("log request  Exception: " + e);
        }
    }

}
