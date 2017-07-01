package com.fct.pay.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by jon on 2017/6/4.
 */
public class Constants {

    public Constants()
    {

    }

    public static final Logger logger = LoggerFactory.getLogger("EX");

    public static String getProjectPath() {

        /*/String courseFile= "";
        try {
            //D:\git\daotie\daotie
            File directory = new File("");// 参数为空

            courseFile = directory.getCanonicalPath();
        }
        catch (IOException exp)
        {
            exp.printStackTrace();
        }
        return courseFile;*/
        return "";
    }


}
