package com.fct.message.service;

import com.fct.common.utils.PageUtil;
import com.fct.message.data.entity.MessageQueue;
import com.fct.message.data.repository.MessageQueueRepository;
import com.fct.message.interfaces.MessageService;
import com.fct.message.interfaces.PageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by jon on 2017/4/11.
 */
@Service(value = "messageService")
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageQueueRepository messageQueueRepository;

    @Autowired
    private JdbcTemplate jt;

    @Override
    public void create(MessageQueue message) {

        message.setStatus(0);
        message.setRequestCount(0);
        message.setCreateTime(new Date());
        messageQueueRepository.save(message);
    }

    @Override
    public void send(String typeId,String targetModule,String sourceAppName,String jsonBody,String remark)
    {
        MessageQueue message = new MessageQueue();
        message.setTypeId(typeId);
        message.setTargetModule(targetModule);
        message.setSourceAppName(sourceAppName);
        message.setBody(jsonBody);
        message.setRemark(remark);
        message.setStatus(0);
        message.setRequestCount(0);
        message.setCreateTime(new Date());

        messageQueueRepository.save(message);
    }

    @Override
    public void complete(Integer id) {
        messageQueueRepository.complete(id,new Date());
    }

    @Override
    public void fail(Integer id, String message) {
        MessageQueue msg = messageQueueRepository.findOne(id);
        if(msg.getRequestCount()>=3)
        {
            msg.setStatus(-1);  //异常不在处理。
        }
        msg.setRequestCount(msg.getRequestCount()+1);
        msg.setFailMessage(message);
        msg.setProcessTime(new Date());
        messageQueueRepository.saveAndFlush(msg);
    }

    @Override
    public void resume(Integer id) {
        messageQueueRepository.resume(id);
    }

    @Override
    public List<MessageQueue> find(String typeId) {
        return messageQueueRepository.findByTypeId(typeId);
    }

    @Override
    public PageResponse<MessageQueue> findAll(String targetModule, Integer status, Integer pageIndex, Integer pageSize) {

        List<Object> param = new ArrayList<>();
        String condition ="";
        if (StringUtils.isEmpty(targetModule)) {
            condition +=" AND targetModule=?";
            param.add(targetModule);
        }
        if(status > -1)
        {
            condition +=" AND status="+status;
        }

        String table="MessageQueue";
        String field ="*";
        String orderBy = "Id Desc";

        String sql = "SELECT Count(0) FROM MessageQueue WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<MessageQueue> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<MessageQueue>(MessageQueue.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }

        PageResponse<MessageQueue> pageResponse = new PageResponse<>();
        pageResponse.setTotalCount(count);
        pageResponse.setCurrent(end);
        pageResponse.setElements(ls);
        pageResponse.setHasMore(hasmore);

        return pageResponse;

    }

    @Autowired
    VerifyCodeManager verifyCodeManager;

    @Autowired
    SMSRecordManager smsRecordManager;

    @Override
    public void sendSMS(String cellPhone,String content,String ip,String action)
    {
        smsRecordManager.send(cellPhone,content,ip,action);
    }

    @Override
    public String getVerifyCode(String sessionId,String cellPhone)
    {
        return verifyCodeManager.create(sessionId,cellPhone);
    }

    @Override
    public int checkVerifyCode(String sessionId,String cellPhone,String code)
    {
        return verifyCodeManager.check(sessionId,cellPhone,code);
    }
}
