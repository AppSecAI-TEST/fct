package com.fct.source.data.repository;

import com.fct.source.data.entity.ArticleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ArticleCategoryRepository extends JpaRepository<ArticleCategory, Integer> {

    @Query(nativeQuery = true,
            value = "select count(0) from ArticleCategory where name=?1 and parentId=?2 and id!=?3")
    int exitSameName(String name, Integer parentId, Integer categoryId);

    @Modifying
    @Query(nativeQuery = true,
            value = "UPDATE ArticleCategory set SortIndex=?2 WHERE Id=?1")
    void updateSortIndex(Integer id,Integer sortIndex);

}
