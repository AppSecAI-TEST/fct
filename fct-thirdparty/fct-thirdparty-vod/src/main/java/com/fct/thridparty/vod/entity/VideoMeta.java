package com.fct.thridparty.vod.entity;

import lombok.Data;

/**
 * Created by ningyang on 2017/5/22.
 */
@Data
public class VideoMeta {

    //视频ID
    private String VideoId;
    //视频标题
    private String Title;
    //视频时长(秒)
    private int Duration;
    //视频封面
    private String CoverURL;
    /** 视频状态，Uploading(上传中)，
     *  UploadFail(上传失败)，
     *  UploadSucc(上传完成)，
     *  Transcoding(转码中)，
     *  TranscodeFail(转码失败)，
     *  Blocked(屏蔽)，
     *  Normal(正常)
     */
    private String Status;
}
