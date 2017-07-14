package com.fct.finance.data.repository;

import com.fct.finance.data.entity.SettleRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/4/20.
 */
public interface SettleRecordRepository extends JpaRepository<SettleRecord, Integer> {

    Integer countByTradeIdAndTradeType(String tradeId, String tradeType);

    @Query(nativeQuery = true, value = "select * from SettleRecord where status=?1")
    List<SettleRecord> findByStatus(Integer status);
}
