package com.fct.task.service;

import com.fct.finance.interfaces.FinanceService;
import com.fct.message.data.entity.MessageQueue;
import com.fct.message.interfaces.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayTradeTask {

    @Autowired
    private MessageService messageService;

    @Autowired
    private FinanceService financeService;

    /**
     * 业务方处理后通知支付方。每隔10分钟执行一次
     * */

    @Scheduled(cron = "0 */10 * * * ?")
    public void doWork()
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
        }
    }
}
