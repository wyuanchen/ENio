package com.enio;

import com.enio.Channel.Channel;
import com.enio.message.Message;
import com.enio.pipeline.handler.InitialHandler;
import com.enio.pipeline.handler.http.HttpRequestDecoder;
import org.junit.Before;

/**
 * Created by yuan on 1/11/17.
 */
public class JSONTest {


    @Before
    public void initServer(){

    }

    @org.junit.Test
    public void testServer() throws Exception{

    }

    public static void main(String[] args){
        ServerBootstraper serverBootstraper=new ServerBootstraper(12345,1,3);
        serverBootstraper.addClientHandler(new InitialHandler() {
            @Override
            public void onChannelInitialized(Channel channel, Message message) {
                channel.pipeline().addLast(new HttpRequestDecoder());
            }
        });
        serverBootstraper.connect();
    }

}
