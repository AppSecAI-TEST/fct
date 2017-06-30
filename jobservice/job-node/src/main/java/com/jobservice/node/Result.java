package com.jobservice.node;

import lombok.Data;

/**
 * Created by ningyang on 2017/4/11.
 */
@Data
public class Result {

    private Integer code;

    private String msg;

    public Result(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public Result(int code){
        this(code, null);
    }

    public Result(String msg){
        this(null, msg);
    }

    public Result(){

    }
}
