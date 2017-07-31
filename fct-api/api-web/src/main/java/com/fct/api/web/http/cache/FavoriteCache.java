package com.fct.api.web.http.cache;

import com.fct.api.web.utils.FctResourceUrl;
import com.fct.artist.data.entity.Artist;
import com.fct.artist.interfaces.ArtistService;
import com.fct.core.utils.ConvertUtils;
import com.fct.mall.data.entity.Goods;
import com.fct.member.data.entity.MemberFavourite;
import com.fct.member.interfaces.MemberService;
import com.fct.member.interfaces.PageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FavoriteCache {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ProductCache productCache;

    @Autowired
    private FctResourceUrl fctResourceUrl;

    @Autowired
    private ArtistService artistService;


    public PageResponse<Map<String, Object>> findPageFavorite(Integer memberId, Integer fromType,
                                                              Integer pageIndex, Integer pageSize) {
        PageResponse<MemberFavourite> lsFavorite = memberService.findFavourite(memberId, fromType,
                pageIndex, pageSize);

        PageResponse<Map<String, Object>> pageMaps = new PageResponse<>();

        if (lsFavorite != null && lsFavorite.getTotalCount() > 0) {
            String typeIds = "";
            for (MemberFavourite favourite: lsFavorite.getElements()) {
                typeIds += (StringUtils.isEmpty(typeIds) ? "" : ",") + favourite.getRelatedId();
            }

            Map<Integer, Object> map = null;
            if (fromType == 0)
                map = this.findProducts(typeIds);
            else if (fromType == 1)
                map = this.findArtists(typeIds);

            List<Map<String, Object>> lsMaps = new ArrayList<>();
            Map<String, Object> subMap = null;
            for (MemberFavourite favourite: lsFavorite.getElements()) {

                if (map.containsKey(favourite.getRelatedId())) {
                    subMap = (Map<String, Object>) map.get(favourite.getRelatedId());
                    subMap.put("favoriteId", favourite.getId());
                    subMap.put("favType", favourite.getFavType());

                    lsMaps.add(subMap);
                }
            }

            pageMaps.setElements(lsMaps);
            pageMaps.setCurrent(lsFavorite.getCurrent());
            pageMaps.setTotalCount(lsFavorite.getTotalCount());
            pageMaps.setHasMore(lsFavorite.isHasMore());
        }

        return pageMaps;
    }

    public Boolean validRelatedId(Integer relatedId, Integer favType) {

        if (favType == 0)
            return productCache.findProductByIds(ConvertUtils.toString(relatedId)).size() > 0
                    ? true : false;
        else if (favType == 1)
            return artistService.findArtistByIds(ConvertUtils.toString(relatedId)).size() > 0
                    ? true : false;

        return false;
    }

    public Map<Integer, Object> findProducts(String productIds) {

        List<Goods> lsGoods = productCache.findProductByIds(productIds);

        Map<Integer, Object> map = new HashMap<>();
        if (lsGoods != null) {
            Map<String, Object> productMap = null;
            for (Goods goods: lsGoods) {

                productMap = new HashMap<>();
                productMap.put("id", goods.getId());
                productMap.put("name", goods.getName());
                productMap.put("title", "");
                productMap.put("image", fctResourceUrl.getImageUrl(goods.getVideoImg()));
                map.put(goods.getId(), productMap);
            }
        }

        return map;
    }


    public Map<Integer, Object> findArtists(String artistIds) {

        List<Artist> lsArtist = artistService.findArtistByIds(artistIds);

        Map<Integer, Object> map = new HashMap<>();
        if (lsArtist != null) {
            Map<String, Object> artistMap = null;
            for (Artist artist: lsArtist) {

                artistMap = new HashMap<>();
                artistMap.put("id", artist.getId());
                artistMap.put("name", artist.getName());
                artistMap.put("title", artist.getTitle());
                artistMap.put("image", fctResourceUrl.getImageUrl(artist.getBanner()));
                map.put(artist.getId(), artistMap);
            }
        }

        return map;
    }
}
