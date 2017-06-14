package com.fct.mall.data.repository;

import com.fct.mall.data.entity.GoodsMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * Created by jon on 2017/5/22.
 */
public interface GoodsMaterialRepository extends JpaRepository<GoodsMaterial, Integer> {

}
