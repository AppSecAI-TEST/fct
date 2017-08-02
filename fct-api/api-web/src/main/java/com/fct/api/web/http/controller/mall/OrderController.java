package com.fct.api.web.http.controller.mall;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fct.api.web.http.cache.OrderCache;
import com.fct.api.web.http.cache.PaymentCache;
import com.fct.api.web.http.controller.BaseController;
import com.fct.core.json.JsonConverter;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.DateUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.mall.data.entity.OrderGoods;
import com.fct.mall.data.entity.Orders;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.OrderGoodsDTO;
import com.fct.mall.interfaces.PageResponse;
import com.fct.member.data.entity.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by z on 17-6-29.
 */
@RestController
@RequestMapping(value = "/mall/orders")
public class OrderController extends BaseController {

    @Autowired
    private MallService mallService;

    @Autowired
    private PaymentCache paymentCache;

    @Autowired
    private OrderCache orderCache;

    /**
     * 获取订单列表
     *
     * @param order_id
     * @param status
     * @param page_index
     * @param page_size
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<Map<String, Object>>> findOrders(String order_id, Integer status,
                                                        Integer comment_status,
                                                        Integer page_index, Integer page_size) {
        String goodsName = "";
        if (!org.apache.commons.lang3.StringUtils.isNumeric(order_id)) {
            goodsName = order_id;
            order_id = "";
        }

        status = ConvertUtils.toInteger(status, -1);
        comment_status = ConvertUtils.toInteger(comment_status, -1);
        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size);

        MemberLogin member = this.memberAuth();

        PageResponse<Map<String, Object>> pageMaps = orderCache.findPageOrder(member.getMemberId(), order_id,
                goodsName, status, comment_status, page_index, page_size);

        ReturnValue<PageResponse<Map<String, Object>>> response = new ReturnValue<>();
        response.setData(pageMaps);

        return response;
    }

    /**
     * 订单详情
     *
     * @param order_id
     * @return
     */
    @RequestMapping(value = "{order_id}", method = RequestMethod.GET)
    public ReturnValue<Map<String, Object>> getOrder(@PathVariable("order_id") String order_id) {

        order_id = ConvertUtils.toString(order_id);
        if (StringUtils.isEmpty(order_id)) {

            return new ReturnValue<>(404, "订单不存在");
        }
        this.memberAuth();

        Map<String, Object> map = new HashMap<>();

        Orders orders = orderCache.getOrder(order_id);
        if (orders == null) {
            return new ReturnValue<>(404, "订单不存在");
        }

        map.put("orderId", orders.getOrderId());
        map.put("payAmount", orders.getPayAmount());
        map.put("status", orders.getStatus());
        map.put("statusName", orderCache.getStatusName(orders.getStatus()));
        map.put("buyTotalCount", orders.getOrderGoods().size());
        map.put("payOrderId", orders.getPayOrderId());
        map.put("payPlatform", orders.getPayPlatform());
        map.put("payName", paymentCache.getNameByCode(orders.getPayPlatform()));
        map.put("payTime", this.getFormatDate(orders.getPayTime(), "yyyy-MM-dd HH:mm:ss"));
        map.put("createTime", this.getFormatDate(orders.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        map.put("expiresTime", this.getFormatDate(orders.getExpiresTime(), "yyyy-MM-dd HH:mm:ss"));
        map.put("finishTime", this.getFormatDate(orders.getFinishTime(), "yyyy-MM-dd HH:mm:ss"));

        Map<String, Object> receiverMap = new HashMap<>();
        if (orders.getOrderReceiver() != null) {
            receiverMap.put("name", orders.getOrderReceiver().getName());
            receiverMap.put("phone", orders.getOrderReceiver().getPhone());
            receiverMap.put("province", orders.getOrderReceiver().getProvince());
            receiverMap.put("city", orders.getOrderReceiver().getCity());
            receiverMap.put("region", orders.getOrderReceiver().getRegion());
            receiverMap.put("address", orders.getOrderReceiver().getAddress());
            receiverMap.put("expressPlatform", orders.getOrderReceiver().getExpressPlatform());
            receiverMap.put("expressNO", orders.getOrderReceiver().getExpressNO());
            receiverMap.put("deliveryTime", this.getFormatDate(orders.getOrderReceiver().getDeliveryTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        map.put("orderReceiver", receiverMap);

        List<Map<String, Object>> lsGoodsMaps = new ArrayList<>();
        if (orders.getOrderGoods() != null) {
            for (OrderGoods goods: orders.getOrderGoods()) {

                Map<String, Object> goodsMaps = new HashMap<>();
                goodsMaps.put("id", goods.getId());
                goodsMaps.put("goodsId", goods.getGoodsId());
                goodsMaps.put("goodsSpecId", goods.getGoodsSpecId());
                goodsMaps.put("name", goods.getName());
                goodsMaps.put("specName", goods.getSpecName());
                goodsMaps.put("img", fctResourceUrl.getImageUrl(goods.getImg()));
                goodsMaps.put("buyCount", goods.getBuyCount());
                goodsMaps.put("status", goods.getStatus());
                goodsMaps.put("statusName", goods.getStatusName());
                goodsMaps.put("price", goods.getPrice());

                lsGoodsMaps.add(goodsMaps);
            }
        }

        map.put("orderGoods", lsGoodsMaps);

        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(map);

        return response;
    }

    /**
     * 生成订单
     *
     * @param points
     * @param accountAmount
     * @param orderGoodsInfo
     * @param couponCode
     * @param remark
     * @param addressId
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ReturnValue<String> saveOrder(Integer shopId, Integer points, BigDecimal accountAmount,
                                         String orderGoodsInfo, String couponCode,
                                         String remark, Integer addressId) {

        shopId = ConvertUtils.toInteger(shopId, 0);
        points = ConvertUtils.toInteger(points);
        accountAmount = ConvertUtils.toBigDeciaml(accountAmount);
        couponCode = ConvertUtils.toString(couponCode);
        remark = ConvertUtils.toString(remark);
        addressId = ConvertUtils.toInteger(addressId);

        //orderGoodsInfo传json列表，如[{goodsId:1,SpecId:1,buyCount:2}...]
        List<OrderGoodsDTO> orderProductIds = JsonConverter.toObject(orderGoodsInfo,
                new TypeReference<List<OrderGoodsDTO>>(){});
        for (OrderGoodsDTO product : orderProductIds) {
            if (product.getGoodsId() < 1) {
                return new ReturnValue<>(404, "订单有产品已下架");
            }

            if (product.getBuyCount() < 1) {
                return new ReturnValue<>(404, "购买数量不能小于1");
            }
            if (product.getSpecId() == null)
                product.setSpecId(0);
        }

        MemberLogin member = this.memberAuth();
        String orderId = mallService.createOrder(member.getMemberId(), member.getCellPhone(),
                shopId, points, accountAmount, orderProductIds, couponCode,
                remark, addressId);

        if (StringUtils.isEmpty(orderId)) {
            return new ReturnValue<String>(404, "订单创建失败");
        }

        ReturnValue<String> response = new ReturnValue(200, "订单创建成功");
        response.setData(orderId);
        return response;
    }

    /**
     * 取消订单
     *
     * @param order_id
     * @return
     */
    @RequestMapping(value = "{order_id}/cancel", method = RequestMethod.POST)
    public ReturnValue cancelOrder(@PathVariable("order_id") String order_id) {

        order_id = ConvertUtils.toString(order_id);

        if (StringUtils.isEmpty(order_id))
            return new ReturnValue(404, "订单不存在");

        MemberLogin member = this.memberAuth();
        mallService.cancelOrders(order_id, member.getMemberId(), 0);

        return new ReturnValue(200, "订单取消成功");
    }
}
