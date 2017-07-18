package com.fct.common.interfaces;

import com.fct.common.data.entity.ImageSource;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by nick on 2017/5/17.
 */
@Data
public class FileRequest implements Serializable {

    private List<byte[]> files;

    private List<ImageSource> images;

    private Map<String, String> userMetaData;

    private String fileFolder;
}
