package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.json.JsonResponseEntity;
import com.fct.api.web.http.model.Product;
import com.fct.artist.data.entity.Artist;
import com.fct.artist.interfaces.ArtistService;
import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsMaterial;
import com.fct.mall.interfaces.MallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by z on 17-6-23.
 */

@RestController
@RequestMapping(value = "/product")
public class ProductController {

    @Autowired
    private MallService mallService;

    @Autowired
    private ArtistService artistService;

    @RequestMapping(value = "show", method = RequestMethod.GET)
    public JsonResponseEntity<Product> show(Integer goodsId) {

        Goods goods = mallService.getGoods(goodsId);
        List<GoodsMaterial> goodsMaterials = mallService.findMaterialByGoods(goodsId);
        List<Artist> artists = artistService.findArtistByGoodsId(goodsId);

        Product product = new Product();
        product.goods = goods;
        product.artists = artists;
        product.goodsMaterials = goodsMaterials;

        JsonResponseEntity<Product> response = new JsonResponseEntity<>();
        response.setData(product);

        return  response;
    }
}
