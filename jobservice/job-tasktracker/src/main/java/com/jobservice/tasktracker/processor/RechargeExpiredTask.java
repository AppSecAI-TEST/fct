package com.jobservice.tasktracker.processor;

import com.fct.finance.interfaces.FinanceService;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.spring.tasktracker.JobRunnerItem;
import com.github.ltsopensource.spring.tasktracker.LTS;
import com.github.ltsopensource.tasktracker.Result;
import com.jobservice.tasktracker.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@LTS
public class RechargeExpiredTask {

    @Autowired
    private FinanceService financeService;

    /**
     * 充值订单过期处理
     * */

    @JobRunnerItem(shardValue = "orderExpired")
    public Result handle()
    {
        try {
            financeService.rechargeExpiredTask();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            return new Result(Action.EXECUTE_LATER,exp.toString());
        }

        return new Result(Action.EXECUTE_SUCCESS, "orderExpired process success!!");
    }
}
