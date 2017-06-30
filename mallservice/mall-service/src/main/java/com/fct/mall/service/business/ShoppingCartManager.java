package com.fct.mall.service.business;

import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsSpecification;
import com.fct.mall.data.entity.OrderGoods;
import com.fct.mall.data.entity.ShoppingCart;
import com.fct.mall.data.repository.ShoppingCartRepository;
import com.fct.promotion.interfaces.PromotionService;
import com.fct.promotion.interfaces.dto.DiscountProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/23.
 * You're all I'm thinking of
 */
@Service
public class ShoppingCartManager {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private GoodsManager goodsManager;

    @Autowired
    private GoodsSpecificationManager goodsSpecificationManager;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private OrderGoodsManager orderGoodsManager;

    //添加或追加到购物车
    public void add(Integer memberId, Integer shopId, Integer goodsId, Integer goodsSpecId, Integer buyCount)
    {
        if (memberId < 1)
        {
            throw new IllegalArgumentException("无此用户");
        }
        if (shopId < 0)
        {
            throw new IllegalArgumentException("没找到商铺");
        }
        if (goodsId < 1)
        {
            throw new IllegalArgumentException("商品不存在");
        }
        if (buyCount < 1)
        {
            throw new IllegalArgumentException("请输入购买数量");
        }

        Goods goods = goodsManager.findById(goodsId);
        if (goods.getStockCount() < 1)
        {
            throw new IllegalArgumentException("库存不足");
        }
        if (goods.getStockCount() < buyCount)
        {
            throw new IllegalArgumentException("购买量不能大于库存");
        }

        GoodsSpecification spec = null;
        if (goodsSpecId > 0)
        {
            spec = goodsSpecificationManager.findById(goodsSpecId);
            if (spec == null)
            {
                throw new IllegalArgumentException("规格有误");
            }
            if (spec.getId() != goods.getId())
            {
                throw new IllegalArgumentException("规格有误");
            }
            if (spec.getStockCount() < 1)
            {
                throw new IllegalArgumentException("规格库存不足");
            }
            if (spec.getStockCount() < buyCount)
            {
                throw new IllegalArgumentException("购买量不能大于规格库存");
            }
        }
        else
        {
            List<GoodsSpecification> lsSpecification = goodsSpecificationManager.findByGoodsId(goodsId);
            if (lsSpecification != null && lsSpecification.size() > 0)
            {
                throw new IllegalArgumentException("此商品为多规格商品，请选择规格");
            }
        }

        //存在修改数量，不存在添加记录
        ShoppingCart cart = shoppingCartRepository.getByMemberId(memberId,shopId,goodsId,goodsSpecId);
        if (cart != null)
        {
            cart.setBuyCount(cart.getBuyCount()+buyCount);
        }
        else
        {
            cart = new ShoppingCart();
            cart.setMemberId(memberId);
            cart.setShopId(shopId);
            cart.setGoodsId(goodsId);
            cart.setGoodsSpecId(goodsSpecId);
            cart.setBuyCount(buyCount);
            cart.setCreateTime(new Date());
        }
        cart.setUpdateTime(new Date());

        if (cart.getBuyCount() > goods.getStockCount())
        {
            throw new IllegalArgumentException("购买量不能大于库存");
        }
        if (goodsSpecId > 0 && cart.getBuyCount() > spec.getStockCount())
        {
            throw new IllegalArgumentException("购买量不能大于规格库存");
        }

        shoppingCartRepository.saveAndFlush(cart);
    }

    public List<ShoppingCart> find(Integer memberId, Integer shopId)
    {
        if (memberId < 1)
        {
            throw new IllegalArgumentException("无此用户");
        }
        if (shopId < 1)
        {
            throw new IllegalArgumentException("没找到商铺");
        }

        List<ShoppingCart> cartList = shoppingCartRepository.findByMemberId(memberId,shopId);
        if(cartList == null || cartList.size()<=0)
        {
            return null;
        }

        List<Integer> goodsIdList = new ArrayList<>();

        for (ShoppingCart cart : cartList
                ) {
            goodsIdList.add(cart.getGoodsId());
        }

        List<DiscountProductDTO> lsDiscountGoods = promotionService.findDiscountProductDTO(goodsIdList, 1);

        if (lsDiscountGoods != null && lsDiscountGoods.size() > 0) {
            for (ShoppingCart cart : cartList
                    ) {
                Goods g = goodsManager.findById(cart.getGoodsId());
                if (g.getIsDel() == 1) {
                    //从购物车中删除
                    shoppingCartRepository.deleteByGoodsId(g.getId());
                    continue;
                }
                OrderGoods orderGoods = new OrderGoods();
                DiscountProductDTO discount = orderGoodsManager.getDiscount(lsDiscountGoods, cart.getGoodsId());
                if (discount != null) {
                    BigDecimal realPrice = discount.getDiscountProduct().getDiscountRate().multiply(g.getSalePrice());
                    orderGoods.setPromotionPrice(realPrice); //重新计算真实销售价，
                }

                if (cart.getGoodsSpecId() > 0) {
                    GoodsSpecification gsp = goodsSpecificationManager.findById(cart.getGoodsSpecId());
                    if (gsp == null || gsp.getGoodsId() != g.getId()) {
                        throw new IllegalArgumentException("非法数据");
                    }
                    orderGoods.setSpecName(gsp.getName());
                    orderGoods.setGoodsSpecId(gsp.getId());
                    orderGoods.setPrice(gsp.getSalePrice());
                } else {
                    List<GoodsSpecification> lsGS = goodsSpecificationManager.findByGoodsId(g.getId());

                    if (lsGS != null && lsGS.size() > 0) {
                        throw new IllegalArgumentException("订单商品存在规格，您没有选择规格");
                    }
                    orderGoods.setPrice(g.getSalePrice());
                }
                orderGoods.setImg(g.getDefaultImage());
                orderGoods.setName(g.getName());
                orderGoods.setTotalAmount(orderGoods.getPromotionPrice().multiply(new BigDecimal(cart.getBuyCount())));

                cart.setGoods(orderGoods);

                if(g.getStatus() != 1)
                {
                    //前端用来判断宝贝状态，下架
                    cart.setGoodsSpecId(-1);
                }
            }
        }

        return cartList;
    }

    public void delete(Integer memberId, Integer shopId, Integer cartId)
    {
        if (memberId < 1)
        {
            throw new IllegalArgumentException("无此用户");
        }
        if (shopId < 1)
        {
            throw new IllegalArgumentException("没找到商铺");
        }

        shoppingCartRepository.delete(memberId,shopId,cartId);
    }
}
