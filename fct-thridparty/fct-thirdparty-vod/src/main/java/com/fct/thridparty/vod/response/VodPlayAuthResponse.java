package com.fct.thridparty.vod.response;

import com.fct.thridparty.vod.entity.VideoMeta;
import lombok.Data;

/**
 * Created by ningyang on 2017/5/22.
 */
@Data
public class VodPlayAuthResponse extends VodResponse {

    private String PlayAuth;
    private VideoMeta VideoMeta;

}
