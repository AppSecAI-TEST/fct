package com.fct.mall.service.business;

import com.fct.mall.data.entity.GoodsSpecification;
import com.fct.mall.data.repository.GoodsSpecificationRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by jon on 2017/5/16.
 */
public class GoodsSpecificationManager {

    @Autowired
    private GoodsSpecificationRepository goodsSpecificationRepository;

    public GoodsSpecification findById(Integer id)
    {
        return goodsSpecificationRepository.findOne(id);
    }

    public List<GoodsSpecification> findByGoodsId(Integer goodsId)
    {
        return goodsSpecificationRepository.findByGoodsId(goodsId);
    }
}
