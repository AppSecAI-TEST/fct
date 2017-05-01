package com.fct.api.http.config;



import com.fct.api.data.config.JPAConfig;
import com.fct.api.data.config.RedisConfig;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by nick on 2017/4/3.
 * @author nick
 */
@SpringBootApplication(scanBasePackages = "com.fct.api")
@ImportResource({"classpath:dubbo/dubbo-consumer.xml"})
@Import({RedisConfig.class, JPAConfig.class})
public class ApplicationStartUp extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApplicationStartUp.class);
    }

    public static void main(String[] args){
        SpringApplication app = new SpringApplication(ApplicationStartUp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

}
