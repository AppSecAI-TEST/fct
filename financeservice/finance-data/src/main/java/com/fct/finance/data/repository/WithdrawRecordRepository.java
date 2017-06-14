package com.fct.finance.data.repository;

import com.fct.finance.data.entity.WithdrawRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * Created by jon on 2017/4/20.
 */
public interface WithdrawRecordRepository extends JpaRepository<WithdrawRecord, Integer> {

    int countByMemberIdAndStatus(Integer memberId, Integer status);

}
