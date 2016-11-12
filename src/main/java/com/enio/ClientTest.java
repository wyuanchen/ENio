package com.enio;

import com.enio.Channel.Channel;
import com.enio.Channel.ClientChannel;
import com.enio.message.Message;
import com.enio.pipeline.handler.InitialHandler;
import com.enio.pipeline.handler.impl.SimpleInHandler;
import com.enio.pipeline.handler.impl.SimpleOutHandler;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

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
        for(int i=0;i<5;i++){
            String message=scanner.next();
//            System.out.println(message);
            clientChannel.send(message);
        }
//        clientChannel.disconnect();
        clientBootstraper.shutdown();
        clientChannel.send("hahaa");

//        Thread.sleep(1000);
//        for(int i=0;i<10;i++){
//            enioClient.send("hello world "+i+" time");
//        }
    }

    public static void ma2in(String[] args){
        ScheduledExecutorService scheduledExecutorService= Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.shutdown();
        scheduledExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                int a=9;
            }
        });
    }
}
