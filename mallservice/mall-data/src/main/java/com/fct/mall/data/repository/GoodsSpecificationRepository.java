package com.fct.mall.data.repository;

import com.fct.mall.data.entity.GoodsSpecification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by jon on 2017/5/16.
 */
public interface GoodsSpecificationRepository  extends JpaRepository<GoodsSpecification, Integer> {

    List<GoodsSpecification> findByGoodsId(Integer goodsId);
}
