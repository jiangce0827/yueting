package com.jiangce.yueting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class YuetingServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(YuetingServerApplication.class, args);
        log.info("悦听音乐启动成功");
    }
}
