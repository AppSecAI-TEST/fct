package com.fct.mall.service.business;

import com.fct.mall.data.entity.OrderRefundMessage;
import com.fct.mall.data.repository.OrderRefundMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        orderRefundMessageRepository.save(message);
    }

    public List<OrderRefundMessage> findByRefund(Integer refundId)
    {
        return  orderRefundMessageRepository.findByRefundId(refundId);
    }
}
