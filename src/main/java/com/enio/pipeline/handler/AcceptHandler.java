package com.enio.pipeline.handler;

import com.enio.Channel.Channel;
import com.enio.message.Message;

/**
 * Created by yuan on 11/4/16.
 */
public interface AcceptHandler extends Handler {
    boolean handlerAccept(Channel channel, Message message);
}
