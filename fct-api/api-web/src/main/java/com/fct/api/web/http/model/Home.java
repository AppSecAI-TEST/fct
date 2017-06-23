package com.fct.api.web.http.model;

import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsCategory;
import com.fct.mall.interfaces.PageResponse;
import lombok.Data;

import java.util.List;

@Data
public class Home {

    public List<GoodsCategory> categoryList;

    public PageResponse<Goods> goodsList;
}
