package com.fct.task.service;

import com.fct.promotion.interfaces.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CouponCodeExpireTask {

    @Autowired
    private PromotionService promotionService;

    /**
     *
     * 优惠券过期处理，天3点和18点各执行一次
     * */
    @Scheduled(cron = "0 0 3,18 * * ?")
    public void doWork()
    {
        try {
            promotionService.couponCodeExpirseTask();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }
}
