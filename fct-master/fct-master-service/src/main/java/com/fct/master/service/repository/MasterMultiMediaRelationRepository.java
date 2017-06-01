package com.fct.master.service.repository;

import com.fct.master.service.domain.MasterMultiMediaRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

/**
 * Created by ningyang on 2017/6/1.
 */
public interface MasterMultiMediaRelationRepository extends JpaRepository<MasterMultiMediaRelation, String> {

    @Modifying
    void deleteByMasterIdAndKeyIsIn(long masterId, List<String> keys);
}
