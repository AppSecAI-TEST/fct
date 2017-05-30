package com.fct.master.service.repository;

import com.fct.master.service.domain.MasterNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by nick on 2017/5/24.
 */
public interface MasterNewsRepository extends JpaRepository<MasterNews, Long>{

    @Query("select count(a) from MasterNews a where a.masterId = ?1 and a.delFlag = 0")
    long countMasterNews(long masterId);

    @Query(nativeQuery = true, value = "select * from master_news a where a.master_id = ?1 and a.del_flag = 0 ORDER BY a.create_time DESC limit ?2, ?3")
    List<MasterNews> getMasterNews(long master_id, int start, int end);
}
