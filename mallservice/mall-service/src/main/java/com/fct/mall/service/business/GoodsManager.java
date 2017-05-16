package com.fct.mall.service.business;

import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.repository.GoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;

/**
 * Created by jon on 2017/5/16.
 */
@Service
public class GoodsManager {

    @Autowired
    private GoodsRepository goodsRepository;

    public Goods findById(Integer id)
    {
        return goodsRepository.findOne(id);
    }
}
