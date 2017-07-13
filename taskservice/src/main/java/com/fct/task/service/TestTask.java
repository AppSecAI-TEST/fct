package com.fct.task.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Created by ningyang on 2017/7/13.
 * 直接用spring的@schedule注解就行了, 不需要用quartz, 这个service里面可以引用dubbo, 只需要配置cron表达式就行了
 */
@Service
public class TestTask {

    @Scheduled(cron = "0/5 * * * * ?")
    public void doWork(){
        System.out.println(String.format("do the work at %d", System.currentTimeMillis()));
    }
}
