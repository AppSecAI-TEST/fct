package com.fct.encyclopedia.test;

import com.fct.encyclopedia.interfaces.NewsService;
import com.fct.encyclopedia.interfaces.dto.News;
import com.fct.encyclopedia.interfaces.dto.NewsType;
import com.fct.encyclopedia.interfaces.dto.PageResponse;
import com.fct.encyclopedia.service.ApplicationStartup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * Created by ningyang on 2017/6/10.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationStartup.class})
@ActiveProfiles({"de"})
public class EncyclopediaTest {

    @Autowired
    private NewsService newsService;

    @Test
    public void insertNewsType(){
        NewsType newsType = new NewsType();
        newsType.setTypeName("體育");
        newsType.setDelFlag("0");
        newsType = newsService.saveNewsType(newsType);
        System.out.println(newsType.getId());
    }

    @Test
    public void insertNews(){
        News news = new News();
        news.setTypeId("402881885c92d7a5015c92d7add70000");
        news.setTitle("今天應該吃什麼第四部");
        news.setStatus("1");
        news.setSource("阿里巴巴");
        news.setNewsDate(new Date());
        news.setContent("今天應該吃麵包");
        news = newsService.saveNews(news);
        System.out.println(news.getNewId());
    }

    @Test
    public void getAllNewsType(){
        List<NewsType> newsTypeList = newsService.getAllNewsType();
        Assert.assertNotNull(newsTypeList);
        Assert.assertNotEquals(0, newsTypeList.size());
    }

    /**
     * page必須重一開始 因為是頁碼
     */
    @Test
    public void getNewsTypePage(){
        PageResponse<NewsType> pageResponse = newsService.getNewsType(1, 20);
        Assert.assertNotNull(pageResponse);
        Assert.assertEquals(1, pageResponse.getTotalCount());
    }

    /**
     * page必須重一開始 因為是頁碼
     */
    @Test
    public void getNewsByTypeAndPage(){
        PageResponse<News> pageResponse = newsService.getNewsByType("402881885c92d7a5015c92d7add70000", 1, 10);
        Assert.assertNotNull(pageResponse);
        Assert.assertEquals(4, pageResponse.getTotalCount());
    }
}
