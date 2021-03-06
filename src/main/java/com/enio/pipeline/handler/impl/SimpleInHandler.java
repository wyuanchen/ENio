package com.enio.pipeline.handler.impl;

import com.enio.Channel.Channel;
import com.enio.buffer.EByteBuffer;
import com.enio.message.Message;
import com.enio.pipeline.handler.InHandler;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yuan on 10/24/16.
 */
public class SimpleInHandler implements InHandler {

    private byte length;
    private EByteBuffer buffer;

    public SimpleInHandler(){
        length=0;
        buffer=new EByteBuffer(1024);
    }

    @Override
    public boolean handleInputMessage(Channel channel,Message message) {
        byte[] bytes= (byte[]) message.getData();
        List<String> strs=new LinkedList<String>();
        buffer.add(bytes);
        while(buffer.curLength()>0&&length<=buffer.curLength()){
            if(length==0)
                length=buffer.read();
            else{
                bytes=buffer.getOringinBytes();
                String str=new String(bytes,buffer.head(),length,Charset.forName("UTF-8"));
                buffer.setHead(buffer.head()+length);
                strs.add(str);
                length=0;
            }
        }
        buffer.compact();
        if(strs.size()<=0)
            return false;
        for(String s:strs){
            System.out.println(s);
        }
        message.setData(strs);
        return true;
    }


}
