package com.fct.finance.data.repository;

import com.fct.finance.data.entity.PayPlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by jon on 2017/4/21.
 */
public interface PayPlatformRepository  extends JpaRepository<PayPlatform, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM PayPlatform WHERE status=1 AND code like ?1 order by sortIndex asc")
    List<PayPlatform> findByCode(String payment);
}
