package com.fct.api.web.http.controller.promotion;

import com.fct.api.web.http.cache.CouponCache;
import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.member.data.entity.MemberLogin;
import com.fct.promotion.data.entity.CouponPolicy;
import com.fct.promotion.interfaces.PromotionService;
import com.fct.promotion.interfaces.dto.CouponCodeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by z on 17-6-30.
 */
@RestController
@RequestMapping(value = "/promotion/coupons")
public class CouponController extends BaseController {

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private CouponCache couponCache;

    /**产品可用优惠券列表
     *
     * @param product_id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<List<Map<String, Object>>> findCoupons(Integer product_id) {

        product_id = ConvertUtils.toInteger(product_id);
        List<Map<String, Object>> lsMaps = couponCache.findCacheCanReceive(product_id);

        ReturnValue<List<Map<String, Object>>> response = new ReturnValue<>();
        response.setData(lsMaps);

        return  response;
    }

    /**领取优惠券
     *
     * @param coupon_id
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ReturnValue saveCoupon(Integer coupon_id) {

        coupon_id = ConvertUtils.toInteger(coupon_id);

        MemberLogin member = this.memberAuth();

        String code = promotionService.receiveCouponCode(member.getMemberId(), coupon_id);
        if (StringUtils.isEmpty(code)) {

            return new ReturnValue(404, "领取失败");
        }

        return new ReturnValue(200, "领取成功");
    }

    /**用户的优惠券
     *
     * @param status
     * @return
     */
    @RequestMapping(value = "by-member", method = RequestMethod.GET)
    public ReturnValue<Map<String, Object>> findMemberCoupons(Integer status) {

        status = ConvertUtils.toInteger(status,-1);

        MemberLogin member = this.memberAuth();

        List<Map<String, Object>> lsMaps = couponCache.findMemberReceive(member.getMemberId(), status);

        Map<String, Object> map = new HashMap<>();
        map.put("couponList", lsMaps);
        if (status == 0)
            map.put("canReceiveCount", couponCache.findCacheCanReceive(0).size());

        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(map);

        return  response;
    }

    /**产品可用券数量
     *
     * @param product_id
     * @return
     */
    @RequestMapping(value = "has-product", method = RequestMethod.GET)
    public ReturnValue<Integer> hasProductCoupon(Integer product_id) {

        product_id = ConvertUtils.toInteger(product_id);
        Integer couponCount = promotionService.getReceiveCountByProduct(product_id);

        ReturnValue<Integer> response = new ReturnValue<>();
        response.setData(couponCount);

        return response;
    }
}
