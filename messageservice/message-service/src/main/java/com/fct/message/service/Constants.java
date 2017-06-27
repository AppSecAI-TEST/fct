package com.fct.message.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jon on 2017/5/6.
 */
public class Constants {

    public static final Logger logger = LoggerFactory.getLogger("EX");

    public  enum enumSMSSource
    {

        register("register",0),findpwd("findpwd",1),
        login("login",2);

        private Integer value;
        private String key;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getkey() {
            return key;
        }

        public void setkey(String key) {
            this.key = key;
        }

        private enumSMSSource(String key,Integer value) {
            this.value = value;
            this.key = key;
        }
    }

}
