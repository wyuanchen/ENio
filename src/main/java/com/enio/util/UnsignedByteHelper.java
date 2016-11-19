package com.enio.util;

/**
 * Created by yuan on 11/19/16.
 */
public class UnsignedByteHelper {

    public static int getUnsignedByte(byte data){
        return data&0x0FF;
    }

    public static int getUnsignedByte(short data){
        return data&0x0FFFF;
    }

    public static long getUnsignedInt(int data){
        return data&0x0FFFFFFFF;
    }
}
