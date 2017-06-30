package com.jobservice.node.interfaces;


import com.jobservice.node.Result;
import com.jobservice.task.JobTask;

/**
 * Created by ningyang on 2017/4/11.
 * job处理器
 */
public interface JobHandler {

    Result raiseJob(JobTask jobTask);
}
