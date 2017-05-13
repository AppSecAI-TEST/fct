package com.fct.thirdparty.http.entity;

import com.fct.thirdparty.http.exceptions.ResponseParseException;
import com.fct.thirdparty.http.util.JsonConverter;

import java.io.IOException;

/**
 * Created by ningyang on 2017/5/13.
 */
public class JsonObjectResponseWrapper<T> extends AbstractResponseWrapper<T> {

    private T objectBody;

    public JsonObjectResponseWrapper() {
    }

    public boolean isRead() {
        return this.objectBody != null;
    }

    @Override
    public T convertBody() {
        if (this.type == null) {
            throw new RuntimeException("target object type must be set.");
        } else {
            if (!this.isRead()) {
                try {
                    this.objectBody = JsonConverter.toObject(this.response.body().byteStream(), this.type);
                } catch (IOException ex) {
                    throw new ResponseParseException(ex);
                }
            }

            return this.objectBody;
        }
    }
}
