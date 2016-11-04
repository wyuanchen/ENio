package com.enio;

import com.enio.Channel.Channel;
import com.enio.Channel.ServerChannel;
import com.enio.message.Message;
import com.enio.pipeline.handler.InitialHandler;
import com.enio.pipeline.handler.SimpleInHandler;
import com.enio.pipeline.handler.SimpleOutHandler;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by yuan on 10/21/16.
 */
public class ServerTest {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ServerBootstraper serverBootstraper=new ServerBootstraper(12345,1,3);
        serverBootstraper.addClientHandler(new InitialHandler() {
            @Override
            public void onChannelInitialized(Channel channel, Message message) {
                channel.pipeline().addLast(new SimpleInHandler());
                channel.pipeline().addLast(new SimpleOutHandler());
            }
        });
//        enioServer.addClientHandler(new SimpleInHandler());
//        enioServer.addClientHandler(new SimpleOutHandler());
        Future<Channel> future=serverBootstraper.connect();
        ServerChannel channel=(ServerChannel)future.get();
        channel.handleAcceptable();
    }
}
