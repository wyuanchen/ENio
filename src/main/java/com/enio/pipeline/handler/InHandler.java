package com.enio.pipeline.handler;

import com.enio.Channel.Channel;
import com.enio.message.Message;

/**
 * Created by yuan on 11/4/16.
 */
public interface InHandler extends Handler{
    /**
     * handle the message from outside
     * @param message
     * @return
     */
    public boolean handleInputMessage(Channel channel, Message message);
}
