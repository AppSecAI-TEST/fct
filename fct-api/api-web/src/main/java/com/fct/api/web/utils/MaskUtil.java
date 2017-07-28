package com.fct.api.web.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


@Service
public class MaskUtil {

    public String maskBankAccount(String bankAccount) {

        return "尾号" + bankAccount.substring(bankAccount.length() - 4);
    }

    public String maskEmail(String email) {

        //rok
        int index = email.indexOf("@");
        //String name = email.substring(0, index);
        String domain = email.substring(index);

        return email.substring(0, 1) + "***" + domain;
    }

    public String autoMask(String str) {

        if (StringUtils.isAlphanumeric(str)) {
            return maskBankAccount(str);
        }

        return maskEmail(str);
    }

    public String maskMobile(String mobile) {

        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }
}
