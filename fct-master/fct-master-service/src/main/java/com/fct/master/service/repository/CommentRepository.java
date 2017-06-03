package com.fct.master.service.repository;

import com.fct.master.service.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by nick on 2017/5/24.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    long countByDelFlagAndMasterId(int delFlag, long masterId);

    @Query(nativeQuery = true, value = "select * from comment a where a.del_flag = ?1 and a.master_id = ?2 order by a.create_time DESC limit ?3, ?4")
    List<Comment> getComments(int delFlag, long masterId, int start, int end);
}
