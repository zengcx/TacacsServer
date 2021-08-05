package com.tacacs.TacacsPlusServer.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
/**
 * 认证请求包
 * @author zengc
 *
 */
public class AuthenStart extends Packet {

    final TACACS_PLUS.AUTHEN.ACTION action;
    final byte priv_lvl;
    final TACACS_PLUS.AUTHEN.TYPE type;
    final TACACS_PLUS.AUTHEN.SVC authen_service;
    final String username;
    final String port;
    final String rem_addr;
    final String dataString;/**data根据认证类型不同，该字段传输的数据差异详见rfc*/
    final byte[] dataBytes;
    public TACACS_PLUS.AUTHEN.ACTION getAction() {
		return action;
	}
	public byte getPriv_lvl() {
		return priv_lvl;
	}
	public TACACS_PLUS.AUTHEN.TYPE getType() {
		return type;
	}
	public TACACS_PLUS.AUTHEN.SVC getAuthen_service() {
		return authen_service;
	}
	public String getUsername() {
		return username;
	}
	public String getPort() {
		return port;
	}
	public String getRem_addr() {
		return rem_addr;
	}
	public String getDataString() {
		return dataString;
	}
	public byte[] getDataBytes() {
		return dataBytes;
	}

    /** 接收包 **/
    public AuthenStart(Header header, byte[] body) throws IOException{
        super(header);
        int overhead = 8;
        if (body.length < overhead) { throw new IOException("invalid packet or bad key"); }
        int chkLen = overhead + body[4] + body[5] + body[6] + body[7];
        if (chkLen != body.length) { throw new IOException("invalid packet or bad key"); }
        //
        action = TACACS_PLUS.AUTHEN.ACTION.forCode(body[0]);
        priv_lvl = body[1];
        type = TACACS_PLUS.AUTHEN.TYPE.forCode(body[2]);
        authen_service = TACACS_PLUS.AUTHEN.SVC.forCode(body[3]);
        int offset = 8;
        username = (body[4] > 0) ? new String(body, offset, body[4], StandardCharsets.UTF_8) : null;
        offset += body[4];
        port = (body[5] > 0) ? new String(body, offset, body[5], StandardCharsets.UTF_8) : null;
        offset += body[5];
        rem_addr = (body[6] > 0) ? new String(body, offset, body[6], StandardCharsets.UTF_8) : null;
        offset += body[6];
        if (body[7] > 0) {
            if(offset + body[7] == body.length) {
                dataBytes = new byte[body[7]];
                System.arraycopy(body, offset, dataBytes, 0, dataBytes.length);
                dataString = new String(dataBytes, StandardCharsets.UTF_8);
            } else {
                throw new IOException("Length of 'data' field (defined in 8th byte) does not match remaining number of packet's bytes.");
            }
        } else {
            dataBytes = null;
            dataString = null;
        }
    }
    /** 发送包,带字符串数据 **/
    public AuthenStart(Header header, TACACS_PLUS.AUTHEN.ACTION action, byte priv_lvl, TACACS_PLUS.AUTHEN.TYPE type, TACACS_PLUS.AUTHEN.SVC service, String username, String port, String rem_addr, String dataString) {
        super(header);
        this.action = action;
        this.priv_lvl = priv_lvl;
        this.type = type;
        this.authen_service = service;
        this.username = username;
        this.port = port;
        this.rem_addr = rem_addr;
        this.dataString = dataString;
        this.dataBytes = dataString == null ? null : dataString.getBytes(StandardCharsets.UTF_8);
    }
    /** 发送包,带二进制数据 **/
    public AuthenStart(Header header, TACACS_PLUS.AUTHEN.ACTION action, byte priv_lvl, TACACS_PLUS.AUTHEN.TYPE type, TACACS_PLUS.AUTHEN.SVC service, String username, String port, String rem_addr, byte[] dataBytes) {
        super(header);
        this.action = action;
        this.priv_lvl = priv_lvl;
        this.type = type;
        this.authen_service = service;
        this.username = username;
        this.port = port;
        this.rem_addr = rem_addr;
        this.dataBytes = dataBytes;
        this.dataString = dataBytes == null ? null : new String(dataBytes, StandardCharsets.UTF_8); ;
    }

    @Override
    public byte[] getWriteByte(byte[] key) throws IOException{

        byte[] userBytes = username==null?null:username.getBytes(StandardCharsets.UTF_8);
        byte[] portBytes = port==null?null:port.getBytes(StandardCharsets.UTF_8);
        byte[] remoBytes = rem_addr==null?null:rem_addr.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        body.write(action.code());
        body.write(priv_lvl);
        body.write(type.code());
        body.write(authen_service.code());
        body.write(userBytes==null?0:userBytes.length);
        body.write(portBytes==null?0:portBytes.length);
        body.write(remoBytes==null?0:remoBytes.length);
        body.write(dataBytes==null?0:dataBytes.length);
        if (userBytes!=null) { body.write(userBytes); }
        if (portBytes!=null) { body.write(portBytes); }
        if (remoBytes!=null) { body.write(remoBytes); }
        if (dataBytes!=null) { body.write(dataBytes); }
        return header.writePacket(body.toByteArray(), key);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+":"+header.toString()+"[action:"+action+" priv_lvl:'"+priv_lvl+"' type:'"+type+"' service:'"+authen_service+"' username:'"+username+"' port:'"+port+"' rem_addr:'"+rem_addr+"' data:'"+dataString+"']";
    }

}
