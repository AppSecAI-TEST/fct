package com.fct.mall.service.business;

import com.fct.common.exceptions.BaseException;
import com.fct.common.utils.PageUtil;
import com.fct.mall.data.entity.*;
import com.fct.mall.data.repository.OrderRefundRepository;
import com.fct.mall.interfaces.PageResponse;
import com.fct.message.model.MQPayRefund;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/18.
 * Love nancy 10000 Years;
 */
@Service
public class OrderRefundManager {

    @Autowired
    private OrderRefundRepository orderRefundRepository;

    @Autowired
    private OrderRefundMessageManager orderRefundMessageManager;

    @Autowired
    private OrderGoodsManager orderGoodsManager;

    @Autowired
    private JdbcTemplate jt;

    public List<MQPayRefund> payException(Integer memberId, Orders orders, String payOrderId, List<OrderGoods> lsOrderGoods)
    {
        List<MQPayRefund> lsRefund = new ArrayList<>();
        BigDecimal orderAccountAmount = orders.getAccountAmount();
        BigDecimal orderCashAmount = orders.getCashAmount();
        Integer orderPoints = orders.getPoints();

        BigDecimal totalRefundAmount = new BigDecimal(0);

        for (OrderGoods g:lsOrderGoods
             ) {
            OrderRefund refund = new OrderRefund();
            refund.setMemberId(memberId);
            refund.setOrderId(orders.getOrderId());
            refund.setGoodsId(g.getGoodsId());
            refund.setGoodsSpecId(g.getGoodsSpecId());
            refund.setIsReceive(0);//支付异常退款，不需发货。
            refund.setIsRefundMoney(1);//退款
            refund.setStatus(Constants.multiRefundStatus.REFUND_MONEY.getValue());  //支付异常直接到达退款中状态
            refund.setRefundMoneyType(Constants.refundMoneyTypes.ONLINE.getValue());    //原路返回
            refund.setCreateTime(new Date());
            refund.setUpdateTime(new Date());
            orderRefundRepository.save(refund);

            //写入交易通知消息，包含退款数据。
            /*
             * 计算退款金额
                * 优先级别为 现金、余额、积分
             * */
            BigDecimal mqCashAmount = new BigDecimal(0);
            BigDecimal mqAccountAmount = new BigDecimal(0);
            Integer mqPoint = 0;

            totalRefundAmount = totalRefundAmount.add(g.getPayAmount());

            if(orderCashAmount.doubleValue()>totalRefundAmount.doubleValue())
            {
                mqCashAmount = g.getPayAmount();
            }
            else
            {
                mqCashAmount = totalRefundAmount.divide(orderCashAmount); //累计退款金额-订单现金支付额=可退现金
                mqPoint = orderPoints;  //退积分
                mqAccountAmount = orderAccountAmount;   //退余额
            }

            MQPayRefund mqRefund = new MQPayRefund();
            mqRefund.setAccount_amount(mqAccountAmount);
            mqRefund.setCash_amount(mqCashAmount);
            mqRefund.setMethod(1);    //原路返回
            mqRefund.setDesc("交易过程中支付异常系统自动发起退款。");
            mqRefund.setPay_amount(g.getPayAmount());
            mqRefund.setPay_orderid(payOrderId);
            mqRefund.setPoints(mqPoint);
            mqRefund.setTrade_id(refund.getId().toString());
            mqRefund.setTrade_type("buy");

            lsRefund.add(mqRefund);

        }
        return lsRefund;
    }

    public OrderRefund findById(Integer refundId)
    {
        return orderRefundRepository.findOne(refundId);
    }

    //保存退换货
    private void save(OrderRefund refund, Integer status, Integer operatorId, String description, String images)
    {
        if (refund.getMemberId() < 1) {
            throw new IllegalArgumentException ("非法操作");
        }

        //处理退换货状态
        refund.setStatus(status);
        refund.setUpdateTime(new Date());
        orderRefundRepository.saveAndFlush(refund);

        //处理退换货信息
        OrderRefundMessage content = new OrderRefundMessage ();
        content.setRefundId(refund.getId());
        content.setOperatorId(operatorId);
        content.setDescription(description);
        content.setImages(images);
        content.setCreateTime(new Date());
        orderRefundMessageManager.save(content);
    }

    //用户申请
    public void apply (Integer memberId, String orderId, Integer goodsId, Integer goodsSpecId, Integer isReceive,
                       Integer isRefundMoney, Integer refundMoneyType, String description, String images)
    {
        if (memberId < 1) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (StringUtils.isEmpty(orderId)) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (goodsId < 1) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (StringUtils.isEmpty(description)) {
            throw new IllegalArgumentException ("说明不能为空");
        }

        //规格ID
        //goodsSpecId = goodsSpecId > 0 ? goodsSpecId : 0;
        //是否已收货
        isReceive = isReceive > 0 ? 1 : 0;
        //是否需退款
        isRefundMoney = isRefundMoney > 0 ? 1 : 0;
        //退款方式
        refundMoneyType = isReceive == 0 && isRefundMoney == 1 ? refundMoneyType : Constants.refundMoneyTypes.NORMAL.getValue();

        //查询是否曾经申请过，如果申请过，修改原记录，没有就重新申请
        OrderRefund refund = orderRefundRepository.getRefund(memberId,orderId,goodsId,goodsSpecId);

        if (refund == null) {
            refund = new OrderRefund();
            refund.setMemberId(memberId);
            refund.setOrderId(orderId);
            refund.setGoodsId(goodsId);
            refund.setGoodsSpecId(goodsSpecId);
            refund.setIsReceive(isReceive);
            refund.setIsRefundMoney(isRefundMoney);
            refund.setStatus(Constants.multiRefundStatus.WAIT_REFUND.getValue());
            refund.setRefundMoneyType(refundMoneyType);
            refund.setCreateTime(new Date());
            refund.setUpdateTime(new Date());
            orderRefundRepository.save(refund);
        } else {
            refund.setIsReceive(isReceive);
            refund.setIsRefundMoney(isRefundMoney);
            refund.setStatus(Constants.multiRefundStatus.WAIT_REFUND.getValue());
            refund.setRefundMoneyType(refundMoneyType);
            refund.setUpdateTime(new Date());
            orderRefundRepository.saveAndFlush(refund);
        }

        //退款留言
        OrderRefundMessage content = new OrderRefundMessage ();
        content.setRefundId(refund.getId());
        content.setDescription(description);
        content.setImages(images);
        content.setCreateTime(new Date());
        orderRefundMessageManager.save(content);
    }

    //用户关闭申请
    public void close(Integer refundId, Integer memberId, String description, String images)
    {
        if (refundId < 1) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (StringUtils.isEmpty (description)) {
            throw new IllegalArgumentException ("说明不能为空");
        }
        OrderRefund refund = orderRefundRepository.findOne(refundId);
        if (refund == null) {
            throw new IllegalArgumentException ("非法操作");
        }

        this.save(refund, Constants.multiRefundStatus.CLOSE_REFUND.getValue(), 0, description, images);
    }

    //管理员同意申请
    public void agreeApply (Integer refundId, Integer refundMoneyType, String description, String images, Integer operatorId)
    {
        if (refundId < 1) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (operatorId < 1) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (StringUtils.isEmpty (description)) {
            throw new IllegalArgumentException ("说明不能为空");
        }
        OrderRefund refund = orderRefundRepository.findOne(refundId);
        if (refund == null) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (refund.getStatus() != Constants.multiRefundStatus.WAIT_REFUND.getValue()) {
            throw new BaseException ("非法操作");
        }

        //判断是否为申请退款
        if (refund.getIsReceive() == 0 && refund.getIsRefundMoney() == 1) {
            refund.setRefundMoneyType(refundMoneyType);
        }
        Integer status = refund.getIsReceive() > 0 ? Constants.multiRefundStatus.ACCEPT_REFUND_GOODS.getValue() :
                Constants.multiRefundStatus.ACCEPT_REFUND.getValue();

        this.save(refund, status, 0, description, images);
    }

    //管理员拒绝申请
    public void refuseApply (Integer refundId, String description, String images, Integer operatorId)
    {
        if (refundId < 1) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (operatorId < 1) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (StringUtils.isEmpty (description)) {
            throw new IllegalArgumentException ("说明不能为空");
        }

        OrderRefund refund = orderRefundRepository.findOne(refundId);
        if (refund == null) {
            throw new IllegalArgumentException ("非法操作");
        }

        this.save(refund, Constants.multiRefundStatus.REFUSE_REFUND.getValue(), 0, description, images);

    }

    //管理员同意退款
    public void agreeRefund (Integer refundId, Integer refundMoneyType, String description, String images, Integer operatorId)
    {
        if (refundId < 1) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (operatorId < 1) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (StringUtils.isEmpty (description)) {
            throw new IllegalArgumentException ("说明不能为空");
        }
        OrderRefund refund = orderRefundRepository.findOne(refundId);
        if (refund == null) {
            throw new BaseException("非法操作");
        }
        if (refund.getStatus() != Constants.multiRefundStatus.WAIT_REFUND.getValue()) {
            throw new BaseException ("非法操作");
        }

        //判断是否为申请退款
        if (refund.getIsRefundMoney() == 1) {
            refund.setRefundMoneyType(refundMoneyType);
        }
        refund.setStatus(Constants.multiRefundStatus.REFUND_MONEY.getValue());
        this.save(refund, Constants.multiRefundStatus.REFUND_MONEY.getValue(), operatorId, description, images);
    }


    //用户快递发回公司
    public void expressByMember (Integer refundId, Integer memberId, String description, String images)
    {

        if (refundId < 1) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (StringUtils.isEmpty (description)) {
            throw new IllegalArgumentException ("说明不能为空");
        }
        OrderRefund refund = orderRefundRepository.findOne(refundId);
        if (refund == null) {
            throw new IllegalArgumentException ("非法操作");
        }
        this.save(refund, Constants.multiRefundStatus.USER_EXPRESS_GOODS.getValue(), 0, description, images);

    }

    //管理员发货
    public void expressByAdmin (Integer refundId, String description, String images,  Integer operatorId)
    {
        if (operatorId < 1) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (refundId < 1) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (StringUtils.isEmpty(description)) {
            throw new IllegalArgumentException ("说明不能为空");
        }
        OrderRefund refund = orderRefundRepository.findOne(refundId);
        if (refund == null) {
            throw new IllegalArgumentException ("非法操作");
        }
        this.save(refund, Constants.multiRefundStatus.ADMIN_EXPRESS_GOODS.getValue(), 0, description, images);

    }

    private String getContion(String orderId, Integer goodsId, Integer memberId, Integer status, String beginTime,
                              String endTime,List<Object> param)
    {
        String condition = "";

        if (!StringUtils.isEmpty(orderId)) {
            condition += " AND orderId=?";
            param.add(orderId);
        }
        if(goodsId>0)
        {
            condition +=" AND goodsId="+goodsId;
        }

        if (memberId>0) {
            condition += " AND memberId="+memberId;
        }

        if (status>-1) {
            condition += " AND status="+status;
        }
        if(!StringUtils.isEmpty(beginTime))
        {
            condition += " AND createTime >=?";
            param.add(beginTime);
        }
        if(!StringUtils.isEmpty(endTime))
        {
            condition += " AND createTime <=?";
            param.add(endTime);
        }
        return  condition;

    }

    public PageResponse<OrderRefund> findAll(String orderId, Integer goodsId, Integer memberId, Integer status, String beginTime,
                                             String endTime, Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="OrderRefund";
        String field ="*";
        String orderBy = "Id Desc";
        String condition= getContion(orderId,goodsId,memberId,status,beginTime,endTime,param);

        String sql = "SELECT Count(0) FROM OrderRefund WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<OrderRefund> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<OrderRefund>(OrderRefund.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }

        PageResponse<OrderRefund> pageResponse = new PageResponse<>();
        pageResponse.setTotalCount(count);
        pageResponse.setCurrent(end);
        pageResponse.setElements(ls);
        pageResponse.setHasMore(hasmore);

        return pageResponse;
        /*Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<OrderRefund> spec = new Specification<OrderRefund>() {
            @Override
            public Predicate toPredicate(Root<OrderRefund> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(orderId)) {
                    predicates.add(cb.equal(root.get("orderId"), orderId));
                }
                if(goodsId>0)
                {
                    predicates.add(cb.equal(root.get("goodsId"),goodsId));
                }

                if (memberId>0) {
                    predicates.add(cb.equal(root.get("memberId"), memberId));
                }

                if (status>-1) {
                    predicates.add(cb.equal(root.get("status"), status));
                }

                if (!org.apache.commons.lang3.StringUtils.isEmpty(beginTime)) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("beginTime"), beginTime));
                }
                if (!org.apache.commons.lang3.StringUtils.isEmpty(endTime)) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("endTime"), endTime));
                }
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };

        return orderRefundRepository.findAll(spec,pageable);*/
    }


    //财务处理完成后处理接口
    @Transactional
    public void refundSuccess (Integer refundId, String description)
    {
        if (refundId < 1) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (StringUtils.isEmpty (description)) {
            throw new IllegalArgumentException ("说明不能为空");
        }
        OrderRefund refund = orderRefundRepository.findOne(refundId);
        if (refund == null) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (refund.getStatus() != Constants.multiRefundStatus.WAIT_REFUND.getValue()) {
            throw new BaseException ("非法操作");
        }

        OrderGoods og = orderGoodsManager.findByOrderIdAndGoods(refund.getOrderId(),refund.getGoodsId(),refund.getGoodsSpecId());

        this.save(refund, Constants.multiRefundStatus.REFUND_MONEY_SUCCESS.getValue(), 0, description, "");

        jt.update(String.format("UPDATE Goods SET sellCount=sellCount-%d WHERE Id=%d",
                og.getBuyCount(),og.getGoodsId()));

    }
}
