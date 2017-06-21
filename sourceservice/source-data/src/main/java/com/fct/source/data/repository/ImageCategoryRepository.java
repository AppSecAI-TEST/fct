package com.fct.source.data.repository;

import com.fct.source.data.entity.ImageCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImageCategoryRepository extends JpaRepository<ImageCategory, Integer> {

    @Query(nativeQuery = true,
            value = "select count(0) from ImageCategory where name=?1 and id!=?2")
    int exitSameName(String name, Integer id);
}
