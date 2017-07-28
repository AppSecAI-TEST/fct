package com.fct.artist.data.repository;

import com.fct.artist.data.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArtistRepository extends JpaRepository<Artist, Integer> {

}
