package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.cache.MemberCache;
import com.fct.api.web.http.cache.ProductCommentCache;
import com.fct.api.web.http.controller.BaseController;
import com.fct.core.json.JsonConverter;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.DateUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.mall.data.entity.OrderComment;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.PageResponse;
import com.fct.member.data.entity.MemberLogin;
import com.fct.member.interfaces.MemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**产品评论
 *
 */
@RestController
@RequestMapping(value = "/mall/order/comments")
public class OrderCommentController extends BaseController {

    @Autowired
    private MallService mallService;

    @Autowired
    private ProductCommentCache productCommentCache;

    /**获取列表
     *
     * @param product_id
     * @param page_index
     * @param page_size
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<Map<String, Object>>> findComments(Integer product_id,
                                                               Integer page_index, Integer page_size) {
        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size, 20);

        ReturnValue<PageResponse<Map<String, Object>>> response = new ReturnValue<>();

        response.setData(productCommentCache.findPageProductComment(product_id, page_index, page_size));

        return response;
    }

    /**添加评论
     *
     * @param order_id
     * @param express_score
     * @param has_anonymous
     * @param sale_score
     * @param product_info
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ReturnValue saveComment(String order_id, Integer express_score, Integer has_anonymous, Integer sale_score, String product_info) {
        //[{goodsId:1, descScore:5, content:xxx, picture:xxxx},...]

        order_id = ConvertUtils.toString(order_id);
        express_score = ConvertUtils.toInteger(express_score);
        has_anonymous = ConvertUtils.toInteger(has_anonymous);
        sale_score = ConvertUtils.toInteger(sale_score);
        if (StringUtils.isEmpty(order_id)) {
            return new ReturnValue(404, "订单号不存在");
        }

        MemberLogin member = this.memberAuth();

        List<OrderComment> lsComment = JsonConverter.toObject(product_info, List.class);

        mallService.createOrderCommment(order_id, has_anonymous, express_score, sale_score, lsComment);

        return new ReturnValue(200, "评论成功");
    }
}
