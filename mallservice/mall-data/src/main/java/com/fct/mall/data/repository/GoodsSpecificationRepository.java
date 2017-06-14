package com.fct.mall.data.repository;

import com.fct.mall.data.entity.GoodsSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by jon on 2017/5/16.
 */
public interface GoodsSpecificationRepository  extends JpaRepository<GoodsSpecification, Integer> {

    List<GoodsSpecification> findByGoodsIdAndIsdel(Integer goodsId,Integer isdel);

    GoodsSpecification findByGoodsIdAndName(Integer goodsId,String name);

    @Modifying
    @Query("update GoodsSpecification set isdel=1 where goodsId=?1 and id not IN ?2")
    void updateDelStatus(Integer goodsId,List<Integer> ids);
}
