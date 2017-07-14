package com.fct.task.service;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by jon on 2017/6/20.
 */
@SpringBootApplication(scanBasePackages = "com.fct.task")
@ImportResource("classpath:dubbo/dubbo-consumer.xml")
@EnableScheduling
public class StartupApplication {

    public static void main(String[] args){
        SpringApplication application = new SpringApplication(StartupApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}
