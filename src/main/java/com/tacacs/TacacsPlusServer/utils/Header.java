package com.tacacs.TacacsPlusServer.utils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 报文头文件
 * @author zengc
 *
 */
public class Header {

    static final int FF = 0xFF;
    //会话中唯一的序列号 
    public final byte seqNum;
    //此字段包含各种位映射标志 
    final byte flags;
    //TACACS版本
    public final TACACS_PLUS.PACKET.VERSION version;
    //报文类型 
    public final TACACS_PLUS.PACKET.TYPE type;
    //此次TACACS+会话ID
    final byte[] sessionID;
    //客户端解码服务端响应的数据包时设置;程序创建时不需设置,在writePacket调用时根据需要计算主体的长度 
    public  int bodyLength;
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder(100);
		sb.append(getClass().getSimpleName()).append(":");
		sb.append("[");
		sb.append("seqNum:").append(seqNum);
		sb.append(" flags:'").append(flags).append("'");
		sb.append(" version:'").append(version).append("'");
		sb.append(" type:'").append(type).append("'");
		sb.append(" sessionID:'").append(byteArrayToInt(sessionID)).append("'");
		sb.append(" bodyLength:'").append(bodyLength).append("'");
		sb.append("]]");
		return sb.toString();
    }

    public Header(byte seqNum, byte flags, TACACS_PLUS.PACKET.VERSION version, TACACS_PLUS.PACKET.TYPE type, byte[] sessionID) {
        this.seqNum = seqNum;
        this.flags = flags;
        this.version = version;
        this.type = type;
        this.sessionID = sessionID;
        this.bodyLength = -1;
    }

    public Header(byte flags, TACACS_PLUS.PACKET.VERSION version, TACACS_PLUS.PACKET.TYPE type, byte[] sessionID) {
        this(
                (byte)1,
                flags,
                version,
                type,
                sessionID
        );
    }

    /**
     * 构造函数，接收字节流数据包时在内部使用 
     * @param bytes
     */
    public Header(byte[] bytes)
    {
        version = TACACS_PLUS.PACKET.VERSION.forCode(bytes[0]);
        type = TACACS_PLUS.PACKET.TYPE.forCode(bytes[1]);
        seqNum = bytes[2];
        flags = bytes[3];
        sessionID = Arrays.copyOfRange(bytes, 4, 8);
        bodyLength = toInt(bytes[8],bytes[9],bytes[10],bytes[11]);
    }
    static int toInt(byte a, byte b, byte c, byte d)
    {
        return ((a&FF)<<24) | (b&FF<<16) | ((c&FF)<<8) | (d&FF);
    }

    /**
     * 创建响应报文,支持单连接模式,seqNum每次加1
     * @param version
     * @return
     * @throws IOException
     */
    public Header next() throws IOException
    {
        if ((FF&seqNum)>=FF) { 
        	throw new IOException("Session's sequence numbers exhausted; try new session."); 
        	}
        return new Header((byte)((Packet.FF&seqNum)+1), flags, version, type, sessionID);
    }

    public boolean hasFlag(TACACS_PLUS.PACKET.FLAG flag)
    {
        return (flags & flag.code()) != 0;
    }

    public byte[] getSessionID(){
        return sessionID;
    }
    
	/**
	 * 获取int类型的SessionID
	 */
    public int getSessionIDByInt() {
    	return byteArrayToInt(sessionID);
    }
    
    /**
     * bytes数组转换int
     * @param bytes
     * @return
     */
	public static int byteArrayToInt(byte[] bytes) {
	    int value= 0;
	    for (int i = 0; i < bytes.length; i++) {
	        int shift= (bytes.length - 1 - i) * 8;
	        value +=(bytes[i] & 0x000000FF) << shift;//往高位游
	    }
	    return value;
	}
	
    /**
     * Toggles the encryption of the given packet body byte[] returning the result.
     * The calculation depends on the given key, and these header fields:
     * sessionID, version, and seqNum.
     * @param body
     * @param key
     * @throws NoSuchAlgorithmException if the MD5 message digest can't be found; shouldn't happen.
     * @return A new byte[] containing the ciphered/deciphered body; or just
     * the unchanged body itself if TAC_PLUS.PACKET.FLAG.UNENCRYPTED is set.
     */
    public byte[] toggleCipher(byte[] body, byte[] key) throws NoSuchAlgorithmException {
        if (hasFlag(TACACS_PLUS.PACKET.FLAG.UNENCRYPTED)) { return body; }
        MessageDigest md = MessageDigest.getInstance("MD5");
        int length = body.length;
        byte[] pad = new byte[length];
        md.update(sessionID); // reset() not necessary since each digest() resets
        md.update(key);
        md.update(version.code());
        md.update(seqNum);
        byte[] digest=md.digest(); // first digest applies only header info
        System.arraycopy(digest, 0, pad, 0, Math.min(digest.length,length));
        length -= digest.length;
        int pos = digest.length;
        while (length>0)
        {
            md.update(sessionID);
            md.update(key);
            md.update(version.code());
            md.update(seqNum);
            md.update(Arrays.copyOfRange(pad, pos-digest.length, pos)); // apply previous digest too
            digest=md.digest();//digest() 后md被初始化
            System.arraycopy(digest, 0, pad, pos, Math.min(digest.length,length));
            pos += digest.length;
            length -= digest.length;
        }
        byte[] toggled = new byte[body.length];
        for (int i=body.length-1; i>=0; i--)
        {
            toggled[i] = (byte)((body[i] & 0xff) ^ (pad[i] & 0xff));
        }
        return toggled;
    }

    /**
     * 发送前数据报文处理
     * @param body 报文数据
     * @param key 密钥
     * @return
     * @throws IOException
     */
    public byte[] writePacket(byte[] body, byte[] key) throws IOException {
        int len = body.length;
        bodyLength = len;//重新设置body长度
        ByteArrayOutputStream bout = new ByteArrayOutputStream(12+len);
        bout.write(version.code());
        bout.write(type.code());
        bout.write(seqNum);
        bout.write(flags);
        bout.write(sessionID);
        bout.write(Packet.toBytes4(bodyLength));//重新计算设置head加密数据长度
        try { 
        	bout.write(toggleCipher(body, key)); //body数据加密
        	}catch (NoSuchAlgorithmException e){ 
        	throw new IOException(e.getMessage()); 
        }
        return bout.toByteArray();
    }
}
