package com.fct.thridparty.vod.request;

import lombok.Data;

/**
 * Created by nick on 2017/5/19.
 */
@Data
public class VodAPIUpdateRequest extends VodAPIRequest{

    private String VideoId;
    private String Title;
    private String Description;
    private String CoverURL;
    private String CateId;
    private String[] Tags;
}
