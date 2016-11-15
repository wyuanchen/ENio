package com.enio.pipeline.handler.impl;

import com.enio.Channel.Channel;
import com.enio.message.Message;
import com.enio.pipeline.handler.InHandler;
import com.enio.pipeline.handler.OutHandler;

/**
 * Created by yuan on 11/15/16.
 */
public class WebSocketFrameHandler implements InHandler,OutHandler{
    @Override
    public boolean handleInputMessage(Channel channel, Message message) {
        return true;
    }

    @Override
    public boolean handleOutputMessage(Channel channel, Message message) {
        return false;
    }
}
