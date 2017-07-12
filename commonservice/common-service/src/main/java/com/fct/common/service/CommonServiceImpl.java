package com.fct.common.service;

import com.fct.common.data.entity.*;
import com.fct.common.interfaces.*;
import com.fct.common.service.business.*;
import com.fct.common.service.oauth.WeChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("commonService")
public class CommonServiceImpl implements CommonService {

    @Autowired
    private ArticleManager articleManager;

    @Autowired
    private ArticleCategoryManager articleCategoryManager;

    @Autowired
    private ImageSourceManager imageSourceManager;

    @Autowired
    private ImageCategoryManager imageCategoryManager;

    @Autowired
    private VideoCategoryManager videoCategoryManager;

    @Autowired
    private WeChat weChat;

    public PageResponse<Article> findArticle(String title, String categoryCode, Integer status, String startTime,
                                      String endTime, Integer pageIndex, Integer pageSize)
    {
        try {
            return articleManager.findAll(title, categoryCode, status, startTime, endTime, pageIndex, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public Article getArticle(Integer id)
    {

        try {
            return articleManager.findById(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public Article saveArticle(Article article)
    {
        try {
            return articleManager.save(article);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void updateArticleStatus(Integer id)
    {
        try {
            articleManager.updateStatus(id);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public List<ArticleCategory> findArticleCategory(Integer parentId, String name, String code)
    {
        try {
            return  articleCategoryManager.findAll(name,code,parentId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public ArticleCategory getArticleCategory(Integer id)
    {
        try {
            return articleCategoryManager.findById(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void saveArticleCategory (ArticleCategory category)
    {
        try {
            articleCategoryManager.save(category);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void deleteArticleCategory(Integer id)
    {
        try {

            articleCategoryManager.delete(id);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public PageResponse<ImageSource> findImageSource(String name, Integer categoryId, Integer status, String fileType,
                                              String startTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        try {
            return imageSourceManager.findAll(name,categoryId,status,fileType,startTime,endTime,pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public ImageSource getImageSource(String guid)
    {
        try {
            return imageSourceManager.findById(guid);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public String saveImageSource(ImageSource imageSource)
    {
        try {

            return imageSourceManager.save(imageSource);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return "";
    }

    public List<ImageResponse> uploadImage(FileRequest fileRequest)
    {
        try {

            return imageSourceManager.upload(fileRequest);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public List<ImageSource> findImageSourceByGuid(String guids)
    {

        try {
            return imageSourceManager.findByGuid(guids);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void updateImageSourceStatus(String guid)
    {
        try {

            imageSourceManager.updateStatus(guid);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public List<ImageCategory> findImageCategory()
    {
        try {
            return imageCategoryManager.findAll("");
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public ImageCategory getImageCategory(Integer id)
    {
        try {
            return imageCategoryManager.findById(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void saveImageCategory (ImageCategory category)
    {
        try {

            imageCategoryManager.save(category);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void deleteImageCategory(Integer id)
    {
        try {

            imageCategoryManager.delete(id);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public PageResponse<VideoSource> findVideoSource(String name, Integer categoryId, Integer status, Integer fileType,
                                              String startTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        return null;
    }
    public VideoSource getVideoSource(String id)
    {
        return null;
    }

    public void saveVideoSource(VideoSource videoSource)
    {}

    public List<VideoSource> findVideoSourceByGuid(String ids)
    {
        return null;
    }

    public void updateVideoSourceStatus(String id)
    {

    }

    public List<VideoCategory> findVideoCategory()
    {
        try {
            return videoCategoryManager.findAll("");
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public VideoCategory getVideoCategory(Integer id)
    {
        try {
            return videoCategoryManager.findById(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void saveVideoCategory (VideoCategory category)
    {
        try {

            videoCategoryManager.save(category);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void deleteVideoCategory(Integer id)
    {
        try {

            videoCategoryManager.delete(id);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public String oauthUrl(String redirectURI, String scope) {

        return weChat.oauthUrl(redirectURI, scope);
    }

    public String wechatCallback(String code) {

        return this.wechatCallback(code);
    }

    public WeChatResponse getUserInfo(String openid) {

        return weChat.getUserInfo(openid);
    }

    public WeChatShareResponse jsShare(String url) {

        return weChat.jsShare(url);
    }
}
