package com.fct.web.admin.http.cache;

import com.fct.artist.data.entity.Artist;
import com.fct.artist.interfaces.ArtistService;
import com.fct.artist.interfaces.PageResponse;
import com.fct.mall.data.entity.GoodsCategory;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CacheArtistManager {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private JedisPool jedisPool;

    private int expireSecond = 60 * 35; //35分钟

    public Artist getCacheArtist(Integer id)
    {
        String key = "cache_artist_"+id;
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if(object != null)
            {
                return (Artist) SerializationUtils.deserialize(object);
            }
            else
            {
                Artist artist = getArtist(id);
                if (artist != null) {
                    jedis.set(key.getBytes(),SerializationUtils.serialize(artist));
                    jedis.expire(key,expireSecond);
                }
                return artist;
            }

        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        finally {
            if(jedis != null)
            {
                jedis.close();
            }
        }
        return getArtist(id);
    }

    public Artist getArtist(Integer id)
    {
        try {
            if (id > 0) {
                return artistService.getArtist(id);
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return new Artist();
    }

    public List<Artist> findCacheArtist()
    {
        String key = "cache_artist_all";
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if(object != null)
            {
                return (List<Artist>) SerializationUtils.deserialize(object);
            }
            else
            {
                List<Artist> artist = findArtist();
                if (artist != null) {
                    jedis.set(key.getBytes(),SerializationUtils.serialize(artist));
                    jedis.expire(key,expireSecond);
                }
                return artist;
            }

        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        finally {
            if(jedis != null)
            {
                jedis.close();
            }
        }
        return findArtist();
    }

    public List<Artist> findArtist()
    {
        try {

            PageResponse<Artist> pageResponse = artistService.findArtist("",1,1, 100);
            return pageResponse.getElements();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return new ArrayList<>();
    }

    public String getArtistName(String ids)
    {
        List<Artist> artistList = findCacheArtist();
        String[] arrId = ids.split(",");
        List<String> idList = Arrays.asList(arrId);
        String name = "";
        for (Artist artist: artistList
                ) {
            if(idList.contains(artist.getId().toString()))
            {
                if(!StringUtils.isEmpty(name))
                {
                    name += "、";
                }
                name += artist.getName();
            }
        }
        return name;
    }
}
