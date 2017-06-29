package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.json.JsonConverter;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.mall.data.entity.OrderGoods;
import com.fct.mall.data.entity.OrderReceiver;
import com.fct.mall.data.entity.Orders;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.PageResponse;
import com.fct.member.data.entity.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by z on 17-6-29.
 */
@RestController
@RequestMapping(value = "orders")
public class OrderController extends BaseController {

    @Autowired
    private MallService mallService;
    private OrderGoods orderGoods;

    /**获取订单列表
     *
     * @param order_id
     * @param status
     * @param page_index
     * @param page_size
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<Orders>> getOrders(String order_id, Integer status,
                                                      Integer page_index, Integer page_size) {
        String goodsName = "";
        if (!org.apache.commons.lang3.StringUtils.isNumeric(order_id))
        {
            goodsName = order_id;
            order_id = "";
        }

        status = ConvertUtils.toInteger(status);
        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_index);

        MemberLogin member = this.memberAuth();

        PageResponse<Orders> lsOrder = mallService.findOrders(member.getMemberId(), "", order_id,
                                                        0, goodsName, status, "", "",
                                                        0, "", "", page_index, page_size);

        ReturnValue<PageResponse<Orders>> response = new ReturnValue<>();
        response.setData(lsOrder);

        return  response;
    }

    /**生成订单
     *
     * @param points
     * @param accountAmount
     * @param orderGoodsInfo //[{goodsId:1,goodsSpecId:1,buyCount:2}...]
     * @param couponCode
     * @param remark
     * @param orderReceiverInfo //{name:张三, phone:13812345678, province:上海, city:上海, region:静安, address:长寿路1号 postCode:}
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ReturnValue saveOrder(Integer points, BigDecimal accountAmount,
                                 String orderGoodsInfo, String couponCode,
                                 String remark, String orderReceiverInfo) {

        points = ConvertUtils.toInteger(points);
        accountAmount = ConvertUtils.toBigDeciaml(accountAmount);
        couponCode = ConvertUtils.toString(couponCode);
        remark = ConvertUtils.toString(remark);

        //orderGoodsInfo传json列表，如[{goodsId:1,goodsSpecId:1,buyCount:2}...]
        List<OrderGoods> lsOrderGoods = JsonConverter.toObject(orderGoodsInfo, List.class);
        //orderReceiver传JSON对象为{name:张三, phone:13812345678, province:上海, city:上海, region:静安, address:长寿路1号 postCode:}
        OrderReceiver orderReceiver = JsonConverter.toObject(orderReceiverInfo, OrderReceiver.class);

        MemberLogin member = this.memberAuth();
        mallService.createOrder(member.getMemberId(), member.getUserName(),
                0, points, accountAmount, lsOrderGoods, couponCode,
                remark, orderReceiver);

        return new ReturnValue(200, "订单创建成功");
    }

    /**取消订单
     *
     * @param order_id
     * @return
     */
    @RequestMapping(value = "{order_id}", method = RequestMethod.GET)
    public ReturnValue<Orders> getOrder(@PathVariable("order_id") String order_id) {

        order_id = ConvertUtils.toString(order_id);

        MemberLogin member = this.memberAuth();
        Orders orders = mallService.getOrders(order_id);

        ReturnValue<Orders> response = new ReturnValue<>();
        response.setData(orders);

        return  response;
    }

    @RequestMapping(value = "{order_id}/cancel", method = RequestMethod.POST)
    public ReturnValue cancelOrder(@PathVariable("order_id") String order_id) {

        order_id = ConvertUtils.toString(order_id);

        MemberLogin member = this.memberAuth();
        mallService.cancelOrders(order_id, member.getMemberId(), 0);

        return new ReturnValue(200, "订单取消成功");
    }
}
