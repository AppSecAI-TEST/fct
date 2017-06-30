package com.jobservice.tasktracker.test;

import com.github.ltsopensource.core.domain.Job;
import com.jobservice.tasktracker.TaskTrackerStarter;
import com.jobservice.tasktracker.shard.PaymentShard;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.UUID;

/**
 * Created by ningyang on 2017/7/1.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TaskTrackerStarter.class})
@ActiveProfiles(value = {"de"})
public class JobTaskTrackerTest {

    @Autowired
    private PaymentShard paymentShard;

    @Test
    public void paymentProcess(){
        Job job = new Job();
        job.setReplaceOnExist(true);
        job.setTaskId(UUID.randomUUID().toString());
        job.setParam("fct-job", "PAYMENT");
        job.setParam("amount", "5000.00");
        job.setParam("account", "12321515");
        job.setTriggerDate(new Date());
        paymentShard.processPayment(job);
    }
}
