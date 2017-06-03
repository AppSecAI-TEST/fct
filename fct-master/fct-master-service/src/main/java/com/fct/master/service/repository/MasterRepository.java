package com.fct.master.service.repository;

import com.fct.master.service.domain.Master;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by nick on 2017/5/24.
 */
public interface MasterRepository extends JpaRepository<Master, Long>{

    Long countAllByDelFlag(int delflag);

    @Query(nativeQuery = true, value = "select * from master a where a.del_flag = 0 order by a.create_time desc limit ?1, ?2 ")
    List<Master> getAllMaster(int start, int end);
}
