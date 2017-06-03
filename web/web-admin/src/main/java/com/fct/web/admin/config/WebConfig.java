package com.fct.web.admin.config;

import com.fct.web.admin.http.exceptions.handlers.DefaultExceptionHandler;
import com.fct.web.admin.http.exceptions.handlers.ServiceExceptionHandler;
import com.fct.web.admin.http.filters.RequestWrapperFilter;
import com.fct.web.admin.http.support.session.SessionExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.DispatcherType;
import java.util.List;

/**
 *
 * @author ningyang
 */
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter{

//    @Autowired
//    private SessionUtil sessionService;

//    @Autowired
//    private ReplayAttackDefender defender;

    @Autowired
    private Environment environment;

//    @Bean
//    @Order(0)
//    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
//
//        RequestMappingHandlerMapping handlerMapping = new VersionedRequestMappingHandlerMapping();
//        String profile = getActiveProfile();
//        Boolean isSandbox = "de".equals(profile) || "te".equals(profile);
//        List<Object> interceptorList = Lists.newLinkedList();
//        interceptorList.add(new GateInterceptor(sessionService));
//        if (!"de".equals(profile)) {
//            interceptorList.add(new RequestHeaderInterceptor(isSandbox));
//            interceptorList.add(new RequestAccessTokenInterceptor(sessionService, isSandbox));
//            interceptorList.add(new RequestSignatureInterceptor(sessionService, isSandbox));
//            interceptorList.add(new RequestReplayDefenderInterceptor(defender, isSandbox));
//        }
//        handlerMapping.setInterceptors(interceptorList.toArray());
//        return handlerMapping;
//    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        super.addFormatters(registry);
    }

//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//        argumentResolvers.add(new AccessTokenResolver(sessionService));
//    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new SessionExceptionHandler());
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

}