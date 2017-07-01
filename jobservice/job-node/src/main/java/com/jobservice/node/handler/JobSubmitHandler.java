package com.jobservice.node.handler;

import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.jobclient.JobClient;
import com.jobservice.node.Result;
import com.jobservice.node.ShardConstant;
import com.jobservice.node.interfaces.JobHandler;
import com.jobservice.task.JobTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by ningyang on 2017/4/11.
 * @author ningyang
 */
@Service
public class JobSubmitHandler implements JobHandler {

    @Autowired
    private JobClient jobClient;

    /**
     * 如果提交任务 有cron表达式并且触发时间为空则设置cron表达式
     * 提交的任务必须要不有cron表达式要不就有triggerdate
     * @param jobTask
     * @return
     */
    @Override
    public Result raiseJob(JobTask jobTask) {
        Job job = new Job();
        job.setTaskId(jobTask.getTaskId());
        for(Map.Entry entry: jobTask.getParams().entrySet()){
            job.setParam((String) entry.getKey(), (String) entry.getValue());
        }
        //这个地方不要修改
        job.setParam(ShardConstant.SHARD_VALUE, jobTask.getTaskType().name());
        job.setReplaceOnExist(true);
        if(!StringUtils.isEmpty(jobTask.getCronExpression())&&jobTask.getTriggerTime()==null){
            job.setCronExpression(jobTask.getCronExpression());
        }
        if(jobTask.getTriggerTime()!=null){
            job.setTriggerDate(jobTask.getTriggerTime());
        }
        jobClient.submitJob(job);
        Result result = new Result(0, "提交成功");
        return result;
    }
}
