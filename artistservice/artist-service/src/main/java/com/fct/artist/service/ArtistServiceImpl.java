package com.fct.artist.service;

import com.fct.artist.data.entity.Artist;
import com.fct.artist.data.entity.ArtistComment;
import com.fct.artist.data.entity.ArtistDynamic;
import com.fct.artist.data.entity.ArtistLive;
import com.fct.artist.interfaces.ArtistService;
import com.fct.artist.interfaces.PageResponse;
import com.fct.artist.service.business.*;
import org.hibernate.boot.spi.InFlightMetadataCollector;
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
        try {
            return artistManager.findAll(name, status, pageIndex, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public List<Artist> findArtistByGoodsId(Integer goodsId)
    {
        try {
            return artistManager.findByGoodsId(goodsId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void saveArtist(Artist artist)
    {
        try {
            artistManager.save(artist);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public Artist getArtist(Integer id)
    {
        try {
            return artistManager.findById(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void updateArtistStatus(Integer id)
    {
        try {
            artistManager.updateStatus(id);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void addArtistViewCount(Integer id,Integer count)
    {
        try {
            artistManager.addViewCount(id,count);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void addArtistFollowCount(Integer id,Integer count)
    {
        try {
            artistManager.addFollowCount(id,count);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public PageResponse<ArtistComment> findArtistComment(Integer artistId, Integer memberId, String username, Integer status,
                                                  Integer replyId, Integer pageIndex, Integer pageSize)
    {
        try {
            return artistCommentManager.findAll(artistId,memberId,username,status,replyId,pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public List<ArtistComment> findReplyComment(Integer commentId,int top)
    {
        try {
            return artistCommentManager.findByComment(commentId,top);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public Integer replyArtistComment(Integer id,Integer memberId,String userName,String content)
    {
        try {
            return artistCommentManager.reply(id,memberId,userName,content);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return 0;
    }

    public Integer saveArtistComment(ArtistComment artistComment)
    {
        try {
            return artistCommentManager.save(artistComment);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return 0;
    }

    public ArtistComment getArtistComment(Integer id)
    {
        try {
            return artistCommentManager.findById(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void updateArtistCommentStatus(Integer id)
    {
        try {
            artistCommentManager.updateStatus(id);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void saveArtistGoods(List<Integer> artistId, Integer goodsId)
    {
        try {
            artistGoodsManager.save(artistId,goodsId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public Integer saveArtistLive(ArtistLive live)
    {
        try {
            return artistLiveManager.save(live);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return 0;
    }

    public ArtistLive getArtistLive(Integer id)
    {
        try {
            return artistLiveManager.findByArtist(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PageResponse<ArtistLive> findArtistLive(Integer artistId,Integer status,String startTime,String endTime,
                                            Integer pageIndex, Integer pageSize)
    {
        try {
            return artistLiveManager.findAll(artistId,status,startTime,endTime,pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void updateArtistLiveStatus(Integer id)
    {
        try {
            artistLiveManager.updateStatus(id);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public Integer saveArtistDynamic(ArtistDynamic artistDynamic)
    {
        try {
            return artistDynamicManager.save(artistDynamic);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return 0;
    }

    public void setArtistDynamicTop(Integer id)
    {
        try {
            artistDynamicManager.setTop(id);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void updateArtistDynamicStatus(Integer id)
    {
        try {
            artistDynamicManager.updateStatus(id);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public ArtistDynamic getArtistDynamic(Integer id)
    {
        try {
            return artistDynamicManager.findById(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PageResponse<ArtistDynamic> findArtistDynamic(Integer artistId,String content,Integer status,String startTime,String endTime,
                                                  Integer pageIndex, Integer pageSize)
    {
        try {
            return artistDynamicManager.findAll(artistId, content, status, startTime, endTime, pageIndex, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

}
