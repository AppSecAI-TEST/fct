package com.fct.mall.data.repository;

import com.fct.mall.data.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by jon on 2017/5/17.
 */
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {

    @Query(nativeQuery = true,
            value = "SELECT * FROM ShoppingCart WHERE MemberId=?1 And ShopId=?2 AND GoodsId=?3 AND GoodsSpecId=?4")
    ShoppingCart getByMemberId(Integer memberId,Integer shopId,Integer goodsId,Integer goodsSpecId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM ShoppingCart WHERE MemberId=?1 And ShopId=?2")
    List<ShoppingCart> findByMemberId(Integer memberId, Integer shopId);

    @Query(nativeQuery = true,
            value = "Delete FROM ShoppingCart WHERE MemberId=?1 And ShopId=?2 AND id=?3")
    void  delete(Integer memberId,Integer shopId,Integer cartId);
}
