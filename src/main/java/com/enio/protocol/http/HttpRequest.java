package com.enio.protocol.http;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by yuan on 11/15/16.
 */
public class HttpRequest extends HttpProtocol{

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


    private HttpMethod method;
    private String query;
    private String url;



    public HttpMethod getMethod(){
        return this.method;
    }
    public void setMethod(String method){
        for(HttpMethod httpMethod:HttpMethod.values()){
            if(httpMethod.value().equals(method)){
                this.method=httpMethod;
                return;
            }
        }
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
        Iterator<Map.Entry<String,String>> iterator=header.entrySet().iterator();
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
