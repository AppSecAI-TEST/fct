package com.fct.api.web.http.model;

import com.fct.artist.data.entity.Artist;
import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsMaterial;
import lombok.Data;

import java.util.List;

/**
 * Created by z on 17-6-23.
 */
@Data
public class Product {

    public Goods goods;
    public List<GoodsMaterial> goodsMaterials;
    public List<Artist> artists;
}
