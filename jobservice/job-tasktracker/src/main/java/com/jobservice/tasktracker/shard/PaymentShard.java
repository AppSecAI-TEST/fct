package com.jobservice.tasktracker.shard;

import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.spring.tasktracker.JobRunnerItem;
import com.github.ltsopensource.spring.tasktracker.LTS;
import com.github.ltsopensource.tasktracker.Result;
import com.jobservice.tasktracker.processor.PaymentProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by ningyang on 2017/6/30.
 * 这个是job的处理分配器, 每个类必须带有LTS注解
 * 里面的方法必须带有@JobRunnerItem注解
 * 其中@JobRunnerItem注解的ShardValue必须跟JobTask的taskType名称保持一致
 * 这样提交的任务就会分配到相应的分配器去处理
 * 可以有多个分配器
 * eg. 支付分配器专门分配提交的支付任务, 短信分配器专门分配短信的任务
 */
@Slf4j
@LTS
public class PaymentShard {
    
    @Autowired
    private PaymentProcessor paymentProcessor;

    @JobRunnerItem(shardValue = "PAYMENT")
    public Result processPayment(Job job){
        try{
            Long amount = Long.valueOf(job.getParam("amount"));
            String account = job.getParam("account");
            paymentProcessor.processPayment(amount, account);
        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            log.error(sw.toString());
            return new Result(Action.EXECUTE_LATER, sw.toString());
        }
        return new Result(Action.EXECUTE_SUCCESS, "payment process success!!");
    }
}
