package com.fct.mall.data.repository;

import com.fct.mall.data.entity.GoodsGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by jon on 2017/5/17.
 */
public interface GoodsGradeRepository  extends JpaRepository<GoodsGrade, Integer> {

    @Query(nativeQuery = true,
            value = "select count(0) from GoodsGrade where name=?1 and id!=?2")
    int exitSameName(String name, Integer id);

    @Query(nativeQuery = true,
            value = "UPDATE GoodsGrade set SortIndex=?2 WHERE Id=?1")
    void updateSortIndex(Integer id,Integer sortIndex);

}
