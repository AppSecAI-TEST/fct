package com.fct.promotion.service.business;

import com.fct.core.utils.DateUtils;
import com.fct.promotion.data.entity.Discount;
import com.fct.promotion.data.entity.DiscountProduct;
import com.fct.promotion.interfaces.dto.CouponCodeDTO;
import com.fct.promotion.interfaces.dto.DiscountCouponDTO;
import com.fct.promotion.interfaces.dto.DiscountProductDTO;
import com.fct.promotion.interfaces.dto.OrderProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by jon on 2017/5/13.
 */
@Service
public class DiscountProductDTOManager {

    @Autowired
    DiscountProductManager discountProductManager;

    @Autowired
    DiscountManager discountManager;

    @Autowired
    CouponCodeDTOManager couponCodeDTOManager;

    @Autowired
    private JdbcTemplate jt;

    public List<DiscountProductDTO> findByProduct(List<Integer> productIds, Integer filterNoBegin)
    {
        if (productIds == null || productIds.size() < 1)
        {
            throw new IllegalArgumentException("产品编号列表不能为空");
        }

        List<DiscountProduct> discountProductList = discountProductManager.findByValid(productIds,filterNoBegin);
        if (discountProductList == null || discountProductList.size() < 1)
        {
            return null;
        }
        List<Integer> discountIds = new ArrayList<>();
        for (DiscountProduct p: discountProductList
             ) {
            discountIds.add(p.getDiscountId());
        }
        List<Discount> discountList = discountManager.findByDiscountId(discountIds);
        Map<Integer, Discount> map = new HashMap<>();
        for (Discount dicount:discountList
             ) {
            map.put(dicount.getId(),dicount);
        }

        List<DiscountProductDTO> lst = new ArrayList<>();

        for (DiscountProduct p: discountProductList
                ) {

            DiscountProductDTO dto = new DiscountProductDTO();
            dto.setProductId(p.getProductId());
            dto.setDiscountProduct(p);
            dto.setDiscount(map.get(p.getDiscountId()));

            lst.add(dto);
        }
        return lst;
    }


    public DiscountProductDTO findByProductId(Integer productId)
    {
        String sql = "select p.* from Discount as d inner join DiscountProduct as p on d.id=p.discountid";
        sql += String.format(" WHERE p.productId=%d and d.endtime>='%s' and d.AuditStatus=1 limit 1",
                productId, DateUtils.format(new Date()));

        DiscountProduct product = null;
        try {
             product = jt.queryForObject(sql, new BeanPropertyRowMapper<DiscountProduct>(DiscountProduct.class),
                    new Object[]{});
        }
        catch (Exception exp)
        {

        }

        if(product != null) {
            Discount discount = discountManager.findById(product.getDiscountId());
            DiscountProductDTO dto = new DiscountProductDTO();
            dto.setProductId(productId);
            dto.setDiscount(discount);
            dto.setDiscountProduct(product);
            return dto;
        }
        return null;
    }

    /// <summary>
    /// 订单提交前，根据订单购物商品，获取有效折扣及优惠券
    /// </summary>
    /// <returns></returns>
    public DiscountCouponDTO getDiscountCoupon(Integer memberId, List<OrderProductDTO> lsProduct, String couponCode)
    {
        if (lsProduct == null || lsProduct.size() < 1)
        {
            throw new IllegalArgumentException("产品编号列表不能为空");
        }

        List<Integer> productIds = new ArrayList<>();
        for (OrderProductDTO p:lsProduct
             ) {
            productIds.add(p.getProductId());
        }

        DiscountCouponDTO dc = new DiscountCouponDTO();
        List<OrderProductDTO> discountProductList = discountProductManager.findBySubmitOrder(productIds);
        if (discountProductList == null || discountProductList.size() < 1)
        {
            return null;
        }
        for (OrderProductDTO p:lsProduct
                ) {

            OrderProductDTO dp = single(discountProductList,p.getProductId());
            if (dp != null)
            {
                p.setDiscountId(dp.getDiscountId());
                //查询数据库，特意将discountRate转换成对像discountPrice.避免少加一个model;
                p.setDiscountPrice(p.getRealPrice().multiply(dp.getDiscountPrice()));
                p.setSingleCount(dp.getSingleCount());

                //冗余，为了提交订单判断是否未开始活动和限购数量
                p.setStartTime(dp.getStartTime());
                p.setNotStartCanNotBuy(dp.getNotStartCanNotBuy());
            }
        }

        if(!StringUtils.isEmpty(couponCode)) {
            CouponCodeDTO coupon = couponCodeDTOManager.findByMemberId(memberId, lsProduct, couponCode);
            dc.setCoupon(coupon);
        }
        dc.setDiscount(lsProduct);
        return dc;
    }

    private  OrderProductDTO single(List<OrderProductDTO> lsProduct,Integer productId)
    {
        for (OrderProductDTO p:lsProduct
             ) {
            if(p.getProductId() == productId)
            {
                return p;
            }
        }
        return null;
    }
}
