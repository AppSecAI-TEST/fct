package com.fct.api.web.http.cache;

import com.fct.api.web.utils.FctResourceUrl;
import com.fct.artist.data.entity.ArtistDynamic;
import com.fct.artist.interfaces.ArtistService;
import com.fct.artist.interfaces.PageResponse;
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
public class ArtistDynamicCache {

    @Autowired
    private FctResourceUrl fctResourceUrl;

    @Autowired
    private ArtistService artistService;

    public PageResponse<Map<String, Object>> findPageArtistDynamic(Integer artistId, Integer pageIndex, Integer pageSize) {

        PageResponse<ArtistDynamic> pageDynamic = artistService.findArtistDynamic(artistId, "", 1,
                "", "", pageIndex, pageSize);

        PageResponse<Map<String, Object>> pageMaps = new PageResponse<>();
        if (pageDynamic != null && pageDynamic.getTotalCount() > 0) {

            List<Map<String, Object>> lsMaps = new ArrayList<>();
            for (ArtistDynamic dynamic:pageDynamic.getElements()) {

                Map<String, Object> map = new HashMap<>();
                map.put("id", dynamic.getId());
                map.put("isTop", dynamic.getIsTop());
                map.put("content", dynamic.getContent());
                map.put("images", fctResourceUrl.getMutilImageUrl(dynamic.getImages()));
                map.put("videoImage", fctResourceUrl.getImageUrl(dynamic.getVideoImg()));
                map.put("videoId", dynamic.getVideoId());

                lsMaps.add(map);
            }

            pageMaps.setElements(lsMaps);
            pageMaps.setCurrent(pageDynamic.getCurrent());
            pageMaps.setTotalCount(pageDynamic.getTotalCount());
            pageMaps.setHasMore(pageDynamic.isHasMore());
        }

        return pageMaps;
    }
}
