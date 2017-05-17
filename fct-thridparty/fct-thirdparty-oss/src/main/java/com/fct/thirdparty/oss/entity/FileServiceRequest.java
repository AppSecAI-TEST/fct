package com.fct.thirdparty.oss.entity;

import lombok.Data;

import java.io.File;
import java.util.Map;

/**
 * Created by nick on 2017/5/17.
 */
@Data
public class FileServiceRequest {

    private File file;

    private String key;

    private Map<String, String> userMetaData;
}
