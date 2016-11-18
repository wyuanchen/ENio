package com.enio.pipeline.handler.websocket;

import com.enio.Channel.Channel;
import com.enio.buffer.EByteBuffer;
import com.enio.message.Message;
import com.enio.pipeline.handler.InHandler;
import com.enio.protocol.websocket.WebSocketFrame;
import com.enio.protocol.websocket.WebSocketProtocol;

import java.util.LinkedList;
import java.util.List;



/**
 * Created by yuan on 11/17/16.
 */
public class WebSocketProtocolDecoder implements InHandler{

    /**
     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-------+-+-------------+-------------------------------+
     |F|R|R|R| opcode|M| Payload len |    Extended payload length    |
     |I|S|S|S|  (4)  |A|     (7)     |             (16/63)           |
     |N|V|V|V|       |S|             |   (if payload len==126/127)   |
     | |1|2|3|       |K|             |                               |
     +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
     |     Extended payload length continued, if payload len == 127  |
     + - - - - - - - - - - - - - - - +-------------------------------+
     |                               |Masking-key, if MASK set to 1  |
     +-------------------------------+-------------------------------+
     | Masking-key (continued)       |          Payload Data         |
     +-------------------------------- - - - - - - - - - - - - - - - +
     :                     Payload Data continued ...                :
     + - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
     |                     Payload Data continued ...                |
     +---------------------------------------------------------------+

     */

    enum DecodeState{
        FlagDecoding,
        PayloadLengthDecoding,
        MaskDecoding,
        PayloadDecoding,
        Completed
    }
    enum PayloadLengthState{
        OneByte,
        TwoByte,
        FourByte
    }

    private DecodeState decodeState;
    private PayloadLengthState payloadLengthState;
    private EByteBuffer byteBuffer;
    private byte[] mask;
    private long payloadLength;
    private WebSocketProtocol webSocketProtocol;
    private List<WebSocketProtocol> webSocketProtocols;

    public WebSocketProtocolDecoder(){
        decodeState=DecodeState.FlagDecoding;
        payloadLengthState=PayloadLengthState.OneByte;
        byteBuffer=new EByteBuffer();
    }

    @Override
    public boolean handleInputMessage(Channel channel, Message message) {
        byte[] bytes= (byte[]) message.getData();
        byteBuffer.add(bytes);
        boolean isLoop=false;
        do{
            switch (decodeState){
                case FlagDecoding:
                    isLoop=decodeFlag();
                    break;
                case PayloadLengthDecoding:
                    isLoop=decodePayloadLength();
                    break;
                case MaskDecoding:
                    isLoop=decodeMask();
                    break;
                case PayloadDecoding:
                    isLoop=decodePayLoad();
                    break;
                case Completed:
                    isLoop=handleDecodeCompleted();
                    break;
            }
        }while(isLoop);
        if(webSocketProtocols!=null){
            message.setData(webSocketProtocols);
            webSocketProtocols=null;
            byteBuffer.compact();
            return true;
        }else{
            return false;
        }
    }



    /**
     * Decode the first byte at the Websocket frame which contains the Fin flag and Opcode flag
     */
    private boolean decodeFlag() {
        if(byteBuffer.curLength()<=0)
            return false;
        if(webSocketProtocol==null)
            webSocketProtocol=new WebSocketFrame();
        byte flag=byteBuffer.read();
        if((flag&0x80)>0){
            webSocketProtocol.setFin(true);
        }else{
            webSocketProtocol.setFin(false);
        }
        webSocketProtocol.setOpcode((byte) (flag&0x0F));
        decodeState=DecodeState.PayloadLengthDecoding;
        return true;
    }

    /**
     * Decode the payload length of Websocket frame
     */
    private boolean decodePayloadLength() {
        byte b=0;
        payloadLength=0;
        switch (payloadLengthState){
            case OneByte:
                if(byteBuffer.curLength()<1)
                    return false;
                b=byteBuffer.read();
                boolean isMasked=(b&0x80)>0;
                if(isMasked)
                    webSocketProtocol.setTransferMasked(isMasked);
                b= (byte) (b&0x7F);
                if(b<126){
                    this.payloadLength=b;
                    decodeState=DecodeState.MaskDecoding;
                }else if(b==126){
                    payloadLengthState=PayloadLengthState.TwoByte;
                }else{
                    payloadLengthState=PayloadLengthState.FourByte;
                }
                break;
            case TwoByte:
                if(byteBuffer.curLength()<2)
                    return false;
                for(int i=1;i>=0;i--){
                    b=byteBuffer.read();
                    payloadLength|=b;
                    payloadLength=payloadLength<<8;
                }
                payloadLength>>=8;
                decodeState=DecodeState.MaskDecoding;
                break;
            case FourByte:
                if(byteBuffer.curLength()<4)
                    return false;
                for(int i=3;i>=0;i--){
                    b=byteBuffer.read();
                    payloadLength|=b;
                    payloadLength=payloadLength<<8;
                }
                payloadLength>>=8;
                decodeState=DecodeState.MaskDecoding;
                break;
        }
        return true;

    }

    /**
     * Decode Mask if the mask of Websocket frame exist.
     * @return isLoop
     */
    private boolean decodeMask() {
        if(!webSocketProtocol.transferMasked()){
            decodeState=DecodeState.PayloadDecoding;
            return true;
        }
        if(byteBuffer.curLength()<4)
            return false;
        if(mask==null)
            mask=new byte[4];
        byteBuffer.read(mask);
        decodeState=DecodeState.PayloadDecoding;
        return true;
    }


    /**
     * Decode the payload of the Websocket frame
     * @return
     */
    private boolean decodePayLoad() {
        if(byteBuffer.curLength()<payloadLength)
            return false;
        byte[] payload=new byte[(int) payloadLength];
        byteBuffer.read(payload);
        if(webSocketProtocol.transferMasked()){
            maskPayload(payload);
        }
        webSocketProtocol.setPayload(payload);
        decodeState=DecodeState.Completed;
        return true;
    }

    private void maskPayload(byte[] payload) {
        for(int i=0;i<payload.length;i++){
            payload[i]^=mask[i%4];
        }
    }


    private boolean handleDecodeCompleted() {
        if(webSocketProtocols==null)
            webSocketProtocols=new LinkedList<WebSocketProtocol>();
        webSocketProtocols.add(webSocketProtocol);
        webSocketProtocol=null;
        decodeState=DecodeState.FlagDecoding;
        payloadLengthState=PayloadLengthState.OneByte;
        return true;
    }

}
