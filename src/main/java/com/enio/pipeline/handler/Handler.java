package com.enio.pipeline.handler;

import com.enio.Channel.Channel;
import com.enio.message.Message;

/**
 * Created by yuan on 10/23/16.
 */
public interface Handler {
    /**
     * handle the message from outside
     * @param message
     * @return
     */
    public boolean handleInputMessage(Channel channel,Message message);

    /**
     * handle the message to outside
     * @param message
     * @return
     */
    public boolean handleOutputMessage(Channel channel,Message message);

    /**
     * Would the handler be shared in all the pipeline
     * @return true-the hanlder can be shared in all the pipeline
     *         false-the hanlder must be new one in different pipeline
     */
    public boolean isShared();

    public Handler clone();
}
