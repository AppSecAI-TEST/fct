package com.fct.promotion.data.repository;

import com.fct.promotion.data.entity.CouponSpareCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * Created by jon on 2017/5/9.
 */
public interface CouponSpareCodeRepository extends JpaRepository<CouponSpareCode, Integer> {

    @Query(nativeQuery = true, value = "select * from CouponSpareCode where status =0 order by Id limit 1")
    CouponSpareCode getTopOne();

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update CouponSpareCode set status =1 where code=?1")
    void updateByCode(String code);

    @Query(nativeQuery = true, value = "select * from CouponSpareCode where code =?1 and status=1 limit 1")
    CouponSpareCode findOneByCode(String code);

    @Query(nativeQuery = true, value = "select count(0) from CouponSpareCode where status =0")
    Integer getCount();

}
