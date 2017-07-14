package com.fct.task.service;

import com.fct.finance.interfaces.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SettleTask {

    @Autowired
    private FinanceService financeService;

    /**
     *
     * 销售结算//每天4点和17点各执行一次，17后财务再确认结算
     * */

    @Scheduled(cron = "0 0 4,17 * * ?")
    public void doWork()
    {
        try {
            financeService.settleTask();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }
}
