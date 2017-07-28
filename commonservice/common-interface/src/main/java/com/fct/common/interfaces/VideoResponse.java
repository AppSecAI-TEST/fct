package com.fct.common.interfaces;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by jon on 2017/7/28.
 */
@Data
public class VideoResponse implements Serializable {

    private String url;

    private String originalName;

    private Float fileSize;

    private String fileId;

    private String fileType;
}
