package com.enio.pipeline.handler;

import com.enio.Channel.Channel;
import com.enio.message.Message;

import java.io.UnsupportedEncodingException;

/**
 * Created by yuan on 10/23/16.
 */
public class SimpleOutHandler implements OutHandler{


    @Override
    public boolean handleOutputMessage(Channel channel,Message message) {
        try {
            Object data=message.getData();
            byte[] bytes= data.toString().getBytes("UTF-8");
            byte[] processBytes=new byte[1+bytes.length];
            processBytes[0]=(byte) bytes.length;
            for(int i=1;i<processBytes.length;i++)
                processBytes[i]=bytes[i-1];
            message.setData(processBytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
