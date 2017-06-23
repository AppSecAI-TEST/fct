package com.fct.mall.service.business;

import com.fct.mall.data.entity.GoodsSpecification;
import com.fct.mall.data.repository.GoodsSpecificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by jon on 2017/5/16.
 */
@Service
public class GoodsSpecificationManager {

    @Autowired
    private GoodsSpecificationRepository goodsSpecificationRepository;

    public GoodsSpecification findById(Integer id)
    {
        if(id <=0)
        {
            throw new IllegalArgumentException("id为空");
        }
        return goodsSpecificationRepository.findOne(id);
    }

    public List<GoodsSpecification> findByGoodsId(Integer goodsId)
    {
        if(goodsId <=0)
        {
            throw new IllegalArgumentException("宝贝id为空");
        }
        return goodsSpecificationRepository.findByGoodsIdAndIsdel(goodsId,0);
    }

    public GoodsSpecification findByGoodsIdAndName(Integer goodsId,String name)
    {
        if(goodsId <=0)
        {
            throw new IllegalArgumentException("宝贝id为空");
        }
        if(StringUtils.isEmpty(name))
        {
            throw new IllegalArgumentException("规格名称为空");
        }
        return goodsSpecificationRepository.findByGoodsIdAndName(goodsId,name);
    }

    public void save(GoodsSpecification gs)
    {
        if(StringUtils.isEmpty(gs.getCode()))
        {
            throw new IllegalArgumentException("规格编号为空");
        }
        if(gs.getCommission().doubleValue() <=0)
        {
            throw new IllegalArgumentException("销售佣金不正确");
        }
        if(gs.getGoodsId()<=0)
        {
            throw new IllegalArgumentException("宝贝id为空");
        }
        goodsSpecificationRepository.save(gs);
    }

    public void delete(Integer goodsId,List<Integer> ids)
    {
        if(goodsId <=0)
        {
            throw new IllegalArgumentException("宝贝id为空");
        }
        if(ids==null || ids.size()<=0)
            ids.add(0);
        goodsSpecificationRepository.updateDelStatus(goodsId,ids);
    }
}
