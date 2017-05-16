package com.fct.promotion.data.repository;

import com.fct.promotion.data.entity.DiscountProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by jon on 2017/5/9.
 */
public interface DiscountProductRepository extends JpaRepository<DiscountProduct, Integer> {

    @Query(nativeQuery = true, value = "select * from DiscountProduct where DiscountId=?1 order by Id desc limit 1000")
    List<DiscountProduct> findByDiscountId(Integer discountId);
}
