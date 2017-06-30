package com.jobservice.jobtracker;

import com.github.ltsopensource.spring.boot.annotation.EnableJobTracker;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * Created by ningyang on 2017/4/11.
 */
@SpringBootApplication(scanBasePackages = "com.jobservice.jobtracker")
@EnableJobTracker
public class JobTrackerStarter extends SpringBootServletInitializer{

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(JobTrackerStarter.class);
    }

    public static void main(String[] args){
        SpringApplication app = new SpringApplication(JobTrackerStarter.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
}
