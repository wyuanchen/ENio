package com.enio.pipeline.handler.websocket;

import com.enio.Channel.Channel;
import com.enio.buffer.EByteBuffer;
import com.enio.message.Message;
import com.enio.pipeline.handler.OutHandler;
import com.enio.protocol.websocket.WebSocketProtocol;

import java.util.Random;

/**
 * Created by yuan on 11/17/16.
 */
public class WebSocketProtocolEncoder implements OutHandler{
    private static Random random=new Random();

    @Override
    public boolean handleOutputMessage(Channel channel, Message message) {
        EByteBuffer byteBuffer=new EByteBuffer();
        WebSocketProtocol webSocketProtocol= (WebSocketProtocol) message.getData();
        encodeHeader(byteBuffer,webSocketProtocol);
        encodePayload(byteBuffer,webSocketProtocol);
        message.setData(byteBuffer.getReadableBytes());
        return true;
    }


    /**
     * Encode the header of Websocket Protocol
     * @param byteBuffer The bytebuffer which contains the encoded byte
     * @param webSocketProtocol The Websocket protocol need to be encoded
     */
    private void encodeHeader(EByteBuffer byteBuffer, WebSocketProtocol webSocketProtocol) {
        byte byteUnit=0;
        if(webSocketProtocol.isFin())
            byteUnit|=0x80;
        byte opCodeValue=webSocketProtocol.getOpcode().getValue();
        byteUnit|=opCodeValue;
        byteBuffer.add(byteUnit);
        byteUnit=0;
        long payloadLength=webSocketProtocol.getPayloadLength();
        byte[] payloadEncodedBytes=encodePayloadLength(payloadLength);
        if(webSocketProtocol.transferMasked()){
            payloadEncodedBytes[0]|=0x80;
        }
        byteBuffer.add(payloadEncodedBytes);
    }

    /**
     * Encode the payload length of Websocket Protocol
     * @param payloadLength
     * @return The encoded byte array of payload length
     */
    private byte[] encodePayloadLength(long payloadLength) {
        byte[] encodedBytes=null;
        if(payloadLength<=125){
            encodedBytes=new byte[1];
            encodedBytes[0]= (byte) payloadLength;
        }else if(payloadLength<=2E16-1){
            encodedBytes=new byte[3];
            encodedBytes[0]=126;
            encodedBytes[1]= (byte) (payloadLength>>8);
            encodedBytes[2]=(byte)(payloadLength);
        }else if(payloadLength<=2E32-1){
            encodedBytes=new byte[5];
            encodedBytes[0]=127;
            for(int i=1;i<=4;i++){
                encodedBytes[i]= (byte) (payloadLength>>(8*(4-i)));
            }
        }
        return encodedBytes;
    }

    /**
     * Encode the payload of Websocket Protocol or do nothing if the payload is null
     * @param byteBuffer The bytebuffer which contains the encoded byte
     * @param webSocketProtocol The Websocket protocol need to be encoded
     */
    private void encodePayload(EByteBuffer byteBuffer, WebSocketProtocol webSocketProtocol) {
        byte[] payload=webSocketProtocol.getPayload();
        if(payload==null)
            return;
        if(webSocketProtocol.transferMasked()){
            byte[] maskBytes=generateMaskByte();
            for(int i=0;i<payload.length;i++){
                byteBuffer.add((byte) (payload[i]^maskBytes[i%4]));
            }
        }else{
            byteBuffer.add(payload);
        }
    }

    /**
     * Generate the mask byte array
     * @return
     */
    private byte[] generateMaskByte() {
        byte[] maskBytes=new byte[4];
        random.nextBytes(maskBytes);
        return maskBytes;
    }

}
