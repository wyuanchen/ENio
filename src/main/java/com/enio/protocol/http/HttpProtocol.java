package com.enio.protocol.http;

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
    protected HttpVersion version;


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

}
