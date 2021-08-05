package com.tacacs.TacacsPlusServer.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tacacs.TacacsPlusServer.services.impl.ServiceHandleDefault;
import com.tacacs.TacacsPlusServer.utils.Packet;
import com.tacacs.TacacsPlusServer.utils.TacacsDecoder;
import com.tacacs.TacacsPlusServer.utils.TacacsEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;

/**
 * @author zengcx
 *
 */
@Service
public class TacacsInitializer extends ChannelInitializer<SocketChannel> {
	/** 共享密钥暂未处理，后续如果实现项目上提到的根据终端确定不同的密钥，不能直接在此处获取，会有线程安全问题*/
    private byte[] key = "venus2017".getBytes(CharsetUtil.UTF_8);
    @Autowired ServiceHandleDefault serviceHandle;
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
        cp.addLast("encoder",new TacacsEncoder(Packet.class, key));
        cp.addLast("decoder",new TacacsDecoder(65536, 0, 4, 0, 0, key));
        cp.addLast("handler",new TacacsServerHandler(serviceHandle));
    }
}
