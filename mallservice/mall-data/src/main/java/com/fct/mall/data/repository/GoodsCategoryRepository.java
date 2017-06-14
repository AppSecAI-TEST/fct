package com.fct.mall.data.repository;

import com.fct.mall.data.entity.GoodsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by jon on 2017/5/17.
 */
public interface GoodsCategoryRepository  extends JpaRepository<GoodsCategory, Integer> {

    @Query(nativeQuery = true,
            value = "select count(0) from GoodsCategory where name=?1 and parentId=?2 and id!=?3")
    int exitSameName(String name, Integer parentId, Integer categoryId);

    @Query(nativeQuery = true,
            value = "UPDATE GoodsCategory set SortIndex=?2 WHERE Id=?1")
    void updateSortIndex(Integer id,Integer sortIndex);
}
