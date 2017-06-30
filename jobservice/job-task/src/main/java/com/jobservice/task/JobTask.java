package com.jobservice.task;

import java.util.Date;
import java.util.Map;

/**
 * Created by ningyang on 2017/6/30.
 */
public class JobTask {

    private String taskId;

    private Map<String, Object> params;

    private TaskType taskType;

    private Date triggerTime;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public Date getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Date triggerTime) {
        this.triggerTime = triggerTime;
    }
}
