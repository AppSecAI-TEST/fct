package com.fct.artist.data.repository;

import com.fct.artist.data.entity.ArtistComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistCommentRepository extends JpaRepository<ArtistComment, Integer> {
}
