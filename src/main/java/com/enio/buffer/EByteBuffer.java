package com.enio.buffer;

import java.util.Arrays;

/**
 * The buffer used to read and write, which support auto increasing capacity when
 * the current capacity isn't enough to save more data
 * Created by yuan on 10/22/16.
 */
public class EByteBuffer {
    public static final int DEFAULT_SIZE=1024;
    private byte[] bytes;
    private int head;
    private int tail;
    private int capacity;


    public EByteBuffer(){
        this(DEFAULT_SIZE);
    }

    public EByteBuffer(int bufferSize){
        capacity=bufferSize;
        bytes=new byte[capacity];
        head=0;
        tail=0;
    }

    public int head(){
        return head;
    }
    public int tail(){
        return tail;
    }

    /**
     * return the actual length of readable bytes in the buffer
     * @return the real length of readable bytes in the buffer
     */
    public int curLength(){
        return tail-head;
    }


    /**
     * return the origin bytes of buffer
     * @return
     */
    public byte[] getOringinBytes() {
        return bytes;
    }



    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }


    public void add(byte b){
        ensureSpace(1);
        bytes[tail++]=b;
    }

    public void add(byte[] bs){
        add(bs,bs.length);
    }
    public void add(byte[] bs, int length) {
        add(bs,0,length);
    }

    /**
     * add bytes to the buffer
     * @param bs      the bytes need to be added
     * @param offset  the index of first byte to be added
     * @param length  the max number of bytes need to be added
     */
    public void add(byte[] bs, int offset, int length) {
        ensureSpace(length);
        for(int i=1;i<=length&&offset<bs.length;i++){
            bytes[tail++]=bs[offset++];
        }
    }


    /**
     * Making sure that the space of buffer is enough to hold the byte array which is going
     * to be added
     * @param length
     */
    private void ensureSpace(int length) {
        if(capacity-tail<length){
            // does the bytes need to compact
            if(head+(capacity-tail)>length){
                compact();
            }else{
                grow(tail+length);
            }
        }
    }

    /**
     * increase the capacity of bytes
     * @param minCapacity the minimum capacity of new bytes
     */
    private void grow(int minCapacity) {
        int newCapacicy=capacity+(capacity>>1);
        if(newCapacicy<minCapacity)
            newCapacicy=minCapacity;
        bytes= Arrays.copyOf(bytes,newCapacicy);
        capacity=newCapacicy;
    }

    /**
     * compact the bytes to save the space
     */
    public void compact(){
        if(head==0)
            return;
        int length=curLength();
        for(int i=0;i<length;i++)
            bytes[i++]=bytes[head++];
        head=0;
        tail=head+length;
    }



    public void setHead(int newHead) {
        this.head=newHead;
    }


    /**
     * create the readable byte array of the origin byte array of buffer
     * @return the new byte array which is copy of the readable bytes of buffer
     *          ,or null if there is not readable bytes in the buffer
     */
    public byte[] getReadableBytes() {
        if(curLength()<=0)
            return null;
        byte[] readableBytes=Arrays.copyOf(bytes,curLength());
        return readableBytes;
    }

    public void clear(){
        head=tail=0;
    }


    public byte read() {
        return bytes[head++];
    }

    /**
     * Read the bytes to the targetBytes and the maximun number of byte read will be less
     * than or equal to the length of target byte array.
     * @param targetBytes The target byte array
     * @return The number of byte which has been read
     */
    public int read(byte[] targetBytes){
        if(targetBytes==null)
            throw new NullPointerException();
        int maxReadableNum=curLength()<targetBytes.length?curLength():targetBytes.length;
        for(int i=0;i<maxReadableNum;i++){
            targetBytes[i]=read();
        }
        return maxReadableNum;
    }
}
