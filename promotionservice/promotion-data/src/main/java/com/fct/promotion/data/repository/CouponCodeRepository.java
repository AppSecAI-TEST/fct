package com.fct.promotion.data.repository;

import com.fct.promotion.data.entity.CouponCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by jon on 2017/5/9.
 */
public interface CouponCodeRepository extends JpaRepository<CouponCode, Integer> {

    CouponCode findByCode(String code);
}
