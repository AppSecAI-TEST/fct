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
public class SettleTask {

    @Autowired
    private FinanceService financeService;

    /**
     *
     * 销售结算
     * */

    @JobRunnerItem(shardValue = "settle")
    public Result handle()
    {
        try {
            financeService.settleTask();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            return new Result(Action.EXECUTE_LATER,exp.toString());
        }

        return new Result(Action.EXECUTE_SUCCESS, "settle process success!!");
    }
}
