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

    /**
     * 发货时，前方订单操作时会校验，这里只需要校验基本信息
     * */
    public void save(OrderReceiver or)
    {
        if(StringUtils.isEmpty(or.getPhone()))
        {
            throw new IllegalArgumentException("联系电话为空");
        }
        if (StringUtils.isEmpty(or.getOrderId()))
        {
            throw new IllegalArgumentException("订单号不能为空");
        }
        if (StringUtils.isEmpty(or.getAddress()))
        {
            throw new IllegalArgumentException("无收货地址");
        }
        if(StringUtils.isEmpty(or.getName()))
        {
            throw new IllegalArgumentException("收货人为空");
        }
        if(StringUtils.isEmpty(or.getProvince()))
        {
            throw new IllegalArgumentException("省份为空");
        }
        if(StringUtils.isEmpty(or.getCity()))
        {
            throw new IllegalArgumentException("城市为空");
        }
        if(StringUtils.isEmpty(or.getRegion()))
        {
            throw new IllegalArgumentException("区域为空");
        }
        /*if(StringUtils.isEmpty(or.getExpressNO()))
        {
            throw new IllegalArgumentException("已发货订单不可修改地址");
        }8*/
        orderReceiverRepository.save(or);
    }

    public OrderReceiver findByOrderId(String orderId)
    {
        if (StringUtils.isEmpty(orderId))
        {
            throw new IllegalArgumentException("订单号不能为空");
        }
        return orderReceiverRepository.findOne(orderId);
    }
}
