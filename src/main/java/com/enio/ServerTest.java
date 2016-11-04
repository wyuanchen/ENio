package com.enio;

import com.enio.Channel.Channel;
import com.enio.message.Message;
import com.enio.pipeline.handler.InitialHandler;
import com.enio.pipeline.handler.SimpleInHandler;
import com.enio.pipeline.handler.SimpleOutHandler;

/**
 * Created by yuan on 10/21/16.
 */
public class ServerTest {
    public static void main(String[] args) throws InterruptedException {
        ServerBootstraper enioServer=new ServerBootstraper(12345,1,3);
        enioServer.addClientHandler(new InitialHandler() {
            @Override
            public void onChannelInitialized(Channel channel, Message message) {
                channel.pipeline().addLast(new SimpleInHandler());
                channel.pipeline().addLast(new SimpleOutHandler());
            }
        });
        enioServer.addClientHandler(new InitialHandler() {
            @Override
            public void onChannelInitialized(Channel channel, Message message) {
                System.out.println("hello");
            }
        });
//        enioServer.addClientHandler(new SimpleInHandler());
//        enioServer.addClientHandler(new SimpleOutHandler());
        enioServer.connect();
    }
}
