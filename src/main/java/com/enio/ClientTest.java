package com.enio;

import com.enio.Channel.Channel;
import com.enio.Channel.ClientChannel;
import com.enio.message.Message;
import com.enio.pipeline.handler.InitialHandler;
import com.enio.pipeline.handler.impl.SimpleInHandler;
import com.enio.pipeline.handler.impl.SimpleOutHandler;

import java.util.Scanner;
import java.util.concurrent.Future;

/**
 * Created by yuan on 10/23/16.
 */
public class ClientTest {
    public static void main(String[] args) throws Exception {
        ClientBootstraper clientBootstraper=new ClientBootstraper("127.0.0.1",12345);
        clientBootstraper.addHandler(new InitialHandler() {
            @Override
            public void onChannelInitialized(Channel channel, Message message) {
                channel.pipeline().addLast(new SimpleInHandler());
                channel.pipeline().addLast(new SimpleOutHandler());
            }
        });
        Future<Channel> future=clientBootstraper.connect();
        ClientChannel clientChannel=(ClientChannel) future.get();

//        enioClient.send("hello world!");

        Scanner scanner=new Scanner(System.in);
        while(true){
            String message=scanner.next();
//            System.out.println(message);
            clientChannel.send(message);
        }
//        Thread.sleep(1000);
//        for(int i=0;i<10;i++){
//            enioClient.send("hello world "+i+" time");
//        }
    }
}
