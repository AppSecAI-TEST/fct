package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.mall.data.entity.OrderRefund;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.OrderRefundDTO;
import com.fct.mall.interfaces.PageResponse;
import com.fct.member.data.entity.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by z on 17-7-13.
 */

@RestController
@RequestMapping(value = "/mall/refunds")
public class RefundController extends BaseController {

    @Autowired
    private MallService mallService;


    /**用户退款列表
     *
     * @param keyword
     * @param page_index
     * @param page_size
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<OrderRefundDTO>> findRefund(String keyword, Integer page_index, Integer page_size) {

        MemberLogin member = this.memberAuth();

        String orderId = "";
        String goodsName = "";

        if (StringUtils.isNumeric(keyword))
            orderId = ConvertUtils.toString(keyword);
        else
            goodsName = ConvertUtils.toString(keyword);

        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size);

        PageResponse<OrderRefundDTO> pageResponse = mallService.findOrderRefund(orderId, goodsName, 0,
                member.getMemberId(), -1, "", "", page_index, page_size);

        ReturnValue<PageResponse<OrderRefundDTO>> response = new ReturnValue<>();
        response.setData(pageResponse);

        return response;
    }

    /**退款详情
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ReturnValue<OrderRefund> getRefund(@PathVariable("id") Integer id) {

        if (id < 1) {
            return new ReturnValue<>(404, "退换货记录不存在");
        }

        OrderRefund refund = mallService.getOrderRefund(id);

        ReturnValue<OrderRefund> response = new ReturnValue<>();
        response.setData(refund);

        return response;
    }

    /**申请退款
     *
     * @param order_id
     * @param order_product_id
     * @param service_type
     * @param reason
     * @param description
     * @param images
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ReturnValue saveRefund(String order_id, Integer order_product_id, Integer service_type,
                                  String reason, String description, String images) {

        if (StringUtils.isEmpty(order_id))
            return new ReturnValue(404, "订单不存在");

        if (order_product_id < 1)
            return new ReturnValue(404, "订单产品不存在");

        if (StringUtils.isEmpty(description) || description.length() < 3)
            return new ReturnValue(404, "请说明退换货原因");


        MemberLogin member = this.memberAuth();

        mallService.createOrderRefund(member.getMemberId(), order_id, order_product_id,
                0, service_type, 1, reason, description, images);

        return new ReturnValue(200, "申请提交成功");
    }

    /**退款返还货物
     *
     * @param id
     * @param description
     * @param images
     * @return
     */
    @RequestMapping(value = "{id}/express", method = RequestMethod.POST)
    public ReturnValue expressRefund(@PathVariable("id") Integer id, String description, String images) {

        MemberLogin member = this.memberAuth();

        if (id < 1)
            return new ReturnValue(404, "退换货记录不存在");

        mallService.handleOrderRefund("express", member.getMemberId(), id,
                1, description, images, 0);

        return new ReturnValue(200, "快递信息提交成功");
    }

    /**关闭退款申请
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/close", method = RequestMethod.POST)
    public ReturnValue closeRefund(@PathVariable("id") Integer id) {

        MemberLogin member = this.memberAuth();
        mallService.handleOrderRefund("close", member.getMemberId(), id,
                1, "", "", 0);

        return new ReturnValue(200, "退换货关闭成功");
    }
}
