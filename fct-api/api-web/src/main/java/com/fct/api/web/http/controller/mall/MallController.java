package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.cache.ArticleCache;
import com.fct.api.web.http.cache.ProductCache;
import com.fct.api.web.http.controller.BaseController;
import com.fct.artist.data.entity.Artist;
import com.fct.artist.interfaces.ArtistService;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsGrade;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by z on 17-6-23.
 */
@RestController
@RequestMapping(value = "/mall")
public class MallController extends BaseController{

    @Autowired
    private MallService mallService;

    @Autowired
    private ProductCache productCache;

    @Autowired
    private ArticleCache articleCache;

    @Autowired
    private ArtistService artistService;

    /**首页数据获取
     *
     * @param category_id
     * @param level_id
     * @param page_index
     * @param page_size
     * @return
     */
    @RequestMapping(value = "home", method = RequestMethod.GET)
    public ReturnValue<Map<String,Object>> getHome(String category_id, Integer level_id,
                                                 Integer page_index, Integer page_size) {

        String categoryId = ConvertUtils.toString(category_id);
        Integer levelId = ConvertUtils.toInteger(level_id);
        Integer pageIndex = ConvertUtils.toPageIndex(page_index);
        Integer pageSize = ConvertUtils.toInteger(page_size);
        pageSize = pageSize > 0 ? pageSize : 20;

        List<GoodsGrade> goodsGrades = mallService.findGoodsGrade();
        List<Map<String,Object>> lsGrade = new ArrayList<>();
        for (GoodsGrade grade:goodsGrades) {
            Map<String,Object> map = new HashMap<>();
            map.put("id", grade.getId());
            map.put("name", grade.getName());
            map.put("img", getImgUrl(grade.getImg()));
            lsGrade.add(map);
        }

        PageResponse<Goods> lsGoods = mallService.findGoods("", categoryId, levelId, 0,
                0,0,0,1,pageIndex, pageSize);
        PageResponse<Map<String, Object>> pageMaps = new PageResponse<>();
        if (lsGoods != null && lsGoods.getTotalCount() > 0) {

            List<Map<String, Object>> lsMap = new ArrayList<>();
            for (Goods goods : lsGoods.getElements()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", goods.getId());
                map.put("categoryCode", goods.getCategoryCode());
                map.put("gradeId", goods.getGradeId());
                map.put("name", goods.getName());
                map.put("subTitle", goods.getSubTitle());
                map.put("intro", goods.getIntro());
                map.put("videoImg", getImgUrl(goods.getVideoImg()));
                map.put("multiImages", getMutilImgUrl(goods.getMultiImages()));
                map.put("viewCount", goods.getViewCount());
                map.put("commentCount", goods.getCommentCount());

                lsMap.add(map);
            }

            pageMaps.setElements(lsMap);
            pageMaps.setCurrent(lsGoods.getCurrent());
            pageMaps.setTotalCount(lsGoods.getTotalCount());
            pageMaps.setHasMore(lsGoods.isHasMore());
        }

        Map<String,Object> map = new HashMap<>();
        map.put("goodsGradeList",lsGrade);
        map.put("goodsList", pageMaps);

        ReturnValue<Map<String,Object>> response = new ReturnValue<>();
        response.setData(map);

        return  response;
    }

    /**获取PC首页数据
     *
     * @param article_code
     * @param article_count
     * @param product_count
     * @param artist_count
     * @return
     */
    @RequestMapping(value = "pc-home", method = RequestMethod.GET)
    public ReturnValue<Map<String, Object>> getPcHome(String article_code, Integer article_count, Integer product_count, Integer artist_count)
    {

        article_code = ConvertUtils.toString(article_code);
        article_count = ConvertUtils.toInteger(article_count, 4);
        product_count = ConvertUtils.toInteger(product_count, 10);
        artist_count = ConvertUtils.toInteger(artist_count, 7);

        //产品列表
        //name,defaultImage,intro
        List<Map<String, Object>> lsProduct = productCache.pcHomeProducts(product_count);

        //艺术家列表
        //name,heand, intro
        com.fct.artist.interfaces.PageResponse<Artist> pageArtist =
                artistService.findArtist("", 1, 1, artist_count);

        List<Map<String, Object>> lsArtist = new ArrayList<>();
        if (pageArtist != null && pageArtist.getTotalCount() > 0) {

            for (Artist artist : pageArtist.getElements()) {
                Map<String, Object> mapArtist = new HashMap<>();
                mapArtist.put("id", artist.getId());
                mapArtist.put("name", artist.getName());
                mapArtist.put("headPortrait", fctResourceUrl.thumbnail(artist.getHeadPortrait(), 100));
                mapArtist.put("intro", artist.getIntro());

                lsArtist.add(mapArtist);
            }
        }

        //文章列表
        PageResponse<Map<String, Object>> pageArticle = articleCache.findPageArticle(article_code,
                1, article_count);

        Map<String, Object> map = new HashMap<>();
        map.put("productList", lsProduct);
        map.put("artistList", lsArtist);
        map.put("articleList", pageArticle != null ? pageArticle.getElements() : null);

        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(map);

        return response;
    }
}
