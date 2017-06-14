package com.fct.mall.service.business;

import com.fct.mall.data.entity.OrderReceiver;
import com.fct.mall.data.repository.OrderReceiverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Created by jon on 2017/5/17.
 * Best nancy
 */
@Service
public class OrderReceiverManager {
    @Autowired
    private OrderReceiverRepository orderReceiverRepository;

    public void save(OrderReceiver or)
    {
        if (StringUtils.isEmpty(or.getOrderId()))
        {
            throw new IllegalArgumentException("订单号不能为空");
        }
        if (StringUtils.isEmpty(or.getAddress()))
        {
            throw new IllegalArgumentException("无收货地址");
        }
        /*if(StringUtils.isEmpty(or.getExpressNO()))
        {
            throw new IllegalArgumentException("已发货订单不可修改地址");
        }8*/
        orderReceiverRepository.save(or);
    }

    public OrderReceiver findByOrderId(String orderId)
    {
        return orderReceiverRepository.findOne(orderId);
    }
}
