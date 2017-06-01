package com.fct.master.service.callback;

import com.fct.master.service.MediaType;
import com.fct.master.service.domain.MasterMultiMediaRelation;
import com.fct.master.service.repository.MasterMultiMediaRelationRepository;
import com.fct.thirdparty.oss.callback.Callback;
import com.fct.thirdparty.oss.response.DeleteResponse;
import com.fct.thirdparty.oss.response.Response;
import com.fct.thirdparty.oss.response.UploadResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by ningyang on 2017/6/1.
 */
@Service
public class MasterCallback implements Callback {

    @Autowired
    private MasterMultiMediaRelationRepository masterMultiMediaRelationRepository;

    @Override
    public Object onSuccess(Response response) {
        MasterMultiMediaRelation multiMediaRelation = new MasterMultiMediaRelation();
        if(response instanceof UploadResponse){
            UploadResponse uploadResponse = (UploadResponse) response;
            multiMediaRelation.setCreateTime(new Date());
            multiMediaRelation.setImgUrl(uploadResponse.getUrl());
            multiMediaRelation.setKey(uploadResponse.getKey());
            if(StringUtils.isEmpty(uploadResponse.getUserMetaData().get("masterId"))){
                throw new IllegalArgumentException("获取数据异常");
            }
            multiMediaRelation.setMasterId(Long.valueOf(uploadResponse.getUserMetaData().get("masterId")));
            multiMediaRelation.setMediaType(MediaType.IMG.name());
            multiMediaRelation.setDelFlag(0);
            masterMultiMediaRelationRepository.save(multiMediaRelation);
        }else if(response instanceof DeleteResponse){
            DeleteResponse deleteResponse = (DeleteResponse) response;
            if(StringUtils.isEmpty(deleteResponse.getUserMetaData().get("masterId")))
                throw new IllegalArgumentException("获取数据异常");
            masterMultiMediaRelationRepository.deleteByMasterIdAndKeyIsIn(Long.valueOf(deleteResponse.getUserMetaData().get("masterId")), deleteResponse.getKeys());
        }
        return null;
    }

    @Override
    public Object onFailure(Response response) {
        return null;
    }
}
