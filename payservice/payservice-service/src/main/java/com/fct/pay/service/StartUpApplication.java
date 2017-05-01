package com.fct.pay.service;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by ningyang on 2017/4/11.
 */
@SpringBootApplication(scanBasePackages = "com.fct.pay")
public class StartUpApplication {

    public static void main(String[] args){
        SpringApplication app = new SpringApplication(StartUpApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
}
