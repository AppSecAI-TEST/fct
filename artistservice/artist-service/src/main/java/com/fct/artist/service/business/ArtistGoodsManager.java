package com.fct.artist.service.business;

import com.fct.artist.data.entity.Artist;
import com.fct.artist.data.entity.ArtistGoods;
import com.fct.artist.data.repository.ArtistGoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArtistGoodsManager {

    @Autowired
    private ArtistGoodsRepository artistGoodsRepository;

    @Autowired
    private ArtistManager artistManager;

    @Transactional
    public void save(List<Integer> lsArtistId, Integer goodsId)
    {
        if(lsArtistId == null || lsArtistId.size()<=0)
        {
            throw new IllegalArgumentException("艺人不存在");
        }
        if(goodsId>0)
        {
            throw new IllegalArgumentException("宝贝为空.");
        }

        //删除宝贝下的艺人
        artistGoodsRepository.deleteByGoodsId(goodsId);
        
        for (Integer artistid:lsArtistId
             ) {
            ArtistGoods goods = new ArtistGoods();
            goods.setArtistId(artistid);
            goods.setGoodsId(goodsId);
            artistGoodsRepository.save(goods);

            Artist artist =artistManager.findById(artistid);
            artist.setGoodsCount(findByArtist(artistid).size());
            artistManager.save(artist); //更新艺人作品数量
        }
    }

    public List<ArtistGoods> findByArtist(Integer artistId)
    {
        return artistGoodsRepository.findByArtistId(artistId);
    }

}
