package com.fct.mall.data.repository;

import com.fct.mall.data.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jon on 2017/5/16.
 */
public interface GoodsRepository extends JpaRepository<Goods, Integer> {

}
