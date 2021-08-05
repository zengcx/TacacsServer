package com.tacacs.TacacsPlusServer.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 会话缓存
 * @author zengcx
 *
 */
@Service
public class LoginSession {
	public static long timeOut;//会话超时时长
	private static Map<String, Authen> session = new HashMap<String, Authen>();
	public long getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(long timeOut) {
		LoginSession.timeOut = timeOut;
	}
	
	public static Map<String, Authen> getSession() {
		return session;
	}
    public LoginSession() {
    }
    
	static {
		ExecutorService cachedThreadPool = Executors.newSingleThreadExecutor();
		 cachedThreadPool.execute(new Runnable() {
			 protected   Logger log = LoggerFactory.getLogger(this.getClass());

		        @Override
		        public void run() {	 
		        	while(true) {
		        	try {
						Thread.sleep(10000);//每10秒轮询一次，清除过期缓存
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		        	Map<String, Authen> session = LoginSession.getSession();
		        	if(session.isEmpty()) {
		        		continue;
		        	}
		        	Iterator<Entry<String, Authen>> iter = session.entrySet().iterator();
		        	while(iter.hasNext()){
		        		Entry<String, Authen> entry=iter.next();
		        		Authen val = entry.getValue();
		        		String key = entry.getKey();
		        		if((System.currentTimeMillis()-val.getLastOperateTime())>timeOut*1000) {
		        			iter.remove();
		        			log.info("Timout. clear session:"+key);
		        		}
		        	}	
		        	}
		        
		        }
		    });
	}
 
}