package com.fct.mall.service.business;

import com.fct.mall.data.entity.OrderGoods;
import com.fct.mall.data.repository.OrderGoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jon on 2017/5/17.
 */
@Service
public class OrderGoodsManager {

    @Autowired
    private OrderGoodsRepository orderGoodsRepository;

    public void save(OrderGoods og)
    {
        orderGoodsRepository.save(og);
    }

    public List<OrderGoods> findByOrderId(String orderId)
    {
        return orderGoodsRepository.findByOrderId(orderId);
    }

    public OrderGoods findById(Integer id)
    {
        return orderGoodsRepository.findOne(id);
    }
}
