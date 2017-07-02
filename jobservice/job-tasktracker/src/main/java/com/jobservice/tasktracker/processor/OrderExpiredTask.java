package com.jobservice.tasktracker.processor;

import com.fct.mall.interfaces.MallService;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.spring.tasktracker.JobRunnerItem;
import com.github.ltsopensource.spring.tasktracker.LTS;
import com.github.ltsopensource.tasktracker.Result;
import com.jobservice.tasktracker.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@LTS
public class OrderExpiredTask {

    @Autowired
    private MallService mallService;

    /**
     * 订单过期处理
     * */
    @JobRunnerItem(shardValue = "orderExpired")
    public Result handle()
    {
        try {
            mallService.orderExpiredTask();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            return new Result(Action.EXECUTE_LATER,exp.toString());
        }

        return new Result(Action.EXECUTE_SUCCESS, "orderExpired process success!!");
    }
}
