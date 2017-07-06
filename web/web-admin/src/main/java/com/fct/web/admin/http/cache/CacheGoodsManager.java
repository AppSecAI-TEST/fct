package com.fct.web.admin.http.cache;

import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsCategory;
import com.fct.mall.data.entity.GoodsGrade;
import com.fct.mall.data.entity.GoodsMaterial;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.PageResponse;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jon on 2017/6/8.
 */
@Service
public class CacheGoodsManager {

    @Autowired
    private MallService mallService;

    @Autowired
    private JedisPool jedisPool;

    private int expireSecond = 60 * 30;

    private List<GoodsCategory> findGoodsCategory()
    {
        String key = "cache_goods_cate";
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if(object != null)
            {
                return (List<GoodsCategory>) SerializationUtils.deserialize(object);
            }
            else
            {
                List<GoodsCategory> lsCategory = mallService.findGoodsCategory(-1, "", "");
                if (lsCategory != null && lsCategory.size() > 0) {
                    jedis.set(key.getBytes(),SerializationUtils.serialize(lsCategory));
                    jedis.expire(key,expireSecond);
                }
                return lsCategory;
            }

        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        finally {
            jedis.close();
        }
        return new ArrayList<>();
    }

    public List<GoodsCategory> findGoodsCategoryByParent()
    {
        List<GoodsCategory> lsCate = new ArrayList<>();

        for (GoodsCategory cate:findGoodsCategory()
                ) {
            if(cate.getParentId() ==0)
                lsCate.add(cate);
        }
        return lsCate;
    }

    public List<GoodsCategory> findGoodsCategoryByParentId(Integer parentId)
    {
        List<GoodsCategory> lsCate = new ArrayList<>();

        for (GoodsCategory cate:findGoodsCategory()
                ) {
            if(cate.getParentId() == parentId)
                lsCate.add(cate);
        }
        return lsCate;
    }

    public List<GoodsGrade> findGoodsGrade()
    {
        try {
            List<GoodsGrade> gradeList = mallService.findGoodsGrade();
            if (gradeList == null && gradeList.size() <= 0) {
                gradeList = new ArrayList<>();
            }
            return gradeList;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return new ArrayList<>();
    }

    public String getGoodsCateName(String ids)
    {
        List<GoodsCategory> cateList = findGoodsCategory();
        String name = "";
        for (GoodsCategory cate: cateList
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

    public String getGoodsGradeName(Integer id)
    {
        List<GoodsGrade> gradeList = findGoodsGrade();
        for (GoodsGrade grade: gradeList
             ) {
            if(id == grade.getId())
            {
                return grade.getName();
            }
        }
        return "";
    }

    public List<GoodsMaterial> findGoodsMaterial()
    {
        try {
            PageResponse<GoodsMaterial> pageResponse = mallService.findMaterial(0, "", 0, -1, 1, 500);
            if (pageResponse.getElements() != null)
                return pageResponse.getElements();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return new ArrayList<>();

    }

    public String getMaterialName(String materialid)
    {
        if(StringUtils.isEmpty(materialid))
            return "";

        List<GoodsMaterial> list = findGoodsMaterial();
        String name = "";
        for (GoodsMaterial m: list
                ) {
            if(materialid.contains(m.getId().toString()))
            {
                if(!StringUtils.isEmpty(name))
                {
                    name +="、";
                }
                name += m.getName();
            }
        }
        return name;
    }

    public Goods getGoods(Integer id)
    {
        try {
            return mallService.getGoods(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return new Goods();
    }

    public List<Goods> findGoodsByIds(String ids)
    {
        try {
            return mallService.findGoodsByIds(ids);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return new ArrayList<>();
    }
}
