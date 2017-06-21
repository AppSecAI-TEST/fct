package com.fct.mall.service.business;

import com.fct.common.json.JsonConverter;
import com.fct.common.utils.DateUtils;
import com.fct.common.utils.PageUtil;
import com.fct.finance.data.entity.MemberAccount;
import com.fct.mall.data.entity.*;
import com.fct.mall.data.repository.OrdersRepository;
import com.fct.mall.interfaces.PageResponse;
import com.fct.message.interfaces.MessageService;
import com.fct.message.model.MQPayRefund;
import com.fct.message.model.MQPayTrade;
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
    JdbcTemplate jt;

    public void save(Orders orders)
    {
        ordersRepository.saveAndFlush(orders);
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

    @Transactional
    public String create(Integer memberId, String cellPhone, Integer shopId, Integer points, BigDecimal accountAmount,
                       List<OrderGoods> lsOrderGoods, String couponCode, String remark,OrderReceiver orderReceiver)
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

        //如有优惠券校验是否有效。异步通知支付结果，如业务正常再次校验优惠券的有效性
        //如非法优惠券订单将关闭交易，通知支付方发起退款。
        //【2】如支付异常订单关闭，通知支付方发起退款并通知促销系统优惠券恢复为使用状态。

        //判断是否有优惠券，如果有和折扣信息一起发送给优惠系统
        Goods goods = null;
        BigDecimal price = new BigDecimal(0);
        BigDecimal commission = new BigDecimal(0);
        Integer stockCount = 0;
        BigDecimal orderCashAmount = new BigDecimal(0);
        BigDecimal orderTotalAmount = new BigDecimal(0);
        couponCode = couponCode.trim();

        List<OrderProductDTO> lsOrderProduct = new ArrayList<>();

        for (OrderGoods g: lsOrderGoods
             ) {
            if (g.getGoodsId() < 1)
            {
                throw new IllegalArgumentException("订单商品错误");
            }
            if (g.getBuyCount() < 1)
            {
                throw new IllegalArgumentException("购买商品数量错误");
            }

            //获取商品
            goods = goodsManager.findById(g.getGoodsId());
            if (goods == null)
            {
                throw new IllegalArgumentException("订单商品错误");
            }
            if (goods.getStatus() < 1)
            {
                throw new IllegalArgumentException("订单商品已下架");
            }
            //无规格的价格、佣金、库存
            price = goods.getSalePrice();
            commission = goods.getCommission();
            stockCount = goods.getStockCount();

            //获取商品规格
            if (g.getGoodsSpecId() > 0)
            {
                GoodsSpecification gs = goodsSpecificationManager.findById(g.getGoodsSpecId());
                if (gs == null)
                {
                    throw new IllegalArgumentException("订单商品规格错误");
                }
                if (goods.getId() != gs.getGoodsId())
                {
                    throw new IllegalArgumentException("订单商品规格错误");
                }

                //有规格的价格、佣金、库存
                price = gs.getSalePrice();
                commission = gs.getCommission();
                stockCount = gs.getStockCount();

                List<GoodsSpecification> lsGS = new ArrayList<>();
                lsGS.add(gs);

                goods.setSpecification(lsGS);

            }
            else
            {
                List<GoodsSpecification> lsGS = goodsSpecificationManager.findByGoodsId(g.getGoodsId());

                if (lsGS != null && lsGS.size() >0)
                {
                    throw new IllegalArgumentException("订单商品存在规格，您没有选择规格");
                }
                goods.setSpecification(lsGS);
            }

            if (stockCount < g.getBuyCount())
            {
                throw new IllegalArgumentException("订单商品库存不足");
            }
            if (price.doubleValue() <= 0)
            {
                throw new IllegalArgumentException("订单商品有还未开始销售的商品");
            }

            g.setName(goods.getName());
            if(goods.getSpecification() != null) {
                g.setSpecName(goods.getSpecification().get(0).getName());
                g.setImg(goods.getSpecification().get(0).getImage());
            }
            //单价
            g.setPrice(price);
            //应付金额
            g.setPayAmount(g.getPromotionPrice().subtract(g.getCouponAmount()).multiply(new BigDecimal(g.getBuyCount())));
            //总价
            g.setTotalAmount(price.multiply(new BigDecimal(g.getBuyCount())));

            OrderProductDTO p = new OrderProductDTO();
            p.setProductId(g.getGoodsId());
            p.setRealPrice(price);
            p.setCount(g.getBuyCount());

            lsOrderProduct.add(p);

            //通过促销接口获取 购买商品打折信息。
        }

        //查询用户是否折扣信息
        DiscountCouponDTO dc = APIClient.promotionService.getPromotion(memberId,lsOrderProduct,couponCode);
        //当前购买商品中可参与享受优惠的总单价
        BigDecimal couponTotalPrice = new BigDecimal(0);
        CouponCodeDTO cc = null;
        if(dc!=null)
        {
            lsOrderProduct = dc.getDiscount();
            cc = dc.getCoupon();
        }

        if(cc != null) {
            String[] arrCPId = cc.getProductIds().split(",");
            for (String pid:arrCPId
                 ) {
                OrderProductDTO p = getSingleProduct(lsOrderProduct,Integer.getInteger(pid));

                if (p != null) {
                    couponTotalPrice.add(p.getDiscountPrice().multiply(new BigDecimal(p.getCount())));//p.RealPrice
                }
            }
        }

        //如有使用账户余额或积分，校验是否满足

        //异步通知结果时支付系统会进行校验，如异常同上【2】。

        //重新计算订单总金额及应付金额是否与前端传过来一致。
        List<OrderGoods> lsGoods = new ArrayList<>();
        for (OrderGoods g: lsOrderGoods
             ) {

            OrderProductDTO p = getSingleProduct(lsOrderProduct,g.getGoodsId());
            g.setPromotionPrice(p.getDiscountPrice());

            //商品使用优惠券面额
            if (cc != null && couponTotalPrice.doubleValue() > cc.getFullAmount().doubleValue())
            {
                //  购买价/ couponTotalPrice
                String[] arrProductId = cc.getProductIds().split(",");
                for (String pid:arrProductId
                     ) {
                    if (Integer.getInteger(pid) == g.getGoodsId()) {
                        BigDecimal couponAmount = g.getPromotionPrice().multiply(new BigDecimal(g.getBuyCount()));

                        couponAmount = couponAmount.divide(couponTotalPrice).multiply(cc.getAmount());

                        g.setCouponAmount(couponAmount.multiply(new BigDecimal(g.getBuyCount())));
                    }
                }

            }
            //单品应付金额
            g.setPayAmount(g.getPromotionPrice().subtract(g.getCouponAmount()).multiply(new BigDecimal(g.getBuyCount())));
            //单品总价
            g.setTotalAmount(g.getPrice().multiply(new BigDecimal(g.getBuyCount())));
            //订单应付
            orderCashAmount = orderCashAmount.add(g.getPayAmount());
            //订单总价
            orderTotalAmount = orderTotalAmount.add(g.getTotalAmount());

            lsGoods.add(g);

        }

        Orders order = new Orders();
        //生成订单号
        order.setOrderId("");
        Integer closeTime = APIClient.promotionService.useCouponCodeDiscount(order.getOrderId(),order.getMemberId(),
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
        MemberAccount memberAccount = APIClient.financeService.getMemberAccount(memberId);

        if (memberAccount == null)
        {
            memberAccount = new MemberAccount();
        }
        if (memberAccount.getAvailableAmount().doubleValue() < accountAmount.doubleValue())
        {
            throw new IllegalArgumentException("使用余额不能大于自己拥有的余额");
        }
        if (memberAccount.getPoints() < points)
        {
            throw new IllegalArgumentException("使用积分不能大于自己拥有的积分");
        }

        BigDecimal autoPay = accountAmount.add(new BigDecimal(points / 100));
        if (orderCashAmount.doubleValue() < autoPay.doubleValue())
        {
            throw new IllegalArgumentException("余额与积分不能大于应付");
        }
        order.setMemberId(memberId);
        order.setCellPhone(cellPhone);
        order.setShopId(shopId);
        order.setPoints(points);
        order.setAccountAmount(accountAmount);
        order.setCashAmount(orderCashAmount.divide(autoPay)); //实际现金支付
        order.setPayAmount(orderCashAmount);    //应付金额
        order.setTotalAmount(orderTotalAmount);
        order.setCouponCode(couponCode);
        order.setStatus(Constants.enumOrderStatus.waitPay.getValue());
        order.setRemark(remark);
        order.setCreateTime(new Date());
        ordersRepository.save(order);

        orderReceiver.setOrderId(order.getOrderId());
        //insert、
        orderReceiverManager.save(orderReceiver);

        String sql = "";
        int sqlExeCount = 0;
        String goodsIds = "";
        String goodsSpecIds = "";
        for (OrderGoods g:lsGoods
             ) {

            //减去规格库存
            if (g.getGoodsSpecId() > 0)
            {
                sql += String.format("UPDATE GoodsSpecification SET StockCount=StockCount-%d WHERE Id=%d AND StockCount>=%d;",
                        g.getBuyCount(), g.getGoodsSpecId(),g.getBuyCount());

                sqlExeCount += 1;

            }
            //减去产品库存
            sql += String.format("UPDATE Goods SET StockCount=StockCount-%d WHERE Id=%d AND StockCount>=%d;",
                    g.getBuyCount(), g.getGoodsId(),g.getBuyCount());

            /*增加销售量
            sql += String.format("UPDATE Goods SET SellCount=SellCount+d% WHERE Id=d%;",
                    g.getBuyCount(), g.getGoodsId());*/

            //从购物车中删除,拼接ID
            if (StringUtils.isEmpty(goodsIds))
            {
                goodsIds = "" + g.getGoodsId();
            }
            else
            {
                goodsIds += "," + g.getGoodsId();
            }

            if (StringUtils.isEmpty(goodsSpecIds))
            {
                goodsSpecIds = "" + g.getGoodsSpecId();
            }
            else
            {
                goodsSpecIds += "," + g.getGoodsSpecId();
            }

            g.setOrderId(order.getOrderId());
            //add orderGoods
            orderGoodsManager.save(g);

            sqlExeCount += 1;
        }
        if (jt.update(sql) != sqlExeCount)
        {
            throw new IllegalArgumentException("库存不足。");
        }
        //删除购物车
        jt.execute(String.format("DELETE ShoppingCart WHERE MemberId=%d AND ShopId=%d GoodsId in (%s) AND GoodsSpecId in (%s);",
                memberId, shopId, goodsIds, goodsSpecIds));

        return order.getOrderId();

    }

    private String getCondition(Integer memberId, String cellPhone, String orderId, Integer shopId, String goodsName,
                                Integer status, String payPaltform, String payOrderId, Integer timeType, String beginTime,
                                String endTime,List<Object> param)
    {
        String condition = "";

        if (!StringUtils.isEmpty(orderId)) {
            condition += " AND orderId=?";
            param.add(orderId);
        }
        if(!StringUtils.isEmpty(cellPhone))
        {
            condition += " AND cellPhone=?";
            param.add(cellPhone);
        }

        if (memberId>0) {
            condition += " AND memberId="+memberId;
        }

        if (shopId>0) {
            condition += " AND shopId="+shopId;
        }

        if(!StringUtils.isEmpty(goodsName))
        {
            condition += "AND orderId In(Select orderId from orderGoods where name like ?)";
            param.add("%"+ goodsName +"%");
        }

        if(!StringUtils.isEmpty(payPaltform))
        {
            condition += " AND payPaltform=?";
            param.add(payPaltform);
        }

        if(!StringUtils.isEmpty(payOrderId))
        {
            condition += " AND payOrderId=?";
            param.add(payOrderId);
        }

        if (status>-1) {
            condition += " AND status="+status;
        }

        String timeColumn = "createTime";
        if (timeType>2) {
            timeColumn = "payTime";
        }
        if (!StringUtils.isEmpty(beginTime)) {
            condition += " AND "+ timeColumn +">=?";
            param.add(beginTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            condition += " AND "+ timeColumn +"<?";
            param.add(endTime);
        }
        return  condition;
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
            APIClient.promotionService.cancelUseCouponCode(order.getCouponCode());

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

            sendPayTradeMessage(payOrderId,orderId,200,null);
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
        APIClient.messageService.send("mq_paytrade","MQPayTrade","com.fct.mallservice",JsonConverter.toJson(result),"购买商品订单处理结果");
    }

    public void add(){
        messageService.send("hello", "hello", "hello", "hello", "hello");
        System.out.println("print hello");
    }
}
