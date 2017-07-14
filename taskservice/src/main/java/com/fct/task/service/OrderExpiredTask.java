package com.fct.task.service;

import com.fct.mall.interfaces.MallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OrderExpiredTask {

    @Autowired
    private MallService mallService;

    /**
     * 订单过期处理，每隔5分钟执行一次
     * */
    @Scheduled(cron = "0 */5 * * * ?")
    public void doWork()
    {
        try {
            mallService.orderExpiredTask();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }
}
