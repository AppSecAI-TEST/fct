package com.fct.master.service.impl;


import com.fct.common.exceptions.Exceptions;
import com.fct.master.dto.*;
import com.fct.master.interfaces.MasterService;
import com.fct.master.service.callback.MasterCallback;
import com.fct.master.service.domain.*;
import com.fct.master.service.repository.*;
import com.fct.thirdparty.oss.FileOperatorHelper;
import com.fct.thirdparty.oss.callback.OSSCallback;
import com.fct.thirdparty.oss.entity.FileServiceRequest;
import com.fct.thirdparty.oss.response.UploadResponse;
import com.fct.thridparty.vod.AliyunVod;
import com.fct.thridparty.vod.RequestType;
import com.fct.thridparty.vod.utils.IDGen;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * Created by nick on 2017/5/25.
 */
@Service("masterService")
public class MasterServiceImpl implements MasterService {

    private static final Logger logger = LoggerFactory.getLogger(MasterServiceImpl.class);

    @Autowired
    private MasterRepository masterRepository;

    @Autowired
    private MasterLiveRepository masterLiveRepository;

    @Autowired
    private MasterNewsRepository masterNewsRepository;

    @Autowired
    private MasterGoodsRepository masterGoodsRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FileOperatorHelper helper;

    @Autowired
    private MasterCallback masterCallback;

    @Value("aliyun.oss.access.key.id")
    private String accessKeyId;

    @Value("aliyun.oss.access.key.secret")
    private String accessKeySecret;

    @Override
    public MasterDto insertMaster(MasterDto masterDto) {
        Master master = new Master(masterDto);
        master = masterRepository.save(master);
        masterDto.setMasterId(master.getMasterId());
        return masterDto;
    }

    @Override
    public PageResponse<MasterDto> getMasters(int start, int size) {
        PageResponse<MasterDto> response = new PageResponse<>();
        boolean hasMore = true;
        Long count = masterRepository.countAllByDelFlag(0);
        int end = start + size;
        if(end >= count){
            hasMore = false;
            end = count.intValue();
        }
        List<Master> masters = masterRepository.getAllMaster(start, end);
        response.setHasMore(hasMore);
        response.setCurrent(end);
        response.setTotalCount(count);
        response.setElements(getMasterDtos(masters));
        return response;
    }

    private List<MasterDto> getMasterDtos(List<Master> masters){
        List<MasterDto> masterDtos = Lists.newArrayList();
        if(masters!=null && masters.size()>0){
            for(Master master: masters){
                MasterDto masterDto = new MasterDto();
                masterDto.setBrief(master.getBrief());
                masterDto.setCoverUrl(master.getCoverUrl());
                masterDto.setDelflag(master.getDelFlag());
                masterDto.setMasterId(master.getMasterId());
                masterDto.setProfile(master.getProfile());
                masterDto.setMasterName(master.getMasterName());
                masterDto.setUpdateTime(master.getUpdateTime());
                masterDto.setTitle(master.getTitle());
                masterDto.setWorksCount((long)master.getGoods().size());
                masterDtos.add(masterDto);
            }
        }
        return masterDtos;
    }

    @Override
    public void openMasterLive() {

    }

    @Override
    public MasterBrief getMasterBrief(int masterId) {
        MasterBrief masterBrief = null;
        try{
            String sql1 = String.format("select a.master_id, a.brief, a.cover_url from master a where a.master_id = %d and a.del_flag = 0", masterId);
            String sql2 = String.format("select count(*) as count from attention a where a.master_id = %d and a.del_flag = 0", masterId);
            Map<String, Object> results1 = jdbcTemplate.queryForMap(sql1);
            Map<String, Object> results2 = jdbcTemplate.queryForMap(sql2);
            masterBrief = new MasterBrief();
            if(results1!=null&&results1.size()>0){
                int id = (results1.get("master_id"))!=null?((Long) results1.get("master_id")).intValue():0;
                String coverUrl = (String) results1.get("cover_url");
                String brief = (String) results1.get("brief");
                Long count = (Long) results2.get("count");
                Double attentionCount = 0.0d;
                if(count>10000){
                    attentionCount = count/10000d;
                    masterBrief.setAttentionCount(String.valueOf(attentionCount) + "万");
                }else{
                    masterBrief.setAttentionCount(String.valueOf(attentionCount.intValue()));
                }
                masterBrief.setBrief(brief);
                masterBrief.setMasterId(id);
                masterBrief.setCoverUrl(coverUrl);
            }
        }catch (EmptyResultDataAccessException e){
            logger.error(Exceptions.getStackTraceAsString(e));
        }
        return masterBrief;
    }

    @Override
    public MasterSerialsDto getMasterCommonData(int masterId) {
        MasterSerialsDto masterSerialsDto = new MasterSerialsDto();
        masterSerialsDto.setBrief(getMasterBrief(masterId));
        masterSerialsDto.setMasterLive(getMasterLive(masterId));
        return masterSerialsDto;
    }

    @Override
    public List<MasterNewsDto> getMasterNews(int masterId, int start, int end) {
        return convertNews(masterNewsRepository.getMasterNews(masterId, start, end));
    }

    @Override
    public PageResponse<MasterNewsDto> getMasterNewsResponse(int masterId, int start, int size) {
        PageResponse<MasterNewsDto> response = new PageResponse<>();
        long count = masterNewsRepository.countMasterNews(masterId);
        int end = start + size;
        boolean hasMore = true;
        if(end >= count){
            hasMore = false;
            end = ((Long)count).intValue();
        }
        List<MasterNewsDto> newsDtos = getMasterNews(masterId, start, end);
        response.setTotalCount(count);
        response.setCurrent(end);
        response.setHasMore(hasMore);
        response.setElements(newsDtos);
        return response;
    }

    @Override
    public MasterLiveDto getMasterLive(int masterId) {
        MasterLive live = masterLiveRepository.findByMasterIdAndStatusAndDelFlag(masterId, 0,0);
        MasterLiveDto liveDto = new MasterLiveDto();
        if(live!=null){
            liveDto.setId(live.getId());
            liveDto.setChannelId(live.getChannelId());
            liveDto.setCoverUrl(live.getCoverUrl());
            liveDto.setCreateTime(live.getCreateTime());
            liveDto.setHttpPullUrl(live.getHttpPullUrl());
            liveDto.setHttpPushUrl(live.getHttpPushUrl());
            liveDto.setMasterId(live.getMasterId());
            liveDto.setStatus(live.getStatus());
            liveDto.setUpdateTime(live.getUpdateTime());
        }
        return liveDto;
    }

    @Override
    public String uploadMasterSingleImg(long masterId, File file) {
        return uploadMasterMultiImg(masterId, new File[]{file}).get(0);
    }

    @Override
    public List<String> uploadMasterMultiImg(long masterId, File[] files) {
        FileServiceRequest fileServiceRequest = new FileServiceRequest();
        List<File> fileList = Arrays.asList(files);
        List<String> keys = Lists.newArrayList();
        Map<String, String> userMetaData = Maps.newConcurrentMap();
        userMetaData.putIfAbsent("masterId", String.valueOf(masterId));
        userMetaData.putIfAbsent("action", "add");
        for(File file: files){
            keys.add(file.getName());
        }
        fileServiceRequest.setFiles(fileList);
        fileServiceRequest.setKeys(keys);
        fileServiceRequest.setUserMetaData(userMetaData);
        fileServiceRequest.setCallback(new OSSCallback(masterCallback));
        List<UploadResponse> responses = helper.uploadFile(fileServiceRequest);
        List<String> urls = Lists.newArrayList();
        if(responses!=null&&responses.size()>0){
            for(UploadResponse response:responses){
                urls.add(response.getUrl());
            }
        }
        return urls;
    }

    @Override
    public void uploadMasterVod(long masterId, UploadVodRequest request) {
        Map<String, Object> selfParam = Maps.newHashMap();
        selfParam.putIfAbsent("title", "视频1");
        selfParam.putIfAbsent("file", request.getVideo());
        selfParam.putIfAbsent("coverUrl", request.getCoverUrl());
        selfParam.putIfAbsent("tags",request.getTags());
        selfParam.putIfAbsent("description",request.getDescription());
        selfParam.putIfAbsent("cateId",request.getCateId());
        new AliyunVod(RequestType.UPLOAD, accessKeyId, accessKeySecret).
                buildRequest().
                withSelfParam(selfParam).
                run().
                response();
    }

    @Override
    public void deleteImgFiles(long masterId, List<String> fileNames) {
        FileServiceRequest fileServiceRequest = new FileServiceRequest();
        Map<String, String> userMetaData = Maps.newHashMap();
        userMetaData.putIfAbsent("masterId", String.valueOf(masterId));
        fileServiceRequest.setKeys(fileNames);
        fileServiceRequest.setUserMetaData(userMetaData);
        fileServiceRequest.setCallback(new OSSCallback(masterCallback));
        helper.deleteFile(fileServiceRequest);
    }

    @Override
    public PageResponse<GoodsDto> getMasterWorks(long masterId, int start, int size) {
        PageResponse<GoodsDto> pageResponse = new PageResponse<>();
        Master master = masterRepository.findOne(masterId);
        if(master!=null){
            if(master.getGoods()!=null&&master.getGoods().size()>0){
                List<Goods> goods = new ArrayList<>(master.getGoods());
                boolean hasMore = true;
                long totalCount = goods.size();
                int end = start + size;
                if(end >= totalCount){
                    end = (int)totalCount;
                    hasMore = false;
                }
                List<Goods> subGoods = new ArrayList<>(goods.subList(start, end));
                List<GoodsDto> elements = convertGoods(subGoods);
                pageResponse.setElements(elements);
                pageResponse.setCurrent(end);
                pageResponse.setHasMore(hasMore);
            }
        }
        return pageResponse;
    }

    @Override
    public void plusMasterWorks(long masterId, long goodsId) {
        MasterGoods masterGoods = new MasterGoods();
        masterGoods.setCreateTime(new Date());
        masterGoods.setGoods_id(goodsId);
        masterGoods.setMaster_id(masterId);
        masterGoods.setId(IDGen.gen());
        masterGoodsRepository.save(masterGoods);
    }

    @Override
    public PageResponse<CommentDto> getComments(long masterId, int start, int size) {
        long totalCount = commentRepository.countByDelFlagAndMasterId(0, masterId);
        PageResponse<CommentDto> pageResponse = new PageResponse<>();
        boolean hasMore = true;
        if(totalCount==0)
            return null;
        int end = start + size;
        if(end >= totalCount){
            end = (int)totalCount;
            hasMore = false;
        }
        List<Comment> comments = commentRepository.getComments(0, masterId, start, end);
        if(comments!=null&&comments.size()>0){
            List<CommentDto> commentDtos = Lists.newArrayList();
            for(Comment comment: comments){
                commentDtos.add(convertComment(comment));
            }
            pageResponse.setElements(commentDtos);
        }
        pageResponse.setHasMore(hasMore);
        pageResponse.setCurrent(end);
        pageResponse.setTotalCount(totalCount);
        return pageResponse;
    }

    @Override
    public CommentDto addMasterrComment(long uid, long masterId, String content, int commentType, long replyCommentId) {
        Comment comment = new Comment();
        comment.setCommentType(commentType);
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        comment.setMasterId(masterId);
        comment.setUid(uid);
        comment.setReplyContent(content);
        if(commentType==2){
            comment.setReplyCommentId(replyCommentId);
        }
        comment = commentRepository.save(comment);
        CommentDto commentDto = convertComment(comment);
        return commentDto;
    }

    private CommentDto convertComment(Comment comment){
        if(comment!=null){
            CommentDto commentDto = new CommentDto();
            commentDto.setCommentId(comment.getCommentId());
            commentDto.setCommentType(comment.getCommentType());
            commentDto.setCreateTime(comment.getCreateTime());
            commentDto.setDelFlag(comment.getDelFlag());
            commentDto.setMasterId(comment.getMasterId());
            commentDto.setReplyCommentId(comment.getReplyCommentId());
            commentDto.setReplyContent(comment.getReplyContent());
            commentDto.setUid(comment.getUid());
            commentDto.setUpdateTime(comment.getUpdateTime());
            if(comment.getReplies()!=null&&comment.getReplies().size()>0){
                List<CommentDto> commentDtos = Lists.newArrayList();
                for(Comment subComment: comment.getReplies()){
                    CommentDto subCommentDto = convertComment(subComment);
                    commentDtos.add(subCommentDto);
                }
                commentDto.setReplies(commentDtos);
            }
            return commentDto;
        }
        return null;
    }

    @Override
    public void deleteComment(long commentId) {
        Comment comment = commentRepository.findOne(commentId);
        if(comment!=null){
            comment.setDelFlag(1);
            comment.setUpdateTime(new Date());
            commentRepository.save(comment);
        }
    }

    private List<GoodsDto> convertGoods(List<Goods> subGoods) {
        List<GoodsDto> goodsDtos = Lists.newArrayList();
        if(subGoods!=null&&subGoods.size()>0){
            for(Goods goods:subGoods){
                GoodsDto goodsDto = new GoodsDto();
                goodsDto.setAdvanceSaleDays(goods.getAdvanceSaleDays());
                goodsDto.setArtistIds(goods.getArtistIds());
                goodsDto.setBarCode(goods.getBarCode());
                goodsDto.setCategoryCode(goods.getCategoryCode());
                goodsDto.setCode(goods.getCode());
                goodsDto.setCommentCount(goods.getCommentCount());
                goodsDto.setCommission(goods.getCommission());
                goodsDto.setCommnetScore(goods.getCommnetScore());
                goodsDto.setContent(goods.getContent());
                goodsDto.setCreateTime(goods.getCreateTime());
                goodsDto.setDefaultImage(goods.getDefaultImage());
                goodsDto.setGradeId(goods.getGradeId());
                goodsDto.setId(goods.getId());
                goodsDto.setIntro(goods.getIntro());
                goodsDto.setIsDel(goods.getIsDel());
                goodsDto.setMarketPrice(goods.getMarketPrice());
                goodsDto.setMultiImages(goods.getMultiImages());
                goodsDto.setName(goods.getName());
                goodsDto.setMaterialId(goods.getMaterialId());
                goodsDto.setPayCount(goods.getPayCount());
                goodsDto.setSalePrice(goods.getSalePrice());
                goodsDto.setSortIndex(goods.getSortIndex());
                goodsDto.setSellCount(goods.getSellCount());
                goodsDto.setStatus(goods.getStatus());
                goodsDto.setStockCount(goods.getStockCount());
                goodsDto.setSubTitle(goods.getSubTitle());
                goodsDto.setUpdateTime(goods.getUpdateTime());
                goodsDto.setVideoId(goods.getVideoId());
                goodsDto.setVideoImg(goods.getVideoImg());
                goodsDto.setViewCount(goods.getViewCount());
                goodsDtos.add(goodsDto);
            }
        }
        return goodsDtos;
    }

    private List<MasterNewsDto> convertNews(List<MasterNews> news){
        if(news!=null && news.size()>0){
            List<MasterNewsDto> newsDtos = Lists.newArrayList();
            for(MasterNews masterNews: news){
                MasterNewsDto newsDto = new MasterNewsDto();
                newsDto.setCreateTime(masterNews.getCreateTime());
                newsDto.setDelFlag(masterNews.getDelFlag());
                newsDto.setId(masterNews.getId());
                newsDto.setImgs(masterNews.getImgs());
                newsDto.setNewsType(masterNews.getNewsType());
                newsDto.setText(masterNews.getText());
                newsDto.setUpdateTime(masterNews.getUpdateTime());
                newsDto.setVodId(masterNews.getVodId());
                newsDtos.add(newsDto);
            }
            return newsDtos;
        }
        return null;
    }
}
