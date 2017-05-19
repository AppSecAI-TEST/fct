package com.fct.thridparty.vod.response;

import com.fct.thridparty.vod.entity.Video;
import lombok.Data;

/**
 * Created by nick on 2017/5/19.
 */
@Data
public class VodInfoResponse extends VodResponse{

    private Video video;
}
