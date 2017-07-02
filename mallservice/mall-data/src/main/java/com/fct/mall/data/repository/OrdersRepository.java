package com.fct.mall.data.repository;

import com.fct.mall.data.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * Created by jon on 2017/5/8.
 */
public interface OrdersRepository extends JpaRepository<Orders, String> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "UPDATE Orders set payPlatform=?1 WHERE orderId=?2")
    void  updatePayPlatform(String payPlatform,String orderId);
}
