package com.fct.finance.data.repository;

import com.fct.finance.data.entity.RechargeRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * Created by jon on 2017/4/21.
 */
public interface RechargeRecordRepository extends JpaRepository<RechargeRecord, Integer> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "UPDATE RechargeRecord set payPlatform=?1 WHERE id=?2")
    void  updatePayPlatform(String payPlatform,Integer id);
}
