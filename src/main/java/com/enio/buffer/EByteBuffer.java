package com.enio.buffer;

import java.util.Arrays;

/**
 * 用来读写的缓冲区,能够实现自动扩充
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
     * 保证有剩余的空间,如果没有剩余的空间则
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
}