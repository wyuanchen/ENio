package com.enio.pipeline.handler.websocket;

import com.enio.Channel.Channel;
import com.enio.message.Message;
import com.enio.pipeline.handler.InHandler;
import com.enio.protocol.websocket.WebSocketProtocol;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by yuan on 11/18/16.
 */
public class WebSocketProtocolHandler implements InHandler{

    @Override
    public boolean handleInputMessage(Channel channel, Message message) {
        List<WebSocketProtocol> webSocketProtocols= (List<WebSocketProtocol>) message.getData();
        for(WebSocketProtocol webSocketProtocol:webSocketProtocols){
            String str=extractString(webSocketProtocol);
            System.out.println(str);
        }
        return true;
    }

    private String extractString(WebSocketProtocol webSocketProtocol) {
        byte[] payload=webSocketProtocol.getPayload();
        try {
            return new String(payload,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "error";
        }
//        return new String(payload);
    }
}
