package com.enio.pipeline.handler;

import com.enio.Channel.Channel;
import com.enio.message.Message;

/**
 * Created by yuan on 11/4/16.
 */
public interface OutHandler extends Handler{
    /**
     * handle the message to outside
     * @param message
     * @return
     */
    public boolean handleOutputMessage(Channel channel, Message message);
}
