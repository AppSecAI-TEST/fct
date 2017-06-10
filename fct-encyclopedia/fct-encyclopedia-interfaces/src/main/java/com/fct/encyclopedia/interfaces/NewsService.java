package com.fct.encyclopedia.interfaces;

import com.fct.encyclopedia.interfaces.dto.News;
import com.fct.encyclopedia.interfaces.dto.NewsType;
import com.fct.encyclopedia.interfaces.dto.PageResponse;

import java.util.List;

/**
 * Created by ningyang on 2017/6/10.
 */
public interface NewsService {

    /**
     * 获取所有的新闻分类 无需分页
     * @return
     */
    List<NewsType> getAllNewsType();
    /**
     * 获取新闻分类并且分页
     * @param page
     * @param pageSize
     * @return
     */
    PageResponse<NewsType> getNewsType(int page, int pageSize);

    /**
     * 根据新闻分类获取新闻并且分页
     * @param typeId
     * @param page
     * @param pageSize
     * @return
     */
    PageResponse<News> getNewsByType(String typeId, int page, int pageSize);

    /**
     * 更新或者新增新闻类型
     * 如果是新增不需要传id
     * @param newsType
     * @return
     */
    NewsType saveNewsType(NewsType newsType);

    /**
     * 更新或者新增新闻
     * 如果是新增不需要传id
     * @param news
     * @return
     */
    News saveNews(News news);
}
