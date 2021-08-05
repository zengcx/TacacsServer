package com.tacacs.TacacsPlusServer.cache;

public class Authen {
	String sessionID;
	String userName;//用户名
	String password;//密码
	long lastOperateTime;//最后操作时间
	String nasIp; //认证源ip
	String permissionId;//权限id
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public long getLastOperateTime() {
		return lastOperateTime;
	}
	public void setLastOperateTime(long lastOperateTime) {
		this.lastOperateTime = lastOperateTime;
	}
	public String getNasIp() {
		return nasIp;
	}
	public void setNasIp(String nasIp) {
		this.nasIp = nasIp;
	}
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	public String getPermissionId() {
		return permissionId;
	}
	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
	}
	@Override
	public String toString() {
		
		return 	"sessionID:"+ sessionID+"userName:" + userName+
		"password:"+ password+
		"lastOperateTime:"+ lastOperateTime+
		"nasIp:"+ nasIp+
		"permissionId:" +permissionId;
	}
}
