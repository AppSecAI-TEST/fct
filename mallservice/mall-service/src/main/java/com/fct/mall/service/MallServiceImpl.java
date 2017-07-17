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
                                         Integer orderType,Integer pageIndex, Integer pageSize) {
        try {
            return goodsManager.find(name, categoryCode, gradeId, materialId, artistId, minVolume, maxVolume, status,
                    orderType,pageIndex, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public Goods getGoods(Integer id) {
        try {
            return goodsManager.findById(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public List<Goods> findGoodsByIds(String ids) {
        try {
            return goodsManager.findByIds(ids);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void saveGoods(Goods goods) {

        try {
            goodsManager.save(goods);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void setGoodsSortIndex(Integer id, Integer sortIndex) {

        try {
            goodsManager.updateSortIndex(id, sortIndex);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void setGoodsStatus(Integer id, Integer status) {
        try {

            goodsManager.updateStatus(id, status);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void deleteGoods(Integer id) {
        try {
            goodsManager.delete(id);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public List<Goods> findGoodsByGuess(String goodsId,String categoryCode, Integer gradeId, Integer materialId,
                                        Integer artistId,int top)
    {
        try {
            return goodsManager.findByGuess(goodsId,categoryCode,gradeId, materialId,artistId,top);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public List<GoodsCategory> findGoodsCategory(Integer parentId, String name, String code) {

        try {
            return goodsCategoryManager.findAll(name, code, parentId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public GoodsCategory getGoodsCategory(Integer id) {

        try {
            return goodsCategoryManager.findById(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void saveGoodsCategory(GoodsCategory category) {

        try {
            goodsCategoryManager.save(category);
        }
        catch (IllegalArgumentException exp)
        {
            throw  exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void setGoodsCategorySortIndex(Integer id, Integer sortIndex) {

        try {
            goodsCategoryManager.saveSortIndex(id, sortIndex);
        }
        catch (IllegalArgumentException exp)
        {
            throw  exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void deleteGoodsCategory(Integer id) {

        try {
            goodsCategoryManager.delete(id);
        }
        catch (IllegalArgumentException exp)
        {
            throw  exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public List<GoodsGrade> findGoodsGrade() {

        try {
            return goodsGradeManager.findAll("");
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public GoodsGrade getGoodsGrade(Integer id) {

        try {
            return goodsGradeManager.findById(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void saveGoodsGrade(GoodsGrade grade) {

        try {
            goodsGradeManager.save(grade);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void setGoodsGradeSortIndex(Integer id, Integer sortIndex) {
        try {
            goodsGradeManager.saveSortIndex(id, sortIndex);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void deleteGoodsGrade(Integer id) {

        try {
            goodsGradeManager.delete(id);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void saveShoppingCart(Integer memberId, Integer shopId, Integer goodsId, Integer goodsSpecId, Integer buyCount) {

        try {
            shoppingCartManager.add(memberId, shopId, goodsId, goodsSpecId, buyCount);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public List<ShoppingCart> findShoppingCart(Integer memberId, Integer shopId) {

        try {
            return shoppingCartManager.find(memberId, shopId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void deleteShoppingCart(Integer memberId, Integer shopId, Integer cartId) {

        try {
            shoppingCartManager.delete(memberId, shopId, cartId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public int getShoopingCartCount(Integer memberId)
    {
        try {
            return shoppingCartManager.getCount(memberId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return 0;
    }

    public OrderGoodsResponse getSubmitOrderGoods(Integer memberId, List<OrderGoodsDTO> lsGoods)
    {
        try {
            return orderGoodsManager.findFinalGoods(memberId,lsGoods);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;

    }

    public String createOrder(Integer memberId, String userName, Integer shopId, Integer points, BigDecimal accountAmount,
                              List<OrderGoodsDTO> lsOrderGoods, String couponCode, String remark, Integer receiverId) {

        try {
            return ordersManager.create(memberId, userName, shopId, points, accountAmount,
                    lsOrderGoods, couponCode, remark, receiverId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return "";
    }

    public PageResponse<Orders> findOrders(Integer memberId, String cellPhone, String orderId, Integer shopId, String goodsName,
                                           Integer status, String payPlatform, String payOrderId, Integer timeType, String beginTime,
                                           String endTime, Integer pageIndex, Integer pageSize) {

        try {
            return ordersManager.findAll(memberId, cellPhone, orderId, shopId, goodsName, status, payPlatform, payOrderId,
                    timeType, beginTime, endTime, pageIndex, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public Orders getOrders(String orderId) {

        try {
            return ordersManager.findById(orderId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public Orders getAloneOrders(String orderId)
    {
        try {
            return ordersManager.findOne(orderId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void offPaySuccess(String orderId, String payPlatform, Integer operatorId) {

        try {
            ordersManager.offPaySuccess(orderId, payPlatform, operatorId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void updateOrderPayPlatform(String orderId,String payPlatform)
    {
        try {
            ordersManager.updatePayPlatform(orderId,payPlatform);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void orderExpiredTask()
    {
        try {
            ordersManager.handleExpire();
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void cancelOrders(String orderId, Integer memberId, Integer operatorId) {

        try {
            ordersManager.cancel(orderId, memberId, operatorId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void orderDeliver(String orderId, String expressPlatform, String expressNo, Integer operatorId) {

        try {
            ordersManager.saveDeliver(orderId, expressPlatform, expressNo, operatorId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public List<OrderGoods> findOrderGoods(String orderId) {
        try {
            return orderGoodsManager.findByOrderId(orderId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void saveOrderReciver(OrderReceiver or) {

        try {
            orderReceiverManager.save(or);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public OrderReceiver getOrderReceiver(String orderId) {

        try {
            return orderReceiverManager.findByOrderId(orderId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void delayExpiresTime(String orderId, Integer hour, Integer operatorId) {

        try {
            ordersManager.delayExpiresTime(orderId, hour, operatorId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void delayFinishTime(String orderId, Integer day, Integer operatorId) {

        try {
            ordersManager.delayFinishTime(orderId, day, operatorId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void orderPaySuccess(String orderId, String payOrderId, String payPlatform, Integer payStatus, String payTime) {

        try {
            ordersManager.paySuccess(orderId, payOrderId, payPlatform, payStatus, payTime);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void orderFinishTask()
    {
        try {
            ordersManager.finishTask();
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void orderFinishByMember(Integer memberid,String orderId)
    {
        try {
            ordersManager.finishByMember(memberid,orderId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void createOrderRefund(Integer memberId, String orderId, Integer orderGoodsId, Integer isReceived,
                                  Integer serviceType, Integer refundMethod, String refundReason, String description, String images) {

        try {
            orderRefundManager.apply(memberId, orderId, orderGoodsId, isReceived, serviceType,
                    refundMethod, refundReason, description, images);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void handleOrderRefund(String action, Integer memberId, Integer refundId, Integer refundMethod,
                                  String description, String images, Integer operatorId) {

        try {
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
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }

    }

    public OrderRefund getOrderRefund(Integer refundId) {

        try {

            return orderRefundManager.findById(refundId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public OrderRefund getOrderRefundByOrderGoodsId(Integer memberId, String orderId, Integer orderGoodId) {

        try {
            return orderRefundManager.findByOrderGoodsId(memberId, orderId, orderGoodId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PageResponse<OrderRefundDTO> findOrderRefund(String orderId, String goodsName, Integer orderGoodsId, Integer memberId,
                                                        Integer status, String beginTime, String endTime, Integer pageIndex, Integer pageSize) {

        try {
            return orderRefundManager.findAll(orderId, goodsName, orderGoodsId, memberId, status, beginTime, endTime, pageIndex, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void refundSuccess(Integer refundId, String description) {

        try {
            orderRefundManager.refundSuccess(refundId, description);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void createOrderCommment(String orderId,Integer anonymous, Integer logisticsScore,
                                    Integer saleScore,List<OrderComment> commentList) {

        try {
            orderCommentManager.createMutil(orderId,anonymous,logisticsScore,saleScore,commentList);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void replyOrderComment(Integer id, String replyContent) {

        try {
            orderCommentManager.reply(id, replyContent);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void updateOrderCommentStatus(Integer id, Integer status) {

        try {
            orderCommentManager.updateStatus(id, status);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public PageResponse<OrderComment> findOrderComment(Integer goodsId, Integer memberId, String cellphone, String orderId,
                                                       Integer status, String beinTime, String endTime, Integer pageIndex, Integer pageSize) {

        try {
            return orderCommentManager.findAll(goodsId, memberId, cellphone, orderId, status, beinTime, endTime, pageIndex, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void saveMaterial(GoodsMaterial goodsMaterial) {

        try {
            goodsMaterialManager.save(goodsMaterial);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public GoodsMaterial getMaterial(Integer id) {

        try {

            return goodsMaterialManager.findById(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void updateMaterialStatus(Integer id) {

        try {
            goodsMaterialManager.updateStatus(id);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public PageResponse<GoodsMaterial> findMaterial(Integer goodsId, String name, Integer typeId, Integer status, Integer pageIndex, Integer pageSize) {

        try {
            return goodsMaterialManager.findAll(goodsId, name, typeId, status, pageIndex, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public List<GoodsMaterial> findMaterialByGoods(String materialIds)
    {
        try {
            return goodsMaterialManager.findByGoods(materialIds);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public  void deleteGoodsMaterial(Integer id)
    {
        try {
            goodsMaterialManager.delete(id);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }
}
