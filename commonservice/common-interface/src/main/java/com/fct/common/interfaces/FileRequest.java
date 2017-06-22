package com.fct.common.interfaces;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by nick on 2017/5/17.
 */
@Data
public class FileRequest {

    private List<byte[]> files;

    private List<String> keys;

    private Map<String, String> userMetaData;
}
