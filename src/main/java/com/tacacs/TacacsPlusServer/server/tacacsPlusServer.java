package com.tacacs.TacacsPlusServer.server;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
@Service
public class tacacsPlusServer {
	protected Logger log = LoggerFactory.getLogger(this.getClass());
    public int port;
    NioEventLoopGroup group = null;
    @Autowired TacacsInitializer  tacacsInitializer;

    public tacacsPlusServer() {
    }
    public tacacsPlusServer(int port) {
        this.port = port;
    }

    public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() throws Exception {
		log.info("init!");
		this.start();
	}

	@PreDestroy
	public void destory() {
		log.info("destroy server resources");
		group.shutdownGracefully();
	}
	public void start() throws Exception {
		log.info("start-up tacacs server..");
		ServerBootstrap b = new ServerBootstrap();
        group = new NioEventLoopGroup();
        try {
        	b.group(group)
        	.channel(NioServerSocketChannel.class)
        	.childHandler(tacacsInitializer)
        	.option(ChannelOption.SO_BACKLOG, 4096)
        	.childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)//心跳存活检测
        	;
        	ChannelFuture f = b.bind(port).sync();
        	f.channel().closeFuture().sync();//当客户端关闭后，服务端关闭通道
        }finally {
			group.shutdownGracefully();
		}
	}
}

