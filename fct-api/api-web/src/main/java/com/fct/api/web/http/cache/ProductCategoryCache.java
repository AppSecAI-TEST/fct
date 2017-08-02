package com.fct.api.web.http.cache;

import com.fct.api.web.utils.Constants;
import com.fct.api.web.utils.FctResourceUrl;
import com.fct.mall.data.entity.GoodsCategory;
import com.fct.mall.interfaces.MallService;
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
 * Created by z on 17-7-11.
 */
@Service
public class ProductCategoryCache {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private FctResourceUrl fctResourceUrl;

    @Autowired
    private MallService mallService;

    public List<Map<String, Object>> findWikiCategory() {
        List<GoodsCategory> lsCategory = this.cacheCategories();

        List<Map<String, Object>> lsMaps = new ArrayList<>();
        if (lsCategory != null && lsCategory.size() > 0) {

            for (GoodsCategory cate : lsCategory) {

                if (cate.getParentId() == 0) {

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", cate.getId());
                    map.put("name", cate.getName());
                    map.put("subList", this.findSubCategory(cate.getId(), lsCategory));

                    lsMaps.add(map);
                }
            }
        }

        return lsMaps;
    }


    public List<Map<String, String>> findParentCategory() {

        List<GoodsCategory> lsCategory = this.cacheCategories();

        List<Map<String, String>> lsMaps = new ArrayList<>();
        if (lsCategory != null && lsCategory.size() > 0) {

            for (GoodsCategory cate : lsCategory) {

                if (cate.getParentId() == 0) {

                    Map<String, String> map = new HashMap<>();
                    map.put("name", cate.getName());
                    map.put("code", cate.getCode());

                    lsMaps.add(map);
                }
            }
        }

        return lsMaps;
    }

    public List<Map<String, Object>> findSubCategory(Integer parentId, List<GoodsCategory> lsCategory) {

        if (lsCategory == null) {

            lsCategory = this.cacheCategories();
        }

        List<Map<String, Object>> lsMaps = new ArrayList<>();
        if (lsCategory != null && lsCategory.size() > 0) {

            for (GoodsCategory cate : lsCategory) {

                if (parentId == cate.getParentId() && parentId > 0) {

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", cate.getId());
                    map.put("name", cate.getName());
                    map.put("image", fctResourceUrl.thumbSmall(cate.getImg()));

                    lsMaps.add(map);
                }
            }
        }

        return lsMaps;
    }


    public Map<String, Object> getWiki(Integer id) {

        if (id < 1) return  null;

        GoodsCategory category = this.getCacheCategory(id);

        if (category == null) return  null;

        Map<String, Object> map = new HashMap<>();

        map.put("id", category.getId());
        map.put("name", category.getName());
        map.put("code", category.getCode());
        map.put("description", category.getDescription());

        return map;
    }


    private List<GoodsCategory> cacheCategories() {

        String key = "goods_categories";
        List<GoodsCategory> lsCategory = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if (object != null) {

                return (List<GoodsCategory>) SerializationUtils.deserialize(object);
            } else {

                lsCategory = mallService.findGoodsCategory(-1, "", "");
                if (lsCategory != null && lsCategory.size() > 0) {

                    jedis.set(key.getBytes(), SerializationUtils.serialize(lsCategory));
                    jedis.expire(key, 86400);
                }

                return lsCategory;
            }
        } catch (Exception e) {
            Constants.logger.error(e.toString());
        } finally {
            if (jedis != null) jedis.close();
        }

        return mallService.findGoodsCategory(-1, "", "");
    }

    private GoodsCategory getCacheCategory(Integer id) {

        return mallService.getGoodsCategory(id);
    }
}
