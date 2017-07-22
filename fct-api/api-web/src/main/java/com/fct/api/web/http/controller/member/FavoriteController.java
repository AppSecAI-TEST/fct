package com.fct.api.web.http.controller.member;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.member.data.entity.MemberFavourite;
import com.fct.member.data.entity.MemberLogin;
import com.fct.member.interfaces.PageResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by z on 17-7-5.
 */
@RestController
@RequestMapping(value = "/member/favorites")
public class FavoriteController extends BaseController {

    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<MemberFavourite>> findFovorite(Integer from_type,
                                                                   Integer page_index, Integer page_size) {

        from_type = ConvertUtils.toInteger(from_type);
        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size);
        if (from_type < 1)
            return new ReturnValue<>(404, "请带入收藏类型");

        MemberLogin member = this.memberAuth();

        PageResponse<MemberFavourite> lsFavorite = memberService.findFavourite(member.getMemberId(), from_type, page_index, page_size);

        ReturnValue<PageResponse<MemberFavourite>> response = new ReturnValue<>();
        response.setData(lsFavorite);

        return response;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ReturnValue<Integer> saveFavorite(Integer from_id, Integer from_type) {

        from_id = ConvertUtils.toInteger(from_id);
        from_type = ConvertUtils.toInteger(from_type);
        if (from_id < 1)
            return new ReturnValue<>(404, "收藏的产品或艺术家不存在");

        MemberLogin member = this.memberAuth();

        Integer favoriteCount = memberService.getFavouriteCount(member.getMemberId(), from_type, from_id);
        if (favoriteCount == 0) {
            memberService.saveFavourite(member.getMemberId(), from_type, from_id);
        } else {
            memberService.deleteFavourite(member.getMemberId(), from_type, from_id);
        }

        ReturnValue<Integer> response = new ReturnValue<>();
        response.setData(favoriteCount == 0 ? 1 : 0);

        return response;
    }
}
