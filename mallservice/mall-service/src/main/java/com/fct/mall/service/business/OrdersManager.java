package com.fct.mall.service.business;

import com.fct.common.exceptions.BaseException;
import com.fct.common.utils.DateUtils;
import com.fct.finance.data.entity.MemberAccount;
import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsSpecification;
import com.fct.mall.data.entity.OrderGoods;
import com.fct.mall.data.entity.Orders;
import com.fct.mall.data.repository.OrdersRepository;
import com.fct.promotion.interfaces.dto.CouponCodeDTO;
import com.fct.promotion.interfaces.dto.DiscountCouponDTO;
import com.fct.promotion.interfaces.dto.OrderProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by jon on 2017/5/16.
 * always loves tutu
 */
@Service
public class OrdersManager {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private GoodsSpecificationManager goodsSpecificationManager;

    @Autowired
    private GoodsManager goodsManager;

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
    public void create(Integer memberId, String userName, Integer shopId, Integer points, BigDecimal accountAmount,
                       List<OrderGoods> lsOrderGoods, String couponCode, String remark)
    {
        if (memberId < 1)
        {
            throw new IllegalArgumentException("用户不存在");
        }
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("用户不存在");
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
                throw new BaseException("订单商品错误");
            }
            if (goods.getStatus() < 1)
            {
                throw new BaseException("订单商品已下架");
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
                    throw new BaseException("订单商品规格错误");
                }
                if (goods.getId() != gs.getGoodsId())
                {
                    throw new BaseException("订单商品规格错误");
                }

                //有规格的价格、佣金、库存
                price = gs.getPrice();
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
                    throw new BaseException("订单商品存在规格，您没有选择规格");
                }
                goods.setSpecification(lsGS);
            }

            if (stockCount < g.getBuyCount())
            {
                throw new BaseException("订单商品库存不足");
            }
            if (price.doubleValue() <= 0)
            {
                throw new BaseException("订单商品有还未开始销售的商品");
            }

            g.setName(goods.getName());
            g.setSpecName(goods.getSpecification() != null ? goods.getSpecification().get(0).getName() : "");
            //单价
            g.setPrice(price);
            //应付金额
            g.setPayAmount(g.getPromotionPrice().subtract(g.getCouponAmount()).multiply(new BigDecimal(g.getBuyCount())));
            //总价
            g.setTotalAmount(price.multiply(new BigDecimal(g.getBuyCount()));

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
                    if(Integer.getInteger(pid) == g.getGoodsId()) {
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
            order.getOrderTime().setExpiresTime(DateUtils.addMinute(new Date(),closeTime));
        }
        else
        {
            order.getOrderTime().setExpiresTime(DateUtils.addDay(new Date(),1));
        }

        //验证
        MemberAccount memberAccount = APIClient.financeService.getMemberAccount(memberId);

        if (memberAccount == null)
        {
            memberAccount = new MemberAccount();
        }
        if (memberAccount.getAvailableAmount().doubleValue() < accountAmount.doubleValue())
        {
            throw new BaseException("使用余额不能大于自己拥有的余额");
        }
        if (memberAccount.getPoints() < points)
        {
            throw new BaseException("使用积分不能大于自己拥有的积分");
        }

        BigDecimal autoPay = accountAmount + (points / 100);
        if (orderCashAmount < autoPay)
        {
            entityContext.Rollback();
            throw new BusinessException("余额与积分不能大于应付");
        }


        //同组支付订单
        order.GroupId = groupPayId;
        order.SupplierId = g.Key;
        order.MemberId = memberId;
        order.UserName = userName;
        order.ShopId = shopId;
        order.Points = points;
        order.AccountAmount = accountAmount;
        order.CashAmount = orderCashAmount - autoPay; //实际现金支付
        order.PayAmount = orderCashAmount; //应付金额
        order.TotalAmount = orderTotalAmount;
        order.CouponCode = coupon;
        order.Status = (int)Constants.EnumOrderStatus.Wait_Buyer_Pay;
        order.Remark = remark;
        order.CreateTime = DateTime.Now;
        entityContext.Add<Orders>(order);
        order.OrderReceiver.OrderId = order.OrderId;
        entityContext.Add<OrderReceiver>(order.OrderReceiver);

        order.OrderTime.OrderId = order.OrderId;
        entityContext.Add<OrderTime>(order.OrderTime);

        string sql = string.Empty;
        int sqlExeCount = 0;
        string goodsIds = string.Empty;
        string goodsSpecIds = string.Empty;
        foreach (OrderGoods orderGoods in orderGoodses)
        {
            //减去规格库存
            if (orderGoods.GoodsSpecId > 0)
            {
                sql += string.Format("UPDATE GoodsSpecification SET StockCount=StockCount-{0} WHERE Id={1} AND StockCount>={0};",
                        orderGoods.BuyCount, orderGoods.GoodsSpecId);

                sqlExeCount += 1;

            }
            //减去产品
            sql += string.Format("UPDATE Goods SET StockCount=StockCount-{0} WHERE Id={1} AND StockCount>={0};",
                    orderGoods.BuyCount, orderGoods.GoodsId);

            ///增加销售量
            sql += string.Format("UPDATE Goods SET SellCount=SellCount+{0} WHERE Id={1};",
                    orderGoods.BuyCount, orderGoods.GoodsId);

            //从购物车中删除,拼接ID
            if (string.IsNullOrEmpty(goodsIds))
            {
                goodsIds = "" + orderGoods.GoodsId;
            }
            else
            {
                goodsIds += "," + orderGoods.GoodsId;
            }

            if (string.IsNullOrEmpty(goodsSpecIds))
            {
                goodsSpecIds = "" + orderGoods.GoodsSpecId;
            }
            else
            {
                goodsSpecIds += "," + orderGoods.GoodsSpecId;
            }

            orderGoods.OrderId = order.OrderId;
            entityContext.Add<OrderGoods>(orderGoods);

            sqlExeCount += 2;
        }

        if (entityContext.ExecuteNonQuery(sql) != sqlExeCount)
        {
            entityContext.Rollback();
            throw new ArgumentException("库存不足。");
        }

        //删除购物车
        entityContext.ExecuteNonQuery(
                string.Format("DELETE ShoppingCart WHERE MemberId={0} AND ShopId={1} GoodsId in ({2}) AND GoodsSpecId in ({3});",
                        memberId, shopId, goodsIds, goodsSpecIds));
    }

    }
}
