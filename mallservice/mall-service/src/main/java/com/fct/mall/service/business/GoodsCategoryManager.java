package com.fct.mall.service.business;

import com.fct.mall.data.entity.GoodsCategory;
import com.fct.mall.data.repository.GoodsCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;

import java.util.List;

/**
 * Created by jon on 2017/5/18.
 */
@Service
public class GoodsCategoryManager {

    @Autowired
    private GoodsCategoryRepository goodsCategoryRepository;

    @Autowired
    private GoodsManager goodsManager;

    @Autowired
    private JdbcTemplate jt;

    //添加商品分类
    @Transactional
    public void save (GoodsCategory category)
    {
        if (StringUtils.isEmpty (category.getName())) {
            throw new IllegalArgumentException ("商品分类名称不能为空");
        }
        if(StringUtils.isEmpty(category.getImg()))
        {
            throw new IllegalArgumentException("图片为空");
        }
        if(category.getSortIndex() == null)
        {
            throw new IllegalArgumentException ("排序值为空");
        }

        String code = "";
        if (category.getParentId() > 0) {
            GoodsCategory parentCategory =  goodsCategoryRepository.findOne(category.getParentId());
            if (parentCategory == null) {
                throw new IllegalArgumentException("父商品分类不存在");
            }

            //设置分类code
            code = parentCategory.getCode();
        }
        else
        {
            code =",";
        }

        //名字不能相同
        int count = 0;
        Boolean newadd = true;
        if(category.getId() == null)
        {
            count = goodsCategoryRepository.exitSameName(category.getName(),0,0);
        }
        else {
            newadd =false;
            code = code + category.getId() +",";
            if(category.getCode() != code)  //父类有变化
            {
                goodsManager.updateCategory(category.getCode(), category.getId());
            }
            category.setCode(code);
            count = goodsCategoryRepository.exitSameName(category.getName(), category.getParentId(), category.getId());
        }
        if (count > 0) {
            throw new IllegalArgumentException("同级商品分类名称不能重复");
        }

        goodsCategoryRepository.save(category);
        if(newadd)
        {
            code = code + category.getId() +",";
            String sql = String.format("update GoodsCategory set code='%s' where id=%d",
                    code, category.getId());
            jt.update(sql);
        }

    }

    //获取分类列表
    public List<GoodsCategory> findAll(String name, String categoryCode,Integer parentId) {

        StringBuilder sb = new StringBuilder();

        List<Object> param = new ArrayList<>();

        if (!StringUtils.isEmpty(name)) {
            sb.append("  AND name like ?");
            param.add("%"+name+"%");
        }
        if (!StringUtils.isEmpty(categoryCode))
        {
            sb.append("  AND code like ?");
            param.add(categoryCode+"%");
        }
        if (parentId>-1)
        {
            sb.append("  AND parentId="+parentId);
        }
        String sql = String.format("select * from GoodsCategory where 1=1 %s order by sortindex asc",sb.toString());

        return  jt.query(sql,param.toArray(),new BeanPropertyRowMapper<GoodsCategory>(GoodsCategory.class));
    }

    public GoodsCategory findById (Integer id)
    {
        if (id < 1) {
            throw new IllegalArgumentException ("ID不存在");
        }
        return goodsCategoryRepository.findOne(id);
    }

    public void saveSortIndex (Integer id, Integer sortIndex)
    {
        if (id < 1) {
            throw new IllegalArgumentException ("ID不存在");
        }
        if(sortIndex==null)
        {
            throw new IllegalArgumentException ("排序值为空");
        }
        goodsCategoryRepository.updateSortIndex(id,sortIndex);

    }

    public void delete(int id)
    {
        if (id < 1)
        {
            throw new IllegalArgumentException("ID不存在");
        }
        //校验产品中是否有使用分类，如有则不可删除
        int exitCount = goodsManager.countByCategory(id);

        if (exitCount > 0)
        {
            throw new IllegalArgumentException("分类下面有宝贝，不可删除。");
        }

        goodsCategoryRepository.delete(id);
    }
}
