package com.enio.pipeline.handler.websocket;

import com.enio.Channel.Channel;
import com.enio.message.Message;
import com.enio.pipeline.handler.Handler;
import com.enio.pipeline.handler.InHandler;
import com.enio.pipeline.handler.OutHandler;
import com.enio.pipeline.handler.http.HttpRequestDecoder;
import com.enio.pipeline.handler.http.HttpResponseEncoder;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yuan on 11/17/16.
 */
public class WebSocketHandler implements InHandler,OutHandler{
    private List<Handler> handlers=new LinkedList<Handler>();

    public WebSocketHandler(){
        handlers.add(new HttpRequestDecoder());
        handlers.add(new WebSocketHandshakeResponser());
        handlers.add(new HttpResponseEncoder());
    }

    @Override
    public boolean handleInputMessage(Channel channel, Message message) {
        boolean isContinue=false;
        for(Handler handler:handlers){
            if(handler instanceof InHandler){
                isContinue=((InHandler)handler).handleInputMessage(channel,message);
                if(!isContinue)
                    return false;
            }
        }
        return true;
    }

    @Override
    public boolean handleOutputMessage(Channel channel, Message message) {
        boolean isContinue=false;
        for(Handler handler:handlers){
            if(handler instanceof OutHandler){
                isContinue=((OutHandler)handler).handleOutputMessage(channel,message);
                if(!isContinue)
                    return false;
            }
        }
        channel.pipeline().remove(this);
        channel.pipeline().addLast(new WebSocketProtocolEncoder());
        channel.pipeline().addLast(new WebSocketProtocolBuilder());
        channel.pipeline().addLast(new WebSocketProtocolDecoder());
        channel.pipeline().addLast(new WebSocketProtocolReceiver());

        return true;
    }
}
