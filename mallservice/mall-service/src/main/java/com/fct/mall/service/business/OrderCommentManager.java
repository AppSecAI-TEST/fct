package com.fct.mall.service.business;

import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.OrderComment;
import com.fct.mall.data.entity.Orders;
import com.fct.mall.data.repository.OrderCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/22.
 * Love will bring us back to you and me
 */
@Service
public class OrderCommentManager {

    @Autowired
    private OrderCommentRepository orderCommentRepository;

    @Autowired
    private GoodsManager goodsManager;

    @Autowired
    private OrdersManager ordersManager;

    @Autowired
    private JdbcTemplate jt;

    @Transactional
    public void create(OrderComment commnet)
    {
        //校验：一笔订单一个商品只能对应一条评论，并且订单是交易完成状态
        if(commnet.getGoodsId()<=0)
        {
            throw  new IllegalArgumentException("商品不存在");
        }
        if(StringUtils.isEmpty(commnet.getContent()))
        {
            throw new IllegalArgumentException("评论内容为空");
        }
        if(commnet.getMemberId()<=0)
        {
            throw new IllegalArgumentException("用户不存在");
        }
        if(!StringUtils.isEmpty(commnet.getOrderId()))
        {
            throw new IllegalArgumentException("订单为空");
        }

        int count = orderCommentRepository.countByOrderIdAndGoodsId(commnet.getOrderId(),commnet.getGoodsId());
        if(count>0)
        {
            throw new IllegalArgumentException("已评论");
        }

        Goods g = goodsManager.findById(commnet.getGoodsId());
        g.setCommentCount(g.getCommentCount()+1);
        g.setCommnetScore(1);//计算动态评分
        goodsManager.save(g);

        commnet.setCreateTime(new Date());
        commnet.setUpdateTime(new Date());
        orderCommentRepository.save(commnet);

        Orders orders = ordersManager.findById(commnet.getOrderId());
        orders.setUpdateTime(new Date());
        orders.setCommentId(commnet.getId());
        ordersManager.save(orders);
    }

    public void reply(Integer id,String replyContent)
    {
        OrderComment comment = orderCommentRepository.findOne(id);
        comment.setReplyContent(replyContent);
        comment.setUpdateTime(new Date());
        orderCommentRepository.save(comment);
    }

    public Page<OrderComment> findAll(Integer goodsId,Integer memberId,String orderId,Integer pageIndex,Integer pageSize)
    {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<OrderComment> spec = new Specification<OrderComment>() {
            @Override
            public Predicate toPredicate(Root<OrderComment> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(goodsId>0)
                {
                    predicates.add(cb.equal(root.get("goodsId"),goodsId));
                }

                if (memberId>0) {
                    predicates.add(cb.equal(root.get("memberId"), memberId));
                }

                if (!StringUtils.isEmpty(orderId)) {
                    predicates.add(cb.equal(root.get("orderId"), orderId));
                }

                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };

        return orderCommentRepository.findAll(spec,pageable);
    }
}
