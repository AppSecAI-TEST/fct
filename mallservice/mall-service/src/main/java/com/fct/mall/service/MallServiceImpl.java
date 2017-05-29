package com.fct.mall.service;

import com.fct.mall.data.entity.*;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.service.business.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    public Page<Goods> findGoods(String name, String categoryCode, Integer gradeId, Integer status,
                                 Integer pageIndex, Integer pageSize) {
        return goodsManager.find(name, categoryCode, gradeId, status, pageIndex, pageSize);
    }

    public Goods getGoods(Integer id) {
        return goodsManager.findById(id);
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

    public void deleteGoods(Integer id)
    {
        goodsManager.delete(id);
    }

    public List<GoodsCategory> findGoodsCategory(Integer parentId, String name, String code)
    {
        return goodsCategoryManager.findAll(name,code,parentId);
    }

    public GoodsCategory getGoodsCategory(Integer id)
    {
        return goodsCategoryManager.findById(id);
    }

    public void saveGoodsCategory (GoodsCategory category)
    {
        goodsCategoryManager.save(category);
    }

    public void setGoodsCategorySortIndex(Integer id, Integer sortIndex)
    {
        goodsCategoryManager.saveSortIndex(id,sortIndex);
    }

    public void deleteGoodsCategory(Integer id)
    {
        goodsCategoryManager.delete(id);
    }

    public List<GoodsGrade> findGoodsGrade()
    {
        return goodsGradeManager.findAll("");
    }

    public GoodsGrade getGoodsGrade(Integer id)
    {
        return goodsGradeManager.findById(id);
    }

    public void saveGoodsGrade (GoodsGrade grade)
    {
        goodsGradeManager.save(grade);
    }

    public void setGoodsGradeSortIndex(Integer id, Integer sortIndex)
    {
        goodsGradeManager.saveSortIndex(id,sortIndex);
    }

    public void DeleteGoodsGrade(Integer id)
    {
        goodsGradeManager.delete(id);
    }

    public void SaveShoppingCart(Integer memberId, Integer shopId, Integer goodsId, Integer goodsSpecId, Integer buyCount)
    {
        shoppingCartManager.add(memberId,shopId,goodsId,goodsSpecId,buyCount);
    }

    public List<ShoppingCart> findShoppingCart(Integer memberId,Integer shopId)
    {
        return shoppingCartManager.find(memberId,shopId);
    }

    public void deleteShoppingCart(Integer memberId,Integer shopId,Integer cartId)
    {
        shoppingCartManager.delete(memberId,shopId,cartId);
    }

    public String createOrder(Integer memberId, String userName, Integer shopId, Integer points, BigDecimal accountAmount,
                              List<OrderGoods> lsOrderGoods, String couponCode, String remark,OrderReceiver orderReceiver)
    {
        return ordersManager.create(memberId,userName,shopId,points,accountAmount,
                lsOrderGoods,couponCode,remark,orderReceiver);
    }

    public Page<Orders> findOrders(Integer memberId,String cellPhone,String orderId,Integer shopId,String goodsName,
                            Integer status,String payPlatform,String payOrderId,Integer timeType,String beginTime,
                            String endTime,Integer pageIndex, Integer pageSize)
    {
        return null;
    }

    public Orders getOrders(String orderId)
    {
        return ordersManager.findById(orderId);
    }

    public void offPaySuccess(String orderId, String payPlatform, Integer operatorId)
    {
        ordersManager.offPaySuccess(orderId,payPlatform,operatorId);
    }

    public void cancelOrders(String orderId,Integer memberId,Integer operatorId)
    {
        ordersManager.cancel(orderId,memberId,operatorId);
    }

    public void orderDeliver(String orderId, String expressPlatform, String expressNo, Integer operatorId)
    {
        ordersManager.saveDeliver(orderId,expressPlatform,expressNo,operatorId);
    }

    public List<OrderGoods> findOrderGoods(String orderId)
    {
        return orderGoodsManager.findByOrderId(orderId);
    }

    public void saveOrderReciver(OrderReceiver or)
    {
        orderReceiverManager.save(or);
    }

    public OrderReceiver getOrderReceiver(String orderId)
    {
        return orderReceiverManager.findByOrderId(orderId);
    }

    public void delayExpiresTime(String orderId, Integer day, Integer operatorId)
    {
        ordersManager.delayExpiresTime(orderId,day,operatorId);
    }

    public void delayFinishTime(String orderId, Integer day, Integer operatorId)
    {
        ordersManager.delayFinishTime(orderId,day,operatorId);
    }

    public void orderPaySuccess(String orderId, String payOrderId, String payPlatform, Integer payStatus, String payTime)
    {
        ordersManager.paySuccess(orderId,payOrderId,payPlatform,payStatus,payTime);
    }

    public void createOrderRefund(Integer memberId, String orderId, Integer goodsId, Integer goodsSpecId, Integer isReceive,
                                  Integer isRefundMoney, Integer refundMoneyType, String description, String images)
    {
        orderRefundManager.apply(memberId,orderId,goodsId,goodsSpecId,isReceive,isRefundMoney,
                refundMoneyType,description,images);
    }

    public void closeOrderRefund(Integer refundId, Integer memberId, String description, String images)
    {
        orderRefundManager.close(refundId,memberId,description,images);
    }

    public void agreeApplyRefund(Integer refundId, Integer refundMoneyType, String description, String images, Integer operatorId)
    {
        orderRefundManager.agreeApply(refundId,refundMoneyType,description,images,operatorId);
    }

    public void refuseApplyRefund(Integer refundId, String description, String images, Integer operatorId)
    {
        orderRefundManager.refuseApply(refundId,description,images,operatorId);
    }

    public void agreeRefund(Integer refundId, Integer refundMoneyType, String description, String images, Integer operatorId)
    {
        orderRefundManager.agreeRefund(refundId,refundMoneyType,description,images,operatorId);
    }

    public void refundDeliverByMember(Integer refundId, Integer memberId, String description, String images)
    {
        orderRefundManager.expressByMember(refundId,memberId,description,images);
    }

    public void refundDeliverByAdmin(Integer refundId, String description, String images,  Integer operatorId)
    {
        orderRefundManager.expressByAdmin(refundId,description,images,operatorId);
    }

    public OrderRefund getOrderRefund(Integer refundId)
    {
        return orderRefundManager.findById(refundId);
    }

    public Page<OrderRefund> findOrderRefund(String orderId,Integer goodsId,Integer memberId,Integer status,String beginTime,
                                      String endTime,Integer pageIndex,Integer pageSize)
    {
        return orderRefundManager.findAll(orderId,goodsId,memberId,status,beginTime,endTime,pageIndex,pageSize);
    }

    public void refundSuccess (Integer refundId, String description)
    {
        orderRefundManager.refundSuccess(refundId,description);
    }

    public void createOrderCommment(OrderComment comment)
    {
        orderCommentManager.create(comment);
    }

    public void replyOrderComment(Integer id,String replyContent)
    {
        orderCommentManager.reply(id,replyContent);
    }

    public Page<OrderComment> findOrderComment(Integer goodsId,Integer memberId,String orderId,Integer pageIndex,
                                               Integer pageSize)
    {
        return orderCommentManager.findAll(goodsId,memberId,orderId,pageIndex,pageSize);
    }

    public void saveMaterial(GoodsMaterial goodsMaterial)
    {
        goodsMaterialManager.save(goodsMaterial);
    }

    public GoodsMaterial getMaterial(Integer id)
    {
        return goodsMaterialManager.findById(id);
    }

    public void updateMaterialStatus(Integer id)
    {
        goodsMaterialManager.updateStatus(id);
    }

    public Page<GoodsMaterial> findMaterial(Integer goodsId, String name, Integer status, Integer pageIndex, Integer pageSize)
    {
        return goodsMaterialManager.findAll(goodsId,name,status,pageIndex,pageSize);
    }
}
