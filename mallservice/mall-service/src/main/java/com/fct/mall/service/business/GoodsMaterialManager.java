package com.fct.mall.service.business;

import com.fct.common.utils.PageUtil;
import com.fct.mall.data.entity.GoodsMaterial;
import com.fct.mall.data.repository.GoodsMaterialRepository;
import com.fct.mall.interfaces.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/22.
 * Im glad we're on this one way street just you and I
   Just you and I
 */
@Service
public class GoodsMaterialManager {

    @Autowired
    private GoodsMaterialRepository goodsMaterialRepository;

    @Autowired
    private JdbcTemplate jt;

    public void save(GoodsMaterial goodsMaterial) {
        goodsMaterial.setUpdateTime(new Date());
        if (goodsMaterial.getId() > 0) {
            goodsMaterialRepository.saveAndFlush(goodsMaterial);
        } else {
            goodsMaterial.setCreateTime(new Date());
            goodsMaterialRepository.save(goodsMaterial);
        }
    }

    public GoodsMaterial findById(Integer id) {
        return goodsMaterialRepository.findOne(id);
    }

    public void updateStatus(Integer id)
    {
        goodsMaterialRepository.updateStatus(id,new Date().toString());
    }

    private String getContion(Integer goodsId, String name, Integer status,List<Object> param)
    {
        String condition = "";
        if(!StringUtils.isEmpty(name))
        {
            condition += " And name like ?";
            param.add("%"+ name +"%");
        }
        if(goodsId>0)
        {
            condition +=" AND GoodsId="+goodsId;
        }
        if(status>-1)
        {
            condition += " AND Status="+status;
        }
        return condition;
    }

    public PageResponse<GoodsMaterial> findAll(Integer goodsId, String name, Integer status, Integer pageIndex, Integer pageSize)
    {

        List<Object> param = new ArrayList<>();

        String table="GoodsMaterial";
        String field ="*";
        String orderBy = "Id Desc";
        String condition= getContion(goodsId,name,status,param);

        String sql = "SELECT Count(0) FROM GoodsMaterial WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<GoodsMaterial> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<GoodsMaterial>(GoodsMaterial.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }

        PageResponse<GoodsMaterial> pageResponse = new PageResponse<>();
        pageResponse.setTotalCount(count);
        pageResponse.setCurrent(end);
        pageResponse.setElements(ls);
        pageResponse.setHasMore(hasmore);

        return pageResponse;

    }
}
