package com.tacacs.TacacsPlusServer.utils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author zhangxin
 * 2018/8/28
 */
public class AuthenContinue extends Packet {

	final byte flags;
	final String user_msg;
	final String data;
    public byte getFlags() {
		return flags;
	}
	public String getUser_msg() {
		return user_msg;
	}
	public String getData() {
		return data;
	}


    /** 接收包**/
    public AuthenContinue(Header header, byte[] body) throws IOException{
        super(header);
        int overhead = 5; //flags(1)+userlength(2)+datalength(2)
        if(body.length < overhead){
            throw new IOException("invalid packet or bad key");
        }
        int chkLen = overhead + body[0] + body[1];
        if(chkLen != body.length){
            throw new IOException("invalid packet or bad key");
        }
        flags = body[4];
        int ulen = toInt(body[0],body[1]);
        user_msg = (ulen > 0) ? new String(body, 5, ulen, StandardCharsets.UTF_8) : null;
        int dlen = toInt(body[2], body[3]);
        data = (dlen > 0) ? new String(body, 5 + ulen, dlen, StandardCharsets.UTF_8) : null;
    }
    /** 发送包 **/
    public AuthenContinue(Header header, String user_msg, byte flags){
        super(header);
        this.user_msg = user_msg;
        this.data = null;
        this.flags = flags;
    }


    @Override
    public byte[] getWriteByte(byte[] key) throws IOException {
        byte[] umsgBytes = user_msg==null?null:user_msg.getBytes(StandardCharsets.UTF_8);
        byte[] dataBytes = data==null?null:data.getBytes(StandardCharsets.UTF_8);
        // Truncating to fit packet...  lengths are limited to 16 bits
        if (umsgBytes!=null && umsgBytes.length>FFFF) { umsgBytes = Arrays.copyOfRange(umsgBytes,0,FFFF); }
        if (dataBytes!=null && dataBytes.length>FFFF) { dataBytes = Arrays.copyOfRange(dataBytes,0,FFFF); }
        ByteArrayOutputStream body = new ByteArrayOutputStream(3 + (umsgBytes==null?0:umsgBytes.length) + (dataBytes==null?0:dataBytes.length));
        body.write(toBytes2(umsgBytes==null?0:umsgBytes.length));
        body.write(toBytes2(dataBytes==null?0:dataBytes.length));
        body.write(flags);
        if (umsgBytes!=null) { body.write(umsgBytes); }
        if (dataBytes!=null) { body.write(dataBytes); }
        byte[] bodyBytes = body.toByteArray();
        header.writePacket(bodyBytes, key);
        return header.writePacket(bodyBytes, key);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+":"+header.toString()+"[flags:"+flags+" user_msg:'"+user_msg+"' data:'"+data+"']";
    }
}
