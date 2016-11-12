package com.enio.Channel;

import com.enio.eventLoop.event.Event;
import com.enio.message.Message;
import com.enio.pipeline.handler.Handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * Created by yuan on 10/8/16.
 */
public class ClientChannel extends Channel {

    private SocketChannel socketChannel;

    public ClientChannel(SocketChannel channel, int initSelectionKeyOP, List<Handler> handlers) {
        super(channel,initSelectionKeyOP,handlers);
        this.socketChannel=channel;
    }
    public ClientChannel(SocketChannel channel,int initSelectionKeyOP) {
        this(channel,initSelectionKeyOP,null);
    }



    @Override
    public void handleReadable() throws IOException {
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        byteBuffer.clear();
        int readBytes=0;
        while((readBytes=socketChannel.read(byteBuffer))>0){
            System.out.println("receive message!");
            byteBuffer.flip();
            byte[] reads=byteBuffer.array();
            readBuffer.add(reads,byteBuffer.remaining());
            byteBuffer.clear();
        }
        if(readBytes==-1){
            System.out.println("connection fail!");
            key.cancel();
            channel.close();
            return;
        }
        byte[] bytes=readBuffer.getReadableBytes();
        readBuffer.clear();
        if(bytes!=null){
            pipeLine.handleInputMessage(this,new Message(bytes));
        }
//        String s=new String(readBuffer.getOringinBytes(),readBuffer.head(),readBuffer.curLength(), Charset.forName("UTF-8"));
//        System.out.println("this time receive:  "+s);

    }

    @Override
    public void handleConnectable() {
        eventLoop.submit(new Event<Object>() {
            @Override
            public Object call() throws Exception {
                System.out.println("客户端尝试请求连接！");
                SocketChannel sc=(SocketChannel)channel;
                try {
                    if(sc.finishConnect()){
                        key.interestOps(key.interestOps()&(~SelectionKey.OP_CONNECT));
                        key.interestOps(key.interestOps()|SelectionKey.OP_READ);
                        System.out.println("The client connect to server successfully!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        super.handleConnectable();
    }

    @Override
    public void handleWritable() throws IOException {
        ByteBuffer byteBuffer=ByteBuffer.wrap(writeBuffer.getOringinBytes(),
                writeBuffer.head(),
                writeBuffer.curLength());
        int writeLength=0;
        while((writeLength=socketChannel.write(byteBuffer))>0){
            writeBuffer.setHead(writeBuffer.head()+writeLength);
        }
        if(writeBuffer.curLength()==0){
            key.interestOps(key.interestOps()&(~SelectionKey.OP_WRITE));
        }
//        System.out.println("has write bytes: "+writeBuffer.head());
    }

    /**
     * send message outside, the data should be temp object because the channel
     * can't promise that it will process the data right now, so the modification of data will be
     * invoked in the future and we need to make sure that the data must not be modified in other
     * place after invoke this method
     * @param data the object need to be sent to outside,
     */
    public void send(Object data) {
        eventLoop.submit(new Event<Object>() {
            @Override
            public Object call() throws Exception {
                Message message=new Message(data);
                boolean isHandleSuccessfully=pipeLine.handleOutputMessage(ClientChannel.this,message);
                if(!isHandleSuccessfully)
                    return null;
                byte[] processedBytes= (byte[])message.getData();
                writeBuffer.add(processedBytes);
                key.interestOps(key.interestOps()|SelectionKey.OP_WRITE);
                return null;
            }
        });
    }

}
