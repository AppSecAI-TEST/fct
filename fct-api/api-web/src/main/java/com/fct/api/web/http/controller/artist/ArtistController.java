package com.fct.api.web.http.controller.artist;

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

import java.util.List;

/**
 * Created by z on 17-6-30.
 */
@RestController
@RequestMapping(value = "/artists")
public class ArtistController extends BaseController {

    @Autowired
    private ArtistService artistService;

    /**列出所有艺术家带分页
     *
     * @param page_index
     * @param page_size
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<Artist>> findArtists(Integer page_index, Integer page_size) {

        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size);

        PageResponse<Artist> lsArtist = artistService.findArtist("", 1, page_index, page_size);

        ReturnValue<PageResponse<Artist>> response = new ReturnValue<>();
        response.setData(lsArtist);

        return  response;
    }

    /**艺术家详情
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ReturnValue<Artist> getArtist(@PathVariable("id") Integer id) {

        Artist artist = artistService.getArtist(id);

        ReturnValue<Artist> response = new ReturnValue<>();
        response.setData(artist);

        return  response;
    }

    /**通过产品的所有艺术家
     *
     * @param product_id
     * @return
     */
    @RequestMapping(value = "by-product")
    public ReturnValue<List<Artist>> getArtistsByProductId(Integer product_id) {

        product_id = ConvertUtils.toInteger(product_id);

        List<Artist> lsArtist = artistService.findArtistByGoodsId(product_id);

        ReturnValue<List<Artist>> response = new ReturnValue<>();
        response.setData(lsArtist);

        return  response;
    }
}
