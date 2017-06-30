package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.mall.data.entity.OrderComment;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.PageResponse;
import com.fct.member.data.entity.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**产品评论
 *
 */
@RestController
@RequestMapping(value = "/products/{product_id}/comments")
public class ProductCommentController extends BaseController {

    @Autowired
    private MallService mallService;

    /**获取列表
     *
     * @param product_id
     * @param page_index
     * @param page_size
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<OrderComment>> findComments(@PathVariable("product_id") Integer product_id,
                                                               Integer page_index, Integer page_size) {
        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size, 20);

        PageResponse<OrderComment> lsComment = mallService.findOrderComment(product_id, 0, "",
                "", 1, "", "", page_index, page_size);

        ReturnValue<PageResponse<OrderComment>> response = new ReturnValue<>();
        response.setData(lsComment);

        return response;
    }

    /**添加评论
     *
     * @param product_id
     * @param order_id
     * @param content
     * @param desc_score
     * @param express_score
     * @param sale_score
     * @param picture
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ReturnValue saveComment(@PathVariable("product_id") Integer product_id,
                                   String order_id, String content, Integer desc_score,
                                   Integer express_score, Integer sale_score, String picture) {

        MemberLogin member = this.memberAuth();

        OrderComment comment = new OrderComment();

        //订单号
        comment.setOrderId(order_id);
        //产品ID
        comment.setGoodsId(product_id);
        //用户id和手机
        comment.setMemberId(member.getMemberId());
        comment.setCellPhone(member.getCellPhone());
        //内容
        comment.setContent(content);
        //描述评分
        comment.setDescScore(desc_score);
        //物流评分
        comment.setLogisticsScore(express_score);
        //销售评分
        comment.setSaleScore(sale_score);
        //晒图
        comment.setPicture(picture);

        mallService.createOrderCommment(comment);

        return new ReturnValue(200, "评论成功");
    }
}
