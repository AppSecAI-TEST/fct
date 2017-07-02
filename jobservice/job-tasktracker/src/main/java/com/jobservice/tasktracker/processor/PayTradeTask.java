package com.jobservice.tasktracker.processor;

import com.fct.finance.interfaces.FinanceService;
import com.fct.message.data.entity.MessageQueue;
import com.fct.message.interfaces.MessageService;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.spring.tasktracker.JobRunnerItem;
import com.github.ltsopensource.spring.tasktracker.LTS;
import com.github.ltsopensource.tasktracker.Result;
import com.jobservice.tasktracker.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
@LTS
public class PayTradeTask {

    @Autowired
    private MessageService messageService;

    @Autowired
    private FinanceService financeService;

    /**
     * 业务方处理后通知支付方。
     * */

    @JobRunnerItem(shardValue = "payTrade")
    public Result handle()
    {
        try {
            List<MessageQueue> lsMSG = messageService.find("mq_paytrade");

            for (MessageQueue msg:lsMSG
                    ) {
                try {

                    financeService.payTradeNotify(msg.getBody());

                    messageService.complete(msg.getId());//消息置位
                }
                catch (Exception exp)
                {
                    messageService.fail(msg.getId(),exp.getMessage());
                    //写入日志
                    Constants.logger.error(exp.toString());
                }

            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            return new Result(Action.EXECUTE_LATER,exp.toString());
        }

        return new Result(Action.EXECUTE_SUCCESS, "payTrade process success!!");
    }
}
