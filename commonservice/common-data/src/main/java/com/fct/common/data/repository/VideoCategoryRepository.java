package com.fct.common.data.repository;

import com.fct.common.data.entity.VideoCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VideoCategoryRepository extends JpaRepository<VideoCategory, Integer> {
    @Query(nativeQuery = true,
            value = "select count(0) from VideoCategory where name=?1 and id!=?2")
    int exitSameName(String name, Integer id);
}
