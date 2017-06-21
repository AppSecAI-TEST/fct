package com.fct.source.data.repository;

import com.fct.source.data.entity.ImageSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ImageSourceRepository extends JpaRepository<ImageSource, String> {

    @Modifying
    @Query(nativeQuery = true,
            value = "UPDATE ImageSource set Status=1-Status WHERE guid=?1")
    void updateStatus(String id);

    int countByCategoryId(Integer cateId);
}
