package com.enio;

import com.enio.Channel.Channel;
import com.enio.Channel.ServerChannel;
import com.enio.message.Message;
import com.enio.pipeline.handler.AcceptHandler;
import com.enio.pipeline.handler.InitialHandler;
import com.enio.pipeline.handler.websocket.WebSocketHandler;

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
//                channel.pipeline().addLast(new HttpRequestDecoder());
                channel.pipeline().addLast(new WebSocketHandler());
//                channel.pipeline().addLast(new HttpResponseEncoder());
//                channel.pipeline().addLast(new SimpleInHandler());
//                channel.pipeline().addLast(new SimpleOutHandler());
//                channel.pipeline().addLast(new InHandler() {
//                    @Override
//                    public boolean handleInputMessage(Channel channel, Message message) {
//                        List<String> strs= (List<String>) message.getData();
//                        for(Channel c:Channel.channels){
//                            if(c!=channel&&c instanceof ClientChannel){
//                                for(String s:strs){
//                                    if(s.equals("shutdown")){
//                                        channel.disconnect();
//                                        return false;
//                                    }
//                                    ((ClientChannel)c).send(s);
//                                }
//                            }
//                        }
//                        return true;
//                    }
//                });
            }
        });
        serverBootstraper.addHandler(new InitialHandler() {
            @Override
            public void onChannelInitialized(Channel channel, Message message) {
                channel.pipeline().addLast(new AcceptHandler() {
                    @Override
                    public boolean handlerAccept(Channel channel, Message message) {
                        System.out.println("新的连接!");
                        return true;
                    }
                });

            }
        });
//        enioServer.addClientHandler(new SimpleInHandler());
//        enioServer.addClientHandler(new SimpleOutHandler());
        Future<Channel> future=serverBootstraper.connect();
        ServerChannel channel=(ServerChannel)future.get();
//        channel.handleAcceptable();
    }
}
