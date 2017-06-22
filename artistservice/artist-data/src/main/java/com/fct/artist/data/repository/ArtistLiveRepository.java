package com.fct.artist.data.repository;

import com.fct.artist.data.entity.ArtistLive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistLiveRepository extends JpaRepository<ArtistLive, Integer> {

    int countByArtistId(Integer artistId);

    ArtistLive findByArtistId(Integer artistId);
}
