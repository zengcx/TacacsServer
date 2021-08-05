package com.tacacs.TacacsPlusServer.services;

import org.springframework.stereotype.Service;

import com.tacacs.TacacsPlusServer.cache.Authen;
import com.tacacs.TacacsPlusServer.services.db.entity.authenEntity;
import com.tacacs.TacacsPlusServer.utils.Argument;

/**
 * 业务处理接口类
 * @author zengc
 *
 */
@Service
public interface IServiceHandle {
	/**
	 * 认证校验
	 * @param auteAuthenEntity
	 * @return
	 */
	public authenEntity authenticate(Authen auteAuthenEntity);
	/**
	 * 授权
	 * @param authenSession
	 * @param args
	 * @return
	 */
	public boolean authorize(Authen authenSession, Argument[] args);

}
