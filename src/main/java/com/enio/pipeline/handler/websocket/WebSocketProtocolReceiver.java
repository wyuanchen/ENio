package com.enio.pipeline.handler.websocket;

import com.enio.Channel.Channel;
import com.enio.message.Message;
import com.enio.pipeline.handler.InHandler;
import com.enio.protocol.websocket.WebSocketProtocol;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yuan on 11/18/16.
 */
public class WebSocketProtocolReceiver implements InHandler{
    private volatile byte heartBeat=3;

    @Override
    public boolean handleInputMessage(Channel channel, Message message) {
        List<WebSocketProtocol> webSocketProtocols= (List<WebSocketProtocol>) message.getData();
        List<String> strs=new LinkedList<String>();
        for(WebSocketProtocol webSocketProtocol:webSocketProtocols){
            handle(channel,webSocketProtocol,strs);
        }
        message.setData(strs);
        return true;
    }

    private void handle(Channel channel,WebSocketProtocol webSocketProtocol,List<String> strs) {
        switch (webSocketProtocol.getOpcode()){
            case Text:
                String str=extractString(webSocketProtocol);
                strs.add(str);
                break;
            case Closing:
                System.out.println("The Websocket channel is closing!");
                channel.disconnect();
                break;
            case Pong:
            case Ping:
                if(heartBeat<=3)
                    heartBeat++;
                break;
        }
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
