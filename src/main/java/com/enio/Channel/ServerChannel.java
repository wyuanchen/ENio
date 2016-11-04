package com.enio.Channel;

import com.enio.eventLoop.EventLoop;
import com.enio.eventLoop.EventLoopGroup;
import com.enio.message.Message;
import com.enio.pipeline.handler.Handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by yuan on 10/8/16.
 */
public class ServerChannel extends Channel {
    private EventLoopGroup eventLoops;
    private ServerSocketChannel serverSocketChannel;
    private List<Handler> clientHandlers;

    public ServerChannel(ServerSocketChannel channel, int initSelectionKeyOP, EventLoopGroup eventLoopGroup, List<Handler> handlerList,List<Handler> clientHandlers) {
        super(channel,initSelectionKeyOP,handlerList);
        this.serverSocketChannel=channel;
        this.eventLoops=eventLoopGroup;
        this.clientHandlers=clientHandlers;
    }
    public ServerChannel(ServerSocketChannel channel,int initSelectionKeyOP,EventLoopGroup eventLoopGroup) {
        this(channel,initSelectionKeyOP,eventLoopGroup,null,null);
    }



    @Override
    public void handleAcceptable() {
        try {
            System.out.println("服务器接收到新的连接请求！");
            SocketChannel socketChannel=serverSocketChannel.accept();
            EventLoop eventLoop=eventLoops.selectOneEventLoop();
            ClientChannel clientChannel=new ClientChannel(socketChannel,SelectionKey.OP_READ,clientHandlers);
            Future<Channel> channelFuture=clientChannel.bindEventLoop(eventLoop);
            Message message=new Message(channelFuture);
            pipeLine.handleAcceptableMessage(this,message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
