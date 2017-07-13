package com.fct.common.interfaces;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by z on 17-7-12.
 */
@Data
public class WeChatResponse implements Serializable {

    private String accessToken;
    private String openid;
}
