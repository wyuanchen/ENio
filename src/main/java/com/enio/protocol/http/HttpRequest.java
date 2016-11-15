package com.enio.protocol.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuan on 11/15/16.
 */
public class HttpRequest implements HttpProtocol{
    private Map<String,String> headers=new HashMap<String,String>();
    private HttpMethod method;
    private HttpVersion version;
    private String body;
    private String query;
    private String url;

    public Map<String, String> getHeaders() {
        return headers;
    }


    public HttpMethod getMethod(){
        return this.method;
    }
    public HttpVersion getVersion(){
        return this.version;
    }
    public void addHeader(String key,String value){
        headers.put(key,value);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpRequest(){}

}
