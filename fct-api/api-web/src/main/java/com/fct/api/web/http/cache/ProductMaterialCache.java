package com.fct.api.web.http.cache;

import com.fct.api.web.utils.Constants;
import com.fct.mall.data.entity.GoodsMaterial;
import com.fct.mall.interfaces.MallService;
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
 * Created by z on 17-7-11.
 */

@Service
public class ProductMaterialCache {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private MallService mallService;

    public List<Map<String, Object>> findWikiMaterial() {

        List<GoodsMaterial> lsMaterial = this.cacheMaterials();
        List<Map<String, Object>> lsMaps = new ArrayList<>();
        if (lsMaterial != null && lsMaterial.size() > 0) {

            for (GoodsMaterial material : lsMaterial) {

                Map<String, Object> map = new HashMap<>();
                map.put("id", material.getId());
                map.put("name", material.getName());

                lsMaps.add(map);
            }
        }

        return lsMaps;
    }

    public Map<String, Object> getWiki(Integer id) {

        if (id < 1) return  null;

        GoodsMaterial material = this.getCacheMaterial(id);

        if (material == null) return null;

        Map<String, Object> map = new HashMap<>();
        map.put("id", material.getId());
        map.put("name", material.getName());
        map.put("code", "");
        map.put("description", material.getDescription());

        return map;
    }



    public List<GoodsMaterial> cacheMaterials() {

        String key = "goods_materials";
        List<GoodsMaterial> lsMaterial = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if (object != null) {

                return (List<GoodsMaterial>) SerializationUtils.deserialize(object);
            } else {

                PageResponse<GoodsMaterial> pageResponse = mallService.findMaterial(0, "",
                        0, -1, 1, 500);
                lsMaterial = pageResponse.getElements();
                if (lsMaterial != null && lsMaterial.size() > 0) {

                    jedis.set(key.getBytes(), SerializationUtils.serialize(lsMaterial));
                    jedis.expire(key, 86400);
                }

                return lsMaterial;
            }
        } catch (Exception e) {
            Constants.logger.error(e.toString());
        } finally {
            if (jedis != null) jedis.close();
        }

        //如果没有取到
        PageResponse<GoodsMaterial> pageResponse = mallService.findMaterial(0, "",
                0, -1, 1, 500);
        lsMaterial = pageResponse.getElements();
        return lsMaterial;
    }

    private GoodsMaterial getCacheMaterial(Integer id) {

        return mallService.getMaterial(id);
    }
}
