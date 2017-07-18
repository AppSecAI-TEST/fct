package com.fct.artist.data.repository;

import com.fct.artist.data.entity.ArtistDynamic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface ArtistDynamicRepository extends JpaRepository<ArtistDynamic, Integer> {

    @Modifying
    @Query(nativeQuery = true,
            value = "UPDATE ArtistDynamic SET isTop=0 WHERE artistId=?1")
    void  cancleTop(Integer artistId);
}
