package com.tacacs.TacacsPlusServer.server;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tacacs.TacacsPlusServer.cache.Authen;
import com.tacacs.TacacsPlusServer.cache.LoginSession;
import com.tacacs.TacacsPlusServer.services.IServiceHandle;
import com.tacacs.TacacsPlusServer.services.db.entity.authenEntity;
import com.tacacs.TacacsPlusServer.utils.Argument;
import com.tacacs.TacacsPlusServer.utils.AuthenContinue;
import com.tacacs.TacacsPlusServer.utils.AuthenReply;
import com.tacacs.TacacsPlusServer.utils.AuthenStart;
import com.tacacs.TacacsPlusServer.utils.AuthorReply;
import com.tacacs.TacacsPlusServer.utils.AuthorRequest;
import com.tacacs.TacacsPlusServer.utils.Header;
import com.tacacs.TacacsPlusServer.utils.Packet;
import com.tacacs.TacacsPlusServer.utils.TACACS_PLUS;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.StringUtil;


public class TacacsServerHandler extends ChannelInboundHandlerAdapter {
	protected  Logger log = LoggerFactory.getLogger(this.getClass());
	protected IServiceHandle serviceHandle;
	private Authen authen = new Authen();
	public TacacsServerHandler(IServiceHandle Handle) {
		this.serviceHandle = Handle;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {		
		Packet in = (Packet) msg;
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIp = insocket.getAddress().getHostAddress();
		Packet replyPacket = getReplyPacket(in,clientIp); 
		ctx.writeAndFlush(replyPacket);
	}

	/**
	 * 请求包业务处理
	 * @param in
	 * @param auteAuthenEntity
	 * @return
	 * @throws IOException
	 */

	private Packet getReplyPacket(Packet in,String clientIp) throws IOException {
		Header header = in.header;
		Header replyHeader = header.next();
		Packet replyPacket = null;
		switch (header.type)
        {
            case AUTHEN://认证业务
            	if(header.seqNum==0x01) {//start packet
            		AuthenStart authenStart = (AuthenStart)in;
            		String userName = authenStart.getUsername();
            		String stringData = authenStart.getDataString();
            		clientIp = StringUtil.isNullOrEmpty(authenStart.getRem_addr())?clientIp
            				:authenStart.getRem_addr();
            		authen.setNasIp(clientIp);
            		if(StringUtil.isNullOrEmpty(userName)) {//用户名为空，回复报文请求用户
            			log.debug("[authenStart]--sessionId:"+header.getSessionIDByInt()+"--username is null,ask for username");
            			replyPacket = new AuthenReply(replyHeader,TACACS_PLUS.AUTHEN.STATUS.GETUSER,TACACS_PLUS.REPLY.FLAG.NOECHO,"send username",null);
            		}else if(StringUtil.isNullOrEmpty(stringData)){
            			authen.setUserName(userName);
            			log.debug("[authenStart]--sessionId:"+header.getSessionIDByInt()+"--password is null,ask for password");
            			replyPacket = new AuthenReply(replyHeader,TACACS_PLUS.AUTHEN.STATUS.GETPASS,TACACS_PLUS.REPLY.FLAG.NOECHO,"send password",null);
            		}else {
            			authen.setUserName(userName);
            			authen.setPassword(stringData);
            		}
            	}else {//ascii continue packet
            		AuthenContinue authenContinue = (AuthenContinue)in;
            		if(StringUtil.isNullOrEmpty(authen.getUserName())) {
            			authen.setUserName(authenContinue.getUser_msg());
            			/**获取到用户，需要再次请求密码*/
            			replyPacket = new AuthenReply(replyHeader,TACACS_PLUS.AUTHEN.STATUS.GETPASS,TACACS_PLUS.REPLY.FLAG.NOECHO,"send password",null);
            		}else {
            			authen.setPassword(authenContinue.getUser_msg());
            		}
            	}
            	if(!StringUtil.isNullOrEmpty(authen.getPassword())&&
            			!StringUtil.isNullOrEmpty(authen.getUserName())) {
            		/***帐号和密码都有了开始验证*/
            		authenEntity entity = serviceHandle.authenticate(authen);
            		if(entity!=null) {//校验成功
            			log.info("Authentication:["+authen.getNasIp()+"]user："+authen.getUserName()+" login succeeded");
            			replyPacket = new AuthenReply(replyHeader, TACACS_PLUS.AUTHEN.STATUS.PASS, TACACS_PLUS.REPLY.FLAG.NOECHO, "welcome", null);
            			//添加会话缓存
            			authen.setLastOperateTime(System.currentTimeMillis());
            			authen.setPermissionId(entity.getPermissionId());
            			LoginSession.getSession().put(authen.getUserName()+"_"+authen.getNasIp(), authen);
            		}else {//登陆失败
            			log.info("Authentication:["+authen.getNasIp()+"]user："+authen.getUserName()+" login faild");
            			replyPacket = new AuthenReply(replyHeader, TACACS_PLUS.AUTHEN.STATUS.FAIL, TACACS_PLUS.REPLY.FLAG.NOECHO, "failed", null);
            		}
            	}
                break;
            case AUTHOR://授权业务
                AuthorRequest authorRequest = (AuthorRequest) in;
                String user = authorRequest.getUser();
                String remodeAddr = StringUtil.isNullOrEmpty(authorRequest.getRem_addr())?clientIp:authorRequest.getRem_addr();
                log.debug("Authorization["+remodeAddr+"]user:"+user);
                Authen authenSession = LoginSession.getSession().get(user+"_"+remodeAddr);
                //获取当前请求授权类容
                Argument[] args = authorRequest.getArguments();
                for(Argument arg:args) {
                	System.out.println("------>"+arg);
                }
                if(authenSession!=null) {
                	//更新缓存最后操作时间
                	authenSession.setLastOperateTime(System.currentTimeMillis());
                	boolean rst = serviceHandle.authorize(authenSession,args);
                	if(rst) {
                		replyPacket = new AuthorReply(replyHeader, TACACS_PLUS.AUTHOR.STATUS.PASS_ADD, "permission", "", null);
                	}else {
                		replyPacket = new AuthorReply(replyHeader, TACACS_PLUS.AUTHOR.STATUS.FAIL,
                    			" No permission", "", null);
                	}
                }else{//找不到缓存，授权失败
                	log.info(user+"：登陆授权失败，找不到登陆信息");
                	replyPacket = new AuthorReply(replyHeader, TACACS_PLUS.AUTHOR.STATUS.FAIL,
                			"Please login again", "", null);
                }
                break;
            case ACCT:
            	log.info("Account will be support later!");
            	break;
            default: 
            	throw new IOException("Packet's header type not be supported: " + in.header.type); 
        }
		return replyPacket;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(cause.getMessage().indexOf("远程主机强迫关闭")>-1||
				cause.getMessage().indexOf("Connection reset by peer")>-1) {
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
			String clientIp = insocket.getAddress().getHostAddress();
			log.debug("远程客户端[-"+clientIp+"-]连接关闭:"+cause.getMessage());
		}else {
			cause.printStackTrace();
		}
		ctx.close(); 
	}
}
