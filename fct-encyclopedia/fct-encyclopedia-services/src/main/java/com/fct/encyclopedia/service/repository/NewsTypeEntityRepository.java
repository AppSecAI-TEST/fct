package com.fct.encyclopedia.service.repository;

import com.fct.encyclopedia.service.domain.NewsTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by ningyang on 2017/6/10.
 */
public interface NewsTypeEntityRepository extends JpaRepository<NewsTypeEntity, String> {

    @Query(nativeQuery = true, value = "select * from news_type a where a.del_flag = ?1 order by a.create_time DESC limit ?2, ?3")
    List<NewsTypeEntity> getNewsType(String delFlag, int start, int end);

    int countNewsTypeEntitiesByDelFlag(String delFlag);

    List<NewsTypeEntity> getNewsTypeEntitiesByDelFlag(String delFlag);
}
