package com.fct.thridparty.vod.request;

import lombok.Data;

/**
 * Created by nick on 2017/5/19.
 * 获取视频信息请求
 */
@Data
public class VodAPIGetInfoRequest extends VodAPIRequest {

    private String VideoId;
}
