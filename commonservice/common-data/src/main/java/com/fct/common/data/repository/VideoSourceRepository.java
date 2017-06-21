package com.fct.common.data.repository;

import com.fct.common.data.entity.VideoSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface VideoSourceRepository extends JpaRepository<VideoSource, String> {

    @Modifying
    @Query(nativeQuery = true,
            value = "UPDATE VideoSource set Status=1-Status WHERE guid=?1")
    void updateStatus(String id);

    int countByCategoryId(Integer cateId);
}
