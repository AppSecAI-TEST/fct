package com.fct.thirdparty.oss.callback;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GenericRequest;
import com.fct.thirdparty.oss.response.Response;
import com.fct.thirdparty.oss.response.UploadResponse;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Created by nick on 2017/5/17.
 */
public class MysqlCallbcak extends AbstractCallback<String> {

    private DataSource dataSource;

    private final static String TB_NAME = "tb_";

    public MysqlCallbcak(DataSource dataSource, OSSClient ossClient, GenericRequest request){
        this.dataSource = dataSource;
        this.ossClient = ossClient;
        this.request = request;
    }

    @Override
    public String onSuccess(Response response) {
        try {
            if(dataSource == null)
                throw new RuntimeException("dataSource should not be Null");
            String executeSQL = buildInsertSQL(response);
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            if(statement.execute(executeSQL))
                return "success";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    @Override
    public String onFailure(Response response) {
        return null;
    }

    private String buildInsertSQL(Response response){
        if(response instanceof UploadResponse){
            UploadResponse uploadResponse = (UploadResponse) response;
            String key = uploadResponse.getKey();
            String imgUrl = uploadResponse.getUrl();
            Map<String, String> userMetaData = uploadResponse.getUserMetaData();
            String insertSQL = String.format("insert into " + TB_NAME + "values(%s, %s)",
                    key, imgUrl, userMetaData.get(""));
            return insertSQL;
        }
        return null;
    }
}
