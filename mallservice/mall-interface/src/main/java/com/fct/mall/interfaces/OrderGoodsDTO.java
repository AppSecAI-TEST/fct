package com.fct.mall.interfaces;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class OrderGoodsDTO implements Serializable {

    private Integer goodsId;

    private Integer buyCount;

    private Integer specId;
}
