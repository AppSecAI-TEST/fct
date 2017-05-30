package com.fct.master.service.repository;

import com.fct.master.service.domain.MasterLive;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by nick on 2017/5/24.
 */
public interface MasterLiveRepository extends JpaRepository<MasterLive, Long>{

    MasterLive findByMasterIdAndStatusAndDelFlag(long masterId, int status, int delFlag);
}
