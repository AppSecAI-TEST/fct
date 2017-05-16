package com.fct.mall.data.repository;

import com.fct.mall.data.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jon on 2017/5/8.
 */
public interface OrdersRepository extends JpaRepository<Orders, Integer> {


    Page<Orders> findAll(Specification<Orders> spec, Pageable pageable);  //分页按条件查询
}
