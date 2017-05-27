package com.fct.mall.interfaces;

import com.fct.mall.data.entity.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by jon on 2017/5/16.
 */
public interface MallService {

    Page<Goods> findGoods(String name, String categoryCode, Integer gradeId, Integer status,
                            Integer pageIndex, Integer pageSize);

    Goods getGoods(Integer id);

    void saveGoods(Goods goods);

    void setGoodsSortIndex(Integer id, Integer sortIndex);

    void setGoodsStatus(Integer id, Integer status);

    void deleteGoods(Integer id);

    List<GoodsCategory> findGoodsCategory(Integer parentId,String name, String code);

    GoodsCategory getGoodsCategory(Integer id);

    void saveGoodsCategory (GoodsCategory category);

    void setGoodsCategorySortIndex(Integer id, Integer sortIndex);

    void deleteGoodsCategory(Integer id);

    List<GoodsGrade> findGoodsGrade();

    GoodsGrade getGoodsGrade(Integer id);

    void saveGoodsGrade (GoodsGrade grade);

    void setGoodsGradeSortIndex(Integer id, Integer sortIndex);

    void DeleteGoodsGrade(Integer id);

    void SaveShoppingCart(Integer memberId, Integer shopId, Integer goodsId, Integer goodsSpecId, Integer buyCount);

    List<ShoppingCart> findShoppingCart(Integer memberId,Integer shopId);

    void deleteShoppingCart(Integer memberId,Integer shopId,Integer cartId);

    String createOrder(Integer memberId, String userName, Integer shopId, Integer points, BigDecimal accountAmount,
                       List<OrderGoods> lsOrderGoods, String couponCode, String remark,OrderReceiver orderReceiver);

    Page<Orders> findOrders(Integer memberId,String cellPhone,String orderId,Integer shopId,String goodsName,
                            Integer status,String payPlatform,String payOrderId,Integer timeType,String beginTime,
                            String endTime,Integer pageIndex, Integer pageSize);

    Orders getOrders(String orderId);

    void offPaySuccess(String orderId, String payPlatform, Integer operatorId);

    void cancelOrders(String orderId,Integer memberId,Integer operatorId);

    void orderDeliver(String orderId, String expressPlatform, String expressNo, Integer operatorId);

    List<OrderGoods> findOrderGoods(String orderId);

    void saveOrderReciver(OrderReceiver or);

    OrderReceiver getOrderReceiver(String orderId);

    void delayExpiresTime(String orderId, Integer day, Integer operatorId);

    void delayFinishTime(String orderId, Integer day, Integer operatorId);

    void orderPaySuccess(String orderId, String payOrderId, String payPlatform, Integer payStatus, String payTime);

    void createOrderRefund(Integer memberId, String orderId, Integer goodsId, Integer goodsSpecId, Integer isReceive,
                           Integer isRefundMoney, Integer refundMoneyType, String description, String images);

    void closeOrderRefund(Integer refundId, Integer memberId, String description, String images);

    void agreeApplyRefund(Integer refundId, Integer refundMoneyType, String description, String images, Integer operatorId);

    void refuseApplyRefund(Integer refundId, String description, String images, Integer operatorId);

    void agreeRefund(Integer refundId, Integer refundMoneyType, String description, String images, Integer operatorId);

    void refundDeliverByMember(Integer refundId, Integer memberId, String description, String images);

    void refundDeliverByAdmin(Integer refundId, String description, String images,  Integer operatorId);

    OrderRefund getOrderRefund(Integer refundId);

    Page<OrderRefund> findOrderRefund(String orderId,Integer goodsId,Integer memberId,Integer status,String beginTime,
                                      String endTime,Integer pageIndex,Integer pageSize);

    void refundSuccess (Integer refundId, String description);

    void createOrderCommment(OrderComment comment);

    void replyOrderComment(Integer id,String replyContent);

    Page<OrderComment> findOrderComment(Integer goodsId,Integer memberId,String orderId,Integer pageIndex,Integer pageSize);

    void saveMaterial(GoodsMaterial goodsMaterial);

    GoodsMaterial getMaterial(Integer id);

    void updateMaterialStatus(Integer id);

    Page<GoodsMaterial> findMaterial(Integer goodsId, String name, Integer status, Integer pageIndex, Integer pageSize);
}
