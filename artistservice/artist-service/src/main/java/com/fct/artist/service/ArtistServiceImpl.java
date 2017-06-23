package com.fct.artist.service;

import com.fct.artist.data.entity.Artist;
import com.fct.artist.data.entity.ArtistComment;
import com.fct.artist.data.entity.ArtistDynamic;
import com.fct.artist.data.entity.ArtistLive;
import com.fct.artist.interfaces.ArtistService;
import com.fct.artist.interfaces.PageResponse;
import com.fct.artist.service.business.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("artistService")
public class ArtistServiceImpl implements ArtistService {

    @Autowired
    private ArtistManager artistManager;

    @Autowired
    private ArtistGoodsManager artistGoodsManager;

    @Autowired
    private ArtistCommentManager artistCommentManager;

    @Autowired
    private ArtistLiveManager artistLiveManager;

    @Autowired
    private ArtistDynamicManager artistDynamicManager;


    public PageResponse<Artist> findArtist(String name, Integer status, Integer pageIndex, Integer pageSize)
    {
        return  artistManager.findAll(name,status,pageIndex,pageSize);
    }

    public List<Artist> findArtistByGoodsId(Integer goodsId)
    {
        return artistManager.findByGoodsId(goodsId);
    }

    public void saveArtist(Artist artist)
    {
        artistManager.save(artist);
    }

    public Artist getArtist(Integer id)
    {
        return artistManager.findById(id);
    }

    public void updateArtistStatus(Integer id)
    {
        artistManager.updateStatus(id);
    }

    public PageResponse<ArtistComment> findArtistComment(Integer artistId, Integer memberId, String username, Integer status,
                                                  Integer replyId, Integer pageIndex, Integer pageSize)
    {
        return artistCommentManager.findAll(artistId,memberId,username,status,replyId,pageIndex,pageSize);
    }

    public Integer saveArtistComment(ArtistComment artistComment)
    {
        return artistCommentManager.save(artistComment);
    }

    public ArtistComment getArtistComment(Integer id)
    {
        return artistCommentManager.findById(id);
    }

    public void updateArtistCommentStatus(Integer id)
    {
        artistCommentManager.updateStatus(id);
    }

    public void saveArtistGoods(List<Integer> artistId, Integer goodsId)
    {
        artistGoodsManager.save(artistId,goodsId);
    }

    public Integer saveArtistLive(ArtistLive live)
    {
        return artistLiveManager.save(live);
    }

    public ArtistLive getArtistLive(Integer id)
    {
        return artistLiveManager.findByArtist(id);
    }

    public PageResponse<ArtistLive> findArtistLive(Integer artistId,Integer status,String startTime,String endTime,
                                            Integer pageIndex, Integer pageSize)
    {
        return artistLiveManager.findAll(artistId,status,startTime,endTime,pageIndex,pageSize);
    }

    public void updateArtistLiveStatus(Integer id)
    {
        artistLiveManager.updateStatus(id);
    }

    public Integer saveArtistDynamic(ArtistDynamic artistDynamic)
    {
        return artistDynamicManager.save(artistDynamic);
    }

    public void updateArtistDynamicStatus(Integer id)
    {
        artistDynamicManager.updateStatus(id);
    }

    public ArtistDynamic getArtistDynamic(Integer id)
    {
        return artistDynamicManager.findById(id);
    }

    public PageResponse<ArtistDynamic> findArtistDynamic(Integer artistId,String content,Integer status,String startTime,String endTime,
                                                  Integer pageIndex, Integer pageSize)
    {
        return artistDynamicManager.findAll(artistId,content,status,startTime,endTime,pageIndex,pageSize);
    }

}
