package com.fct.api.web.http.controller.promotion;

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

import java.util.List;

/**
 * Created by z on 17-6-30.
 */
@RestController
@RequestMapping(value = "coupons")
public class CouponController extends BaseController {

    @Autowired
    private PromotionService promotionService;

    /**产品可用优惠券列表
     *
     * @param product_id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<List<CouponPolicy>> findCoupons(Integer product_id) {

        product_id = ConvertUtils.toInteger(product_id);
        List<CouponPolicy> lsCoupon = promotionService.findCanReceiveCouponPolicy(product_id);

        ReturnValue<List<CouponPolicy>> response = new ReturnValue<>();
        response.setData(lsCoupon);

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
    public ReturnValue<List<CouponCodeDTO>> findMemberCoupons(Integer status) {

        status = ConvertUtils.toInteger(status);

        MemberLogin member = this.memberAuth();
        List<CouponCodeDTO> lsCoupon = promotionService.findMemberCouponCode(0, member.getMemberId(),
                "", status, false, 1, 20);

        ReturnValue<List<CouponCodeDTO>> response = new ReturnValue<>();
        response.setData(lsCoupon);

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
