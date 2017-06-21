package com.fct.source.data.repository;

import com.fct.source.data.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

    @Query(nativeQuery = true,
            value = "select count(0) from Article where categoryCode like ?1")
    int countByCategory(String id);
}
