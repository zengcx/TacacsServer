package com.tacacs.TacacsPlusServer.utils;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Packet {

	protected  Logger log = LoggerFactory.getLogger(this.getClass());
    public static final int FF = 0xFF;
    public static final int FFFF = 0xFFFF;
    public final Header header;

    public Packet(Header header){
        this.header = header;
    }

    boolean isEndOfSession(){
        return false;
    }

    //abstract void write(OutputStream out, byte[] key) throws IOException;

    public Header getHeader(){
        return header;
    }

    public static int toInt(byte a, byte b)
    {
        return ((a&FF)<<8) | (b&FF);
    }

    public static int toInt(byte a, byte b, byte c, byte d)
    {
        return ((a&FF)<<24) | (b&FF<<16) | ((c&FF)<<8) | (d&FF);
    }

    public static String toHex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder(bytes.length*2);
        for (byte b : bytes)
        {
            if ((b&FF)<0xf) sb.append("0");
            sb.append(Integer.toHexString(b&FF));
        }
        return sb.toString();
    }

    public static byte[] toBytes4(int i)
    {
        return new byte[] { (byte)((i>>>24)&FF), (byte)((i>>>16)&FF), (byte)((i>>>8)&FF), (byte)(i&FF) };
    }

    public static byte[] toBytes2(int i)
    {
        return new byte[] { (byte)((i>>>8)&FF), (byte)(i&FF) };
    }
    
    /**
     * 报文封装，转换成字节流
     * @param key 密钥
     * @return
     * @throws IOException
     */
    public abstract byte[] getWriteByte(byte[] key) throws IOException;
}
