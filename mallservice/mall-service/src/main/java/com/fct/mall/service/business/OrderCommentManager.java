package com.fct.mall.service.business;

import com.fct.core.utils.PageUtil;
import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.OrderComment;
import com.fct.mall.data.entity.Orders;
import com.fct.mall.data.repository.OrderCommentRepository;
import com.fct.mall.interfaces.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public void createMutil(String orderId,Integer anonymous, Integer logisticsScore,
                            Integer saleScore,List<OrderComment> commentList)
    {
        if(commentList ==null || commentList.size()<=0)
        {
            throw  new IllegalArgumentException("评论为空");
        }
        if(StringUtils.isEmpty(orderId))
        {
            throw  new IllegalArgumentException("订单为空");
        }
        if(logisticsScore<1 || saleScore<1 || logisticsScore>5 ||
                saleScore>5)
        {
            throw new IllegalArgumentException("动态分不正确");
        }

        Orders orders = ordersManager.findById(orderId);
        if(orders.getCommentStatus() ==1)
        {
            throw  new IllegalArgumentException("已评论过。");
        }
        orders.setUpdateTime(new Date());
        orders.setCommentStatus(1);
        ordersManager.save(orders);

        for (OrderComment comment:commentList
             ) {
            //校验：一笔订单一个商品只能对应一条评论，并且订单是交易完成状态
            if(comment.getGoodsId()<=0)
            {
                throw  new IllegalArgumentException("商品不存在");
            }
            if(StringUtils.isEmpty(comment.getContent()))
            {
                throw new IllegalArgumentException("评论内容为空");
            }
            if(comment.getDescScore()<1 || comment.getDescScore()>5)
            {
                throw new IllegalArgumentException("宝贝描述分不正确");
            }
            comment.setCellPhone(orders.getCellPhone());
            comment.setOrderId(orderId);
            comment.setMemberId(orders.getMemberId());
            comment.setLogisticsScore(logisticsScore);
            comment.setStatus(saleScore);
            comment.setIsAnonymous(anonymous);

            int count = orderCommentRepository.countByOrderIdAndGoodsId(comment.getOrderId(),comment.getGoodsId());
            if(count<=0)
            {
                comment.setCreateTime(new Date());
                comment.setUpdateTime(new Date());
                comment.setStatus(0);
                orderCommentRepository.save(comment);
            }

        }
    }

    private Float getGoodsScore(Integer goodsId,Integer newScore)
    {
        List<OrderComment> ls = orderCommentRepository.findByGoodsId(goodsId);
        Integer totalScore = newScore;
        for (OrderComment oc:ls
             ) {
            totalScore += oc.getDescScore();
        }
        Integer count = ls.size()+1;
        Float score =  new Float(count/totalScore);

        return score*5;
    }

    @Transactional
    public void updateStatus(Integer id,Integer status)
    {
        if(id<=0)
        {
            throw  new IllegalArgumentException("id不存在");
        }
        OrderComment commnet = orderCommentRepository.findOne(id);
        if(status ==1)
        {
            Goods g = goodsManager.findById(commnet.getGoodsId());
            g.setCommentCount(g.getCommentCount()+1);
            g.setCommentScore(getGoodsScore(g.getId(),commnet.getDescScore()));//计算动态评分
            goodsManager.save(g);
        }
        commnet.setStatus(status);
        commnet.setUpdateTime(new Date());
        orderCommentRepository.save(commnet);
    }

    public void reply(Integer id,String replyContent)
    {
        if(id<=0)
        {
            throw  new IllegalArgumentException("id不存在");
        }
        if(StringUtils.isEmpty(replyContent))
        {
            throw new IllegalArgumentException("回复内容为空");
        }
        OrderComment comment = orderCommentRepository.findOne(id);
        comment.setReplyContent(replyContent);
        comment.setUpdateTime(new Date());
        orderCommentRepository.save(comment);
    }

    private String getContion(Integer goodsId, Integer memberId,String cellphone,String orderId,
                              Integer status,String beginTime,String endTime,List<Object> param)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(" AND status!=2");
        if(goodsId>0)
        {
            sb.append(" AND goodsId="+goodsId);
        }

        if (memberId>0) {
            sb.append(" AND memberId="+memberId);
        }

        if (!StringUtils.isEmpty(orderId)) {
            sb.append(" AND orderId=?");
            param.add(orderId);
        }

        if (!StringUtils.isEmpty(cellphone)) {
            sb.append(" AND cellphone=?");
            param.add(cellphone);
        }
        if(status>-1)
        {
            sb.append(" AND status="+status);
        }
        if(!StringUtils.isEmpty(beginTime))
        {
            sb.append(" AND createTime >=?");
            param.add(beginTime);
        }
        if(!StringUtils.isEmpty(endTime))
        {
            sb.append(" AND createTime <=?");
            param.add(endTime);
        }
        return sb.toString();
    }

    public PageResponse<OrderComment> findAll(Integer goodsId, Integer memberId,String cellphone, String orderId,
                                              Integer status,String beginTime,String endTime,Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="OrderComment";
        String field ="*";
        String orderBy = "Id Desc";
        String condition= getContion(goodsId,memberId,cellphone,orderId,status,beginTime,endTime,param);

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
