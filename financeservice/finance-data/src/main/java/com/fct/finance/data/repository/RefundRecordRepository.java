package com.fct.finance.data.repository;

import com.fct.finance.data.entity.RefundRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

/**
 * Created by jon on 2017/4/20.
 */
public interface RefundRecordRepository extends JpaRepository<RefundRecord, Integer> {

    Integer countByTradeIdAndTradeType(String tradeId, String tradeType);

    @Query(nativeQuery = true, value = "select * from RefundRecord where status=0 and Id in(?1)")
    List<RefundRecord> findByIds(List<Integer> lsIds);

    //更新状态为退款成功，前提必须是财务确认的状态
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE RefundRecord SET Status=?3,NotifyData=?2 WHERE Id=?1 AND Status=1")
    void updatSuccess(Integer id, String notifyData, Integer upStatus);

    RefundRecord findByTradeIdAndTradeType(String tradeId,String tradeType);

}
