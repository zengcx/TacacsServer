package com.tacacs.TacacsPlusServer.utils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 授权请求包
 * @author zengcx
 *
 */
public class AuthorRequest extends Packet
{

	TACACS_PLUS.AUTHEN.METH authen_method;
	byte priv_lvl;
	TACACS_PLUS.AUTHEN.TYPE authen_type;
	TACACS_PLUS.AUTHEN.SVC authen_service;
	String user;
	String port;
	String rem_addr;
	Argument[] arguments;
	
	public TACACS_PLUS.AUTHEN.METH getAuthen_method() {
		return authen_method;
	}


	public void setAuthen_method(TACACS_PLUS.AUTHEN.METH authen_method) {
		this.authen_method = authen_method;
	}


	public byte getPriv_lvl() {
		return priv_lvl;
	}


	public void setPriv_lvl(byte priv_lvl) {
		this.priv_lvl = priv_lvl;
	}


	public TACACS_PLUS.AUTHEN.TYPE getAuthen_type() {
		return authen_type;
	}


	public void setAuthen_type(TACACS_PLUS.AUTHEN.TYPE authen_type) {
		this.authen_type = authen_type;
	}


	public TACACS_PLUS.AUTHEN.SVC getAuthen_service() {
		return authen_service;
	}


	public void setAuthen_service(TACACS_PLUS.AUTHEN.SVC authen_service) {
		this.authen_service = authen_service;
	}


	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public String getPort() {
		return port;
	}


	public void setPort(String port) {
		this.port = port;
	}


	public String getRem_addr() {
		return rem_addr;
	}


	public void setRem_addr(String rem_addr) {
		this.rem_addr = rem_addr;
	}


	public Argument[] getArguments() {
		return arguments;
	}


	public void setArguments(Argument[] arguments) {
		this.arguments = arguments;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append(":\n").append(header.toString());
		sb.append("\nbody:[");
		sb.append("\nauthen_method=").append(authen_method);
		sb.append("\npriv_lvl=").append(priv_lvl);
		sb.append("\nauthen_type=").append(authen_type);
		sb.append("\nauthen_service=").append(authen_service);
		sb.append("\nuser=").append(user);
		sb.append("\nport=").append(port);
		sb.append("\nrem_addr=").append(rem_addr);
		sb.append("\narguments=[").append(Arrays.toString(arguments));
		sb.append("]]");
		return sb.toString();
	}
	
	
	/**
	 * 构造函数，授权请求包
	 */
	AuthorRequest(Header header, byte[] body) throws IOException {
		super(header);
		// Verify 16进制
		int overhead = 8;//基础默认属性
		if (body.length<overhead) { throw new IOException("Corrupt packet or bad key"); }
		int chkLen = overhead + body[4] + body[5] + body[6] + body[7];
		int arg_cnt = body[7];//动态扩展属性数量
		if(arg_cnt>0) {//计算额外参数长度
			for(int index=0;index<arg_cnt;index++) {
				chkLen =chkLen+body[overhead+index];
			}
		}
		if (chkLen != body.length) { throw new IOException("Corrupt packet or bad key"); }
		authen_method = TACACS_PLUS.AUTHEN.METH.forCode(body[0]);
		priv_lvl = body[1];
		authen_type = TACACS_PLUS.AUTHEN.TYPE.forCode(body[2]);
		authen_service = TACACS_PLUS.AUTHEN.SVC.forCode(body[3]);
		int i, offset = overhead + arg_cnt;
		i=body[4];     user = (i>0) ? new String(body, offset, i, StandardCharsets.UTF_8) : null; offset+=i;
		i=body[5];     port = (i>0) ? new String(body, offset, i, StandardCharsets.UTF_8) : null; offset+=i;
		i=body[6]; rem_addr = (i>0) ? new String(body, offset, i, StandardCharsets.UTF_8) : null; offset+=i;
		arguments = new Argument[arg_cnt];
		for (int a=0; a<arg_cnt; a++) 
		{ 
			String arg = new String(body, offset, body[overhead+a], StandardCharsets.UTF_8);
			arguments[a] = new Argument(arg); 
			offset+=body[overhead+a]; 
		}
	}

	
	/**
	 * Constructor for when building outgoing packets.
	 */
	public AuthorRequest(
		Header header, 
		TACACS_PLUS.AUTHEN.METH authen_method,
		byte priv_lvl,
		TACACS_PLUS.AUTHEN.TYPE authen_type,
		TACACS_PLUS.AUTHEN.SVC authen_service,
		String user,
		String port,
		String rem_addr,
		Argument[] arguments) {
		super(header);
		this.authen_method = authen_method;
		this.priv_lvl = priv_lvl;
		this.authen_type = authen_type;
		this.authen_service = authen_service;
		this.user = user;
		this.port = port;
		this.rem_addr = rem_addr;
		this.arguments = arguments;
	}


	/**
	 * Writes the whole packet.
	 * @param key The byte[] secret key shared between the client and server.
	 * @throws IOException if there is a problem writing to the given OutputStream.
	 */
	@Override
	public byte[] getWriteByte(byte[] key) throws IOException {
		byte[] userBytes = user.getBytes(StandardCharsets.UTF_8); 
		byte[] portBytes = port.getBytes(StandardCharsets.UTF_8);
		byte[] remaBytes = rem_addr.getBytes(StandardCharsets.UTF_8);
		// Truncating to fit packet...  lengths are limited to a byte
		if (userBytes!=null && userBytes.length>FF) { userBytes = Arrays.copyOfRange(userBytes,0,FF); }
		if (portBytes!=null && portBytes.length>FF) { portBytes = Arrays.copyOfRange(portBytes,0,FF); }
		if (remaBytes!=null && remaBytes.length>FF) { remaBytes = Arrays.copyOfRange(remaBytes,0,FF); }
		// Truncating the number of arguments, and the length of the byte[] representations... limited to a byte
		byte[][] argsBytes = new byte[Math.min(FF,arguments.length)][];
		for (int i=0; i<argsBytes.length; i++) 
		{
			argsBytes[i] = arguments[i].toString().getBytes(StandardCharsets.UTF_8);
			if (argsBytes[i].length>FF) { argsBytes[i] = Arrays.copyOfRange(argsBytes[i],0,FF); }
		}
		//
		ByteArrayOutputStream body = new ByteArrayOutputStream();
		body.write(authen_method.code());
		body.write(priv_lvl);
		body.write(authen_type.code());
		body.write(authen_service.code());
		body.write(userBytes.length);
		body.write(portBytes.length);
		body.write(remaBytes.length);
		body.write(argsBytes.length);
		for (byte[] aBytes : argsBytes) { body.write(aBytes.length); }
		body.write(userBytes);
		body.write(portBytes);
		body.write(remaBytes);
		for (byte[] aBytes : argsBytes) { body.write(aBytes); }
		return header.writePacket(body.toByteArray(), key);
	}
	
}
