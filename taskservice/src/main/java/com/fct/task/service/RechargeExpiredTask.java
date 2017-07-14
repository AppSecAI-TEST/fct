package com.fct.task.service;

import com.fct.finance.interfaces.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RechargeExpiredTask {

    @Autowired
    private FinanceService financeService;

    /**
     * 充值订单过期处理，每隔30分钟执行一次
     * */

    @Scheduled(cron = "0 */50 * * * ?")
    public void doWork()
    {
        try {
            financeService.rechargeExpiredTask();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }
}
