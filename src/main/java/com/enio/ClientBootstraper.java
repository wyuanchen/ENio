package com.enio;

import com.enio.Channel.Channel;
import com.enio.eventLoop.EventLoop;
import com.enio.eventLoop.EventLoopGroup;
import com.enio.Channel.ClientChannel;
import com.enio.pipeline.handler.Handler;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by yuan on 10/18/16.
 */
public class ClientBootstraper {
    private String serverIP="127.0.0.1";
    private int serverPort=123456;
    private EventLoopGroup eventLoopGroup=new EventLoopGroup();
    private ClientChannel clientChannel;
    private List<Handler> handlers=new LinkedList<Handler>();

    public ClientBootstraper(String serverIP,int serverPort){
        this.serverIP=serverIP;
        this.serverPort=serverPort;
    }

    public Future<Channel> connect(){
        try {
            SocketChannel socketChannel=SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(serverIP,serverPort));
            socketChannel.configureBlocking(false);
            clientChannel=new ClientChannel(socketChannel,SelectionKey.OP_READ,handlers);
            EventLoop eventLoop=eventLoopGroup.selectOneEventLoop();
            Future<Channel> future=clientChannel.bindEventLoop(eventLoop);
            return future;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
//            System.exit(1);
        }

    }


    /**
     * add handler
     * @param handler
     */
    public void addHandler(Handler handler){
        handlers.add(handler);
    }

    public void addHandler(List<Handler> handlerList){
        handlers.addAll(handlerList);
    }


}
