package com.fct.source.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by jon on 2017/6/20.
 * Nancy would like to take trip with jiangjiang
 * */

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageSource implements Serializable {

    @Id
    private String guid;

    /**
     * 图片分类
     * */
    private Integer categoryId;

    /**
     * 图片名称
     * */
    private String name;

    /**
     * 原始名称
     * */
    private String originalName;

    /**
     * .jpg,.png
     * */
    private String fileType;

    /**
     * 文件地址
     * */
    private String url;

    /**
     * 图片宽
     * */
    private Integer width;

    /**
     * 图片高
     * */
    private Integer height;

    /**
     * 文件大小
     * */
    private Float fileLength;

    /**
     * 生成时间
     * */
    private Date createTime;

    /**
     * 状态
     * */
    private Integer status;

    private Integer sortIndex;

}
