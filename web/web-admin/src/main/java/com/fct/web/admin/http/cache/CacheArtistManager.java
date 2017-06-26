package com.fct.web.admin.http.cache;

import com.fct.artist.data.entity.Artist;
import com.fct.artist.interfaces.ArtistService;
import com.fct.artist.interfaces.PageResponse;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CacheArtistManager {

    @Autowired
    private ArtistService artistService;

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
}