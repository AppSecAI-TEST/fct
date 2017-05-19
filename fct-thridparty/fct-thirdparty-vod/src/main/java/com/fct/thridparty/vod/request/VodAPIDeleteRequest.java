package com.fct.thridparty.vod.request;

import lombok.Data;

/**
 * Created by nick on 2017/5/19.
 * 删除视频请求
 */
@Data
public class VodAPIDeleteRequest extends VodAPIRequest {

    private String VideoIds;
}
