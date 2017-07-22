package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.cache.ArticleCache;
import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.mall.interfaces.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by z on 17-7-14.
 */

@RestController
@RequestMapping(value = "/mall/articles")
public class ArticleController extends BaseController {

    @Autowired
    private ArticleCache articleCache;

    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<Map<String, Object>>> findArticle(String code,
                                                                      Integer page_index, Integer page_size) {

        code = ConvertUtils.toString(code);

        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size, 20);

        PageResponse<Map<String, Object>> pageMaps = articleCache.findPageArticle(code, page_index, page_size);

        ReturnValue<PageResponse<Map<String, Object>>> response = new ReturnValue<>();
        response.setData(pageMaps);

        return response;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ReturnValue<Map<String, Object>> getArticle(@PathVariable("id") Integer id) {

        if (id < 1) {
            return new ReturnValue<>(404, "文章不存在");
        }

        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(articleCache.getArticle(id));

        return response;
    }
}
