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
public class CouponCodeExpireTask {

    @Autowired
    private PromotionService promotionService;

    /**
     *
     * 优惠券过期处理
     * */
    @JobRunnerItem(shardValue = "couponCodeExpired")
    public Result handle()
    {
        try {
            promotionService.couponCodeExpirseTask();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            return new Result(Action.EXECUTE_LATER,exp.toString());
        }

        return new Result(Action.EXECUTE_SUCCESS, "couponCodeExpired process success!!");
    }
}
