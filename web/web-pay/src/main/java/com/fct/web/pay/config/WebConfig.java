package com.fct.web.pay.config;

import com.fct.web.pay.http.exceptions.handlers.DefaultExceptionHandler;
import com.fct.web.pay.http.exceptions.handlers.ServiceExceptionHandler;
import com.fct.web.pay.http.filters.RequestWrapperFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.DispatcherType;
import java.nio.charset.Charset;
import java.util.List;

/**
 *
 * @author ningyang
 */
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter{

    @Autowired
    private Environment environment;

    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(
                Charset.forName("UTF-8"));
        return converter;
    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        super.addFormatters(registry);
    }


    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new ServiceExceptionHandler());
        exceptionResolvers.add(new DefaultExceptionHandler());//default exception handler
    }

    @Bean
    public FilterRegistrationBean apiSecurityFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RequestWrapperFilter());
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        return registration;
    }

    @Bean//etag
    public FilterRegistrationBean myFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setOrder(0);
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new ShallowEtagHeaderFilter());
        return registration;
    }

    private String getActiveProfile(){
        if(environment.getActiveProfiles()!=null&&environment.getActiveProfiles().length>0){
            return environment.getActiveProfiles()[0];
        }
        return "pe1";
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}