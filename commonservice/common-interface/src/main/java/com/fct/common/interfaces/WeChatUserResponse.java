package com.fct.common.interfaces;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by z on 17-7-12.
 */
@Data
public class WeChatUserResponse implements Serializable {

    //用户的唯一标识
    private String openid;
    //用户昵称
    private String nickname;
    //用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
    private Integer sex;
    //用户个人资料填写的省份
    private String province;
    //普通用户个人资料填写的城市
    private String city;
    //国家，如中国为CN
    private String contry;
    //用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像）
    // 用户没有头像时该项为空。若用户更换头像，原有头像URL将失效
    private String headimgurl;
    //只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段
    private String unionid;

}
