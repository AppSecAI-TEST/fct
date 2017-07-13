package com.fct.common.interfaces;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by z on 17-7-12.
 */

@Data
public class WeChatShareResponse implements Serializable {

    private Boolean debug;
    private String appId;
    private Integer timestamp;
    private String nonceStr;
    private String signature;
}
