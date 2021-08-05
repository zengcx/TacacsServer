package com.tacacs.TacacsPlusServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * 启动文件
 * @author zengcx
 *
 */
public class AppTacacsPlusServer {
	
	protected static  Logger log = LoggerFactory.getLogger(AppTacacsPlusServer.class);
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
    		new ClassPathXmlApplicationContext("classpath:spring.xml");
    }

}
