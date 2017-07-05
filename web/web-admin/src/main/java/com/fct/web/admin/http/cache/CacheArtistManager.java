package com.fct.web.admin.http.cache;

import com.fct.artist.data.entity.Artist;
import com.fct.artist.interfaces.ArtistService;
import com.fct.artist.interfaces.PageResponse;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CacheArtistManager {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private JedisPool jedisPool;

    private static final int expireSecond = 60 * 24 * 60 * 60;

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

    public List<Artist> findArtist()
    {
        PageResponse<Artist> pageResponse = null;

        try {

            pageResponse = artistService.findArtist("",1,1, 100);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<Artist>();
        }
        return pageResponse.getElements();
    }



    public String getArtistName(String ids)
    {
        List<Artist> artistList = findArtist();
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
