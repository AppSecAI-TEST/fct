package com.fct.mall.service.business;

import com.fct.mall.data.entity.OrderRefundMessage;
import com.fct.mall.data.repository.OrderRefundMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/18.
 */
@Service
public class OrderRefundMessageManager {

    @Autowired
    private OrderRefundMessageRepository orderRefundMessageRepository;

    public  void save(OrderRefundMessage message)
    {
        if(message.getRefundId()<=0)
        {
            throw new IllegalArgumentException("退款id为空");
        }
        if(StringUtils.isEmpty(message.getDescription()))
        {
            throw new IllegalArgumentException("退款描述内容为空。");
        }
        message.setCreateTime(new Date());
        orderRefundMessageRepository.save(message);
    }

    public List<OrderRefundMessage> findByRefund(Integer refundId)
    {
        if(refundId<=0)
        {
            throw new IllegalArgumentException("退款id为空");
        }
        return  orderRefundMessageRepository.findByRefundId(refundId);
    }
}
