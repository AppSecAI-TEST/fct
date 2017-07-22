package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.json.JsonConverter;
import com.fct.core.utils.ConvertUtils;
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

        List<Map<String, Object>> lsMap = JsonConverter.toObject(orderProductInfo, List.class);
        List<OrderGoodsDTO> orderProductIds = new ArrayList<>();
        Integer goodsId = 0;
        Integer specId = 0;
        Integer buyCount = 0;
        for (Map<String, Object> map:lsMap) {

            goodsId = ConvertUtils.toInteger(map.get("goodsId"), 0);
            specId = ConvertUtils.toInteger(map.get("specId"), 0);
            buyCount = ConvertUtils.toInteger(map.get("buyCount"), 0);

            if (goodsId < 1)
                return new ReturnValue<>(404, "订单有产品已下架");
            if (buyCount < 1)
                return new ReturnValue<>(404, "购买数量不能小于1");


            OrderGoodsDTO orderGoodsDTO = new OrderGoodsDTO();
            orderGoodsDTO.setGoodsId(goodsId);
            orderGoodsDTO.setSpecId(specId);
            orderGoodsDTO.setBuyCount(buyCount);

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


    /**根据订单产品自增ID获取订单产品
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ReturnValue<OrderGoods> getProduct(@PathVariable("id") Integer id) {

        OrderGoods product = mallService.getOrderGoods(id);

        ReturnValue<OrderGoods> response = new ReturnValue<>();
        response.setData(product);

        return response;
    }
}
