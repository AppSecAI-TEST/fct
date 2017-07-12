package com.fct.api.web.http.cache;

import com.fct.api.web.utils.FctResourceUrl;
import com.fct.artist.data.entity.ArtistComment;
import com.fct.artist.interfaces.ArtistService;
import com.fct.artist.interfaces.PageResponse;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.DateUtils;
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
public class ArtistCommentCache {

    @Autowired
    private FctResourceUrl fctResourceUrl;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private MemberCache memberCache;

    /**获取艺术家评论分页
     *
     * @param artistId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public PageResponse<Map<String, Object>> findPageArtistComment(Integer artistId, Integer pageIndex, Integer pageSize) {

        PageResponse<ArtistComment> lsComment = artistService.findArtistComment(artistId, 0, "",
                1, 0, pageIndex, pageSize);

        PageResponse<Map<String, Object>> pageMaps = new PageResponse<>();
        if (lsComment != null && lsComment.getTotalCount() > 0) {

            pageMaps.setElements(getFormatCommetns(lsComment.getElements(), false));
            pageMaps.setCurrent(lsComment.getCurrent());
            pageMaps.setTotalCount(lsComment.getTotalCount());
            pageMaps.setHasMore(lsComment.isHasMore());
        }

        return pageMaps;
    }

    /**获取评论回复
     *
     * @param artistId
     * @param replayId
     * @return
     */
    private List<Map<String, Object>> getReplays(Integer artistId, Integer replayId) {

        return getFormatCommetns(artistService.findReplyComment(replayId, 0), true);
    }

    /**格式化内容
     *
     * @param comments
     * @return
     */
    private List<Map<String, Object>> getFormatCommetns(List<ArtistComment> comments, Boolean hasReplay) {

        hasReplay = ConvertUtils.toBoolean(hasReplay);

        List<Map<String, Object>> lsMaps = new ArrayList<>();
        if (comments == null)
        {
            return  lsMaps;
        }

        for (ArtistComment comment:comments) {

            Map<String, Object> map = new HashMap<>();
            MemberDTO member = memberCache.getMember(comment.getMemberId());
            map.put("id", comment.getId());
            map.put("userName", member.getUserName());
            map.put("headPortrait", fctResourceUrl.getImageUrl(member.getHeadPortrait()));
            map.put("content", comment.getContent());
            map.put("replyContent", getReplays(comment.getArtistId(), comment.getReplyId()));
            map.put("createTime", DateUtils.formatDate(comment.getCreateTime(),"yyyy-MM-dd"));

            lsMaps.add(map);
        }

        return lsMaps;
    }
}
