package com.fct.web.admin.session;

import java.util.Map;

/**
 * @author ningyang
 */
public final class Session {

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    private String accessToken;
    private String userId;
    private String secret;
    private Boolean isValid;

    public Session() {
    }

    public Session(String accessToken, Map<String, String> objectMap) {
        this.accessToken = accessToken;
        this.userId = objectMap.get(Key.id.name());
        this.secret = objectMap.get(Key.secret.name());
        this.isValid = "1".equals(objectMap.get(Key.valid.name()));
    }

    public Boolean isGuest() {
        return userId == null;
    }

    public enum Key {
        token,
        id,
        secret,
        valid,
    }


}