package com.jobservice.node.test;

import com.jobservice.node.JobNodeStarter;
import com.jobservice.node.Result;
import com.jobservice.node.interfaces.JobHandler;
import com.jobservice.task.JobTask;
import com.jobservice.task.TaskType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ningyang on 2017/6/30.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JobNodeStarter.class})
@ActiveProfiles({"de"})
public class JobSubmitTest {

    @Autowired
    private JobHandler jobHandler;

    @Test
    public void raiseJobNode(){
        JobTask jobTask = new JobTask();
        jobTask.setTaskId(UUID.randomUUID().toString());
        jobTask.setTaskType(TaskType.PAYMENT);
        jobTask.setTriggerTime(new Date());
        Map<String, Object> params = new HashMap<>();
        params.putIfAbsent("amount", "12314");
        jobTask.setParams(params);
        Result result = jobHandler.raiseJob(jobTask);
        Assert.assertEquals(0, result.getCode().intValue());
    }
}
