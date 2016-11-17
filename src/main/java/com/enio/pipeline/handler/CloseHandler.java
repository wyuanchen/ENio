package com.enio.pipeline.handler;

import com.enio.Channel.Channel;
import com.enio.message.Message;

/**
 * Created by yuan on 11/12/16.
 */
public interface CloseHandler extends Handler{
    boolean handleChannelClose(Channel channel, Message message);
}
