package com.enio.pipeline.handler.websocket;

import com.enio.Channel.Channel;
import com.enio.message.Message;
import com.enio.pipeline.handler.InHandler;

/**
 * Created by yuan on 11/17/16.
 */
public class WebSocketProtocolDecoder implements InHandler{


    @Override
    public boolean handleInputMessage(Channel channel, Message message) {
        return true;
    }


}
