package com.fct.mall.service;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by jon on 2017/5/17.
 */
@SpringBootApplication(scanBasePackages = "com.fct.mall")
@ImportResource(value = "classpath:dubbo/dubbo-consumer.xml")
public class StartupApplication extends SpringBootServletInitializer{

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder){
        return applicationBuilder.sources(StartupApplication.class);
    }

    public static void main(String[] args){
        SpringApplication application = new SpringApplication(StartupApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}
