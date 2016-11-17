package com.enio.protocol.http;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by yuan on 11/15/16.
 */
public class HttpRequest extends HttpProtocol{

    public enum HttpMethod{
        HttpGet("GET"),
        HttpPost("POST"),
        HttpPut("PUT"),
        HttpDelete("DELETE");

        private String value;
        HttpMethod(String value){
            this.value=value;
        }
        private String getValue(){
            return value;
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
            if(httpMethod.getValue().equalsIgnoreCase(method)){
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
        StringBuilder str= new StringBuilder(method.getValue()+" / "+version+"\r\n");
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
