package com.fct.mall.service.business;

import com.fct.common.utils.PageUtil;
import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsMaterial;
import com.fct.mall.data.entity.OrderComment;
import com.fct.mall.data.entity.Orders;
import com.fct.mall.data.repository.OrderCommentRepository;
import com.fct.mall.interfaces.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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

    private String getContion(Integer goodsId, Integer memberId, String orderId,List<Object> param)
    {
        String condition ="";
        if(goodsId>0)
        {
            condition += " AND goodsId="+goodsId;
        }

        if (memberId>0) {
            condition += " AND memberId="+memberId;
        }

        if (!StringUtils.isEmpty(orderId)) {
            condition += " AND orderId=?";
            param.add(orderId);
        }
        return condition;
    }

    public PageResponse<OrderComment> findAll(Integer goodsId, Integer memberId, String orderId, Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="OrderComment";
        String field ="*";
        String orderBy = "Id Desc";
        String condition= getContion(goodsId,memberId,orderId,param);

        String sql = "SELECT Count(0) FROM OrderComment WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<OrderComment> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<OrderComment>(OrderComment.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }

        PageResponse<OrderComment> pageResponse = new PageResponse<>();
        pageResponse.setTotalCount(count);
        pageResponse.setCurrent(end);
        pageResponse.setElements(ls);
        pageResponse.setHasMore(hasmore);

        return pageResponse;
    }
}
