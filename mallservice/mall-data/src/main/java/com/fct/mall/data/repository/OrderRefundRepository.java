package com.fct.mall.data.repository;

import com.fct.mall.data.entity.OrderRefund;
import com.fct.mall.data.entity.Orders;
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
            value = "select * from OrderRefund where memberid=?1 and orderId=?2 and goodsId=?3 and goodsSpecId=?4")
    OrderRefund getRefund(Integer memberId, String orderId, Integer goodsId, Integer goodsSpecId);

    Page<OrderRefund> findAll(Specification<OrderRefund> spec, Pageable pageable);  //分页按条件查询
}
