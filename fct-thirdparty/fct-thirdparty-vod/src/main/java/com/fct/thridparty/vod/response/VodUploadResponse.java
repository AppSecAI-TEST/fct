package com.fct.thridparty.vod.response;

import com.aliyun.vod.upload.resp.UploadVideoResponse;
import lombok.Data;

/**
 * Created by ningyang on 2017/5/23.
 */
@Data
public class VodUploadResponse extends VodResponse {

    private String videoId;

    public VodUploadResponse(UploadVideoResponse response){
        if(response!=null){
            videoId = response.getVideoId();
            setCode(response.getCode());
            setMessage(response.getMessage());
        }
    }
}
