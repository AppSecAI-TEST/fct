package com.fct.encyclopedia.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.fct.encyclopedia.interfaces.NewsService;
import com.fct.encyclopedia.interfaces.dto.News;
import com.fct.encyclopedia.interfaces.dto.NewsType;
import com.fct.encyclopedia.interfaces.dto.PageResponse;
import com.fct.encyclopedia.service.domain.NewsEntity;
import com.fct.encyclopedia.service.domain.NewsTypeEntity;
import com.fct.encyclopedia.service.repository.NewsEntityRepository;
import com.fct.encyclopedia.service.repository.NewsTypeEntityRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ningyang on 2017/6/10.
 * 新闻服务
 */
@Service("newsService")
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsTypeEntityRepository newsTypeEntityRepository;

    @Autowired
    private NewsEntityRepository newsEntityRepository;

    @Override
    public List<NewsType> getAllNewsType() {
        return convertNewsType(newsTypeEntityRepository.getNewsTypeEntitiesByDelFlag("0"));
    }

    @Override
    public PageResponse<NewsType> getNewsType(int page, int pageSize) {
        PageResponse<NewsType> pageResponse = new PageResponse<>();
        Boolean hasMore = true;
        int start = (page-1) * pageSize;
        int end = start + pageSize;
        int totalCount = newsTypeEntityRepository.countNewsTypeEntitiesByDelFlag("0");
        if(end >= totalCount){
            end = totalCount;
            hasMore = false;
        }
        List<NewsType> newsTypes = convertNewsType(newsTypeEntityRepository.getNewsType("0", start, end));
        if(hasMore)
            pageResponse.setCurrent(page + 1);
        else
            pageResponse.setCurrent(page);
        pageResponse.setElements(newsTypes);
        pageResponse.setHasMore(hasMore);
        pageResponse.setTotalCount(totalCount);
        return pageResponse;
    }

    @Override
    public PageResponse<News> getNewsByType(String typeId, int page, int pageSize) {
        PageResponse<News> pageResponse = new PageResponse<>();
        Boolean hasMore = true;
        int start = (page-1) * pageSize;
        int end = start + pageSize;
        int totalCount = newsEntityRepository.countNewsEntitiesByTypeIdAndStatus(typeId,"1");
        if(end >= totalCount){
            end = totalCount;
            hasMore = false;
        }
        List<News> news = convertNews(newsEntityRepository.getNewsByPage(typeId,"1", start, end));
        if(hasMore)
            pageResponse.setCurrent(page + 1);
        else
            pageResponse.setCurrent(page);
        pageResponse.setElements(news);
        pageResponse.setHasMore(hasMore);
        pageResponse.setTotalCount(totalCount);
        return pageResponse;
    }

    @Override
    public NewsType saveNewsType(NewsType newsType) {
        NewsTypeEntity newsTypeEntity = new NewsTypeEntity();
        if(StringUtils.isEmpty(newsType.getId())){
            newsTypeEntity.setCreateTime(new Date());
        }else{
            newsTypeEntity.setUpdateTime(new Date());
            newsTypeEntity.setCreateTime(newsType.getCreateTime());
        }
        newsTypeEntity.setDelFlag(newsType.getDelFlag());
        newsTypeEntity.setId(newsType.getId());
        newsTypeEntity.setTypeName(newsType.getTypeName());
        newsTypeEntity = newsTypeEntityRepository.save(newsTypeEntity);
        newsType.setUpdateTime(newsTypeEntity.getUpdateTime());
        newsType.setCreateTime(newsTypeEntity.getCreateTime());
        newsType.setId(newsTypeEntity.getId());
        return newsType;
    }

    @Override
    public News saveNews(News news) {
        NewsEntity newsEntity = new NewsEntity();
        if(StringUtils.isEmpty(news.getNewId())){
            newsEntity.setCreateTime(new Date());
        }else{
            newsEntity.setUpdateTime(new Date());
            newsEntity.setCreateTime(news.getCreateTime());
        }
        newsEntity.setContent(news.getContent());
        newsEntity.setNewId(news.getNewId());
        newsEntity.setNewsDate(news.getNewsDate());
        newsEntity.setSource(news.getSource());
        newsEntity.setStatus(news.getStatus());
        newsEntity.setTitle(news.getTitle());
        newsEntity.setTypeId(news.getTypeId());
        newsEntity = newsEntityRepository.save(newsEntity);
        news.setUpdateTime(newsEntity.getUpdateTime());
        news.setCreateTime(newsEntity.getCreateTime());
        news.setNewId(newsEntity.getNewId());
        NewsTypeEntity newsTypeEntity = newsTypeEntityRepository.findOne(news.getTypeId());
        List<NewsEntity> newsList = newsTypeEntity.getNews();
        List<NewsEntity> newsListAnother = newsList.parallelStream().
                filter(v->news.getNewId().equalsIgnoreCase(v.getNewId())).
                collect(Collectors.toList());
        if(newsListAnother.size()>0){
            newsList.removeAll(newsListAnother);
        }
        newsList.add(newsEntity);
        newsTypeEntity.setNews(newsList);
        newsTypeEntityRepository.save(newsTypeEntity);
        return news;
    }

    private List<NewsType> convertNewsType(List<NewsTypeEntity> newsTypeEntities){
        if(newsTypeEntities!=null&&newsTypeEntities.size()>0){
            List<NewsType> newsTypeLists = Lists.newArrayList();
            for(NewsTypeEntity entity: newsTypeEntities){
                NewsType newsType = new NewsType();
                newsType.setCreateTime(entity.getCreateTime());
                newsType.setId(entity.getId());
                newsType.setTypeName(entity.getTypeName());
                newsType.setUpdateTime(entity.getUpdateTime());
                newsTypeLists.add(newsType);
            }
            return newsTypeLists;
        }
        return null;
    }

    private List<News> convertNews(List<NewsEntity> newsEntities){
        if(newsEntities!=null&&newsEntities.size()>0){
            List<News> newsList = Lists.newArrayList();
            for(NewsEntity entity: newsEntities){
                News news = new News();
                news.setCreateTime(entity.getCreateTime());
                news.setContent(entity.getContent());
                news.setNewId(entity.getNewId());
                news.setUpdateTime(entity.getUpdateTime());
                news.setNewsDate(entity.getNewsDate());
                news.setSource(entity.getSource());
                news.setStatus(entity.getStatus());
                news.setTitle(entity.getTitle());
                news.setTypeId(entity.getTypeId());
                newsList.add(news);
            }
            return newsList;
        }
        return null;
    }
}
