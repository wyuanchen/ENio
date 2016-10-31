package com.enio.pipeline;

import com.enio.Channel.Channel;
import com.enio.message.Message;
import com.enio.pipeline.handler.Handler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yuan on 10/18/16.
 */
public class Pipeline {
    protected List<Handler> handlers= Collections.synchronizedList(new LinkedList<Handler>());

    /**
     * use the handler in pipeline to process the message which was received from outside,
     * the handler will be invoked from head to end in the pipeline
     * @param channel the channel which has this pipeline
     * @param message the message need to be processed
     * @return  true-process successfully,
     *          false-process fail and will stop the pipeline to continue processing this message
     */
    public boolean handleInputMessage(Channel channel,Message message){
        boolean isContinue=false;
        for(Handler handler:handlers){
            isContinue=handler.handleInputMessage(channel,message);
            if(isContinue)
                return false;
        }
        return true;
    }

    /**
     * use the handler in pipeline to process the message which will be sent to outside,
     * the handler will be invoked from end to head in the pipeline
     * @param channel the channel which has this pipeline
     * @param message the message need to be processed
     * @return  true-process successfully,
     *          false-process fail and will stop the pipeline to continue processing this message
     */
    public boolean handleOutputMessage(Channel channel,Message message){
        int length=handlers.size();
        Handler handler=null;
        boolean isContinue=false;
        for(int i=length-1;i>=0;i--){
            handler=handlers.get(i);
            isContinue=handler.handleOutputMessage(channel,message);
            if(!isContinue)
                return false;
        }
        return true;
    }

    /**
     * append the handler to the tail of pipeline
     * @param handler the handler need to be registered
     */
    public void registerHandler(Handler handler) {
        handlers.add(handler);
    }

    /**
     * append all the handlers of the handlersList to the pipeline
     * @param handlerList
     */
    public void registerHandler(List<Handler> handlerList){
        if(handlerList==null)
            return;
        for(Handler handler:handlerList){
            if(handler.isShared())
                handlers.add(handler);
            else
                handlers.add(handler.clone());
        }
    }


}
