package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.json.JsonConverter;
import com.fct.core.utils.ConvertUtils;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by z on 17-6-29.
 */
@RestController
@RequestMapping(value = "/mall/orders")
public class OrderController extends BaseController {

    @Autowired
    private MallService mallService;

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

        PageResponse<Orders> pageResponse = mallService.findOrdersByAPI(member.getMemberId(), order_id,
                0, goodsName, status, comment_status, page_index, page_size);

        PageResponse<Map<String, Object>> pageMaps = new PageResponse<>();
        if (pageResponse != null) {

            if (pageResponse.getTotalCount() > 0) {

                List<Map<String, Object>> lsMaps = new ArrayList<>();
                Map<String, Object> map = null;

                List<Map<String, Object>> lsProductMaps = null;
                Map<String, Object> productMap = null;

                for (Orders order: pageResponse.getElements()) {

                    map = new HashMap<>();
                    map.put("orderId", order.getOrderId());
                    map.put("payAmount", order.getPayAmount());
                    map.put("status", order.getStatus());
                    map.put("statusName", this.getStatusName(order.getStatus()));
                    map.put("buyTotalCount", order.getOrderGoods().size());

                    lsProductMaps = new ArrayList<>();
                    for (OrderGoods product: order.getOrderGoods()) {

                        productMap = new HashMap<>();
                        productMap.put("name", product.getName());
                        productMap.put("specName", product.getSpecName());
                        productMap.put("img", fctResourceUrl.getImageUrl(product.getImg()));
                        productMap.put("buyCount", product.getBuyCount());
                        productMap.put("price", product.getPrice());
                        lsProductMaps.add(productMap);
                    }
                    map.put("orderGoods", lsProductMaps);

                    lsMaps.add(map);
                }

                pageMaps.setElements(lsMaps);
                pageMaps.setCurrent(pageResponse.getCurrent());
                pageMaps.setTotalCount(pageResponse.getTotalCount());
                pageMaps.setHasMore(pageResponse.isHasMore());
            }
        }


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
    public ReturnValue<Orders> getOrder(@PathVariable("order_id") String order_id) {

        order_id = ConvertUtils.toString(order_id);
        if (StringUtils.isEmpty(order_id)) {

            return new ReturnValue<>(404, "订单不存在");
        }

        this.memberAuth();

        Orders orders = mallService.getOrders(order_id);

        ReturnValue<Orders> response = new ReturnValue<>();
        response.setData(orders);

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
    public ReturnValue<String> saveOrder(Integer points, BigDecimal accountAmount,
                                         String orderGoodsInfo, String couponCode,
                                         String remark, Integer addressId) {

        points = ConvertUtils.toInteger(points);
        accountAmount = ConvertUtils.toBigDeciaml(accountAmount);
        couponCode = ConvertUtils.toString(couponCode);
        remark = ConvertUtils.toString(remark);
        addressId = ConvertUtils.toInteger(addressId);

        //orderGoodsInfo传json列表，如[{goodsId:1,SpecId:1,buyCount:2}...]
        List<Map<String, Object>> lsMap = JsonConverter.toObject(orderGoodsInfo, List.class);
        List<OrderGoodsDTO> orderProductIds = new ArrayList<>();
        Integer goodsId = 0;
        Integer specId = 0;
        Integer buyCount = 0;
        for (Map<String, Object> map : lsMap) {

            goodsId = ConvertUtils.toInteger(map.get("goodsId"), 0);
            specId = ConvertUtils.toInteger(map.get("specId"), 0);
            buyCount = ConvertUtils.toInteger(map.get("buyCount"), 0);
            if (goodsId < 1) {
                return new ReturnValue<>(404, "订单有产品已下架");
            }

            if (buyCount < 1) {
                return new ReturnValue<>(404, "购买数量不能小于1");
            }

            OrderGoodsDTO orderGoodsDTO = new OrderGoodsDTO();
            orderGoodsDTO.setGoodsId(goodsId);
            orderGoodsDTO.setSpecId(specId);
            orderGoodsDTO.setBuyCount(buyCount);

            orderProductIds.add(orderGoodsDTO);
        }

        MemberLogin member = this.memberAuth();
        String orderId = mallService.createOrder(member.getMemberId(), member.getUserName(),
                0, points, accountAmount, orderProductIds, couponCode,
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

    private String getStatusName(Integer status) {

        switch (status) {
            case 0:
                return "待付款";
            case 1:
                return "待发货";
            case 2:
                return "待收货";
            case 3:
                return "交易完成";
            case 4:
                return "关闭";
        }

        return "";
    }
}
