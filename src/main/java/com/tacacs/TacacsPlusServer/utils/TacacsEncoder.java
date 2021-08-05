package com.tacacs.TacacsPlusServer.utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TacacsEncoder extends MessageToByteEncoder<Packet> {

    private static final Logger LOG = LoggerFactory.getLogger(TacacsEncoder.class);

    private Class<?> genericClass;

    private byte[] key;

    public TacacsEncoder(Class<?> genericClass, byte[] key) {
        this.genericClass = genericClass;
        this.key = key;
    }


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
        	//数据加密
            byte[] data = in.getWriteByte(key);
            LOG.debug("TacacsServer reply packet : " + in.toString());
            out.writeBytes(data);
        }
    }
}
