package com.fct.master.data.repository;

import com.fct.master.data.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by nick on 2017/5/24.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
