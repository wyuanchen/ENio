package com.enio.pipeline.handler.websocket;

import com.enio.Channel.Channel;
import com.enio.message.Message;
import com.enio.pipeline.handler.OutHandler;
import com.enio.protocol.websocket.WebSocketFrame;
import com.enio.protocol.websocket.WebSocketProtocol;

import java.io.UnsupportedEncodingException;

/**
 * Created by yuan on 11/17/16.
 */
public class WebSocketProtocolBuilder implements OutHandler{
    @Override
    public boolean handleOutputMessage(Channel channel, Message message) {
        String str= (String) message.getData();
        byte[] bytes;
        try {
            bytes=str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        WebSocketProtocol webSocketProtocol=new WebSocketFrame();
        webSocketProtocol.setFin(true);
        webSocketProtocol.setPayload(bytes);
        webSocketProtocol.setOpcode(WebSocketProtocol.Opcode.Text);
        webSocketProtocol.setTransferMasked(false);
        message.setData(webSocketProtocol);
        return true;
    }
}
