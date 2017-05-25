package com.fct.master.service.repository;

import com.fct.master.service.domain.MasterNews;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by nick on 2017/5/24.
 */
public interface MasterNewsRepository extends JpaRepository<MasterNews, Long>{
}
