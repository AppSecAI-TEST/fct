package com.fct.mall.data.repository;

import com.fct.mall.data.entity.OrderGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by jon on 2017/5/17.
 */
public interface OrderGoodsRepository extends JpaRepository<OrderGoods, Integer> {

    List<OrderGoods> findByOrderId(String orderId);
}
