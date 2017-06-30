package com.jobservice.node.accepter;

import com.jobservice.node.Result;
import com.jobservice.node.interfaces.JobHandler;
import com.jobservice.task.JobTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ningyang on 2017/6/30.
 * 这个是暴露给调用者专门提交job的http接口
 */
@RestController
@RequestMapping(value = "/fct/job")
public class AcceptorController {

    @Autowired
    private JobHandler jobHandler;

    @PostMapping(value = "/submit")
    public Result raiseJob(@RequestBody JobTask jobTask){
        return jobHandler.raiseJob(jobTask);
    }
}
