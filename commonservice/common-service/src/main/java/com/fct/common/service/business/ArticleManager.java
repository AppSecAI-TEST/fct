package com.fct.common.service.business;

import com.fct.common.data.entity.Article;
import com.fct.common.data.repository.ArticleRepository;
import com.fct.common.interfaces.PageResponse;
import com.fct.core.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ArticleManager {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private JdbcTemplate jt;

    private String getCondition(String title, String categoryCode, Integer status, String startTime,
                              String endTime,List<Object> param)
    {
        String condition = "";
        if(!StringUtils.isEmpty(title))
        {
            condition += " AND title like ?";
            param.add("%"+ title +"%");
        }
        if(!StringUtils.isEmpty(categoryCode))
        {
            condition += " AND categoryCode like ?";
            param.add(categoryCode +"%");
        }
        if(status>-1)
        {
            condition +=" AND status="+status;
        }
        if(!StringUtils.isEmpty(startTime))
        {
            condition += " AND startTime >=?";
            param.add(startTime);
        }
        if(!StringUtils.isEmpty(endTime))
        {
            condition += " AND endTime <?";
            param.add(endTime);
        }
        return condition;
    }

    public PageResponse<Article> findAll(String title, String categoryCode, Integer status, String startTime,
                                      String endTime, Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="Article";
        String field ="*";
        String orderBy = "sortIndex asc";
        String condition= getCondition(title,categoryCode,status,startTime,endTime,param);

        String sql = "SELECT Count(0) FROM Article WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<Article> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<>(Article.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }
        PageResponse<Article> p = new PageResponse<>();
        p.setTotalCount(count);
        p.setCurrent(end);
        p.setElements(ls);
        p.setHasMore(hasmore);

        return p;
    }

    public Article findById(Integer id)
    {
        return articleRepository.findOne(id);
    }

    public Article save(Article article)
    {
        if(article.getId() == null || article.getId()<=0)
        {
            article.setCreateTime(new Date());
            article.setViewCount(100);
        }
        article.setUpdateTime(new Date());
        articleRepository.save(article);

        return article;
    }

    public void updateStatus(Integer id)
    {
        Article article = articleRepository.findOne(id);
        article.setStatus(1-article.getStatus());
        article.setUpdateTime(new Date());
        articleRepository.save(article);

    }

    public Integer countByCategory(Integer categoryId)
    {

        return articleRepository.countByCategory(categoryId +",%");
    }

    public void updateCategory(String newCode,Integer cagetoryId)
    {
        String sql = "update Article set categoryCode='"+ newCode +"' where categoryCode like ',"+ cagetoryId +",%'";

        jt.update(sql);
    }
}
