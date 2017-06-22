package com.fct.artist.data.repository;

import com.fct.artist.data.entity.ArtistGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArtistGoodsRepository extends JpaRepository<ArtistGoods, Integer> {

    List<ArtistGoods> findByArtistId(Integer artistId);

    void deleteByGoodsId(Integer goodsId);
}
