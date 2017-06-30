package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.json.JsonConverter;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.mall.data.entity.OrderGoods;
import com.fct.mall.data.entity.Orders;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.OrderGoodsDTO;
import com.fct.mall.interfaces.OrderGoodsResponse;
import com.fct.mall.interfaces.PageResponse;
import com.fct.member.data.entity.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

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
    public ReturnValue<PageResponse<Orders>> findOrders(String order_id, Integer status,
                                                      Integer page_index, Integer page_size) {
        String goodsName = "";
        if (!org.apache.commons.lang3.StringUtils.isNumeric(order_id))
        {
            goodsName = order_id;
            order_id = "";
        }

        status = ConvertUtils.toInteger(status);
        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size);

        MemberLogin member = this.memberAuth();

        PageResponse<Orders> lsOrder = mallService.findOrders(member.getMemberId(), "", order_id,
                                                        0, goodsName, status, "", "",
                                                        0, "", "", page_index, page_size);

        ReturnValue<PageResponse<Orders>> response = new ReturnValue<>();
        response.setData(lsOrder);

        return  response;
    }

    /**根据json格式获取等待生成订单的产品和优惠券
     *
     * @param orderProductInfo
     * @return
     */
    @RequestMapping(value = "order-products", method = RequestMethod.GET)
    public ReturnValue<OrderGoodsResponse> getOrderProducts(String orderProductInfo) {

        MemberLogin member = this.memberAuth();
        List<OrderGoodsDTO> orderProductIds = JsonConverter.toObject(orderProductInfo, List.class);
        OrderGoodsResponse lsOrderGoods = mallService.getSubmitOrderGoods(member.getMemberId(), orderProductIds);

        ReturnValue<OrderGoodsResponse> response = new ReturnValue<>();
        response.setData(lsOrderGoods);

        return  response;
    }

    /**生成订单
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
    public ReturnValue saveOrder(Integer points, BigDecimal accountAmount,
                                 String orderGoodsInfo, String couponCode,
                                 String remark, Integer addressId) {

        points = ConvertUtils.toInteger(points);
        accountAmount = ConvertUtils.toBigDeciaml(accountAmount);
        couponCode = ConvertUtils.toString(couponCode);
        remark = ConvertUtils.toString(remark);
        addressId = ConvertUtils.toInteger(addressId);

        //orderGoodsInfo传json列表，如[{goodsId:1,SpecId:1,buyCount:2}...]
        List<OrderGoodsDTO>  orderProductIds = JsonConverter.toObject(orderGoodsInfo, List.class);

        MemberLogin member = this.memberAuth();
        mallService.createOrder(member.getMemberId(), member.getUserName(),
                0, points, accountAmount, orderProductIds, couponCode,
                remark, addressId);

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
