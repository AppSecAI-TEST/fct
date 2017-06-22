package com.fct.thridparty.vod.request;

import lombok.Data;

/**
 * Created by ningyang on 2017/5/22.
 * 获取视频列表请求
 */
@Data
public class VodAPIGetVideosRequest extends VodAPIRequest {

    /**
     * 视频状态，包括：
     * Uploading(上传中)，
     * UploadFail(上传失败)，
     * UploadSucc(上传完成)，
     * Transcoding(转码中)，
     * TranscodeFail(转码失败)，
     * Blocked(屏蔽)，
     * Normal(正常)
     */
    private String Status;

    //视频分类ID
    private int CateId;

    //页号，默认1
    private int PageNo;

    //可选，默认10，最大不超过100
    private int PageSize;

    //结果排序，范围：CreateTime:Desc、CreateTime:Asc，默认为CreateTime:Desc
    private String SortBy;
}
