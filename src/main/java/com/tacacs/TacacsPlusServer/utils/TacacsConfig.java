package com.tacacs.TacacsPlusServer.utils;

import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacacs.TacacsPlusServer.services.db.DbUtil;

public class TacacsConfig {
	protected static  Logger log = LoggerFactory.getLogger(TacacsConfig.class);
	public static Properties getTacacsConfig(){
		/*** 加载配置文件 */
		String path = DbUtil.class.getClassLoader().getResource("").getPath() + "config\\TacacsConfig.properties";
		Properties pro = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(path);
			pro = new Properties();
			pro.load(in);
		} catch (Exception e) {
			log.error("数据库配置文件加载异常，异常原因：" + e.getStackTrace());
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		return pro;
	}
}
