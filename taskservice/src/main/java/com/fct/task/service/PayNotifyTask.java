package com.fct.task.service;

import com.fct.core.json.JsonConverter;
import com.fct.finance.interfaces.FinanceService;
import com.fct.mall.interfaces.MallService;
import com.fct.message.data.entity.MessageQueue;
import com.fct.message.interfaces.MessageService;
import com.fct.message.interfaces.model.MQPaySuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayNotifyTask {


    @Autowired
    private MessageService messageService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private MallService mallService;

    /**
     *
     * 支付成功，通知业务方进行处理，每隔3秒执行一次
     * */

    @Scheduled(cron = "*/3 * * * * ?")
    public void doWork()
    {
        try {
            List<MessageQueue> lsMSG = messageService.find("mq_paysuccess");

            for (MessageQueue msg:lsMSG
                    ) {
                MQPaySuccess pay = JsonConverter.toObject(msg.getBody(),MQPaySuccess.class);

                try {
                    switch (pay.getTrade_type()) {
                        case "buy":
                            //String orderId, String payOrderId, String payPlatform, Integer payStatus, String payTime
                            mallService.orderPaySuccess(pay.getTrade_id(), pay.getPay_orderid(),
                                    pay.getPay_platform(), pay.getPay_status(), pay.getPay_time());
                            break;
                        case "recharge":
                            //Integer id, String payOrderId, String payPlatform, String payTime,String payStatus
                            financeService.rechargeSuccess(Integer.valueOf(pay.getTrade_id()),
                                    pay.getPay_orderid(), pay.getPay_platform(), pay.getPay_time(),
                                    pay.getPay_status());
                            break;
                    }

                    messageService.complete(msg.getId()); //完成
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
