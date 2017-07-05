package com.fct.common.service.business;

import com.fct.common.data.entity.ArticleCategory;
import com.fct.common.data.repository.ArticleCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleCategoryManager {

    @Autowired
    private ArticleCategoryRepository articleCategoryRepository;

    @Autowired
    private ArticleManager articleManager;

    @Autowired
    private JdbcTemplate jt;

    //添加商品分类
    @Transactional
    public void save (ArticleCategory category)
    {
        if (StringUtils.isEmpty (category.getName())) {
            throw new IllegalArgumentException ("分类名称不能为空");
        }

        String code = "";
        if (category.getParentId() > 0) {
            ArticleCategory parentCategory =  articleCategoryRepository.findOne(category.getParentId());
            if (parentCategory == null) {
                throw new IllegalArgumentException("父分类不存在");
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
            count = articleCategoryRepository.exitSameName(category.getName(),0,0);
        }
        else {
            newadd =false;
            code = code + category.getId() +",";
            if(category.getCode() != code)  //父类有变化
            {
                articleManager.updateCategory(category.getCode(), category.getId());
            }
            category.setCode(code);
            count = articleCategoryRepository.exitSameName(category.getName(), category.getParentId(), category.getId());
        }
        if (count > 0) {
            throw new IllegalArgumentException("同级分类名称不能重复");
        }

        articleCategoryRepository.save(category);
        if(newadd)
        {
            code = code + category.getId() +",";
            String sql = String.format("update ArticleCategory set code='%s' where id=%d",
                    code, category.getId());
            jt.update(sql);
        }

    }

    //获取分类列表
    public List<ArticleCategory> findAll(String name, String categoryCode, Integer parentId) {

        StringBuilder sb = new StringBuilder();

        List<Object> param = new ArrayList<>();

        if (!StringUtils.isEmpty(name)) {
            sb.append(" AND name like ?");
            param.add("%"+name+"%");
        }
        if (!StringUtils.isEmpty(categoryCode))
        {
            sb.append(" AND code like ?");
            param.add(categoryCode+"%");
        }
        if (parentId>-1)
        {
            sb.append(" AND parentId="+parentId);
        }
        String sql = String.format("select * from ArticleCategory where 1=1 %s order by sortindex asc",sb.toString());

        return  jt.query(sql,param.toArray(),new BeanPropertyRowMapper<ArticleCategory>(ArticleCategory.class));
    }

    public ArticleCategory findById (Integer id)
    {
        if (id < 1) {
            throw new IllegalArgumentException ("ID不存在");
        }
        return articleCategoryRepository.findOne(id);
    }

    public void saveSortIndex (Integer id, Integer sortIndex)
    {
        if (id < 1) {
            throw new IllegalArgumentException ("ID不存在");
        }
        articleCategoryRepository.updateSortIndex(id,sortIndex);

    }

    public void delete(int id)
    {
        if (id < 1)
        {
            throw new IllegalArgumentException("ID不存在");
        }
        //校验产品中是否有使用分类，如有则不可删除
        int exitCount = articleManager.countByCategory(id);

        if (exitCount > 0)
        {
            throw new IllegalArgumentException("分类下面有新闻，不可删除。");
        }

        articleCategoryRepository.delete(id);
    }

}
