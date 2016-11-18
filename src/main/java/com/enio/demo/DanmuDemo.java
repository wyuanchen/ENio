package com.enio.demo;

import com.enio.Channel.Channel;
import com.enio.Channel.ClientChannel;
import com.enio.Channel.ServerChannel;
import com.enio.ServerBootstraper;
import com.enio.message.Message;
import com.enio.pipeline.handler.CloseHandler;
import com.enio.pipeline.handler.InHandler;
import com.enio.pipeline.handler.InitialHandler;
import com.enio.pipeline.handler.websocket.WebSocketHandler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by yuan on 11/18/16.
 */
public class DanmuDemo {

    private static List<ClientChannel> clientChannels= Collections.synchronizedList(new LinkedList<ClientChannel>());


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ServerBootstraper serverBootstraper=new ServerBootstraper(12345,1,3);
        serverBootstraper.addClientHandler(new InitialHandler() {
            @Override
            public void onChannelInitialized(Channel channel, Message message) {
                channel.pipeline().addLast(new WebSocketHandler());
                channel.pipeline().addLast(new InHandler() {
                    @Override
                    public boolean handleInputMessage(Channel channel, Message message) {
                        List<String> strs= (List<String>) message.getData();
                        for(String str:strs){
                            for(ClientChannel clientChannel:clientChannels){
                                clientChannel.send(str);
                            }
                        }
                        return true;
                    }
                });
                channel.pipeline().addLast(new CloseHandler() {
                    @Override
                    public boolean handleChannelClose(Channel channel, Message message) {
                        clientChannels.remove(channel);
                        return true;
                    }
                });
                clientChannels.add((ClientChannel) channel);
            }
        });

        Future<Channel> future=serverBootstraper.connect();
        ServerChannel serverChannel= (ServerChannel) future.get();
    }
}
