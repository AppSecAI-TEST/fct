package com.fct.task.service;

import com.fct.mall.interfaces.MallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OrderFinishTask {

    @Autowired
    private MallService mallService;

    /**
     * 订单过期处理，每天5点和18点分钟执行一次
     * */
    @Scheduled(cron = "0 0 5,18 * * ?")
    public void doWork()
    {
        try {
            mallService.orderFinishTask();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }
}
