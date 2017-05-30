package com.fct.master.interfaces;

import com.fct.master.dto.*;

import java.io.File;
import java.util.List;


/**
 * Created by nick on 2017/5/24.
 */
public interface MasterService {

    /**
     * 给管理后台用 添加大师
     * @param masterDto
     * @return
     */
    MasterDto insertMaster(MasterDto masterDto);

    /**
     * 获取大师列表给api用
     * 支持分页功能
     * @param start
     * @param size
     * @return
     */
    MasterResponse getMasters(int start, int size);

    /**
     * 给管理后台用
     * 给大师提供直播功能
     */
    void openMasterLive();

    /**
     * 获取大师简介
     * @param masterId
     * @return
     */
    MasterBrief getMasterBrief(int masterId);

    /**
     * 在大师系统中获取公共数据
     * 大师简介和封面
     * 大师是否有直播
     * @param masterId
     * @return
     */
    MasterSerialsDto getMasterCommonData(int masterId);

    /**
     * 獲取大師動態 並且分頁
     * @param masterId
     * @param start
     * @param end
     * @return
     */
    List<MasterNewsDto> getMasterNews(int masterId, int start, int end);


    /**
     * 獲取動態列表 並且告知api 是否有下一頁
     * 這個方法會調用 @Link{getMasterNews}
     * @param masterId
     * @param start
     * @param size
     * @return
     */
    MasterNewsResponse getMasterNewsResponse(int masterId, int start, int size);

    /**
     * 獲取大師的直播信息
     * @param masterId
     * @return
     */
    MasterLiveDto getMasterLive(int masterId);

    /**
     * 上傳大師封面圖片
     * @param masterId
     * @param file
     */
    String uploadMasterSingleImg(long masterId, File file);

    /**
     * 上傳大師動態圖片 圖片個數不能超過9張
     * @param masterId
     * @param files
     */
    List<String> uploadMasterMultiImg(long masterId, File[] files);

    /**
     * 上傳大師視頻
     * @param masterId
     * @param request
     */
    void uploadMasterVod(long masterId, UploadVodRequest request);

    /**
     * 删除上传的图片
     * @param fileNames
     */
    void deleteImgFiles(List<String> fileNames);

}
