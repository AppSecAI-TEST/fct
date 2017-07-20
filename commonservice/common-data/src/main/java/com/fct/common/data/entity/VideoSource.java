package com.fct.common.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class VideoSource implements Serializable {

    @Id
    private String guid;

    /**
     * 图片分类
     * */
    private Integer categoryId;

    /**
     * 视频名称
     * */
    private String name;

    /**
     * 视频图片
     * */
    private String img;

    /**
     * 原始名称
     * */
    private String originalName;

    /**
     * 视频格式:.mp4,.vod
     * */
    private String fileType;

    /**
     * 文件地址
     * */
    private String url;

    /**
     * 文件大小
     * */
    private Integer fileSize;

    /**
     * 视频时长
     * */
    private Integer timing;

    /**
     * 生成时间
     * */
    private Date createTime;

    /**
     * 排序
     * */
    private Integer sortIndex;

    /**
     * 状态
     * */
    private Integer status;
}
