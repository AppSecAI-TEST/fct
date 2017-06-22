package com.fct.thirdparty.oss.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by nick on 2017/5/17.
 */
@Data
public class DeleteResponse extends Response {

    private Map<String, String> userMetaData;

    private List<String> keys;
}
