package com.fct.master.data.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by nick on 2017/5/24.
 */
@Data
@Entity
@Table(name = "attention")
public class Attention extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "master_id")
    private Long masterId;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "nick_name")
    private String nickName;
}
