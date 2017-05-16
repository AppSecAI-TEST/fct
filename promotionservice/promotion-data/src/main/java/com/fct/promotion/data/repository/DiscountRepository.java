package com.fct.promotion.data.repository;

import com.fct.promotion.data.entity.CouponPolicy;
import com.fct.promotion.data.entity.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jon on 2017/5/9.
 */
public interface DiscountRepository extends JpaRepository<Discount, Integer> {

    Page<Discount> findAll(Specification<Discount> spec, Pageable pageable);  //分页按条件查询
}
