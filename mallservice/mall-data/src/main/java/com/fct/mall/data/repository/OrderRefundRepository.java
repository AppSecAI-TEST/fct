package com.fct.mall.data.repository;

import com.fct.mall.data.entity.OrderRefund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by jon on 2017/5/17.
 */
public interface OrderRefundRepository  extends JpaRepository<OrderRefund, Integer> {

    @Query(nativeQuery = true,
            value = "select * from OrderRefund where memberid=?1 and orderId=?2 and orderGoodsId=?3")
    OrderRefund getRefund(Integer memberId, String orderId, Integer orderGoodsId);
}
