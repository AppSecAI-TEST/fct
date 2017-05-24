package com.fct.master.data.repository;

import com.fct.master.data.entity.MasterNews;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by nick on 2017/5/24.
 */
public interface MasterNewsRepository extends JpaRepository<MasterNews, Long>{
}
