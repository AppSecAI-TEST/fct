package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.json.JsonConverter;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.interfaces.FinanceService;
import com.fct.mall.data.entity.OrderGoods;
import com.fct.mall.data.entity.Orders;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.OrderGoodsDTO;
import com.fct.mall.interfaces.OrderGoodsResponse;
import com.fct.mall.interfaces.PageResponse;
import com.fct.member.data.entity.MemberAddress;
import com.fct.member.data.entity.MemberLogin;
import com.fct.promotion.interfaces.dto.OrderProductDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private FinanceService financeService;

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

        status = ConvertUtils.toInteger(status, -1);
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

    /**订单详情
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

    /**根据json格式获取等待生成订单的产品和优惠券
     *
     * @param orderProductInfo
     * @return
     */
    @RequestMapping(value = "order-products", method = RequestMethod.GET)
    public ReturnValue<Map<String, Object>> getOrderProducts(String orderProductInfo) {

        MemberLogin member = this.memberAuth();

        List<Map<String, Object>> lsMap = JsonConverter.toObject(orderProductInfo, List.class);
        List<OrderGoodsDTO> orderProductIds = new ArrayList<>();
        for (Map<String, Object> map:lsMap) {

            OrderGoodsDTO orderGoodsDTO = new OrderGoodsDTO();
            orderGoodsDTO.setGoodsId(ConvertUtils.toInteger(map.get("goodsId")));
            orderGoodsDTO.setSpecId(ConvertUtils.toInteger(map.get("specId")));
            orderGoodsDTO.setBuyCount(ConvertUtils.toInteger(map.get("buyCount")));

            orderProductIds.add(orderGoodsDTO);
        }

        OrderGoodsResponse lsOrderGoods = mallService.getSubmitOrderGoods(member.getMemberId(), orderProductIds);

        //优惠券使用List<OrderProductDTO
        List<OrderProductDTO> lsOrderProduct = new ArrayList<>();
        if (lsOrderGoods != null
                && lsOrderGoods.getItems() != null
                && StringUtils.isEmpty(lsOrderGoods.getCouponCode())) {

            for (OrderGoods orderGoods:lsOrderGoods.getItems()) {

                //设置图片
                orderGoods.setImg(this.getImgUrl(orderGoods.getImg()));

                OrderProductDTO productDTO = new OrderProductDTO();
                productDTO.setProductId(orderGoods.getGoodsId());
                productDTO.setSizeId(orderGoods.getGoodsSpecId());
                productDTO.setCount(orderGoods.getBuyCount());
                productDTO.setRealPrice(orderGoods.getPrice());
                productDTO.setDiscountId(0);
                productDTO.setSingleCount(0);
                productDTO.setDiscountPrice(orderGoods.getPromotionPrice());
                lsOrderProduct.add(productDTO);
            }
        }

        //积分余额
        Integer points = 0;
        BigDecimal availableAmount = new BigDecimal(0);
        MemberAccount account = financeService.getMemberAccount(member.getMemberId());
        if (account != null) {
            points = account.getPoints();
            availableAmount = account.getAvailableAmount();
        }
        //收货地址
        Map<String, Object> addressMap = new HashMap<>();
        MemberAddress address = memberService.getDefaultAddress(member.getMemberId());
        if (address != null) {
            addressMap.put("id", address.getId());
            addressMap.put("name", address.getName());
            addressMap.put("cellPhone", address.getCellPhone());
            addressMap.put("province", address.getProvince());
            addressMap.put("cityId", address.getCityId());
            addressMap.put("townId", address.getTownId());
            addressMap.put("address", address.getAddress());
            addressMap.put("postCode", address.getPostCode());
        }

        //优惠券封装
        Map<String, Object> couponMap = new HashMap<>();
        if (!StringUtils.isEmpty(lsOrderGoods.getCouponCode())) {

            couponMap.put("couponCode", lsOrderGoods.getCouponCode());
            couponMap.put("couponAmount", lsOrderGoods.getCouponAmount());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("goodsList", lsOrderGoods.getItems());
        result.put("couponGoodsList", lsOrderProduct);
        result.put("coupon", couponMap);
        result.put("points", points);
        result.put("availableAmount", availableAmount);
        result.put("address", addressMap);

        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(result);

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
        for (Map<String, Object> map:lsMap) {

            OrderGoodsDTO orderGoodsDTO = new OrderGoodsDTO();
            orderGoodsDTO.setGoodsId(ConvertUtils.toInteger(map.get("goodsId")));
            orderGoodsDTO.setSpecId(ConvertUtils.toInteger(map.get("specId")));
            orderGoodsDTO.setBuyCount(ConvertUtils.toInteger(map.get("buyCount")));

            orderProductIds.add(orderGoodsDTO);
        }

        MemberLogin member = this.memberAuth();
        String orderId = mallService.createOrder(member.getMemberId(), member.getUserName(),
                0, points, accountAmount, orderProductIds, couponCode,
                remark, addressId);

        if (StringUtils.isEmpty(orderId))
        {
            return new ReturnValue<String>(404, "订单创建失败");
        }

        ReturnValue<String> response = new ReturnValue(200, "订单创建成功");
        response.setData(orderId);
        return response;
    }

    /**取消订单
     *
     * @param order_id
     * @return
     */
    @RequestMapping(value = "{order_id}/cancel", method = RequestMethod.POST)
    public ReturnValue cancelOrder(@PathVariable("order_id") String order_id) {

        order_id = ConvertUtils.toString(order_id);

        MemberLogin member = this.memberAuth();
        mallService.cancelOrders(order_id, member.getMemberId(), 0);

        return new ReturnValue(200, "订单取消成功");
    }
}
