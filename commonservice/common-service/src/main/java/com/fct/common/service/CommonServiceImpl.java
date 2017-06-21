package com.fct.common.service;

import com.fct.common.data.entity.*;
import com.fct.common.interfaces.CommonService;
import com.fct.common.interfaces.PageResponse;
import com.fct.common.service.business.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("commonService")
public class CommonServiceImpl implements CommonService{

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

    public PageResponse<Article> findArticle(String title, String categoryCode, Integer status, String startTime,
                                      String endTime, Integer pageIndex, Integer pageSize)
    {
        return articleManager.findAll(title,categoryCode,status,startTime,endTime,pageIndex,pageSize);
    }

    public Article getArticle(Integer id)
    {
        return articleManager.findById(id);
    }

    public Article saveArticle(Article article)
    {
        return articleManager.save(article);
    }

    public void updateArticleStatus(Integer id)
    {
        articleManager.updateStatus(id);
    }

    public List<ArticleCategory> findArticleCategory(Integer parentId, String name, String code)
    {

        return  articleCategoryManager.findAll(name,code,parentId);
    }

    public ArticleCategory getArticleCategory(Integer id)
    {

        return articleCategoryManager.findById(id);
    }

    public void saveArticleCategory (ArticleCategory category)
    {
        articleCategoryManager.save(category);
    }

    public void deleteArticleCategory(Integer id)
    {
        articleCategoryManager.delete(id);
    }

    public PageResponse<ImageSource> findImageSource(String name, Integer categoryId, Integer status, String fileType,
                                              String startTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        return imageSourceManager.findAll(name,categoryId,status,fileType,startTime,endTime,pageIndex,pageSize);
    }

    public ImageSource getImageSource(String guid)
    {

        return imageSourceManager.findById(guid);
    }

    public String saveImageSource(ImageSource imageSource)
    {
        return imageSourceManager.save(imageSource);
    }

    public List<ImageSource> findImageSourceByGuid(String guids)
    {
        return imageSourceManager.findByGuid(guids);
    }

    public void updateImageSourceStatus(String guid)
    {
        imageSourceManager.updateStatus(guid);
    }

    public List<ImageCategory> findImageCategory()
    {
        return imageCategoryManager.findAll("");
    }

    public ImageCategory getImageCategory(Integer id)
    {
        return imageCategoryManager.findById(id);
    }

    public void saveImageCategory (ImageCategory category)
    {
        imageCategoryManager.save(category);
    }

    public void deleteImageCategory(Integer id)
    {
        imageCategoryManager.delete(id);
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
        return videoCategoryManager.findAll("");
    }

    public VideoCategory getVideoCategory(Integer id)
    {
        return videoCategoryManager.findById(id);
    }

    public void saveVideoCategory (VideoCategory category)
    {
        videoCategoryManager.save(category);
    }

    public void deleteVideoCategory(Integer id)
    {
        videoCategoryManager.delete(id);
    }
}
