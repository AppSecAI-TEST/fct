package com.fct.task.service;

import com.fct.promotion.interfaces.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CouponSpareCodeTask {

    @Autowired
    private PromotionService promotionService;

    /**
     * 生成空闲优惠券，每隔10分钟执行一次
     * */
    @Scheduled(cron = "0 */10 * * * ?")
    public void doWork()
    {
        try {
            promotionService.generateSpareCodeTask();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }
}
