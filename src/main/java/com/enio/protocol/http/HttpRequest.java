package com.enio.protocol.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yuan on 11/15/16.
 */
public class HttpRequest implements HttpProtocol{
    public void setVersion(String version) {
        this.version = version;
    }

    enum HttpMethod{
        HttpGet("Get"),
        HttpPost("Post"),
        HttpPut("Put"),
        HttpDelete("Delete");

        private String v;
        HttpMethod(String value){
            this.v=value;
        }
        private String value(){
            return v;
        }
    };

    private Map<String,String> headers=new HashMap<String,String>();
    private HttpMethod method;
    private String version;
    private String body;
    private String query;
    private String url;

    public Map<String, String> getHeaders() {
        return headers;
    }


    public HttpMethod getMethod(){
        return this.method;
    }
    public void setMethod(String method){
        if(method.equalsIgnoreCase("GET"))
            this.method=HttpMethod.HttpGet;
        else if(method.equalsIgnoreCase("POST"))
            this.method=HttpMethod.HttpPost;
        else if(method.equalsIgnoreCase("PUT"))
            this.method=HttpMethod.HttpPut;
        else if(method.equalsIgnoreCase("DELETE"))
            this.method=HttpMethod.HttpDelete;
    }

    public String getVersion(){
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

    @Override
    public String toString() {
        StringBuilder str= new StringBuilder(method.value()+" / "+version+"\r\n");
        Iterator<Map.Entry<String,String>> iterator=headers.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry=iterator.next();
            str.append(entry.getKey());
            str.append(": ");
            str.append(entry.getValue());
            str.append("\r\n");
        }
        str.append("\r\n");
        return str.toString();
    }
}
