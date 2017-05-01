package com.fct.api.data.repository;

import com.fct.api.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by ningyang on 2017/4/6.
 * JPA 的實現
 */
public interface UserRepository extends JpaRepository<UserEntity, String> {

    /**
     * 查询语句findBy后面跟着的名字是entity的属性名称 比如findById Id代表entity UserEntity 里面的id, 也对应表里面的id列
     * sql : select * from user where id = ?
     * @param id
     * @return
     */
    UserEntity findById(String id);

    /**
     * 同上
     * sql : select * from user where name = ?
     * @param name
     * @return
     */
    List<UserEntity> findByName(String name);

    /**
     * 分页查询
     * sql: select * from user where name = ? limit ?, ?
     */
    @Query(nativeQuery = true, value = "select * from user a where a.name =?1 limit ?2,?3")
    List<UserEntity> findUserByPage(String name, int offset, int limit);

}
