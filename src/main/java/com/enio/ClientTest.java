package com.enio;

import com.enio.Channel.Channel;
import com.enio.Channel.ClientChannel;
import com.enio.pipeline.handler.SimpleInHandler;
import com.enio.pipeline.handler.SimpleOutHandler;

import java.util.Scanner;
import java.util.concurrent.Future;

/**
 * Created by yuan on 10/23/16.
 */
public class ClientTest {
    public static void main(String[] args) throws Exception {
        ClientBootstraper clientBootstraper=new ClientBootstraper("127.0.0.1",12345);
        clientBootstraper.addHandler(new SimpleInHandler());
        clientBootstraper.addHandler(new SimpleOutHandler());
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
