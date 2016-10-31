package com.enio;

import com.enio.pipeline.handler.SimpleInHandler;
import com.enio.pipeline.handler.SimpleOutHandler;

/**
 * Created by yuan on 10/21/16.
 */
public class ServerTest {
    public static void main(String[] args) throws InterruptedException {
        ServerBootstraper enioServer=new ServerBootstraper(12345,1,3);
        enioServer.addClientHandler(new SimpleInHandler());
        enioServer.addClientHandler(new SimpleOutHandler());
        enioServer.connect();
    }
}
