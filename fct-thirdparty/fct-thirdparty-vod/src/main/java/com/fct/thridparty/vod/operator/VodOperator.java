package com.fct.thridparty.vod.operator;

/**
 * Created by nick on 2017/5/19.
 */
public interface VodOperator {

    void upload();

    void deleteVod();

    void getVod();

    void getVods();

    void updateVod();

    void getPlayUrl();
}
