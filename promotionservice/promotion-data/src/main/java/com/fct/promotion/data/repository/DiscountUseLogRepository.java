package com.fct.promotion.data.repository;

import com.fct.promotion.data.entity.DiscountUseLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jon on 2017/5/12.
 */
public interface DiscountUseLogRepository extends JpaRepository<DiscountUseLog, Integer> {
}
