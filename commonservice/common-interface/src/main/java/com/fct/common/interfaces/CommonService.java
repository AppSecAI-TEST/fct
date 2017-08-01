package com.fct.common.interfaces;

import com.fct.common.data.entity.*;

import java.util.List;

public interface CommonService {

    PageResponse<Article> findArticle(String title, String categoryCode, Integer status, String startTime,
                                      String endTime, Integer pageIndex, Integer pageSize);

    Article getArticle(Integer id);

    Article saveArticle(Article article);

    void updateArticleStatus(Integer id);

    List<ArticleCategory> findArticleCategory(Integer parentId, String name, String code);

    ArticleCategory getArticleCategory(Integer id);

    void saveArticleCategory (ArticleCategory category);

    void deleteArticleCategory(Integer id);

    PageResponse<ImageSource> findImageSource(String name, Integer categoryId, Integer status, String fileType,
                                              String startTime, String endTime, Integer pageIndex, Integer pageSize);
    ImageSource getImageSource(String guid);

    String saveImageSource(ImageSource imageSource);

    List<ImageResponse> uploadImage(FileRequest fileRequest);

    List<ImageSource> findImageSourceByGuid(String guids);

    void updateImageSourceStatus(String guid);

    List<ImageCategory> findImageCategory();

    ImageCategory getImageCategory(Integer id);

    void saveImageCategory (ImageCategory category);

    void deleteImageCategory(Integer id);

    PageResponse<VideoSource> findVideoSource(String name, Integer categoryId, Integer status, String fileType,
                                              String startTime, String endTime, Integer pageIndex, Integer pageSize);
    VideoSource getVideoSource(String id);

    VideoResponse uploadVideo(byte[] fileByte,String originalName,Float fileSize);

    void saveVideoSource(VideoSource videoSource);

    List<VideoSource> findVideoSourceByGuid(String ids);

    void updateVideoSourceStatus(String id);

    List<VideoCategory> findVideoCategory();

    VideoCategory getVideoCategory(Integer id);

    void saveVideoCategory (VideoCategory category);

    void deleteVideoCategory(Integer id);

    String oAuthURL(String redirectURI, String scope);

    WeChatResponse wechatCallback(String code);

    WeChatUserResponse getUserInfo(String openid);

    WeChatShareResponse jsShare(String url, Boolean debug);

}
