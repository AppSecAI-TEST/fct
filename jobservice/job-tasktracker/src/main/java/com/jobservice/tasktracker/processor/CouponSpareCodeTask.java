package com.jobservice.tasktracker.processor;

import com.fct.promotion.interfaces.PromotionService;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.spring.tasktracker.JobRunnerItem;
import com.github.ltsopensource.spring.tasktracker.LTS;
import com.github.ltsopensource.tasktracker.Result;
import com.jobservice.tasktracker.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@LTS
public class CouponSpareCodeTask {

    @Autowired
    private PromotionService promotionService;

    /**
     * 生成空闲优惠券
     * */
    @JobRunnerItem(shardValue = "couponSpareCode")
    public Result handle()
    {
        try {
            promotionService.generateSpareCodeTask();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            return new Result(Action.EXECUTE_LATER,exp.toString());
        }

        return new Result(Action.EXECUTE_SUCCESS, "couponSpareCode process success!!");
    }
}
