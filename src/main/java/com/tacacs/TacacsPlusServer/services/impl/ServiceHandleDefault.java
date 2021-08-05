package com.tacacs.TacacsPlusServer.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tacacs.TacacsPlusServer.cache.Authen;
import com.tacacs.TacacsPlusServer.services.IServiceHandle;
import com.tacacs.TacacsPlusServer.services.db.AuthenticationMapper;
import com.tacacs.TacacsPlusServer.services.db.entity.authenEntity;
import com.tacacs.TacacsPlusServer.utils.Argument;
import com.tacacs.TacacsPlusServer.utils.security.enterprise.CryptUtil;

import io.netty.util.internal.StringUtil;
/**
 * 默认处理类
 * @author zengcx
 *
 */
@Service
public class ServiceHandleDefault implements IServiceHandle{
	protected  Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired AuthenticationMapper mapper;
	@Override
	public authenEntity authenticate(Authen auteAuthenEntity) {
		if(null == auteAuthenEntity) {
			return null;
		}
		String name = auteAuthenEntity.getUserName();
		if(StringUtil.isNullOrEmpty(name)) {
			return null;
		}
		String pass = auteAuthenEntity.getPassword();
		if(StringUtil.isNullOrEmpty(pass)) {
			return null;
		}
		String clientIp = auteAuthenEntity.getNasIp();
		if(StringUtil.isNullOrEmpty(clientIp)) {
			return null;
		}
		Map<String, Object> parm = new HashMap<String, Object>();
		parm.put("name", name);
		parm.put("pass", CryptUtil.encrypt(pass));
		parm.put("clientIp", clientIp);
		log.debug(parm.get("name")+"-----------"+parm.get("pass")+"------------"+parm.get("clientIp"));
		List<authenEntity> list = mapper.getUserInfo(parm);
		if(list!=null&list.size()>0) {
			return list.get(0);
		}else {
			return null;
		}
		
	}
	@Override
	public boolean authorize(Authen authenSession, Argument[] args) {
		String permissionId = authenSession.getPermissionId();
		//缓存
		for(Argument arg:args) {
			System.out.println("--->"+arg);
		}
		return true;
	}

}
