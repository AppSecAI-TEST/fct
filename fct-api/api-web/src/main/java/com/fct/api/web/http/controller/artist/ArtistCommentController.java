package com.fct.api.web.http.controller.artist;

import com.fct.api.web.http.cache.ArtistCommentCache;
import com.fct.api.web.http.controller.BaseController;
import com.fct.artist.data.entity.ArtistComment;
import com.fct.artist.interfaces.ArtistService;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.artist.interfaces.PageResponse;
import com.fct.member.data.entity.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by z on 17-7-7.
 */
@RestController
@RequestMapping(value = "/artists/{artist_id}/comments")
public class ArtistCommentController extends BaseController {

    @Autowired
    private ArtistCommentCache artistCommentCache;

    @Autowired
    ArtistService artistService;

    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<Map<String, Object>>> findComment(@PathVariable("artist_id") Integer artist_id,
                                                                      Integer page_index, Integer page_size) {

        if (artist_id < 1) {
            return new ReturnValue<>(404, "艺术家不存在");
        }

        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size, 20);

        ReturnValue<PageResponse<Map<String, Object>>> response = new ReturnValue<>();
        response.setData(artistCommentCache.findPageArtistComment(artist_id, 0, page_index, page_size));

        return response;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ReturnValue saveComment(@PathVariable("artist_id") Integer artist_id, String content) {

        artist_id = ConvertUtils.toInteger(artist_id);
        content = ConvertUtils.toString(content);
        if (artist_id < 1) {

            return new ReturnValue(404, "艺术家不存在");
        }

        if (StringUtils.isEmpty(content)) {

            return new ReturnValue(404, "请输入评论内容");
        }

        MemberLogin member = this.memberAuth();

        ArtistComment comment = new ArtistComment();
        comment.setMemberId(member.getMemberId());
        comment.setUserName(member.getUserName());
        comment.setArtistId(artist_id);
        comment.setContent(content);

        artistService.saveArtistComment(comment);

        return new ReturnValue(200, "评论发送成功");
    }
}
