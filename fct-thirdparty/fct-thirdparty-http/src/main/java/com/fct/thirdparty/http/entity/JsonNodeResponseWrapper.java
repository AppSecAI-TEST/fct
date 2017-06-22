package com.fct.thirdparty.http.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fct.thirdparty.http.exceptions.ResponseParseException;
import com.fct.thirdparty.http.util.JsonConverter;

import java.io.IOException;

/**
 * Created by ningyang on 2017/5/13.
 */
public class JsonNodeResponseWrapper extends AbstractResponseWrapper<JsonNode> {

    private JsonNode jsonNodeBody;

    public boolean isRead() {
        return jsonNodeBody != null;
    }

    @Override
    public JsonNode convertBody() {
        if(!this.isRead()) {
            try {
                jsonNodeBody = JsonConverter.toJsonNode(this.response.body().byteStream());
            } catch (IOException ex) {
                throw new ResponseParseException(ex);
            }
        }
        return jsonNodeBody;
    }
}
