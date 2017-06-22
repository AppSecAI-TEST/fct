package com.fct.common.service.oss;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by nick on 2017/5/17.
 */
@Data
public class DeleteResponse {

    private Map<String, String> userMetaData;

    private List<String> keys;

    /**
     * 返回码 0 成功 1000 失败
     */
    private int code;

    /**
     * 返回信息
     */
    private String msg;
}
