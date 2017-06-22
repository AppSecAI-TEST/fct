package com.fct.thridparty.vod.response;

import com.fct.thridparty.vod.entity.Video;
import lombok.Data;

import java.util.List;

/**
 * Created by ningyang on 2017/5/22.
 */
@Data
public class VodInfosResponse extends VodResponse {

    List<Video> VideoList;
}
