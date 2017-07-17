package com.fct.common.interfaces;

import lombok.Data;

import java.io.Serializable;

@Data
public class ImageResponse implements Serializable {

    private String name;

    private String url;

    private String fullUrl;

    private String guid;
}
