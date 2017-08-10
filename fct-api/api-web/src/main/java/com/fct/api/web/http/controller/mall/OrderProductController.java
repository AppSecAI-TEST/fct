package com.fct.api.web.http.controller.mall;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fct.api.web.http.controller.BaseController;
import com.fct.core.json.JsonConverter;
import com.fct.core.utils.ReturnValue;
import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.interfaces.FinanceService;
import com.fct.mall.data.entity.OrderGoods;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.OrderGoodsDTO;
import com.fct.mall.interfaces.OrderGoodsResponse;
import com.fct.member.data.entity.MemberAddress;
import com.fct.member.data.entity.MemberLogin;
import com.fct.promotion.interfaces.dto.OrderProductDTO;
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

@RestController
@RequestMapping(value = "/mall/orders/products")
public class OrderProductController extends BaseController {

    @Autowired
    private MallService mallService;

    @Autowired
    private FinanceService financeService;

    /**根据json格式获取等待生成订单的产品和优惠券
     *
     * @param orderProductInfo
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<Map<String, Object>> getOrderProducts(String orderProductInfo) {


        MemberLogin member = this.memberAuth();

        List<OrderGoodsDTO> orderProductIds = JsonConverter.toObject(orderProductInfo,
                new TypeReference<List<OrderGoodsDTO>>(){});

        for (OrderGoodsDTO product : orderProductIds) {
            if (product.getGoodsId() < 1)
                return new ReturnValue<>(404, "订单有产品已下架");
            if (product.getBuyCount() < 1)
                return new ReturnValue<>(404, "购买数量不能小于1");
        }

        OrderGoodsResponse lsOrderGoods = mallService.getSubmitOrderGoods(member.getMemberId(), orderProductIds);
        if (lsOrderGoods == null || lsOrderGoods.getItems().size() < 1)
            return new ReturnValue<>(404, "非法请求");

        //优惠券使用List<OrderProductDTO
        List<OrderProductDTO> lsOrderProduct = new ArrayList<>();
        for (OrderGoods orderGoods:lsOrderGoods.getItems()) {

            //设置图片
            orderGoods.setImg(fctResourceUrl.thumbSmall(orderGoods.getImg()));

            if (StringUtils.isEmpty(lsOrderGoods.getCouponCode())) {

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

    /**根据订单产品自增ID获取订单产品
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ReturnValue<Map<String, Object>> getProduct(@PathVariable("id") Integer id) {

        OrderGoods product = mallService.getOrderGoods(id);
        Map<String, Object> map = new HashMap<>();
        if (product != null) {

            map.put("id", product.getId());
            map.put("orderId", product.getOrderId());
            map.put("goodsId", product.getGoodsId());
            map.put("goodsSpecId", product.getGoodsSpecId());
            map.put("name", product.getName());
            map.put("specName", product.getSpecName());
            map.put("img", fctResourceUrl.thumbSmall(product.getImg()));
            map.put("price", product.getPayAmount().divide(new BigDecimal(product.getBuyCount())));
            map.put("buyCount", product.getBuyCount());
            map.put("payAmount", product.getPayAmount());

            List<String> lsReason = new ArrayList<>();
            lsReason.add("做工问题");
            lsReason.add("其他原因");
            map.put("reasons", lsReason);
        }

        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(map);

        return response;
    }
}
