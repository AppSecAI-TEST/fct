package com.fct.encyclopedia.service.repository;

import com.fct.encyclopedia.service.domain.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by ningyang on 2017/6/10.
 */
public interface NewsEntityRepository extends JpaRepository<NewsEntity, String> {

    List<NewsEntity> findAllByStatus(String status);

    @Query(nativeQuery = true, value = "select * from news a where a.type_id = ?1 and a.status = ?2 ORDER BY a.create_time limit ?3, ?4")
    List<NewsEntity> getNewsByPage(String typeId, String status, int start, int end);

    int countNewsEntitiesByTypeIdAndStatus(String typeId, String status);
}
