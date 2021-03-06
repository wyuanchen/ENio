package com.enio.Channel;

import com.enio.buffer.EByteBuffer;
import com.enio.eventLoop.EventLoop;
import com.enio.eventLoop.event.Event;
import com.enio.message.Message;
import com.enio.pipeline.Pipeline;
import com.enio.pipeline.handler.Handler;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by yuan on 10/8/16.
 */
public abstract class Channel {
//    public static final List<Channel> channels= Collections.synchronizedList(new LinkedList<Channel>());

    protected SelectionKey key;
    protected SelectableChannel channel;
    protected EventLoop eventLoop;
    protected EByteBuffer readBuffer;
    protected EByteBuffer writeBuffer;
    protected Pipeline pipeLine;
    //the initial selection key option when the Channel is create
    private final int initSelectionKeyOP;

    public Pipeline pipeline(){
        return pipeLine;
    }

    public Channel(SelectableChannel channel,int initSelectionKeyOP,List<Handler> handlers){
        this.channel=channel;
        this.initSelectionKeyOP=initSelectionKeyOP;
        this.readBuffer=new EByteBuffer();
        this.writeBuffer=new EByteBuffer();
        this.pipeLine=new Pipeline();
        registerHanlders(handlers);
    }
    public Channel(SelectableChannel channel,int initSelectionKeyOP){
        this(channel,initSelectionKeyOP,null);
    }


    public EByteBuffer getWriteBuffer() {
        return writeBuffer;
    }
    public EByteBuffer getReadBuffer(){
        return readBuffer;
    }
    public SelectionKey getKey(){
        return key;
    }

    public Future<Channel> bindEventLoop(EventLoop eventLoop) throws IOException {
        this.eventLoop=eventLoop;
        channel.configureBlocking(false);
        Future<Channel> future=eventLoop.submit(new Event<Channel>(Event.Priority.Top) {
            @Override
            public Channel call() throws Exception {
                Selector selector=eventLoop.getSelector();
                SelectionKey key=channel.register(selector,initSelectionKeyOP);
                key.attach(Channel.this);
                Channel.this.key=key;
//                Channel.this.channel=key.channel();
//                Channel.channels.add(Channel.this);
                Channel.this.handleInitial();
                return Channel.this;
            }
        });

        return future;
    }



//    /**
//     * This method will be invoked after the event loop bind the channel successfully
//     */
//    abstract protected void bindEventLoopSuccessfully();


    private void registerHanlders(List<Handler> handlers){
        pipeLine.addLast(handlers);
    }

    /**
     * The handle Channel initialization after the channel is bound to a eventLoop
     */
    private void handleInitial() {
        pipeLine.onChannelInitialized(this,new Message());
    }

    public void handle() throws IOException {
        if(!key.isValid())
            return;
        if(key.isAcceptable()){
            handleAcceptable();
        }else if(key.isReadable()){
            handleReadable();
        }else if(key.isWritable()){
            handleWritable();
        }else if(key.isConnectable()){
            handleConnectable();
        }
    }

    public void handleConnectable() {
    }

    public void handleReadable() throws IOException {
    }

    public void handleWritable() throws IOException {
    }

    public void handleAcceptable() {
    }


    /**
     * Disconnect the channel
     */
    public void disconnect(){
        eventLoop.submit(new Event<Object>(Event.Priority.Low) {
            @Override
            public Object call() throws Exception {
                Message message=new Message();
                pipeline().handleChannelClose(Channel.this,message);
                channel.close();
                return null;
            }
        });
    }




}
