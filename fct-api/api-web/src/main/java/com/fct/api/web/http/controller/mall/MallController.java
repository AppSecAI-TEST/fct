package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.controller.BaseController;
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
@RequestMapping(value = "/")
public class MallController extends BaseController{

    @Autowired
    private MallService mallService;

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
}
