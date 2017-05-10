package com.fct.message.service;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by ningyang on 2017/4/11.
 */
@SpringBootApplication(scanBasePackages = "com.fct.message")
@ImportResource("classpath:dubbo/dubbo-provider.xml")
public class StartUpApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StartUpApplication.class);
    }

    public static void main(String[] args){
        SpringApplication app = new SpringApplication(StartUpApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
}
