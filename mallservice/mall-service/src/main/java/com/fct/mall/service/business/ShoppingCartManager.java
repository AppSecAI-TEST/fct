package com.fct.mall.service.business;

import com.fct.common.exceptions.BaseException;
import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsSpecification;
import com.fct.mall.data.entity.ShoppingCart;
import com.fct.mall.data.repository.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            throw new BaseException("库存不足");
        }
        if (goods.getStockCount() < buyCount)
        {
            throw new BaseException("购买量不能大于库存");
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
                throw new BaseException("规格库存不足");
            }
            if (spec.getStockCount() < buyCount)
            {
                throw new BaseException("购买量不能大于规格库存");
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
            throw new BaseException("购买量不能大于库存");
        }
        if (goodsSpecId > 0 && cart.getBuyCount() > spec.getStockCount())
        {
            throw new BaseException("购买量不能大于规格库存");
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

        List<ShoppingCart> result = shoppingCartRepository.findByMemberId(memberId,shopId);
        if (result != null)
        {
            List<ShoppingCart> temp = new ArrayList<>();
            for (ShoppingCart cart:result
                 ) {
                Goods g = goodsManager.findById(cart.getGoodsId());
                if (g.getIsDel() == 1)
                {
                    continue;
                }
                cart.setGoods(g);
                if (cart.getGoodsSpecId() > 0)
                {
                    GoodsSpecification gsp = goodsSpecificationManager.findById(cart.getGoodsSpecId());
                    if (gsp.getGoodsId() != g.getId())
                    {
                        throw new BaseException("非法数据");
                    }
                    List<GoodsSpecification> lsGS = new ArrayList<>();
                    lsGS.add(gsp);
                    g.setSpecification(lsGS);
                }
                temp.add(cart);
            }
            result = temp;
        }

        return result;
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