package com.fct.promotion.data.repository;

import com.fct.promotion.data.entity.CouponUseLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jon on 2017/5/12.
 */
public interface CouponUseLogRepository extends JpaRepository<CouponUseLog, Integer> {
}
