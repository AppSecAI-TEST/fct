package com.fct.master.service.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by nick on 2017/5/24.
 * 项目启动类
 */
@SpringBootApplication(scanBasePackages = "com.fct.master")
@ImportResource("classpath:dubbo/dubbo-provider.xml")
public class ApplicationStartup {

    public static void main(String[] args){
        SpringApplication.run(ApplicationStartup.class, args);
    }
}
