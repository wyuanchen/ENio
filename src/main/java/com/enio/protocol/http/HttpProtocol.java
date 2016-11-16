package com.enio.protocol.http;

/**
 * Created by yuan on 11/15/16.
 */
public interface HttpProtocol {


    enum HttpVersion{
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

    String CRLF="\r\n";

    String HTTP_HEADER_USER_AGENT="User-Agent:";
    String HTTP_HEADER_ACCPET="Accpet:";
    String HTTP_HEADER_HOST="Host:";
    String HTTP_HEADER_ORIGIN="Origin:";
    String HTTP_HEADER_UPGRADE="Upgrade:";
    String HTTP_HEADER_CONNECTION="Connection:";
    String HTTP_SEC_WEBSOCKET_KEY="Sec-WebSocket-Key:";
    String HTTP_SEC_WEBSOCKET_VERSION="Sec-WebSocket-Version:";
    String HTTP_HEADER_CONTENT_LENGTH="Content-Length";
    String HTTP_SEC_WEBSOCKET_ACCPET="Sec-WebSocket-Accept:";

}
