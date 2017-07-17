package com.fct.api.web.http.cache;

import com.fct.api.web.utils.Constants;
import com.fct.common.data.entity.Article;
import com.fct.common.data.entity.ArticleCategory;
import com.fct.common.interfaces.CommonService;
import com.fct.mall.interfaces.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by z on 17-7-14.
 */
@Service
public class ArticleCache {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private CommonService commonService;

    public PageResponse<Map<String, Object>> findPageArticle(String code, Integer pageIndex, Integer pageSize) {

        com.fct.common.interfaces.PageResponse<Article> pageArticle =
                commonService.findArticle("", code, 1, "", "", pageIndex, pageSize);

        PageResponse<Map<String, Object>> pageMaps = new PageResponse<>();

        if (pageArticle != null && pageArticle.getTotalCount() > 0) {

            Map<String, String> categoryMap = cacheCategoryMap();

            List<Map<String, Object>> lsMaps = new ArrayList<>();
            for (Article article: pageArticle.getElements()) {

                Map<String, Object> map = new HashMap<>();
                map.put("id", article.getId());
                map.put("title", article.getTitle());
                map.put("categoryName", (categoryMap != null ? categoryMap.get(article.getCategoryCode()) : ""));
                map.put("intro", article.getIntro());
                map.put("createTime", article.getCreateTime());
                lsMaps.add(map);
            }
            pageMaps.setElements(lsMaps);
            pageMaps.setCurrent(pageArticle.getCurrent());
            pageMaps.setTotalCount(pageArticle.getTotalCount());
            pageMaps.setHasMore(pageArticle.isHasMore());
        }

        return pageMaps;
    }

    public Map<String, Object> getArticle(Integer id) {

        Article article = commonService.getArticle(id);
        Map<String, Object> map = new HashMap<>();
        if (article != null) {

            Map<String, String> categoryMap = cacheCategoryMap();

            map.put("id", article.getId());
            map.put("title", article.getTitle());
            map.put("categoryName", (categoryMap != null ? categoryMap.get(article.getCategoryCode()) : ""));
            map.put("content", article.getContent());
            map.put("createTime", article.getCreateTime());
        }

        return map;
    }

    private Map<String, String> cacheCategoryMap()
    {
        String key = "article_categories";
        Jedis jedis = null;
        try {

            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if (object != null) {

                return (Map<String, String>) SerializationUtils.deserialize(object);
            } else {

                Map<String, String> categories = new HashMap<>();
                List<ArticleCategory> lsCategory = commonService.findArticleCategory(-1,"", "");
                if (lsCategory != null && lsCategory.size() > 0) {

                    for (ArticleCategory category: lsCategory) {

                        categories.put(category.getCode(), category.getName());
                    }

                    jedis.set(key.getBytes(), SerializationUtils.serialize(categories));
                    jedis.expire(key, 86400);
                }

                return categories;
            }
        } catch (Exception e) {
            Constants.logger.error(e.toString());
        } finally {
            if (jedis != null) jedis.close();
        }

        return null;
    }
}
