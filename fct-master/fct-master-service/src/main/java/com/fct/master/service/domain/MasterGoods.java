package com.fct.master.service.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by ningyang on 2017/6/3.
 */
@Data
@Entity
@Table(name = "tb_master_goods")
public class MasterGoods {

    @Id
    @Column(nullable = false, unique = true)
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @Column
    private long master_id;

    @Column
    private long goods_id;

    @Column(name = "create_time")
    private Date createTime;
}
