package com.fct.master.data.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by nick on 2017/5/24.
 */
@Data
@Entity
@Table(name = "comment")
public class Comment extends Base{

    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "master_id")
    private Long masterId;

    @Column(name = "reply_comment_id")
    private Long replyCommentId;

    @Column(name = "reply_content")
    private String replyContent;

}
