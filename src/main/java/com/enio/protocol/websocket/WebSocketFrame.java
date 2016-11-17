package com.enio.protocol.websocket;

/**
 * Created by yuan on 11/17/16.
 */
public class WebSocketFrame implements WebSocketProtocol{
    private boolean fin;
    private Opcode opcode;
    private Object payload;
    private boolean transfermasked;

    @Override
    public void setFin(boolean fin) {
        this.fin=fin;
    }

    @Override
    public boolean isFin() {
        return fin;
    }

    @Override
    public void setTransferMasked(boolean transferMasked) {
        this.transfermasked=transferMasked;
    }

    @Override
    public boolean getTransferMasked() {
        return transfermasked;
    }

    @Override
    public void setOpcode(Opcode opcode) {
        this.opcode=opcode;
    }

    @Override
    public Opcode getOpcode() {
        return opcode;
    }

    @Override
    public void setPayload(Object payload) {
        this.payload=payload;
    }

    @Override
    public Object getPayload() {
        return payload;
    }
}
