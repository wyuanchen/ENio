package com.enio.pipeline;

import com.enio.Channel.Channel;
import com.enio.message.Message;
import com.enio.pipeline.handler.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by yuan on 10/18/16.
 */
public class Pipeline implements PipelineHandler{
//    protected List<Handler> handlers= Collections.synchronizedList(new LinkedList<Handler>());
    protected List<Handler> handlers=new CopyOnWriteArrayList<Handler>();


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
            if(!(handler instanceof InHandler))
                continue;
            isContinue=((InHandler)handler).handleInputMessage(channel,message);
            if(!isContinue)
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
            if(!(handler instanceof OutHandler))
                continue;
            isContinue=((OutHandler)handler).handleOutputMessage(channel,message);
            if(!isContinue)
                return false;
        }
        return true;
    }

    /**
     * This method is invoked only once after the channel is bound to a eventLoop,
     * it's usually used to register the other handler after the channel is bound to a eventLoop
     * @param channel
     * @param message
     * @return
     */
    @Override
    public boolean onChannelInitialized(Channel channel,Message message){
        boolean isContinue=false;
        int len=handlers.size();
        for(Handler handler:handlers){
            if(handler instanceof InitialHandler){
                ((InitialHandler)handler).onChannelInitialized(channel,message);
                handlers.remove(handler);
            }
        }
        return true;

    }

    /**
     * The method is invoked when the server channel accept a new connection from outside
     * @param channel The Channel which has the pipeline
     * @param message The message come from previous handler in the pipeline
     */
    @Override
    public boolean handlerAccept(Channel channel,Message message) {
        boolean isContinue=false;
        for(Handler handler:handlers){
            if(handler instanceof AcceptHandler){
                isContinue=((AcceptHandler)handler).handlerAccept(channel,message);
                if(!isContinue)
                    return false;
            }
        }
        return true;
    }

    /**
     * The method is invoked when the channel is going to close
     */
    @Override
    public boolean handleChannelClose(Channel channel,Message message) {
        boolean isContinue=false;
        for(Handler handler:handlers){
            if(handler instanceof CloseHandler){
                isContinue=((CloseHandler)handler).handleChannelClose(channel,message);
                if(!isContinue)
                    return false;
            }
        }
        return true;
    }

    /**
     * append the handler to the tail of pipeline
     * @param handler the handler need to be registered
     */
    public void addLast(Handler handler) {
        if(handler==null)
            return;
        handlers.add(handler);
    }

    public void addFirst(Handler handler){
        if(handler==null)
            return;
        handlers.add(0,handler);
    }

    public void insert(int index,Handler handler){
        if(handler==null)
            return;
        handlers.add(index,handler);
    }

    /**
     * append all the handlers of the handlersList to the pipeline
     * @param handlers
     */
    public void addLast(List<Handler> handlers){
        if(handlers==null)
            return;
        this.handlers.addAll(handlers);
    }

    public void addFirst(List<Handler> handlers){
        if(handlers==null)
            return;
        this.handlers.addAll(0,handlers);
    }


    /**
     * Remove the handler in the pipeline
     * @param handler the handler need to be removed
     */
    public void remove(Handler handler) {
        handlers.remove(handler);
    }



}
