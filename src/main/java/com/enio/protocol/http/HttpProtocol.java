package com.enio.protocol.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuan on 11/15/16.
 */
public abstract class HttpProtocol {


    public enum HttpVersion{
        HttpVersion_0_0("HTTP/0.0"),
        HttpVersion_1_0("HTTP/1.0"),
        HttpVersion_1_1("HTTP/1.1");

        HttpVersion(String value){
            this.v=value;
        }
        private String v;
        public String value(){
            return v;
        }
    };


    public static final String CRLF="\r\n";

    public static final String HTTP_HEADER_USER_AGENT="User-Agent:";
    public static final String HTTP_HEADER_ACCPET="Accpet:";
    public static final String HTTP_HEADER_HOST="Host:";
    public static final String HTTP_HEADER_ORIGIN="Origin:";
    public static final String HTTP_HEADER_UPGRADE="Upgrade:";
    public static final String HTTP_HEADER_CONNECTION="Connection:";
    public static final String HTTP_SEC_WEBSOCKET_KEY="Sec-WebSocket-Key:";
    public static final String HTTP_SEC_WEBSOCKET_VERSION="Sec-WebSocket-Version:";
    public static final String HTTP_HEADER_CONTENT_LENGTH="Content-Length";
    public static final String HTTP_SEC_WEBSOCKET_ACCPET="Sec-WebSocket-Accept:";

    protected HttpVersion version;
    protected Map<String,String> header=new HashMap<String,String>();
    protected String body;


    public void setVersion(String version) {
        for(HttpVersion httpVersion:HttpVersion.values()){
            if(httpVersion.value().equals(version)){
                this.version=httpVersion;
                return;
            }
        }
    }
    public void setVersion(HttpVersion version){
        this.version=version;
    }

    public HttpVersion getVersion(){
        return this.version;
    }

    public void addHeader(String key,String value){
        header.put(key,value);
    }
    public Map<String, String> getHeader() {
        return header;
    }
    public String getHeader(String key){
        return header.get(key);
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
