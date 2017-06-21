package com.fct.common.interfaces;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageResponse implements Serializable{

    /**
     * 图片地址
     * */
    private String url;

    /**
    * 图片名称
     **/
    private String name;

    /**
     * 图片唯一guid
     * */
    private String guid;
}
