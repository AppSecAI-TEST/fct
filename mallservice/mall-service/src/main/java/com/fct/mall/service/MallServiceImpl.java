package com.fct.mall.service;

import com.fct.mall.data.entity.*;
import com.fct.mall.interfaces.*;
import com.fct.mall.service.business.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by jon on 2017/5/16.
 */
@Service("mallService")
public class MallServiceImpl implements MallService {

    @Autowired
    private GoodsManager goodsManager;

    @Autowired
    private GoodsCategoryManager goodsCategoryManager;

    @Autowired
    private GoodsGradeManager goodsGradeManager;

    @Autowired
    private GoodsMaterialManager goodsMaterialManager;

    @Autowired
    private OrderCommentManager orderCommentManager;

    @Autowired
    private OrderGoodsManager orderGoodsManager;

    @Autowired
    private OrderReceiverManager orderReceiverManager;

    @Autowired
    private OrderRefundManager orderRefundManager;

    @Autowired
    private OrdersManager ordersManager;

    @Autowired
    private ShoppingCartManager shoppingCartManager;

    public PageResponse<Goods> findGoods(String name, String categoryCode, Integer gradeId, Integer materialId,
                                         Integer artistId, Integer minVolume, Integer maxVolume, Integer status,
                                         Integer pageIndex, Integer pageSize) {
        return goodsManager.find(name, categoryCode, gradeId, materialId, artistId, minVolume, maxVolume, status,
                pageIndex, pageSize);
    }

    public Goods getGoods(Integer id) {
        return goodsManager.findById(id);
    }

    public List<Goods> findGoodsByIds(String ids) {
        return goodsManager.findByIds(ids);
    }

    public void saveGoods(Goods goods) {
        goodsManager.save(goods);
    }

    public void setGoodsSortIndex(Integer id, Integer sortIndex) {
        goodsManager.updateSortIndex(id, sortIndex);
    }

    public void setGoodsStatus(Integer id, Integer status) {
        goodsManager.updateStatus(id, status);
    }

    public void deleteGoods(Integer id) {
        goodsManager.delete(id);
    }

    public List<GoodsCategory> findGoodsCategory(Integer parentId, String name, String code) {
        return goodsCategoryManager.findAll(name, code, parentId);
    }

    public GoodsCategory getGoodsCategory(Integer id) {
        return goodsCategoryManager.findById(id);
    }

    public void saveGoodsCategory(GoodsCategory category) {
        goodsCategoryManager.save(category);
    }

    public void setGoodsCategorySortIndex(Integer id, Integer sortIndex) {
        goodsCategoryManager.saveSortIndex(id, sortIndex);
    }

    public void deleteGoodsCategory(Integer id) {
        goodsCategoryManager.delete(id);
    }

    public List<GoodsGrade> findGoodsGrade() {
        return goodsGradeManager.findAll("");
    }

    public GoodsGrade getGoodsGrade(Integer id) {
        return goodsGradeManager.findById(id);
    }

    public void saveGoodsGrade(GoodsGrade grade) {
        goodsGradeManager.save(grade);
    }

    public void setGoodsGradeSortIndex(Integer id, Integer sortIndex) {
        goodsGradeManager.saveSortIndex(id, sortIndex);
    }

    public void deleteGoodsGrade(Integer id) {
        goodsGradeManager.delete(id);
    }

    public void saveShoppingCart(Integer memberId, Integer shopId, Integer goodsId, Integer goodsSpecId, Integer buyCount) {
        shoppingCartManager.add(memberId, shopId, goodsId, goodsSpecId, buyCount);
    }

    public List<ShoppingCart> findShoppingCart(Integer memberId, Integer shopId) {
        return shoppingCartManager.find(memberId, shopId);
    }

    public void deleteShoppingCart(Integer memberId, Integer shopId, Integer cartId) {
        shoppingCartManager.delete(memberId, shopId, cartId);
    }

    public int getShoopingCartCount(Integer memberId)
    {
        return shoppingCartManager.getCount(memberId);
    }

    public OrderGoodsResponse getSubmitOrderGoods(Integer memberId, List<OrderGoodsDTO> lsGoods)
    {
        return orderGoodsManager.findFinalGoods(memberId,lsGoods);
    }

    public String createOrder(Integer memberId, String userName, Integer shopId, Integer points, BigDecimal accountAmount,
                              List<OrderGoodsDTO> lsOrderGoods, String couponCode, String remark, Integer receiverId) {
        return ordersManager.create(memberId, userName, shopId, points, accountAmount,
                lsOrderGoods, couponCode, remark, receiverId);
    }

    public PageResponse<Orders> findOrders(Integer memberId, String cellPhone, String orderId, Integer shopId, String goodsName,
                                           Integer status, String payPlatform, String payOrderId, Integer timeType, String beginTime,
                                           String endTime, Integer pageIndex, Integer pageSize) {
        return ordersManager.findAll(memberId, cellPhone, orderId, shopId, goodsName, status, payPlatform, payOrderId,
                timeType, beginTime, endTime, pageIndex, pageSize);
    }

    public Orders getOrders(String orderId) {
        return ordersManager.findById(orderId);
    }

    public void offPaySuccess(String orderId, String payPlatform, Integer operatorId) {
        ordersManager.offPaySuccess(orderId, payPlatform, operatorId);
    }

    public void updateOrderPayPlatform(String orderId,String payPlatform)
    {
        ordersManager.updatePayPlatform(orderId,payPlatform);
    }

    public void orderExpiredTask()
    {
        ordersManager.handleExpire();
    }

    public void cancelOrders(String orderId, Integer memberId, Integer operatorId) {
        ordersManager.cancel(orderId, memberId, operatorId);
    }

    public void orderDeliver(String orderId, String expressPlatform, String expressNo, Integer operatorId) {
        ordersManager.saveDeliver(orderId, expressPlatform, expressNo, operatorId);
    }

    public List<OrderGoods> findOrderGoods(String orderId) {
        return orderGoodsManager.findByOrderId(orderId);
    }

    public void saveOrderReciver(OrderReceiver or) {
        orderReceiverManager.save(or);
    }

    public OrderReceiver getOrderReceiver(String orderId) {
        return orderReceiverManager.findByOrderId(orderId);
    }

    public void delayExpiresTime(String orderId, Integer hour, Integer operatorId) {
        ordersManager.delayExpiresTime(orderId, hour, operatorId);
    }

    public void delayFinishTime(String orderId, Integer day, Integer operatorId) {
        ordersManager.delayFinishTime(orderId, day, operatorId);
    }

    public void orderPaySuccess(String orderId, String payOrderId, String payPlatform, Integer payStatus, String payTime) {
        ordersManager.paySuccess(orderId, payOrderId, payPlatform, payStatus, payTime);
    }

    public void orderFinishTask()
    {
        ordersManager.finishTask();
    }

    public void orderFinishByMember(Integer memberid,String orderId)
    {
        ordersManager.finishByMember(memberid,orderId);
    }

    public void createOrderRefund(Integer memberId, String orderId, Integer orderGoodsId, Integer isReceived,
                                  Integer serviceType, Integer refundMethod, String refundReason, String description, String images) {
        orderRefundManager.apply(memberId, orderId, orderGoodsId, isReceived, serviceType,
                refundMethod, refundReason, description, images);
    }

    public void handleOrderRefund(String action, Integer memberId, Integer refundId, Integer refundMethod,
                                  String description, String images, Integer operatorId) {
        switch (action) {
            case "close":
                orderRefundManager.close(refundId, memberId, description, images);
                break;
            case "accept":
                orderRefundManager.agreeApply(refundId, refundMethod, description, images, operatorId);
                break;
            case "agree":
                orderRefundManager.agreeRefund(refundId, description, images, operatorId);
                break;
            case "refuse":
                orderRefundManager.refuseApply(refundId, description, images, operatorId);
                break;
            case "express":
                orderRefundManager.expressByMember(refundId, memberId, description, images);
                break;
        }

    }

    public OrderRefund getOrderRefund(Integer refundId) {
        return orderRefundManager.findById(refundId);
    }

    public OrderRefund getOrderRefundByOrderGoodsId(Integer memberId, String orderId, Integer orderGoodId) {
        return orderRefundManager.findByOrderGoodsId(memberId, orderId, orderGoodId);
    }

    public PageResponse<OrderRefundDTO> findOrderRefund(String orderId, String goodsName, Integer orderGoodsId, Integer memberId,
                                                        Integer status, String beginTime, String endTime, Integer pageIndex, Integer pageSize) {
        return orderRefundManager.findAll(orderId, goodsName, orderGoodsId, memberId, status, beginTime, endTime, pageIndex, pageSize);
    }

    public void refundSuccess(Integer refundId, String description) {
        orderRefundManager.refundSuccess(refundId, description);
    }

    public void createOrderCommment(String orderId,Integer anonymous, Integer logisticsScore,
                                    Integer saleScore,List<OrderComment> commentList) {
        orderCommentManager.createMutil(orderId,anonymous,logisticsScore,saleScore,commentList);
    }

    public void replyOrderComment(Integer id, String replyContent) {
        orderCommentManager.reply(id, replyContent);
    }

    public void updateOrderCommentStatus(Integer id, Integer status) {
        orderCommentManager.updateStatus(id, status);
    }

    public PageResponse<OrderComment> findOrderComment(Integer goodsId, Integer memberId, String cellphone, String orderId,
                                                       Integer status, String beinTime, String endTime, Integer pageIndex, Integer pageSize) {
        return orderCommentManager.findAll(goodsId, memberId, cellphone, orderId, status, beinTime, endTime, pageIndex, pageSize);
    }

    public void saveMaterial(GoodsMaterial goodsMaterial) {
        goodsMaterialManager.save(goodsMaterial);
    }

    public GoodsMaterial getMaterial(Integer id) {
        return goodsMaterialManager.findById(id);
    }

    public void updateMaterialStatus(Integer id) {
        goodsMaterialManager.updateStatus(id);
    }

    public PageResponse<GoodsMaterial> findMaterial(Integer goodsId, String name, Integer typeId, Integer status, Integer pageIndex, Integer pageSize) {
        return goodsMaterialManager.findAll(goodsId, name, typeId, status, pageIndex, pageSize);
    }

    public List<GoodsMaterial> findMaterialByGoods(String materialIds)
    {
        return goodsMaterialManager.findByGoods(materialIds);
    }

    public  void deleteGoodsMaterial(Integer id)
    {
        goodsMaterialManager.delete(id);
    }
}
