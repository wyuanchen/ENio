package com.enio.pipeline.handler.websocket;

import com.enio.Channel.Channel;
import com.enio.Channel.ClientChannel;
import com.enio.message.Message;
import com.enio.pipeline.handler.InHandler;
import com.enio.protocol.http.HttpProtocol;
import com.enio.protocol.http.HttpRequest;
import com.enio.protocol.http.HttpResponse;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * Created by yuan on 11/16/16.
 */
public class WebSocketHandshakeResponser implements InHandler{
    @Override
    public boolean handleInputMessage(Channel channel, Message message) {
        HttpRequest httpRequest= (HttpRequest) message.getData();
        String secWesocketKey=httpRequest.getHeader(HttpProtocol.HTTP_SEC_WEBSOCKET_KEY);
        String secWebSocketAccpetKey=generateWebSocketAcceptKey(secWesocketKey);
        if(secWebSocketAccpetKey==null)
            return false;
        HttpResponse httpResponse=new HttpResponse();
        httpResponse.setVersion(HttpProtocol.HttpVersion.HttpVersion_1_1);
        httpResponse.setStatusCode(HttpResponse.HttpStatusCode.HttpStatusSwichingProtocols);
        httpResponse.addHeader(HttpProtocol.HTTP_SEC_WEBSOCKET_ACCPET,secWebSocketAccpetKey);
        httpResponse.addHeader(HttpProtocol.HTTP_HEADER_UPGRADE,"websocket");
        httpResponse.addHeader(HttpProtocol.HTTP_HEADER_CONNECTION,"Upgrade");
        httpResponse.addHeader(HttpProtocol.HTTP_SEC_WEBSOCKET_PROTOCOL,"chat");

        ((ClientChannel)channel).send(httpResponse);
        channel.pipeline().remove(this);
        return true;
    }

    /**
     * Generate websocket accept key for the handshake response header
     * @param secWesocketKey the Sec-WebSocket-Key in the handshake request header
     * @return The key of Sec-WebSocket-Accept or null
     */
    private String generateWebSocketAcceptKey(String secWesocketKey) {
        if(secWesocketKey==null)
            return null;
        String webSocketAcceptKey=secWesocketKey.trim()+"258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        try{
            MessageDigest messageDigest=MessageDigest.getInstance("SHA1");
            return Base64.getEncoder().encodeToString(messageDigest.digest(webSocketAcceptKey.getBytes()));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
