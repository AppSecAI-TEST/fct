package com.fct.mall.service.business;

import com.fct.core.json.JsonConverter;
import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsSpecification;
import com.fct.mall.data.entity.OrderGoods;
import com.fct.mall.data.repository.OrderGoodsRepository;
import com.fct.mall.interfaces.OrderGoodsDTO;
import com.fct.mall.interfaces.OrderGoodsResponse;
import com.fct.promotion.interfaces.PromotionService;
import com.fct.promotion.interfaces.dto.CouponCodeDTO;
import com.fct.promotion.interfaces.dto.DiscountProductDTO;
import com.fct.promotion.interfaces.dto.OrderProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jon on 2017/5/17.
 */
@Service
public class OrderGoodsManager {

    @Autowired
    private OrderGoodsRepository orderGoodsRepository;

    @Autowired
    private GoodsManager goodsManager;

    @Autowired
    private GoodsSpecificationManager goodsSpecificationManager;

    @Autowired
    private PromotionService promotionService;

    /***
     * 生成订单时调用，前方已作校验
     * */
    public void save(OrderGoods og)
    {
        orderGoodsRepository.save(og);
    }

    public List<OrderGoods> findByOrderId(String orderId)
    {
        if(StringUtils.isEmpty(orderId))
        {
            throw new IllegalArgumentException("订单id为空");
        }
        return orderGoodsRepository.findByOrderId(orderId);
    }

    public OrderGoods findById(Integer id)
    {
        if(id<=0)
        {
            throw new IllegalArgumentException("id为空");
        }
        return orderGoodsRepository.findOne(id);
    }

    public OrderGoodsResponse findFinalGoods(Integer memberId, List<OrderGoodsDTO> lsGoods)
    {
        ////商品 [{"ProductId":"0","RealPrice","700",Count:"1",SizeId:"1"},{循环}]

        if(lsGoods == null || lsGoods.size()<=0)
        {
            return null;
        }
        OrderGoodsResponse goodsDTO = new OrderGoodsResponse();

        List<Integer> goodsIdList = new ArrayList<>();

        for (OrderGoodsDTO cart:lsGoods
                ) {
            goodsIdList.add(cart.getGoodsId());
        }

        List<DiscountProductDTO> lsDiscountGoods = promotionService.findDiscountProductDTO(goodsIdList,1);
        List<OrderGoods> orderGoodsList = new ArrayList<>();

        List<OrderProductDTO> lsProduct = new ArrayList<>();

        for (OrderGoodsDTO cart: lsGoods
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

            OrderGoods orderGoods = new OrderGoods();
            orderGoods.setGoodsId(g.getId());
            if (cart.getSpecId() > 0)
            {
                GoodsSpecification gsp = goodsSpecificationManager.findById(cart.getSpecId());
                if (gsp == null || gsp.getGoodsId() != g.getId())
                {
                    throw new IllegalArgumentException("非法数据");
                }

                orderGoods.setSpecName(gsp.getName());
                orderGoods.setGoodsSpecId(gsp.getId());
                orderGoods.setPrice(gsp.getSalePrice());
            }
            else
            {
                List<GoodsSpecification> lsGS = goodsSpecificationManager.findByGoodsId(g.getId());

                if (lsGS != null && lsGS.size() >0)
                {
                    throw new IllegalArgumentException("订单商品存在规格，您没有选择规格");
                }
                orderGoods.setPrice(g.getSalePrice());
            }
            orderGoods.setPromotionPrice(g.getSalePrice());
            //从当前用户购买的商品中，获取是否有折扣信息
            DiscountProductDTO discount = getDiscount(lsDiscountGoods,cart.getGoodsId());
            if(discount != null)
            {
                BigDecimal realPrice =  discount.getDiscountProduct().getDiscountRate().multiply(orderGoods.getPrice());
                orderGoods.setPromotionPrice(realPrice); //重新计算真实销售价，
            }

            orderGoods.setImg(g.getDefaultImage());
            orderGoods.setName(g.getName());
            orderGoods.setBuyCount(cart.getBuyCount());
            orderGoods.setGoodsId(g.getId());
            orderGoods.setTotalAmount(orderGoods.getPromotionPrice().multiply(new BigDecimal(cart.getBuyCount())));

            orderGoodsList.add(orderGoods);

            OrderProductDTO productDTO = new OrderProductDTO();
            productDTO.setRealPrice(orderGoods.getPrice());
            productDTO.setDiscountPrice(orderGoods.getPromotionPrice());
            productDTO.setCount(orderGoods.getBuyCount());
            productDTO.setProductId(orderGoods.getGoodsId());
            productDTO.setSizeId(orderGoods.getGoodsSpecId());

            lsProduct.add(productDTO);
        }

        goodsDTO.setItems(orderGoodsList);


        //暂时隐藏掉自动获取可用优惠券，测试并发有无影响
        CouponCodeDTO coupon = promotionService.getCouponCodeDTOByOrder(memberId,lsProduct);;
        if (coupon != null)
        {
            goodsDTO.setCouponAmount(coupon.getAmount());
            goodsDTO.setCouponCode(coupon.getCode());
        }

        return goodsDTO;
    }
    
    public DiscountProductDTO getDiscount(List<DiscountProductDTO> lsDiscountGoods, Integer productid)
    {
        if(lsDiscountGoods == null || lsDiscountGoods.size()<1)
        {
            return null;
        }
        for (DiscountProductDTO dp: lsDiscountGoods
             ) {
            if(dp.getProductId() == productid)
            {
                return dp;
            }
        }
        return null;
    }
}
