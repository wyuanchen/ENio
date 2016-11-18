package com.enio;

import com.enio.Channel.Channel;
import com.enio.Channel.ServerChannel;
import com.enio.eventLoop.EventLoop;
import com.enio.eventLoop.EventLoopGroup;
import com.enio.pipeline.handler.Handler;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by yuan on 10/8/16.
 */
public class ServerBootstraper {
    private int serverPort;
    private EventLoopGroup clientLoopGroup;
    private EventLoopGroup serverLoopGroup;
    private ServerSocketChannel serverSocketChannel;

//    private List<Channel> clientChannels= Channel.channels;

    private List<Handler> serverHandlers=new LinkedList<Handler>();
    private List<Handler> clientHandlers=new LinkedList<Handler>();


    public ServerBootstraper(int port,int serverLoopGroupSize,int clientLoopGroupSize){
        this.serverPort=port;
        this.serverLoopGroup=new EventLoopGroup(serverLoopGroupSize);
        this.clientLoopGroup=new EventLoopGroup(clientLoopGroupSize);
    }

    public Future<Channel> connect(){
        try {
            //打开监听通道
            serverSocketChannel=ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(serverPort),1024);
            ServerChannel channel=new ServerChannel(serverSocketChannel,
                    SelectionKey.OP_ACCEPT,
                    clientLoopGroup,
                    serverHandlers,
                    clientHandlers);
            EventLoop eventLoop=serverLoopGroup.selectOneEventLoop();
            Future<Channel> future=channel.bindEventLoop(eventLoop);
            return future;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Close the server
     */
    public void shutdown(){
        serverLoopGroup.shutdown();
        clientLoopGroup.shutdown();
    }

    /**
     * add handler to the server handlers
     * @param handler
     */
    public void addHandler(Handler handler){
        serverHandlers.add(handler);
    }
    public void addHandler(List<Handler> handlerList){
        serverHandlers.addAll(handlerList);
    }

    public void addClientHandler(Handler handler){
        clientHandlers.add(handler);
    }
    public void addClientHanlder(List<Handler> handlerList){
        clientHandlers.addAll(handlerList);
    }


}
