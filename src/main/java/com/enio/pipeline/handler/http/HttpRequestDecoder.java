package com.enio.pipeline.handler.http;

import com.enio.Channel.Channel;
import com.enio.buffer.EByteBuffer;
import com.enio.message.Message;
import com.enio.pipeline.handler.InHandler;
import com.enio.protocol.http.HttpProtocol;
import com.enio.protocol.http.HttpRequest;

import java.io.UnsupportedEncodingException;

/**
 * Created by yuan on 11/15/16.
 */
public class HttpRequestDecoder implements InHandler{

    /**
     * The DecodeState means the phrase of the decoder,
     * The decoder will read and decode the header of http request firstly,
     * then read and decode the body of the http request finally
     */
    enum DecodeState{
        HeaderDecoding,
        BodyDecodng,
        Completed,
        Error
    }
    private DecodeState decodeState;
    private EByteBuffer byteBuffer;
    private HttpRequest httpRequest;
    private int bodyLength;

    public HttpRequestDecoder(){
        this.byteBuffer=new EByteBuffer(1024);
        decodeState=DecodeState.HeaderDecoding;
    }

    @Override
    public boolean handleInputMessage(Channel channel, Message message) {
        boolean flag=false;
        boolean isLoop=false;
        byte[] bytes= (byte[]) message.getData();
        byteBuffer.add(bytes);
        do {
            isLoop=false;
            switch (decodeState) {
                case HeaderDecoding:
                    isLoop=decodeHeader();
                    break;
                case BodyDecodng:
                    isLoop=decodeBody();
                    break;
                default:
                    break;
            }
            if (decodeState == DecodeState.Error) {
                httpRequest = null;
                byteBuffer.clear();
                decodeState = DecodeState.HeaderDecoding;
            } else if (decodeState == DecodeState.Completed) {
                flag = true;
                message.setData(httpRequest);
                System.out.println(httpRequest);
                httpRequest = null;
                byteBuffer.compact();
                if (byteBuffer.curLength() > 0)
                    isLoop=true;
                decodeState=DecodeState.HeaderDecoding;
                channel.pipeline().remove(this);
            }
        }while(isLoop);
        return flag;
    }

    /**
     * Decode the header of the http request
     */
    private boolean decodeHeader() {
        if(httpRequest==null){
            httpRequest=new HttpRequest();
        }
        String line=readLine(byteBuffer);
        while(line!=null&&line.length()>0){
            if(httpRequest.getMethod()==null){
                String[] firstLineTokens=line.split(" ",3);
                if(firstLineTokens.length<3){
                    decodeState=DecodeState.Error;
                    return false;
                }
                httpRequest.setMethod(firstLineTokens[0]);
                httpRequest.setVersion(firstLineTokens[2]);
            }else{
                String[] tuple=line.split(":",2);
                if(tuple.length!=2){
                    decodeState=DecodeState.Error;
                    return false;
                }
                httpRequest.addHeader(tuple[0].trim(),tuple[1].trim());
            }
            line=readLine(byteBuffer);
        }
        if(line!=null){
            String lengthStr=httpRequest.getHeader(HttpProtocol.HTTP_HEADER_CONTENT_LENGTH);
            String contentType=httpRequest.getHeader(HttpProtocol.HTTP_HEADER_CONTENT_TYPE);
            if(lengthStr!=null){
                bodyLength=Integer.valueOf(lengthStr);
                decodeState=DecodeState.BodyDecodng;
                return true;
            }else {
                decodeState=DecodeState.Completed;
                return true;
            }
        }
        return false;
    }

    private String readLine(EByteBuffer buffer) {
        if(buffer.curLength()<=0)
            return null;
        byte[] bytes=buffer.getOringinBytes();
        int start=buffer.head();
        int end=buffer.tail();
        int cur=start;
        while(cur<end){
            if(cur>start&&bytes[cur]=='\n'&&bytes[cur-1]=='\r'){
                try {
                    buffer.setHead(cur+1);
                    return new String(bytes,start,cur-1-start,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            cur++;
        }
        return null;
    }

    /**
     * Decode the payload of the http request
     */
    private boolean decodeBody() {
        if(byteBuffer.curLength()<bodyLength)
            return false;
        byte[] body=new byte[bodyLength];
        byteBuffer.read(body);
        String contentType=httpRequest.getHeader(HttpProtocol.HTTP_HEADER_CONTENT_TYPE);
        if(contentType.equals(HttpProtocol.CONTENT_TYPE_APPLICATION_JSON)){
            String jsonStr=new String(body);
            System.out.println(jsonStr);
            decodeState=DecodeState.Completed;
            return true;
        }
        return false;
    }


}
