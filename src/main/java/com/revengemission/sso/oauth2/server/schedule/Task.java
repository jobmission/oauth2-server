package com.revengemission.sso.oauth2.server.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Task {

    @Scheduled(cron = "0 0 * * * *")
    public void task() {
        System.out.println("[ 当前时间" + LocalDateTime.now() + " ]");
    }
}
