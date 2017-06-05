package com.fct.mall.data.repository;

import com.fct.mall.data.entity.GoodsMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by jon on 2017/5/22.
 */
public interface GoodsMaterialRepository extends JpaRepository<GoodsMaterial, Integer> {

    @Query(nativeQuery = true,
            value = "UPDATE GoodsMaterial set Status=1-Status,updatetime=?2 WHERE Id=?1")
    void updateStatus(Integer id,String updateTime);

    Page<GoodsMaterial> findAll(Specification<GoodsMaterial> spec, Pageable pageable);  //分页按条件查询
}
