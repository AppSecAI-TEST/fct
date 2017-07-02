package com.fct.promotion.data.repository;

import com.fct.promotion.data.entity.CouponPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by jon on 2017/5/9.
 */
public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Integer> {

    @Modifying
    @Query(nativeQuery = true, value = "update CouponPolicy set ReceivedCount+=1 where Id=?1")
    void addReceiveCount(Integer policyId);

    @Modifying
    @Query(nativeQuery = true, value = "update CouponPolicy set GenerateStatus=1 where Id=?1")
    void updateGenerateStatus(Integer policyId);

    @Query(nativeQuery = true, value = "select * from CouponPolicy where FetchType = 1 and Status = 1 and GenerateStatus = 0 order by id desc limit 100")
    List<CouponPolicy> findNeedGenerate();
}
