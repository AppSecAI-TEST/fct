package com.fct.api.web.http.cache;

import com.fct.api.web.utils.FctResourceUrl;
import com.fct.core.utils.DateUtils;
import com.fct.mall.data.entity.OrderComment;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.PageResponse;
import com.fct.member.interfaces.MemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by z on 17-7-7.
 */
@Service
public class ProductCommentCache {

    @Autowired
    private MallService mallService;

    @Autowired
    private FctResourceUrl fctResourceUrl;

    @Autowired
    private MemberCache memberCache;

    public PageResponse<Map<String, Object>> findPageProductComment(Integer productId, Integer pageIndex, Integer pageSize) {

        PageResponse<OrderComment> pageComment = mallService.findOrderComment(productId, 0, "",
                "", 1, "", "", pageIndex, pageSize);

        PageResponse<Map<String, Object>> pageMaps = new PageResponse<>();

        if (pageComment != null && pageComment.getTotalCount() > 0) {

            List<Map<String, Object>> lsMaps = new ArrayList<>();
            for (OrderComment comment:pageComment.getElements()) {

                Map<String, Object> map = new HashMap<>();
                MemberDTO member = memberCache.getMember(comment.getMemberId());

                map.put("id", comment.getId());
                map.put("userName", member.getUserName());
                map.put("headPortrait", fctResourceUrl.getImageUrl(member.getHeadPortrait()));
                map.put("descScore", comment.getDescScore());
                map.put("content", comment.getContent());
                map.put("replyContent", comment.getReplyContent());
                map.put("createTime", DateUtils.formatDate(comment.getCreateTime(),"yyyy-MM-dd"));
                map.put("pictures", fctResourceUrl.getMutilImageUrl(comment.getPicture()));

                lsMaps.add(map);
            }

            pageMaps.setElements(lsMaps);
            pageMaps.setCurrent(pageComment.getCurrent());
            pageMaps.setTotalCount(pageComment.getTotalCount());
            pageMaps.setHasMore(pageComment.isHasMore());
        }

        return pageMaps;
    }
}
