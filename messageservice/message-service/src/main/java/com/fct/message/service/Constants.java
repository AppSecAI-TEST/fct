package com.fct.message.service;

/**
 * Created by jon on 2017/5/6.
 */
public class Constants {

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
