package com.revengemission.sso.oauth2.server;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Spring boot 事件监听，可以额外增加处理逻辑
 */
@Component
public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        System.out.format("\33[32;3m" + "Application started @ " + LocalDateTime.now() + " ! %n");
        System.out.format("\33[32;4m" + "-----------------------------------------------%n");
        System.out.format("\33[10;2m" + "%n");
    }
}
