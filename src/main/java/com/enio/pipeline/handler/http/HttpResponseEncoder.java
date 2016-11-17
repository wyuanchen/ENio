package com.enio.pipeline.handler.http;

import com.enio.Channel.Channel;
import com.enio.message.Message;
import com.enio.pipeline.handler.OutHandler;
import com.enio.protocol.http.HttpProtocol;
import com.enio.protocol.http.HttpResponse;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yuan on 11/16/16.
 */
public class HttpResponseEncoder implements OutHandler{
    @Override
    public boolean handleOutputMessage(Channel channel, Message message) {
        HttpResponse httpResponse= (HttpResponse) message.getData();
        byte[] bytes=encode(httpResponse);
        if(bytes!=null){
            message.setData(bytes);
            channel.pipeline().remove(this);
            return true;
        }
        return false;
    }


    /**
     * Encode the http response and return the byte array
     * @param httpResponse the http response need to be encoded
     * @return the byte array of decoded http response
     */
    private byte[] encode(HttpResponse httpResponse) {
        StringBuilder str=new StringBuilder();
        str.append(httpResponse.getVersion().getValue()+" "+httpResponse.getStatusCode().getValue()+ " "+HttpProtocol.HTTP_SWITING_PROTOCOLS+HttpProtocol.CRLF);
        Iterator<Map.Entry<String,String>>iterator=httpResponse.getHeader().entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry=iterator.next();
            str.append(entry.getKey()+": "+entry.getValue()+HttpProtocol.CRLF);
        }
        str.append(HttpProtocol.CRLF);
        try {
            return str.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
