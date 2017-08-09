package com.fct.promotion.service.business;

import com.fct.core.utils.DateUtils;
import com.fct.promotion.data.entity.*;
import com.fct.promotion.interfaces.dto.OrderProductDTO;
import com.fct.promotion.service.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by jon on 2017/5/13.
 */
@Service
public class OrderManager {

    @Autowired
    private CouponUseLogManager couponUseLogManager;

    @Autowired
    private DiscountUseLogManager discountUseLogManager;

    @Autowired
    private CouponCodeManager couponCodeManager;

    @Autowired
    private CouponPolicyManager couponPolicyManager;

    @Autowired
    private DiscountProductManager discountProductManager;

    @Autowired
    private DiscountManager discountManager;

    @Transactional
    public Integer use(String orderId, Integer memberId, List<OrderProductDTO> products, String couponCode, Integer memberGradeId)
    {
        Integer orderCloseTime = 0;
        check(orderId, memberId, products, couponCode);

        //check discount
        orderCloseTime = this.checkDiscount(products,memberGradeId);

        //check coupon
        CouponCode coupon = checkAndReceiveCouponCode(memberId, products, couponCode);

        coupon.setUseTime(new Date());
        coupon.setStatus(1);
        couponCodeManager.save(coupon);

        //use coupon
        //couponCodeManager.setCodeUsing(coupon.getPolicyId(),coupon.getCode());

        //record
        this.addLog(orderId, coupon, products);

        return orderCloseTime;

    }

    private void check(String orderId, Integer memberId, List<OrderProductDTO> products, String couponCode)
    {
        if (StringUtils.isEmpty(orderId))
        {
            throw new IllegalArgumentException("订单编号不能为空");
        }
        if (memberId < 1)
        {
            throw new IllegalArgumentException("您还没有登录呢");
        }
        if (products == null || products.size() < 1)
        {
            throw new IllegalArgumentException("您还没有选择产品呢");
        }
    }

    private Integer checkDiscount(List<OrderProductDTO> products, Integer memberGradeId)
    {
        int orderCloseTime = 0;
        List<Integer> productIdList = new ArrayList<>();
        List<Integer> discountIdList = new ArrayList<>();

        for (OrderProductDTO obj:products
             ) {
            productIdList.add(obj.getProductId());
            if (!discountIdList.contains(obj.getDiscountId()))
            {
                discountIdList.add(obj.getDiscountId());
            }
        }
        Map<Integer,DiscountProduct> mapDiscountProduct = new HashMap<>();
        Map<Integer,Discount> mapDiscount = new HashMap<>();

        List<DiscountProduct> discountProductList = discountProductManager.findByValid(productIdList,1);
        List<Discount> discountList = discountManager.findByDiscountId(discountIdList);

        if (discountProductList != null)
        {
            for (DiscountProduct obj:discountProductList
                 ) {
                mapDiscountProduct.put(obj.getProductId(),obj);
            }
        }
        if (discountList != null)
        {
            for (Discount obj:discountList
                    ) {
                mapDiscount.put(obj.getId(),obj);
            }
        }

        //产品Id、规格、数量字典
        Map<Integer,Map<Integer,Integer>> dicProductSizeCount = new HashMap<>();
        Map<Integer,Integer> dicProductCount = new HashMap<>();
        for (OrderProductDTO obj:products
             ) {
            if (!dicProductSizeCount.containsKey(obj.getProductId()))
            {
                dicProductCount.put(obj.getProductId(),obj.getCount());

                Map<Integer,Integer> mapSize = new HashMap<>();
                mapSize.put(obj.getSizeId(),obj.getCount());

                dicProductSizeCount.put(obj.getProductId(),mapSize);
            }
            else
            {
                Integer productCount = dicProductCount.get(obj.getProductId());
                dicProductCount.put(obj.getProductId(),productCount+obj.getCount());

                Map<Integer,Integer> mapSize = dicProductSizeCount.get(obj.getProductId());

                if (mapSize !=null && mapSize.containsKey(obj.getSizeId()))
                {
                    Integer sizeId = mapSize.get(obj.getSizeId());
                    mapSize.put(obj.getSizeId(),sizeId+obj.getCount());
                }
                else
                {
                    mapSize = new HashMap<>();
                    mapSize.put(obj.getSizeId(),obj.getCount());

                }
                dicProductSizeCount.put(obj.getProductId(),mapSize);
            }
        }

        for (OrderProductDTO obj:products
             ) {
            if (obj.getDiscountId() < 1)
            {
                continue;
            }

            if (!mapDiscount.containsKey(obj.getDiscountId()))
            {
                throw new IllegalArgumentException("无效的促销折扣活动：" + obj.getDiscountId());
            }

            Discount discount = mapDiscount.get(obj.getDiscountId());
            if (discount.getAuditStatus() != 1)
            {
                throw new IllegalArgumentException("促销折扣未审核：" + discount.getAuditStatus());
            }
            if (discount.getMemberGradeId() > memberGradeId)
            {
                throw new IllegalArgumentException("暂无购买资格：" + discount.getMemberGradeId());
            }
            if (DateUtils.compareDate(discount.getEndTime(),new Date())<0)
            {
                throw new IllegalArgumentException("该促销折扣已过期");
            }
            if (DateUtils.compareDate(discount.getStartTime(),new Date()) >0)
            {
                throw new IllegalArgumentException("该促销折扣已过期或未开始");
            }
            if (!mapDiscountProduct.containsKey(obj.getProductId()))
            {
                throw new IllegalArgumentException("该产品暂无促销折扣信息：" + obj.getProductId());
            }
            DiscountProduct discountProduct = mapDiscountProduct.get(obj.getProductId());

            if (discount.getId() != discountProduct.getDiscountId())
            {
                throw new IllegalArgumentException("折扣促销信息与产品信息不一致");
            }

            if (obj.getRealPrice().multiply(discountProduct.getDiscountRate()).doubleValue() != obj.getDiscountPrice().doubleValue())
            {
                Constants.logger.error(String.format("折后价格不正确,d%,d%,s%", obj.getProductId(), obj.getDiscountId(), discountProduct.getDiscountRate()));
                throw new IllegalArgumentException("折后价格不正确");
            }

            if (discountProduct.getIsValidForSize()) //对规格有效??
            {
                if (discountProduct.getSingleCount() < dicProductSizeCount.get(obj.getProductId()).get(obj.getSizeId()))
                {
                    throw new IllegalArgumentException("超过购买的件数限制");
                }
            }
            else
            {
                if (discountProduct.getSingleCount()>0 &&
                        discountProduct.getSingleCount() < dicProductCount.get(obj.getProductId()))
                {
                    throw new IllegalArgumentException("超过购买的件数限制");
                }
            }
            if (discount.getOrderCloseTime() > 0)
            {
                orderCloseTime = orderCloseTime == 0 ? discount.getOrderCloseTime() : Math.min(orderCloseTime, discount.getOrderCloseTime());
            }
        }



        return orderCloseTime;
    }

    private CouponCode checkAndReceiveCouponCode(Integer memberId, List<OrderProductDTO> products, String couponCode)
    {
        if (StringUtils.isEmpty(couponCode) || couponCode.length() != 8)
        {
            return null;
        }
        CouponCode coupon = couponCodeManager.findByCode(couponCode);
        if (coupon == null)
        {
            throw new IllegalArgumentException("优惠券不存在");
        }
        CouponPolicy policy = couponPolicyManager.findById(coupon.getPolicyId());
        if (policy.getAuditStatus() != 1)
        {
            throw new IllegalArgumentException("优惠券已失效");
        }
        if (DateUtils.compareDate(policy.getStartTime(),new Date())>0 ||
                DateUtils.compareDate(policy.getEndTime(),new Date())<0)
        {
            throw new IllegalArgumentException("优惠券已过期");
        }
        if (coupon.getMemberId() == 0 && coupon.getStatus() == 0)
        {
            couponCodeManager.receiveSystemCouponCode(memberId,policy,coupon);
        }
        if (coupon.getMemberId() != memberId)
        {
            throw new IllegalArgumentException("你没有使用该优惠券的权限");
        }
        if (coupon.getStatus() != 0)
        {
            throw new IllegalArgumentException("优惠券已使用或已失效");
        }

        BigDecimal orderTotalPrice = new BigDecimal(0);
        for (OrderProductDTO product:products
             ) {
            orderTotalPrice.add(product.getRealPrice().multiply(new BigDecimal(product.getCount())));
        }

        if (!StringUtils.isEmpty(policy.getProductIds())) //只针对某些产品
        {
            BigDecimal price = new BigDecimal(0);
            String temp = String.format(",%s,", policy.getProductIds());
            for (OrderProductDTO product:products
                 ) {
                if (temp.contains("," + product.getProductId() + ","))
                {
                    price = price.add(product.getRealPrice().multiply(new BigDecimal(product.getCount())));
                }
            }
            if (policy.getFullAmount().doubleValue() > 0)
            {
                if (price.doubleValue() < policy.getFullAmount().doubleValue()) //小于满额
                {
                    throw new IllegalArgumentException("订单总额小于优惠券满额要求");
                }
            }
            else
            {
                if (price.doubleValue() < policy.getAmount().doubleValue()) //小于面额
                {
                    throw new IllegalArgumentException("订单总额小于优惠券面值，不能使用");
                }
            }
        }
        else if (policy.getFullAmount().doubleValue() > 0) //针对全场且有条件
        {
            if (orderTotalPrice.doubleValue() < policy.getFullAmount().doubleValue()) //小于满额
            {
                throw new IllegalArgumentException("订单总额小于优惠券满额要求");
            }
        }
        else//针对全场
        {
            if (orderTotalPrice.doubleValue() < policy.getAmount().doubleValue()) //小于面额
            {
                throw new IllegalArgumentException("订单总额小于优惠券面值，不能使用");
            }
        }
        return coupon;
    }

    private void addLog(String orderId, CouponCode coupon, List<OrderProductDTO> products)
    {
        if (coupon != null)
        {
            CouponUseLog couponLog = new CouponUseLog();
            couponLog.setOrderId(orderId);
            couponLog.setCreateTime(new Date());
            couponLog.setPolicyId(coupon.getPolicyId());
            couponLog.setCouponCode(coupon.getCode());
            couponUseLogManager.add(couponLog);

            List<DiscountUseLog> lst = new ArrayList<>();
            for (OrderProductDTO obj:products
                 ) {
                if (obj.getDiscountId() != null && obj.getDiscountId() > 0)
                {
                    DiscountUseLog log = new DiscountUseLog();
                    log.setOrderId(orderId);
                    log.setDiscountId(obj.getDiscountId());
                    log.setProductId(obj.getProductId());
                    log.setCreateTime(new Date());

                    lst.add(log);
                }

            }
            if (lst.size() > 0)
            {
                discountUseLogManager.add(lst);
            }
        }
        //折扣日记暂时去掉，测试抢购。
    }
}
