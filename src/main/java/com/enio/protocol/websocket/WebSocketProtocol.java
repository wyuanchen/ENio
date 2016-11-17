package com.enio.protocol.websocket;

/**
 * Created by yuan on 11/17/16.
 */
public interface WebSocketProtocol {
    enum Opcode{
        Continuous,
        Text,
        Binary,
        Ping,
        Pong,
        Closing
    }
    void setFin(boolean fin);
    boolean isFin();
    void setTransferMasked(boolean transferMasked);
    boolean getTransferMasked();
    void setOpcode(Opcode opcode);
    Opcode getOpcode();
    void setPayload(Object payload);
    Object getPayload();

}
