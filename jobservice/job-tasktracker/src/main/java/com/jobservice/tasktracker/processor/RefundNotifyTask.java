package com.jobservice.tasktracker.processor;

import com.fct.core.json.JsonConverter;
import com.fct.finance.interfaces.FinanceService;
import com.fct.message.data.entity.MessageQueue;
import com.fct.message.interfaces.MessageService;
import com.fct.message.interfaces.model.MQPayRefund;
import com.fct.pay.interfaces.MobilePayService;
import com.fct.pay.interfaces.PayNotify;
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
public class RefundNotifyTask {

    @Autowired
    private MessageService messageService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private MobilePayService mobilePayService;

    /**
     * 原路退款至第三方支付平台
     * */

    @JobRunnerItem(shardValue = "refundNotify")
    public Result handle()
    {
        try {
            List<MessageQueue> lsMSG = messageService.find("mq_payrefund");

            for (MessageQueue msg:lsMSG
                    ) {
                MQPayRefund refund = JsonConverter.toObject(msg.getBody(),MQPayRefund.class);

                PayNotify payNotify = null;
                Boolean updateRefundStatus = false;
                try {
                    switch (refund.getPay_platform()) {
                        case "wxpay_fctwap":
                            //String payment,String payOrderId,String refundId,BigDecimal payAmount,BigDecimal refundAmount
                            payNotify = mobilePayService.wxpayWapRefund(refund.getPay_platform(),refund.getPay_orderid(),
                                    refund.getRefund_id().toString(),refund.getPay_amount(),refund.getRefund_amount());
                            updateRefundStatus = true;
                            break;
                        case "wxpay_fctapp":
                            payNotify = mobilePayService.wxpayAppRefund(refund.getPay_platform(),refund.getPay_orderid(),
                                    refund.getRefund_id().toString(),refund.getPay_amount(),refund.getRefund_amount());
                            updateRefundStatus = true;
                            break;
                        case "unionpay_fctwap":
                            payNotify = mobilePayService.unionpayWapRefund(refund.getPay_platform(),refund.getPay_orderid(),
                                    refund.getRefund_id().toString(),refund.getPay_amount(),refund.getRefund_amount());
                            break;
                    }
                    if(payNotify !=null && !payNotify.getHasError())
                    {
                        if(updateRefundStatus) {
                            financeService.refundSuccess(Integer.valueOf(payNotify.getPayOrderNo()),
                                    JsonConverter.toJson(payNotify.getExtandProperties()));
                        }
                        messageService.complete(msg.getId()); //完成
                    }
                }
                catch (Exception exp)
                {
                    messageService.fail(msg.getId(),exp.getMessage());
                    //写入日志；
                    Constants.logger.error(exp.toString());
                }

            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            return new Result(Action.EXECUTE_LATER,exp.toString());
        }

        return new Result(Action.EXECUTE_SUCCESS, "refundNotify process success!!");
    }
}
