package com.fct.mall.data.repository;

import com.fct.mall.data.entity.OrderComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jon on 2017/5/17.
 */
public interface OrderCommentRepository extends JpaRepository<OrderComment, Integer> {

    int countByOrderIdAndGoodsId(String orderId,Integer goodsId);

}
