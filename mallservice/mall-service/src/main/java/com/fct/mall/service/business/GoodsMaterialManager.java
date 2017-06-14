package com.fct.mall.service.business;

import com.fct.common.utils.DateUtils;
import com.fct.common.utils.PageUtil;
import com.fct.mall.data.entity.Goods;
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
 */
@Service
public class GoodsMaterialManager {

    @Autowired
    private GoodsMaterialRepository goodsMaterialRepository;

    @Autowired
    private GoodsManager goodsManager;

    @Autowired
    private JdbcTemplate jt;

    public void save(GoodsMaterial goodsMaterial) {

        if (goodsMaterial.getId() ==null ||
                goodsMaterial.getId() == 0) {//你 这个地方有问题
            goodsMaterial.setStatus(1);
            goodsMaterial.setCreateTime(new Date());
        }
        // shi zhe ge
        goodsMaterial.setUpdateTime(new Date());
        goodsMaterialRepository.save(goodsMaterial);
    }

    public GoodsMaterial findById(Integer id) {
        return goodsMaterialRepository.findOne(id);
    }

    public void updateStatus(Integer id)
    {
        String sql = "UPDATE GoodsMaterial set Status=1-Status,updatetime=? WHERE Id=?";
        List<Object> param = new ArrayList<>();
        param.add(DateUtils.getNowDateStr("yyyy-MM-dd hh:mm:ss"));
        param.add(id);
        jt.update(sql,param.toArray());
    }

    public void delete(Integer id)
    {
        //检查商品是否有选择泥料，如有则提示。建议只隐藏。
        PageResponse<Goods> pageResponse = goodsManager.find("","",0,id,0,
                0,0,-1,1,10);

        if(pageResponse.getTotalCount()>0)
            throw new IllegalArgumentException("该泥料中存在宝贝，建议状态设置为隐藏。");

        goodsMaterialRepository.delete(id);
    }

    private String getContion(Integer goodsId, String name, Integer typeId,Integer status,List<Object> param)
    {
        String condition = "";
        if(!StringUtils.isEmpty(name))
        {
            condition += " And name like ?";
            param.add("%"+ name +"%");
        }
        if(goodsId>0)
        {
            condition +=" AND id in(select materialid from goods where id="+ goodsId +")";
        }
        if(status>-1)
        {
            condition += " AND Status="+status;
        }
        if(typeId>-1){
            condition += " AND typeid="+typeId;
        }
        return condition;
    }

    public PageResponse<GoodsMaterial> findAll(Integer goodsId, String name,Integer typeId,Integer status, Integer pageIndex, Integer pageSize)
    {

        List<Object> param = new ArrayList<>();

        String table="GoodsMaterial";
        String field ="*";
        String orderBy = "Id Desc";
        String condition= getContion(goodsId,name,typeId,status,param);

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
