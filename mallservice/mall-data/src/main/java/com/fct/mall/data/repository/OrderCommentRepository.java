package com.fct.mall.data.repository;

import com.fct.mall.data.entity.OrderComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by jon on 2017/5/17.
 */
public interface OrderCommentRepository extends JpaRepository<OrderComment, Integer> {

    int countByOrderIdAndGoodsId(String orderId,Integer goodsId);

    @Query(nativeQuery = true,
            value = "select * from OrderComment where goodsId=?1 and status =1")
    List<OrderComment> findByGoodsId(Integer goodsId);

}
