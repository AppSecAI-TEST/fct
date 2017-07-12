package com.fct.api.web.http.controller.artist;

import com.fct.api.web.http.cache.ArtistDynamicCache;
import com.fct.api.web.http.controller.BaseController;
import com.fct.artist.interfaces.PageResponse;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by z on 17-7-7.
 */
@RestController
@RequestMapping(value = "/artists/{artist_id}/dynamics")
public class ArtistDynamicController extends BaseController {

    @Autowired
    private ArtistDynamicCache artistDynamicCache;


    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<Map<String, Object>>> findDynamic(@PathVariable("artist_id") Integer artist_id,
                                                                      Integer page_index, Integer page_size) {
        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size, 20);

        ReturnValue<PageResponse<Map<String, Object>>> response = new ReturnValue<>();
        response.setData(artistDynamicCache.findPageArtistDynamic(artist_id, page_index, page_size));

        return response;
    }
}
