package com.fct.mall.service.business;

import com.fct.core.json.JsonConverter;
import com.fct.core.utils.PageUtil;
import com.fct.mall.data.entity.*;
import com.fct.mall.data.repository.OrderRefundRepository;
import com.fct.mall.interfaces.OrderRefundDTO;
import com.fct.mall.interfaces.PageResponse;
import com.fct.message.interfaces.MessageService;
import com.fct.message.interfaces.model.MQPayRefund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    private OrdersManager ordersManager;

    @Autowired
    private MessageService messageService;

    @Autowired
    private JdbcTemplate jt;

    private void agreeRefund(OrderRefund refund)
    {
        Orders orders = ordersManager.findOne(refund.getOrderId());
        OrderGoods g = orderGoodsManager.findById(refund.getOrderGoodsId());

        BigDecimal orderAccountAmount = orders.getAccountAmount();
        BigDecimal orderCashAmount = orders.getCashAmount();
        Integer orderPoints = orders.getPoints();

        BigDecimal totalRefundAmount = new BigDecimal(0);
        //获取订单已退款宝贝的总退款金额：

        List<OrderGoods> lsGoods = orderGoodsManager.findByOrderId(orders.getOrderId());
        for (OrderGoods og: lsGoods
             ) {
            totalRefundAmount = totalRefundAmount.add(g.getPayAmount());
        }

        //写入交易通知消息，包含退款数据。
            /*
             * 计算退款金额
                * 优先级别为 现金、余额、积分
             * */
        BigDecimal mqCashAmount = new BigDecimal(0);
        BigDecimal mqAccountAmount = new BigDecimal(0);
        Integer mqPoint = 0;

        totalRefundAmount = totalRefundAmount.add(g.getPayAmount());

        if(orderCashAmount.doubleValue()>=totalRefundAmount.doubleValue())
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
        mqRefund.setMethod(refund.getRefundMethod());    //原路返回
        mqRefund.setDesc("用户发起的退款申请。");
        mqRefund.setPay_amount(g.getPayAmount());
        mqRefund.setPay_orderid(orders.getPayOrderId());
        mqRefund.setPoints(mqPoint);
        mqRefund.setTrade_id(refund.getId().toString());
        mqRefund.setTrade_type("buy");
        messageService.send("mq_payrefund","MQPayRefund","com.fct.mallservice", JsonConverter.toJson(mqRefund),"购买商品申请退款");
    }

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
            refund.setOrderGoodsId(g.getId());
            refund.setIsReceived(0);//支付异常退款，不需发货。
            refund.setServiceType(1);//仅退款
            refund.setStatus(Constants.enumRefundStatus.agree.getValue());  //支付异常直接到达退款中状态
            refund.setRefundMethod(Constants.enumRefundMethod.return_original_road.getValue());    //原路返回
            refund.setRefundReason("支付异常，系统自动退款。");
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

            if(orderCashAmount.doubleValue()>=totalRefundAmount.doubleValue())
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
            mqRefund.setMethod(Constants.enumRefundMethod.return_original_road.getValue());    //原路返回
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
        if(refundId<=0)
        {
            throw new IllegalArgumentException("退款id");
        }
        OrderRefund refund = orderRefundRepository.findOne(refundId);
        OrderGoods goods = orderGoodsManager.findById(refund.getOrderGoodsId());
        List<OrderRefundMessage> lsMessage = orderRefundMessageManager.findByRefund(refundId);

        refund.setOrderGoods(goods);
        refund.setRefundMessage(lsMessage);
        return refund;
    }

    //保存退换货
    @Transactional
    private void save(OrderRefund refund, Integer status, Integer operatorId, String description, String images)
    {
        if (refund.getMemberId() < 1) {
            throw new IllegalArgumentException ("非法操作");
        }

        //处理退换货状态
        refund.setStatus(status);
        refund.setUpdateTime(new Date());
        orderRefundRepository.save(refund);

        if(!StringUtils.isEmpty(description)) {
            //处理退换货信息
            OrderRefundMessage content = new OrderRefundMessage();
            content.setRefundId(refund.getId());
            content.setOperatorId(operatorId);
            content.setDescription(description);
            content.setImages(images);
            content.setCreateTime(new Date());
            orderRefundMessageManager.save(content);
        }
        if(refund.getStatus() == Constants.enumRefundStatus.agree.getValue())
        {
            agreeRefund(refund);//同意退款处理，
        }
    }

    //用户申请
    public void apply (Integer memberId, String orderId, Integer orderGoodsId, Integer isReceived,
                       Integer serviceType, Integer refundMethod, String refundReason, String description,
                       String images)
    {
        if (memberId < 1) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (StringUtils.isEmpty(orderId)) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (orderGoodsId < 1) {
            throw new IllegalArgumentException ("非法操作");
        }
        if (StringUtils.isEmpty(description)) {
            throw new IllegalArgumentException ("说明不能为空");
        }
        if(StringUtils.isEmpty(refundReason))
        {
            throw new IllegalArgumentException ("退款原因为空");
        }
        if(StringUtils.isEmpty(description))
        {
            throw new IllegalArgumentException ("退款描述为空");
        }

        //规格ID
        //goodsSpecId = goodsSpecId > 0 ? goodsSpecId : 0;
        //是否已收货
        //isReceived = isReceived > 0 ? 1 : 0;
        //是否需退款
        serviceType = serviceType > 0 ? 1 : 0;
        //如果申请服务为退货退款，则为已收到货
        if(serviceType == 0)
            isReceived =1; //退货退款，肯定是收到货
        else
        {
            isReceived=0;
        }
        //refundMethod = isReceived == 0 && serviceType == 1 ? refundMethod : Constants.enumRefundMethod.NORMAL.getValue();

        //查询是否曾经申请过，如果申请过，修改原记录，没有就重新申请
        OrderRefund refund = orderRefundRepository.getRefund(memberId,orderId,orderGoodsId);

        if (refund == null) {
            refund = new OrderRefund();
            refund.setMemberId(memberId);
            refund.setOrderId(orderId);
            refund.setOrderGoodsId(orderGoodsId);
            refund.setIsReceived(isReceived);
            refund.setServiceType(serviceType);
            refund.setStatus(Constants.enumRefundStatus.wait.getValue());
            refund.setRefundMethod(refundMethod);
            refund.setRefundReason(refundReason);
            refund.setCreateTime(new Date());
            refund.setUpdateTime(new Date());
            orderRefundRepository.save(refund);
        } else {
            refund.setIsReceived(isReceived);
            refund.setServiceType(serviceType);
            refund.setStatus(Constants.enumRefundStatus.wait.getValue());
            refund.setRefundMethod(refundMethod);
            refund.setRefundReason(refundReason);
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

        this.save(refund, Constants.enumRefundStatus.close.getValue(), 0, description, images);
    }

    //管理员同意申请
    public void agreeApply (Integer refundId, Integer refundMethod, String description, String images, Integer operatorId)
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
        if (refund.getStatus() != Constants.enumRefundStatus.wait.getValue()) {
            throw new IllegalArgumentException ("非法操作");
        }

        //判断是否为申请退款
        Integer status = refund.getIsReceived() > 0 ? Constants.enumRefundStatus.accept.getValue() :
                Constants.enumRefundStatus.agree.getValue();

        refund.setRefundMethod(refundMethod);

        this.save(refund, status, operatorId, description, images);
    }

    //管理员同意退款
    public void agreeRefund (Integer refundId, String description, String images, Integer operatorId)
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
        if(refund.getStatus() != Constants.enumRefundStatus.express.getValue() &&
                refund.getStatus() != Constants.enumRefundStatus.accept.getValue())
        {
            throw new IllegalArgumentException ("非法操作");
        }

        this.save(refund, Constants.enumRefundStatus.agree.getValue(), operatorId, description, images);
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

        this.save(refund, Constants.enumRefundStatus.refuse.getValue(), operatorId, description, images);

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
        this.save(refund, Constants.enumRefundStatus.express.getValue(), 0, description, images);

    }

    private String getContion(String orderId, String goodsName,Integer orderGoodsId, Integer memberId, Integer status, String beginTime,
                              String endTime,List<Object> param)
    {
        StringBuilder sb = new StringBuilder();

        if (!StringUtils.isEmpty(orderId)) {
            sb.append(" AND r.orderId=?");
            param.add(orderId);
        }
        if(orderGoodsId>0)
        {
            sb.append(" AND r.orderGoodsId="+orderGoodsId);
        }

        if (memberId>0) {
            sb.append(" AND r.memberId="+memberId);
        }
        if(!StringUtils.isEmpty(goodsName))
        {
            sb.append(" AND g.name=?");
            param.add(goodsName);
        }

        if (status>-1) {
            sb.append(" AND r.status="+status);
        }
        if(!StringUtils.isEmpty(beginTime))
        {
            sb.append(" AND r.createTime >=?");
            param.add(beginTime);
        }
        if(!StringUtils.isEmpty(endTime))
        {
            sb.append(" AND r.createTime <=?");
            param.add(endTime);
        }
        return  sb.toString();

    }

    public PageResponse<OrderRefundDTO> findAll(String orderId, String goodsName, Integer orderGoodsId, Integer memberId, Integer status, String beginTime,
                                                String endTime, Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="OrderRefund as r INNER JOIN OrderGoods as g on r.orderGoodsId=g.id";
        String field ="r.*,g.name,g.goodsId,g.goodsSpecId,g.specName,g.img,g.payAmount";
        String orderBy = "r.Id Desc";
        String condition= getContion(orderId,goodsName,orderGoodsId,memberId,status,beginTime,endTime,param);

        String sql = "SELECT Count(0) FROM "+ table +" WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<OrderRefundDTO> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<OrderRefundDTO>(OrderRefundDTO.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }

        PageResponse<OrderRefundDTO> pageResponse = new PageResponse<>();
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

    public OrderRefund findByOrderGoodsId(Integer memberId,String orderId, Integer orderGoodsId)
    {
        if(memberId<=0)
        {
            throw new IllegalArgumentException("会员为空");
        }
        if(StringUtils.isEmpty(orderId))
        {
            throw new IllegalArgumentException("订单id为空");
        }
        if(orderGoodsId<=0)
        {
            throw new IllegalArgumentException("订单宝贝为空");
        }
        return orderRefundRepository.getRefund(memberId,orderId,orderGoodsId);
    }

    public OrderRefund findByStatus(Integer orderGoodsId)
    {
        return orderRefundRepository.findByStatus(orderGoodsId);
    }

    public String getStatusName(Integer status)
    {
        switch (status)
        {
            case 0:
                return "等待处理";
            case 1:
                return "接受申请";
            case 2:
                return "等待寄送";
            case 3:
                return "同意处理";
            case 4:
                return "退款成功";
            case 5:
                return "拒绝处理";
            case 6:
                return "关闭退款";

        }
        return "";
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
        if (refund.getStatus() != Constants.enumRefundStatus.wait.getValue()) {
            throw new IllegalArgumentException ("非法操作");
        }

        OrderGoods og = orderGoodsManager.findById(refund.getOrderGoodsId());

        this.save(refund, Constants.enumRefundStatus.success.getValue(), 0, description, "");

        jt.update(String.format("UPDATE Goods SET sellCount=sellCount-%d WHERE Id=%d",
                og.getBuyCount(),og.getGoodsId()));

    }
}
