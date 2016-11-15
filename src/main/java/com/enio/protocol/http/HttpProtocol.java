package com.enio.protocol.http;

/**
 * Created by yuan on 11/15/16.
 */
public interface HttpProtocol {
    enum HttpMethod{
        HttpGet,
        HttpPost,
        HttpPut,
        HttpDelete
    };

    enum HttpVersion{
        HttpVersion_0_0,
        HttpVersion_1_0,
        HttpVersion_1_1;
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

}
