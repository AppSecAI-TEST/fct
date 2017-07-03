package com.jobservice.node.cron;

import com.jobservice.node.ShardConstant;
import com.jobservice.node.interfaces.JobHandler;
import com.jobservice.task.JobTask;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class OrderFinishCron implements InitializingBean {

    @Autowired
    private JobHandler jobHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        JobTask jobTask = new JobTask();
        Map<String, Object> params = new HashMap<>();
        params.putIfAbsent(ShardConstant.SHARD_VALUE, "orderfinish");
        jobTask.setParams(params);
        jobTask.setTaskId(UUID.randomUUID().toString());
        jobTask.setCronExpression("0 0 5,18 * * ?");    //每天5点和18点分钟执行一次
        jobHandler.raiseJob(jobTask);
    }
}
