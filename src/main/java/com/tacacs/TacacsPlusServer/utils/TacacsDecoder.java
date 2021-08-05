package com.tacacs.TacacsPlusServer.utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class TacacsDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(TacacsEncoder.class);

    /**
     * 密钥
     */
    private byte[] key;

    /**
     * 
     * @param maxFrameLength 发送包最大长度
     * @param lengthFieldOffset 长度域偏移量，指的是长度域位于整个数据包字节数组中的下标
     * @param lengthFieldLength 长度域的自己的字节数长度
     * @param lengthAdjustment 长度域的偏移量矫正
     * @param initialBytesToStrip 丢弃的起始字节数。丢弃处于有效数据前面的字节数量。比如前面有4个节点的长度域，则它的值为4。
     * @param key Tacacs密钥
     */
    public TacacsDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                         int lengthAdjustment, int initialBytesToStrip, byte[] key) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
        this.key = key;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
    	//tacacs 包头包含12个字节的
        byte[] headerBytes = new byte[12];
        in.readBytes(headerBytes);
        Header header = new Header(headerBytes);
        byte[] body = new byte[header.bodyLength];
        in.readBytes(body); // read the body before potentially throwing any exceptions below, so that the input stream is left clean
        if (header.version==null) { throw new IOException("Received unknown packet header version code: "+((headerBytes[0]&0xf0)>>>4)+"."+(headerBytes[0]&0x0f)); }
        if (header.type==null) { throw new IOException("Received unknown packet header type code: "+headerBytes[1]); }
        byte[] bodyClear;
        try { bodyClear = header.toggleCipher(body, key); } catch (NoSuchAlgorithmException e) { throw new IOException(e.getMessage()); }
        switch (header.type)
        {
            case AUTHEN:
            	if(header.seqNum==1) {
            		AuthenStart authenStart = new AuthenStart(header, bodyClear);
            		LOG.debug("TacacsServer ask packet : " + authenStart.toString());
            		return authenStart;
            	}else if(header.version==TACACS_PLUS.PACKET.VERSION.v13_0){//继续报文(ASCII值认证类型)
            		AuthenContinue authenContinue = new AuthenContinue(header, bodyClear);
            		LOG.debug("TacacsServer ask packet : " + authenContinue.toString());
            		return authenContinue;
            	}
            case AUTHOR:
            	AuthorRequest authorRequest = new AuthorRequest(header, bodyClear);
                LOG.debug("TacacsServer ask packet : " + authorRequest.toString());
                return authorRequest;
		default: throw new IOException("Client-side packet header type not supported: " + header.type); // shouldn't happen
        }
    }
}
