package com.fct.api.web.http.controller.artist;

import com.fct.api.web.http.cache.ArtistDynamicCache;
import com.fct.api.web.http.cache.ProductCache;
import com.fct.api.web.http.controller.BaseController;
import com.fct.artist.data.entity.Artist;
import com.fct.artist.interfaces.ArtistService;
import com.fct.artist.interfaces.PageResponse;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by z on 17-6-30.
 */
@RestController
@RequestMapping(value = "/artists")
public class ArtistController extends BaseController {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ArtistDynamicCache artistDynamicCache;

    @Autowired
    private ProductCache productCache;

    /**
     * 列出所有艺术家带分页
     *
     * @param page_index
     * @param page_size
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<Map<String, Object>>> findArtists(Integer page_index, Integer page_size) {

        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size, 20);

        PageResponse<Artist> lsArtist = artistService.findArtist("", 1, page_index, page_size);

        PageResponse<Map<String, Object>> pageMaps = new PageResponse<>();
        List<Map<String, Object>> lsMaps = new ArrayList<>();
        if (lsArtist != null && lsArtist.getTotalCount() > 0) {

            for (Artist artist : lsArtist.getElements()) {
                Map<String, Object> map = new HashMap<>();

                map.put("id", artist.getId());
                map.put("name", artist.getName());
                map.put("intro", artist.getIntro());
                map.put("followCount", artist.getFollowCount());
                map.put("goodsCount", artist.getGoodsCount());
                map.put("image", fctResourceUrl.thumbLarge(artist.getMainImg()));

                lsMaps.add(map);
            }

            pageMaps.setElements(lsMaps);
            pageMaps.setCurrent(lsArtist.getCurrent());
            pageMaps.setTotalCount(lsArtist.getTotalCount());
            pageMaps.setHasMore(lsArtist.isHasMore());
        }

        ReturnValue<PageResponse<Map<String, Object>>> response = new ReturnValue<>();
        response.setData(pageMaps);

        return response;
    }

    /**
     * 艺术家详情
     *
     * @param id
     * @param page_index
     * @param page_size
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ReturnValue<Map<String, Object>> getArtist(@PathVariable("id") Integer id,
                                                      Integer page_index, Integer page_size) {
        id = ConvertUtils.toInteger(id);
        if (id < 1) {
            return new ReturnValue<>(404, "艺术家不存在");
        }

        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size, 20);

        Artist artist = artistService.getArtist(id);
        Map<String, Object> map = new HashMap<>();
        if (artist != null) {
            map.put("id", artist.getId());
            map.put("name", artist.getName());
            //因为网站现在人气不高，占时用浏览数代替关注数
            map.put("followCount", artist.getViewCount());
            map.put("banner", fctResourceUrl.thumbLarge(artist.getBanner()));
            //动态
            map.put("dynamicList", artistDynamicCache.findPageArtistDynamic(id, page_index, page_size));
        }

        //添加浏览量
        artistService.addArtistFollowCount(id, 1);

        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(map);

        return response;
    }

    /**
     * 产品的所有艺术家
     *
     * @param product_id
     * @return
     */
    @RequestMapping(value = "by-product", method = RequestMethod.GET)
    public ReturnValue<List<Map<String, Object>>> getArtistsByProductId(Integer product_id) {

        product_id = ConvertUtils.toInteger(product_id);
        if (product_id < 1) {
            return new ReturnValue<>(404, "产品不存在");
        }

        //根据产品ID获取艺人列表
        List<Artist> lsArtist = artistService.findArtistByGoodsId(product_id);
        //重新封装艺人列表
        List<Map<String, Object>> lsMap = new ArrayList<>();
        if (!lsArtist.isEmpty()) {
            for (Artist artist : lsArtist) {

                Map<String, Object> map = new HashMap<>();
                map.put("id", artist.getId());
                map.put("name", artist.getName());
                map.put("headPortrait", fctResourceUrl.getAvatarUrl(artist.getHeadPortrait()));
                map.put("description", artist.getDescription());

                //获取艺术家指定个数产品
                map.put("products", productCache.guessProducts(product_id, 0, artist.getId(), 3));

                lsMap.add(map);
            }
        }
        ReturnValue<List<Map<String, Object>>> response = new ReturnValue<>();
        response.setData(lsMap);

        return response;
    }
}
