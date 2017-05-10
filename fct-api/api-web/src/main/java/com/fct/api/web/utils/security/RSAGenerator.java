package com.fct.api.web.utils.security;

import okio.ByteString;

import java.security.*;

/**
 *
 * <p>
 * Created by qiujun on 6/14/16.
 */
public class RSAGenerator {

    public static String[] generateKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair keyPair = kpg.genKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            ByteString publicKeyStr = ByteString.of(publicKey.getEncoded());
            PrivateKey privateKey = keyPair.getPrivate();
            ByteString privateKeyStr = ByteString.of(privateKey.getEncoded());
            return new String[]{publicKeyStr.base64(), privateKeyStr.base64()};
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void main(String[] args){
//        String pwd = "123456";
//        String encrypted = RSA.encryptByPublicKey(pwd, SecurityKeyMap.keyMap.get("public"));//"DnjZL8m8NdU7N6CRT1r0P1h9tG3wo0GxPWeBPbXJ4BCQQBe3zGFFuJ+VMmJJUcg13rg5S8MrXuhzX5PhndobgWnFshAvstFv5Jbn28oAM3c5MuMmnCAr7TFwHArjVONX+q5rAxf673LHpIXDo3FsolR5D1E6nKPtc61ujFEFleyEVbjibMQSW48F1VhoUUj4mm+qLnarQbTpp2IahsJegIgUcAdyT4eWZDwpinn5OUkELXtQyAnAGc6mW74wO+vyWzJW2Ek3hrC2LJJFRRqtkeF3EqsqOBYDF+f1RpESKjRi7Rs/jIrDH10uZ9QryqSG5ymTWjpDYFMPFBT/uitnrg==";
//        System.out.println(encrypted);
//        String decrypted = RSA.decryptByPrivateKey(encrypted, SecurityKeyMap.keyMap.get("private"));
//        System.out.println(decrypted);
//    }
}
