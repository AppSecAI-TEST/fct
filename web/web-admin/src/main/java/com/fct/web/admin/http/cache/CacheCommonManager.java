package com.fct.web.admin.http.cache;

import com.fct.artist.data.entity.Artist;
import com.fct.common.data.entity.ArticleCategory;
import com.fct.common.data.entity.ImageCategory;
import com.fct.common.data.entity.VideoCategory;
import com.fct.common.interfaces.CommonService;
import com.fct.web.admin.utils.Constants;
import org.apache.zookeeper.server.util.SerializeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CacheCommonManager {

    @Autowired
    private CommonService commonService;

    @Autowired
    private JedisPool jedisPool;

    private int expireSeconds = 60 * 40;        //30分钟

    public List<ArticleCategory> findCacheArticleCategory()
    {
        String key = "cache_articlecate_all";
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if(object != null)
            {
                return (List<ArticleCategory>) SerializationUtils.deserialize(object);
            }
            else
            {
                List<ArticleCategory> ls = findArticleCategory();
                if (ls != null && ls.size()>0) {
                    jedis.set(key.getBytes(),SerializationUtils.serialize(ls));
                    jedis.expire(key,expireSeconds);
                }
                return ls;
            }

        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        finally {
            if(jedis != null)
            {
                jedis.close();
            }
        }
        return findArticleCategory();
    }
    private List<ArticleCategory> findArticleCategory() {
        try {

            List<ArticleCategory> lsCategory = commonService.findArticleCategory(-1, "", "");
            if (lsCategory != null && lsCategory.size() > 0) {
                return lsCategory;
            }
        } catch (Exception exp) {
            Constants.logger.error(exp.toString());
        }
        return new ArrayList();
    }

    public List<ArticleCategory> findArticleCategoryByParent()
    {
        List<ArticleCategory> lsCate = new ArrayList<>();

        for (ArticleCategory cate:findCacheArticleCategory()
                ) {
            if(cate.getParentId() ==0)
                lsCate.add(cate);
        }
        return lsCate;
    }

    public List<ArticleCategory> findArticleCategoryByParentId(Integer parentId)
    {
        List<ArticleCategory> lsCate = new ArrayList<>();

        for (ArticleCategory cate:findCacheArticleCategory()
                ) {
            if(cate.getParentId().equals(parentId))
                lsCate.add(cate);
        }
        return lsCate;
    }

    public String getArticleCateName(String ids)
    {
        List<ArticleCategory> cateList = findCacheArticleCategory();
        String name = "";
        for (ArticleCategory cate: cateList
                ) {
            if(ids.contains(cate.getCode()))
            {
                if(!StringUtils.isEmpty(name))
                {
                    name +="-";
                }
                name += cate.getName();
            }
        }
        return name;
    }

    public List<ImageCategory> findCacheImageCategory()
    {
        String key = "cache_imagecategory_all";
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if(object != null)
            {
                return (List<ImageCategory>) SerializationUtils.deserialize(object);
            }
            else
            {
                List<ImageCategory> ls = findImageCategory();
                if (ls != null && ls.size()>0) {
                    jedis.set(key.getBytes(),SerializationUtils.serialize(ls));
                    jedis.expire(key,expireSeconds);
                }
                return ls;
            }

        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        finally {
            if(jedis != null)
            {
                jedis.close();
            }
        }
        return findImageCategory();
    }

    public List<ImageCategory> findImageCategory()
    {
        try {
            List<ImageCategory> list = commonService.findImageCategory();
            if (list == null && list.size() <= 0) {
                list = new ArrayList<>();
            }
            return list;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return new ArrayList<>();
    }

    public String getImageCategoryName(Integer id)
    {
        List<ImageCategory> list = findCacheImageCategory();
        for (ImageCategory cate: list
                ) {
            if(id == cate.getId())
            {
                return cate.getName();
            }
        }
        return "";
    }

    public List<VideoCategory> findCacheVideoCategory()
    {
        String key = "cache_videocategory_all";
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if(object != null)
            {
                return (List<VideoCategory>) SerializationUtils.deserialize(object);
            }
            else
            {
                List<VideoCategory> ls = findVideoCategory();
                if (ls != null && ls.size()>0) {
                    jedis.set(key.getBytes(),SerializationUtils.serialize(ls));
                    jedis.expire(key,expireSeconds);
                }
                return ls;
            }

        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        finally {
            if(jedis != null)
            {
                jedis.close();
            }
        }
        return findVideoCategory();
    }

    public List<VideoCategory> findVideoCategory()
    {
        try {
            List<VideoCategory> list = commonService.findVideoCategory();
            if (list == null && list.size() <= 0) {
                list = new ArrayList<>();
            }
            return list;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return new ArrayList<>();
    }

    public String getVideoCategoryName(Integer id)
    {
        List<VideoCategory> list = findCacheVideoCategory();
        for (VideoCategory cate: list
                ) {
            if(id == cate.getId())
            {
                return cate.getName();
            }
        }
        return "";
    }
}
