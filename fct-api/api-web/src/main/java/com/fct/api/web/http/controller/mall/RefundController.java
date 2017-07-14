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
     * @param order_product_id
     * @param page_index
     * @param page_size
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<OrderRefundDTO>> findRefund(String order_product_id, Integer page_index, Integer page_size) {

        MemberLogin member = this.memberAuth();

        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size);

        PageResponse<OrderRefundDTO> pageResponse = mallService.findOrderRefund("", order_product_id, 0,
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

        return new ReturnValue(200, "退货关闭成功");
    }
}
