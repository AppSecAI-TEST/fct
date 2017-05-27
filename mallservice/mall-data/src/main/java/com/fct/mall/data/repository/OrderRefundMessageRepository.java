package com.fct.mall.data.repository;

import com.fct.mall.data.entity.OrderRefundMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by jon on 2017/5/17.
 */
public interface OrderRefundMessageRepository  extends JpaRepository<OrderRefundMessage, Integer> {

    List<OrderRefundMessage> findByRefundId(Integer refundId);

}
