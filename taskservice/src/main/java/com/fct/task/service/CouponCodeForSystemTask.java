package com.fct.task.service;

import com.fct.promotion.interfaces.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CouponCodeForSystemTask {

    @Autowired
    private PromotionService promotionService;

    /**
     * 生成系统发放优惠券，每隔5分钟执行一次
     * */
    @Scheduled(cron = "0 */5 * * * ?")
    public void doWork()
    {
        try {
            promotionService.generateCouponCodeForSystemTask();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }
}
