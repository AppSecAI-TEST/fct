package com.jobservice.node;

import com.github.ltsopensource.spring.boot.annotation.EnableJobClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * Created by ningyang on 2017/4/11.
 */
@SpringBootApplication(scanBasePackages = "com.jobservice.node")
@EnableJobClient
public class JobNodeStarter extends SpringBootServletInitializer{

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(JobNodeStarter.class);
    }

    public static void main(String[] args){
        SpringApplication.run(JobNodeStarter.class, args);
    }
}
