package com.fct.promotion.data.repository;

import com.fct.promotion.data.entity.DiscountProduct;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jon on 2017/5/9.
 */
public interface DiscountProductRepository extends JpaRepository<DiscountProduct, Integer> {
}
