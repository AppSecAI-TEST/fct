package com.fct.mall.service.business;

import com.fct.core.json.JsonConverter;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.DateUtils;
import com.fct.core.utils.PageUtil;
import com.fct.core.utils.StringHelper;
import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.interfaces.FinanceService;
import com.fct.mall.data.entity.*;
import com.fct.mall.data.repository.OrdersRepository;
import com.fct.mall.interfaces.OrderGoodsDTO;
import com.fct.mall.interfaces.PageResponse;
import com.fct.member.data.entity.MemberAddress;
import com.fct.member.interfaces.MemberService;
import com.fct.message.interfaces.MessageService;
import com.fct.message.interfaces.model.MQPayRefund;
import com.fct.message.interfaces.model.MQPayTrade;
import com.fct.promotion.interfaces.PromotionService;
import com.fct.promotion.interfaces.dto.CouponCodeDTO;
import com.fct.promotion.interfaces.dto.DiscountCouponDTO;
import com.fct.promotion.interfaces.dto.OrderProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by jon on 2017/5/24.
 * The more I know of you is the more I know I love you
 */
@Service
public class OrdersManager {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private GoodsSpecificationManager goodsSpecificationManager;

    @Autowired
    private GoodsManager goodsManager;

    @Autowired
    private OrderGoodsManager orderGoodsManager;

    @Autowired
    private OrderReceiverManager orderReceiverManager;

    @Autowired
    private OrderRefundManager orderRefundManager;

    @Autowired
    private MessageService messageService;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private MemberService memberService;

    @Autowired
    JdbcTemplate jt;

    public void save(Orders orders)
    {
        ordersRepository.save(orders);
    }

    private OrderProductDTO getSingleProduct(List<OrderProductDTO> lsProduct, Integer pid)
    {
        for (OrderProductDTO p:lsProduct
             ) {
            if(p.getProductId() == pid)
            {
                return p;
            }

        }
        return null;
    }

    private void handleOrderProduct(List<OrderGoodsDTO> lsOrderGoods,List<OrderProductDTO> lsOrderProduct)
    {
        for (OrderGoodsDTO cart: lsOrderGoods
                ) {
            Goods g = goodsManager.findById(cart.getGoodsId());
            if (g.getIsDel() == 1 || g.getStatus() != 1)
            {
                throw new IllegalArgumentException("宝贝不存在。");
            }
            if(g.getStockCount()<cart.getBuyCount())
            {
                throw new IllegalArgumentException("宝贝库存不足。");
            }

            OrderProductDTO p = new OrderProductDTO();
            if (cart.getSpecId() > 0)
            {
                GoodsSpecification gsp = goodsSpecificationManager.findById(cart.getSpecId());
                if (gsp == null || gsp.getGoodsId() != g.getId())
                {
                    throw new IllegalArgumentException("非法数据");
                }
                p.setSizeId(gsp.getId());
                p.setRealPrice(gsp.getSalePrice());
            }
            else
            {
                List<GoodsSpecification> lsGS = goodsSpecificationManager.findByGoodsId(g.getId());

                if (lsGS != null && lsGS.size() >0)
                {
                    throw new IllegalArgumentException("订单商品存在规格，您没有选择规格");
                }
                p.setRealPrice(g.getSalePrice());
            }
            p.setProductId(g.getId());
            p.setCount(cart.getBuyCount());

            lsOrderProduct.add(p);
        }
    }

    @Transactional
    public String create(Integer memberId, String cellPhone, Integer shopId, Integer points, BigDecimal accountAmount,
                         List<OrderGoodsDTO> lsOrderGoods, String couponCode, String remark, Integer receiverId)
    {
        if (memberId < 1)
        {
            throw new IllegalArgumentException("用户不存在");
        }
        if (StringUtils.isEmpty(cellPhone))
        {
            throw new IllegalArgumentException("手机不存在");
        }
        if (shopId < 0)
        {
            throw new IllegalArgumentException("店铺不存在");
        }

        if (lsOrderGoods == null || lsOrderGoods.size() <= 0)
        {
            throw new IllegalArgumentException("没有订单商品");
        }
        if(points<0)
        {
            throw new IllegalArgumentException("积分不正确。");
        }
        if(accountAmount.doubleValue()<0)
        {
            throw new IllegalArgumentException("账户余额为空。");
        }
        if(receiverId<=0)
        {
            throw new IllegalArgumentException("收货地址为空");
        }

        MemberAddress address = memberService.getMemberAddress(receiverId);

        //如有优惠券校验是否有效。异步通知支付结果，如业务正常再次校验优惠券的有效性
        //如非法优惠券订单将关闭交易，通知支付方发起退款。
        //【2】如支付异常订单关闭，通知支付方发起退款并通知促销系统优惠券恢复为使用状态。

        List<OrderProductDTO> lsOrderProduct = new ArrayList<>();

        //处理生成促销商品对像数组
        handleOrderProduct(lsOrderGoods,lsOrderProduct);

        //查询用户是否折扣信息
        couponCode = couponCode.trim();
        DiscountCouponDTO dc = promotionService.getPromotion(memberId,lsOrderProduct,couponCode);
        //当前购买商品中可参与享受优惠的总单价
        BigDecimal couponTotalPrice = new BigDecimal(0);
        CouponCodeDTO cc = null;
        if(dc!=null)
        {
            lsOrderProduct = dc.getDiscount();//宝贝促销信息重新赋值 。
            cc = dc.getCoupon();
        }

        if(cc != null) {
            String[] arrCPId = cc.getProductIds().split(",");
            for (String pid:arrCPId
                    ) {
                OrderProductDTO p = getSingleProduct(lsOrderProduct,Integer.getInteger(pid));

                if (p != null) {
                    couponTotalPrice = couponTotalPrice.add(p.getDiscountPrice().multiply(new BigDecimal(p.getCount())));//p.RealPrice
                }
            }
        }
        String orderId =  StringHelper.generateOrderId();
        BigDecimal orderCashAmount = new BigDecimal(0); //订单应付
        BigDecimal orderTotalAmount = new BigDecimal(0); //订单总价

        String goodsIds = "";
        String goodsSpecIds = "";

        for (OrderGoodsDTO goodsDTO:lsOrderGoods
             ) {
            OrderProductDTO p = getSingleProduct(lsOrderProduct,goodsDTO.getGoodsId());
            Goods g  = goodsManager.findById(goodsDTO.getGoodsId());
            OrderGoods orderGoods = new OrderGoods();

            orderGoods.setName(g.getName());

            if(goodsDTO.getSpecId()>0)
            {
                GoodsSpecification spec = goodsSpecificationManager.findById(goodsDTO.getSpecId());
                orderGoods.setSpecName(spec.getName());
                orderGoods.setPrice(spec.getSalePrice());
                orderGoods.setGoodsSpecId(spec.getId());
                orderGoods.setCommission(spec.getCommission());
                orderGoods.setGoodsSpecId(spec.getId());
            }
            else
            {
                orderGoods.setPrice(g.getSalePrice());
                orderGoods.setCommission(g.getCommission());
                orderGoods.setGoodsSpecId(0);
            }
            orderGoods.setGoodsId(goodsDTO.getGoodsId());
            orderGoods.setPromotionPrice(p != null ? p.getDiscountPrice() : orderGoods.getPrice());
            orderGoods.setBuyCount(goodsDTO.getBuyCount());
            orderGoods.setImg(g.getDefaultImage());
            orderGoods.setCommission(orderGoods.getCommission());
            orderGoods.setOrderId(orderId);

            orderGoods.setCouponAmount(new BigDecimal(0));

            //商品使用优惠券面额
            if (cc != null && couponTotalPrice.doubleValue() > cc.getFullAmount().doubleValue())
            {
                //  购买价/ couponTotalPrice
                String[] arrProductId = cc.getProductIds().split(",");
                for (String pid:arrProductId
                        ) {
                    if (ConvertUtils.toInteger(pid) == orderGoods.getGoodsId()) {
                        BigDecimal amount = orderGoods.getPromotionPrice().multiply(new BigDecimal(orderGoods.getBuyCount()));

                        amount = amount.divide(couponTotalPrice).multiply(cc.getAmount());

                        orderGoods.setCouponAmount(amount.multiply(new BigDecimal(orderGoods.getBuyCount())));
                    }
                }

            }

            //单品应付金额
            orderGoods.setPayAmount(orderGoods.getPromotionPrice().subtract(orderGoods.getCouponAmount()).
                    multiply(new BigDecimal(orderGoods.getBuyCount())));
            //单品总价
            orderGoods.setTotalAmount(orderGoods.getPrice().multiply(new BigDecimal(orderGoods.getBuyCount())));

            orderCashAmount = orderCashAmount.add(orderGoods.getPayAmount());
            orderTotalAmount = orderTotalAmount.add(orderGoods.getTotalAmount());

            //保存订单相关宝贝数据
            orderGoodsManager.save(orderGoods);

            //减去规格库存
            String sql = "";
            if (orderGoods.getGoodsSpecId() > 0)
            {
                sql = String.format("UPDATE GoodsSpecification SET StockCount=StockCount-%d WHERE Id=%d AND StockCount>=%d ",
                        orderGoods.getBuyCount(), orderGoods.getGoodsSpecId(),orderGoods.getBuyCount());

                jt.update(sql);

            }
            //减去产品库存
            sql = String.format("UPDATE Goods SET StockCount=StockCount-%d WHERE Id=%d AND StockCount>=%d ",
                    orderGoods.getBuyCount(), orderGoods.getGoodsId(),orderGoods.getBuyCount());

            jt.update(sql);

            /*增加销售量
            sql += String.format("UPDATE Goods SET SellCount=SellCount+d% WHERE Id=d%;",
                    g.getBuyCount(), g.getGoodsId());*/

            //从购物车中删除,拼接ID
            if (StringUtils.isEmpty(goodsIds))
            {
                goodsIds = "" + orderGoods.getGoodsId();
            }
            else
            {
                goodsIds += "," + orderGoods.getGoodsId();
            }

            if (StringUtils.isEmpty(goodsSpecIds))
            {
                goodsSpecIds = "" + orderGoods.getGoodsSpecId();
            }
            else
            {
                goodsSpecIds += "," + orderGoods.getGoodsSpecId();
            }

        }

        //生成订单号
        Orders order = new Orders();
        order.setOrderId(orderId);

        Integer closeTime = promotionService.useCouponCodeDiscount(order.getOrderId(),order.getMemberId(),
                0,lsOrderProduct,couponCode);

        //关闭时间
        if (closeTime > 0)
        {
            order.setExpiresTime(DateUtils.addMinute(new Date(),closeTime));
        }
        else
        {
            order.setExpiresTime(DateUtils.addDay(new Date(),1));
        }

        //验证
        MemberAccount memberAccount = financeService.getMemberAccount(memberId);
        BigDecimal autoPay = new BigDecimal(0);
        if (memberAccount != null)
        {
            if (memberAccount.getAvailableAmount().doubleValue() < accountAmount.doubleValue()) {
                throw new IllegalArgumentException("使用余额不能大于自己拥有的余额");
            }
            if (memberAccount.getPoints() < points) {
                throw new IllegalArgumentException("使用积分不能大于自己拥有的积分");
            }

            autoPay = accountAmount.add(new BigDecimal(points / 100));
            if (orderCashAmount.doubleValue() < autoPay.doubleValue()) {
                throw new IllegalArgumentException("余额与积分不能大于应付");
            }
        }
        order.setMemberId(memberId);
        order.setCellPhone(cellPhone);
        order.setShopId(shopId);
        order.setPoints(points);
        order.setAccountAmount(accountAmount);
        order.setCashAmount(orderCashAmount.subtract(autoPay)); //实际现金支付
        order.setPayAmount(orderCashAmount);    //应付金额
        order.setTotalAmount(orderTotalAmount);
        order.setCouponCode(couponCode);
        if(order.getCashAmount().doubleValue()==0)
        {
            order.setStatus(Constants.enumOrderStatus.paySuccess.getValue());
        }
        else {
            order.setStatus(Constants.enumOrderStatus.waitPay.getValue());
        }
        order.setRemark(remark);
        order.setCreateTime(new Date());
        ordersRepository.save(order);

        OrderReceiver orderReceiver = new OrderReceiver();
        orderReceiver.setAddress(address.getAddress());
        orderReceiver.setName(address.getName());
        orderReceiver.setPhone(address.getCellPhone());
        orderReceiver.setProvince(address.getProvince());
        orderReceiver.setCity(address.getCityId());
        orderReceiver.setRegion(address.getTownId());
        orderReceiver.setPostCode(address.getPostCode());
        orderReceiver.setOrderId(order.getOrderId());
        //insert、
        orderReceiverManager.save(orderReceiver);
        //删除购物车
        String cartSql = String.format("DELETE FROM ShoppingCart WHERE MemberId=%d AND ShopId=%d AND GoodsId in ("+goodsIds+") ",
                memberId, shopId);
        if(!StringUtils.isEmpty(goodsSpecIds))
        {
            cartSql += " AND GoodsSpecId in ("+ goodsSpecIds +")";
        }
        jt.update(cartSql);

        return order.getOrderId();

    }

    private String getCondition(Integer memberId, String cellPhone, String orderId, Integer shopId, String goodsName,
                                Integer status, String payPaltform, String payOrderId, Integer timeType, String beginTime,
                                String endTime,List<Object> param)
    {
        StringBuilder sb = new StringBuilder();

        if (!StringUtils.isEmpty(orderId)) {
            sb.append(" AND orderId=?");
            param.add(orderId);
        }
        if(!StringUtils.isEmpty(cellPhone))
        {
            sb.append(" AND cellPhone=?");
            param.add(cellPhone);
        }

        if (memberId>0) {
            sb.append(" AND memberId="+memberId);
        }

        if (shopId>0) {
            sb.append(" AND shopId="+shopId);
        }

        if(!StringUtils.isEmpty(goodsName))
        {
            sb.append(" AND orderId In(Select orderId from orderGoods where name like ?)");
            param.add("%"+ goodsName +"%");
        }

        if(!StringUtils.isEmpty(payPaltform))
        {
            sb.append(" AND payPaltform=?");
            param.add(payPaltform);
        }

        if(!StringUtils.isEmpty(payOrderId))
        {
            sb.append(" AND payOrderId=?");
            param.add(payOrderId);
        }

        if (status>-1) {
            sb.append(" AND status="+status);
        }

        String timeColumn = "createTime";
        if (timeType>2) {
            timeColumn = "payTime";
        }
        if (!StringUtils.isEmpty(beginTime)) {
            sb.append(" AND "+ timeColumn +">=?");
            param.add(beginTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            sb.append(" AND "+ timeColumn +"<?");
            param.add(endTime);
        }
        return  sb.toString();
    }

    //获取订单列表
    public PageResponse<Orders> findAll(Integer memberId, String cellPhone, String orderId, Integer shopId, String goodsName,
                                        Integer status, String payPaltform, String payOrderId, Integer timeType, String beginTime,
                                        String endTime, Integer pageIndex, Integer pageSize)
    {
        //定义一个Expression
        //Expression<String> exp = root.get("orderId");
        List<Object> param = new ArrayList<>();

        String table="Orders";
        String field ="*";
        String orderBy = "createTime Desc";
        String condition= getCondition(memberId,cellPhone,orderId,shopId,goodsName,status,payPaltform,payOrderId,
                timeType,beginTime,endTime,param);

        String sql = "SELECT Count(0) FROM Orders WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<Orders> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<Orders>(Orders.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }

        PageResponse<Orders> pageResponse = new PageResponse<>();
        pageResponse.setTotalCount(count);
        pageResponse.setCurrent(end);
        pageResponse.setElements(ls);
        pageResponse.setHasMore(hasmore);

        return pageResponse;
    }

    //获取订单
    public Orders findById(String orderId)
    {
        if (StringUtils.isEmpty(orderId))
        {
            throw new IllegalArgumentException("订单号不能为空");
        }
        Orders order = ordersRepository.findOne(orderId);
        order.setOrderReceiver(orderReceiverManager.findByOrderId(orderId));
        order.setOrderGoods(orderGoodsManager.findByOrderId(orderId));

        return order;
    }

    //后台管理员设置为已支付（线下行为）
    public void offPaySuccess(String orderId, String payPlatform, Integer operatorId)
    {
        if (StringUtils.isEmpty(orderId))
        {
            throw new IllegalArgumentException("订单号不能为空");
        }
        if (StringUtils.isEmpty(payPlatform))
        {
            throw new IllegalArgumentException("支付方式不能为空");
        }

        //多订单处理
        Orders order = ordersRepository.findOne(orderId);
        if (order == null)
        {
            throw new IllegalArgumentException("订单号不能为空");
        }
        if (order.getStatus() != Constants.enumOrderStatus.waitPay.getValue())
        {
            throw new IllegalArgumentException("不能执行此操作");
        }
        order.setStatus(Constants.enumOrderStatus.paySuccess.getValue());
        order.setPayPlatform(payPlatform);
        order.setPayTime(new Date()); //设置支付时间
        order.setUpdateTime(new Date());
        order.setExpiresTime(null);
        order.setOperatorId(order.getOperatorId() + "paysuccess:"+operatorId+",");
        ordersRepository.saveAndFlush(order);

        //设置销售量
        List<OrderGoods> lsOrderGoods= orderGoodsManager.findByOrderId(orderId);
        for (OrderGoods g: lsOrderGoods
             ) {
            jt.update(String.format("UPDATE Goods SET PayCount=PayCount+%d,SellCount=SellCount+%d WHERE Id=%d",
                    g.getBuyCount(),g.getBuyCount(),g.getGoodsId()));
        }
    }

    public void updatePayPlatform(String orderId,String platform) {

        Orders orders = ordersRepository.findOne(orderId);
        orders.setPayPlatform(platform);
        orders.setUpdateTime(new Date());
        ordersRepository.save(orders);
    }

    //设置为已取消
    public void cancel(String orderId,Integer memberId,Integer operatorId)
    {
        if (StringUtils.isEmpty(orderId))
        {
            throw new IllegalArgumentException("订单号不能为空");
        }

        Orders order = ordersRepository.findOne(orderId);
        if (order == null)
        {
            throw new IllegalArgumentException("订单号不能为空");
        }
        if(order.getMemberId() != memberId)
        {
            throw new IllegalArgumentException("非法操作");
        }

        if (order.getStatus() != Constants.enumOrderStatus.waitPay.getValue())
        {
            throw new IllegalArgumentException("不能执行此操作");
        }

        order.setStatus(Constants.enumOrderStatus.close.getValue());
        order.setUpdateTime(new Date());
        order.setExpiresTime(new Date());
        order.setOperatorId(order.getOperatorId() + "cancel:"+operatorId+",");
        ordersRepository.saveAndFlush(order);

        //恢复库存
        List<OrderGoods> lsOrderGoods= orderGoodsManager.findByOrderId(orderId);
        for (OrderGoods g: lsOrderGoods
                ) {
            //减去规格库存
            if (g.getGoodsSpecId() > 0)
            {
                jt.update("UPDATE GoodsSpecification SET StockCount=StockCount+" + g.getBuyCount() + " WHERE Id=" + g.getGoodsSpecId());

            }
            //减去产品
            jt.update("UPDATE Goods SET StockCount=StockCount+" + g.getBuyCount() + " WHERE Id=" + g.getGoodsId());
        }
    }

    @Transactional
    //设置为已发货
    public void saveDeliver(String orderId, String expressPlatform, String expressNo, Integer operatorId)
    {
        if (StringUtils.isEmpty(orderId))
        {
            throw new IllegalArgumentException("订单号不能为空");
        }
        if (StringUtils.isEmpty(expressPlatform))
        {
            throw new IllegalArgumentException("物流不能为空");
        }
        if (StringUtils.isEmpty(expressNo))
        {
            throw new IllegalArgumentException("物流单号不能为空");
        }
        //设置发货
        Orders order = ordersRepository.findOne(orderId);
        if (order == null)
        {
            throw new IllegalArgumentException("订单不存在");
        }
        if (order.getStatus() != Constants.enumOrderStatus.paySuccess.getValue())
        {
            throw new IllegalArgumentException("不能执行此操作");
        }
        order.setStatus(Constants.enumOrderStatus.delivered.getValue());
        order.setOperatorId(order.getOperatorId() + "deliver:"+operatorId+",");
        order.setUpdateTime(new Date());
        order.setFinishTime(DateUtils.addDay(new Date(),12));
        ordersRepository.save(order);

        OrderReceiver or = orderReceiverManager.findByOrderId(orderId);
        or.setExpressNO(expressNo);
        or.setExpressPlatform(expressPlatform);
        or.setDeliveryTime(new Date());
        orderReceiverManager.save(or);

        //发送短信提醒客户。
        //APIClient.messageService.sendSMS();

    }

    ///延长订单取消时间
    public void delayExpiresTime(String orderId, Integer hour, Integer operatorId)
    {
        if (StringUtils.isEmpty(orderId))
        {
            throw new IllegalArgumentException("订单号不能为空");
        }
        if (hour < 1)
        {
            throw new IllegalArgumentException("增加天数不能少于1");
        }

        Orders order = ordersRepository.findOne(orderId);
        if (order == null)
        {
            throw new IllegalArgumentException("订单不存在");
        }
        if (order.getStatus() != Constants.enumOrderStatus.waitPay.getValue())
        {
            throw new IllegalArgumentException("订单不能执行此操作");
        }
        //延长订单取消时间
        order.setExpiresTime(DateUtils.addHour(order.getExpiresTime(),hour));
        order.setOperatorId(order.getOperatorId() + "delayOrderCloseTime:"+operatorId+",");
        ordersRepository.saveAndFlush(order);
    }


    ///延长订单收货时间
    public void delayFinishTime(String orderId, Integer day, Integer operatorId)
    {
        if (StringUtils.isEmpty(orderId))
        {
            throw new IllegalArgumentException("订单号不能为空");
        }
        if (day < 1)
        {
            throw new IllegalArgumentException("增加天数不能少于1");
        }

        Orders order = ordersRepository.findOne(orderId);
        if (order == null)
        {
            throw new IllegalArgumentException("订单不存在");
        }
        if (order.getStatus() != Constants.enumOrderStatus.delivered.getValue())
        {
            throw new IllegalArgumentException("订单不能执行此操作");
        }
        //延长订单收货时间
        order.setFinishTime(DateUtils.addDay(order.getFinishTime(),day));
        order.setOperatorId(order.getOperatorId() + "delayFinishTime:"+operatorId+",");
        ordersRepository.saveAndFlush(order);
    }


    /// <summary>
    /// 支付系统响应业务方进行业务处理。
    /// </summary>
    public void paySuccess(String orderId, String payOrderId, String payPlatform, Integer payStatus, String payTime)
    {
        if (StringUtils.isEmpty(orderId))
        {
            throw new IllegalArgumentException("订单号不能为空");
        }
        if (StringUtils.isEmpty(payPlatform))
        {
            throw new IllegalArgumentException("支付方式不能为空");
        }

        Orders order = ordersRepository.findOne(orderId);
        if (order == null)
        {
            throw new IllegalArgumentException("订单号不能为空");
        }

        //异常或关闭订单处理退款
        if (payStatus == 1000 || order.getStatus() == Constants.enumOrderStatus.close.getValue())
        {
            //如果是支付异常就退优惠券
            promotionService.cancelUseCouponCode(order.getCouponCode());

            //设置取消时间
            order.setStatus(Constants.enumOrderStatus.close.getValue());
            order.setUpdateTime(new Date());
            order.setExpiresTime(new Date());
            ordersRepository.saveAndFlush(order);

            List<OrderGoods> lsOrderGoods= orderGoodsManager.findByOrderId(orderId);
            for (OrderGoods g: lsOrderGoods
                    ) {
                //减去规格库存
                if (g.getGoodsSpecId() > 0)
                {
                    jt.update("UPDATE GoodsSpecification SET StockCount=StockCount+" + g.getBuyCount() + " WHERE Id=" + g.getGoodsSpecId());
                }
                //减去产品
                jt.update("UPDATE Goods SET StockCount=StockCount+" + g.getBuyCount() + " WHERE Id=" + g.getGoodsId());
            }
            //系统发起退款
            List<MQPayRefund> lsRefund = orderRefundManager.payException(order.getMemberId(),order,payOrderId,lsOrderGoods);

            sendPayTradeMessage(payOrderId,orderId,1000,lsRefund);
        }
        else
        {
            if (order.getStatus() != Constants.enumOrderStatus.waitPay.getValue())
            {
                throw new IllegalArgumentException("不能执行此操作");
            }
            //设置支付时间
            order.setPayOrderId(payOrderId);
            order.setPayPlatform(payPlatform);
            order.setPayTime(DateUtils.parseString(payTime));
            order.setExpiresTime(null);
            order.setStatus(Constants.enumOrderStatus.paySuccess.getValue());

            ordersRepository.saveAndFlush(order);

            //设置销售量
            List<OrderGoods> lsOrderGoods= orderGoodsManager.findByOrderId(orderId);
            for (OrderGoods g: lsOrderGoods
                    ) {
                    jt.update(String.format("UPDATE Goods SET payCount=payCount+%d,sellCount=sellCount+%d WHERE Id=%d",
                    g.getBuyCount(),g.getBuyCount(),g.getGoodsId()));
            }

            //sendPayTradeMessage(payOrderId,orderId,200,null);
        }
    }

    void sendPayTradeMessage(String payOrderId,String orderId,Integer tradeState,List<MQPayRefund> lsRefund)
    {
        //String typeId, String targetModule, String sourceAppName, String jsonBody, String remark

        MQPayTrade result = new MQPayTrade();
        result.setPay_orderid(payOrderId);
        result.setRefund(lsRefund);
        result.setTrade_id(orderId);
        result.setTrade_type("buy");
        result.setTrade_status(tradeState); //200:success,1000:fail
        result.setDesc("");
        messageService.send("mq_paytrade","MQPayTrade","com.fct.mallservice",JsonConverter.toJson(result),"购买商品订单处理结果");
    }

    public void handleExpire()
    {
        String sql = String.format("select * from Orders where status=0 and expiresTime<'%s'",DateUtils.format(new Date()));
        List<Orders> ls = jt.query(sql, new Object[]{}, new BeanPropertyRowMapper<Orders>(Orders.class));

        for (Orders o:ls
             ) {
            o.setUpdateTime(new Date());
            o.setStatus(Constants.enumOrderStatus.close.getValue());
            ordersRepository.save(o);

            //恢复库存
            List<OrderGoods> lsOrderGoods= orderGoodsManager.findByOrderId(o.getOrderId());
            for (OrderGoods g: lsOrderGoods
                    ) {
                //减去规格库存
                if (g.getGoodsSpecId() > 0)
                {
                    jt.update("UPDATE GoodsSpecification SET StockCount=StockCount+" + g.getBuyCount() + " WHERE Id=" + g.getGoodsSpecId());

                }
                //减去产品
                jt.update("UPDATE Goods SET StockCount=StockCount+" + g.getBuyCount() + " WHERE Id=" + g.getGoodsId());
            }
        }

    }

    private void setFinishStatus(Orders orders)
    {
        if(orders.getStatus() != Constants.enumOrderStatus.delivered.getValue())
        {
            throw new IllegalArgumentException("非法操作");
        }
        orders.setStatus(Constants.enumOrderStatus.finished.getValue());
        orders.setUpdateTime(new Date());
        ordersRepository.save(orders);

        //赠送积分
        //String tradeId,String tradeType,Integer memberId,Integer points
        Integer points = orders.getCashAmount().intValue();
        if(points>0) {
            financeService.giftPoints(orders.getOrderId(), "buy", orders.getMemberId(), points);
        }
    }

    public void finishByMember(Integer memberid,String orderId)
    {
        Orders orders = ordersRepository.findOne(orderId);
        if(orders.getMemberId() != memberid)
        {
            throw new IllegalArgumentException("非法操作，用户不合法。");
        }
        setFinishStatus(orders);
    }

    public void finishTask()
    {
        String sql = String.format("SELECT * FROM Orders WHERE Status=%d AND finishTime<'%s'",
                Constants.enumOrderStatus.delivered.getValue(),DateUtils.format(new Date()));
        List<Orders> ls = jt.query(sql, new Object[]{}, new BeanPropertyRowMapper<Orders>(Orders.class));

        for (Orders order:ls
             ) {
            setFinishStatus(order);
        }
    }

}
