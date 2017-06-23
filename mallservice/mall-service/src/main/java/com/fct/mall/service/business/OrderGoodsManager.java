package com.fct.mall.service.business;

import com.fct.mall.data.entity.OrderGoods;
import com.fct.mall.data.repository.OrderGoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.print.DocFlavor;
import java.util.List;

/**
 * Created by jon on 2017/5/17.
 */
@Service
public class OrderGoodsManager {

    @Autowired
    private OrderGoodsRepository orderGoodsRepository;

    /***
     * 生成订单时调用，前方已作校验
     * */
    public void save(OrderGoods og)
    {
        orderGoodsRepository.save(og);
    }

    public List<OrderGoods> findByOrderId(String orderId)
    {
        if(StringUtils.isEmpty(orderId))
        {
            throw new IllegalArgumentException("订单id为空");
        }
        return orderGoodsRepository.findByOrderId(orderId);
    }

    public OrderGoods findById(Integer id)
    {
        if(id<=0)
        {
            throw new IllegalArgumentException("id为空");
        }
        return orderGoodsRepository.findOne(id);
    }
}
