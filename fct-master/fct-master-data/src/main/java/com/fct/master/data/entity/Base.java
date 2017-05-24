package com.fct.master.data.entity;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * Created by nick on 2017/5/24.
 */
@Data
public class Base {

    @Column(name = "del_flag")
    protected int delFlag; //逻辑删除 0 是不删除 1 是删除 默认是0

    @Column(name = "create_time")
    protected Date createTime;

    @Column(name = "update_time")
    protected Date updateTime;
}
