package com.fct.thridparty.vod.request;

import lombok.Data;

/**
 * Created by nick on 2017/5/18.
 */
@Data
public class VodAPIRequest {

    private String Format;
    private String Signature;
    private String Version;
    private String AccessKeyId;
    private String SignatureMethod;
    private String Timestamp;
    private String SignatureVersion;
    private String SignatureNonce;
}
