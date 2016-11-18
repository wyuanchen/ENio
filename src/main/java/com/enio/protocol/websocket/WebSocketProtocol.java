package com.enio.protocol.websocket;

/**
 * Created by yuan on 11/17/16.
 */
public interface WebSocketProtocol {
    enum Opcode{
        Continuous((byte)0x0),
        Text((byte)0x1),
        Binary((byte)0x2),
        Ping((byte)0x9),
        Pong((byte)0xA),
        Closing((byte)0x8),
        Other((byte)0x3);

        Opcode(byte value){
            this.value=value;
        }
        private byte value;
        public byte getValue(){
            return value;
        }
    }

    void setFin(boolean fin);
    boolean isFin();
    void setTransferMasked(boolean transferMasked);
    boolean transferMasked();
    void setOpcode(Opcode opcode);
    void setOpcode(byte opcodeValue);
    Opcode getOpcode();
    void setPayload(byte[] payload);
    byte[] getPayload();
    long getPayloadLength();

}
