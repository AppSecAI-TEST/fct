package com.fct.artist.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ArtistComment implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户id
     * */
    private Integer memberId;

    /**
     * 用户名
     * */
    private String userName;

    /**
     * 评论艺人
     * */
    private Integer artistId;

    /**
     * 评论内容
     * */
    private String content;

    /**
     * 回复哪条评论的Id
     * **/
    private Integer replyId;

    /**
     * 状态
     * */
    private Integer status;

    /**
     * 生成时间
     * */
    private Date createTime;

    /**
     * 更新时间
     * */
    private Date updateTime;
}
