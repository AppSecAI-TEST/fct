package com.fct.thirdparty.http.builder;

import com.fct.thirdparty.http.exceptions.RequestNotCompleteException;
import com.squareup.okhttp.*;
import com.squareup.okhttp.Request.Builder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by ningyang on 2017/5/13.
 */
public class RequestBuilder {

    private Builder requestBuilder = new Builder();

    private String url;
    private String method;
    private LinkedList<String> keys = new LinkedList();
    private LinkedList<String> values = new LinkedList();
    private String body;

    public RequestBuilder() {

    }

    public RequestBuilder url(String url) {
        this.url = url;
        return this;
    }

    public RequestBuilder param(String key, String value) {
        this.keys.add(key);
        this.values.add(value);
        return this;
    }

    public RequestBuilder params(String[] params) {
        if(params != null) {
            for(int i = 0; i < params.length / 2; ++i) {
                if(params[i * 2 + 1] != null) {
                    this.keys.add(params[i * 2]);
                    this.values.add(params[i * 2 + 1]);
                }
            }
        }
        return this;
    }

    public RequestBuilder params(Map<String, String> params) {
        if(params != null) {
            Iterator<String> it = params.keySet().iterator();
            while(it.hasNext()) {
                String key = it.next();
                this.keys.add(key);
                this.values.add(params.get(key));
            }
        }
        return this;
    }

    public RequestBuilder body(String body) {
        this.body = body;
        return this;
    }

    public RequestBuilder header(String key, String value) {
        this.requestBuilder.addHeader(key, value);
        return this;
    }

    public RequestBuilder headers(String[] headers) {
        if(headers != null) {
            for(int i = 0; i < headers.length / 2; ++i) {
                if(headers[i * 2 + 1] != null) {
                    this.requestBuilder.addHeader(headers[i * 2], headers[i * 2 + 1]);
                }
            }
        }
        return this;
    }

    public RequestBuilder headers(Map<String, String> headers) {
        if(headers != null) {
            Iterator<String> it = headers.keySet().iterator();
            while(it.hasNext()) {
                String key = it.next();
                this.requestBuilder.addHeader(key, headers.get(key));
            }
        }

        return this;
    }

    public RequestBuilder get() {
        this.method = "GET";
        return this;
    }

    public RequestBuilder post() {
        this.method = "POST";
        return this;
    }

    public RequestBuilder delete() {
        this.method = "DELETE";
        return this;
    }

    public Request build() {
        if(this.url != null && this.method != null) {
            com.squareup.okhttp.HttpUrl.Builder urlBuilder;
            Iterator keyIterator;
            Iterator valueIterator;
            if("GET".equals(this.method)) {
                urlBuilder = HttpUrl.parse(this.url).newBuilder();
                keyIterator = this.keys.iterator();
                valueIterator = this.values.iterator();
                while(keyIterator.hasNext()) {
                    urlBuilder.addQueryParameter((String)keyIterator.next(), (String)valueIterator.next());
                }
                this.requestBuilder.url(urlBuilder.build());
                this.requestBuilder.get();
            } else if("POST".equals(this.method)) {
                if(this.body != null) {
                    RequestBody urlBuilder2 = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), this.body);
                    requestBuilder.post(urlBuilder2);
                } else {
                    FormEncodingBuilder urlBuilder1 = new FormEncodingBuilder();
                    keyIterator = this.keys.iterator();
                    valueIterator = this.values.iterator();

                    while(keyIterator.hasNext()) {
                        urlBuilder1.add((String)keyIterator.next(), (String)valueIterator.next());
                    }

                    requestBuilder.post(urlBuilder1.build());
                }

                requestBuilder.url(this.url);
            } else {
                urlBuilder = HttpUrl.parse(this.url).newBuilder();
                keyIterator = this.keys.iterator();
                valueIterator = this.values.iterator();

                while(keyIterator.hasNext()) {
                    urlBuilder.addQueryParameter((String)keyIterator.next(), (String)valueIterator.next());
                }

                requestBuilder.url(urlBuilder.build());
                requestBuilder.delete();
            }

            return requestBuilder.build();
        } else {
            throw new RequestNotCompleteException("request not complete");
        }
    }
}
