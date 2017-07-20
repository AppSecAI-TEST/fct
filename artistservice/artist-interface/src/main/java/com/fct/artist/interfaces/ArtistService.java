package com.fct.artist.interfaces;


import com.fct.artist.data.entity.Artist;
import com.fct.artist.data.entity.ArtistComment;
import com.fct.artist.data.entity.ArtistDynamic;
import com.fct.artist.data.entity.ArtistLive;

import java.util.List;

public interface ArtistService {

    PageResponse<Artist> findArtist(String name,Integer status,Integer pageIndex, Integer pageSize);

    List<Artist> findArtistByGoodsId(Integer goodsId);

    void saveArtist(Artist artist);

    Artist getArtist(Integer id);

    void updateArtistStatus(Integer id);

    void addArtistFollowCount(Integer id,Integer count);

    void addArtistViewCount(Integer id,Integer count);

    PageResponse<ArtistComment> findArtistComment(Integer artistId,Integer memberId,String username, Integer status,
                                                  Integer replyId,Integer pageIndex, Integer pageSize);

    List<ArtistComment> findReplyComment(Integer commentId,int top);

    Integer saveArtistComment(ArtistComment artistComment);

    Integer replyArtistComment(Integer id,Integer replyId,Integer memberId,String userName,String content);

    ArtistComment getArtistComment(Integer id);

    void updateArtistCommentStatus(Integer id);

    void saveArtistGoods(List<Integer> artistId, Integer goodsId);

    Integer saveArtistLive(ArtistLive live);

    ArtistLive getArtistLive(Integer id);

    PageResponse<ArtistLive> findArtistLive(Integer artistId,Integer status,String startTime,String endTime,
                                            Integer pageIndex, Integer pageSize);

    void updateArtistLiveStatus(Integer id);

    Integer saveArtistDynamic(ArtistDynamic artistDynamic);

    void updateArtistDynamicStatus(Integer id);

    void setArtistDynamicTop(Integer id);

    ArtistDynamic getArtistDynamic(Integer id);

    PageResponse<ArtistDynamic> findArtistDynamic(Integer artistId,String content,Integer status,String startTime,String endTime,
                                            Integer pageIndex, Integer pageSize);
}
