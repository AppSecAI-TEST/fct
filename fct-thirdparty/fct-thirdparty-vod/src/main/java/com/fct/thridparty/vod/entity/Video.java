package com.fct.thridparty.vod.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Created by nick on 2017/5/19.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Video {

    /**
     * "Video": {
        "VideoId": "93ab850b4f6f44eab54b6e91d24d81d4",
         "Title": "阿里云VOD视频标题",
         "Description": "阿里云VOD视频描述",
         "Duration": 135.6,
         "CoverURL": "https://image.example.com/coversample.jpg",
         "Status": "Normal",
         "CreateTime": "2017-03-10 12:45:56",
         "ModifyTime": "2017-03-20 10:25:06",
         "Size": 10897890,
         "Snapshots": [{"https://image.example.com/snapshotsample1.jpg"}, {"https://image.example.com/snapshotsample2.jpg"}],
         "CateId": 78,
         "CateName": "分类名",
         "Tags": ["标签1", "标签2"]
     }
     */
    private String VideoId;
    private String Title;
    private String Description;
    private String Duration;
    private String CoverURL;
    private String Status;
    private String CreateTime;
    private String ModifyTime;
    private String Size;
    private String[] Snapshots;
    private String CateId;
    private String CateName;
    private String[] Tags;

}
