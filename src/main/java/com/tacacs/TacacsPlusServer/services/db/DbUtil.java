package com.tacacs.TacacsPlusServer.services.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.tacacs.TacacsPlusServer.utils.TacacsConfig;

/**
 * 基于durid的数据库连接池，为了使组件轻量，不引入其他orm框架
 * 
 * @author zengcx
 *
 */
public class DbUtil {
	public static void main(String[] args) throws Exception {
		executeQuery("select * from t_acc_master where name=? and cnname=?", "acc1","测试");
	}

	private static DruidDataSource druidDataSource;// Druid数据源，全局唯一（只创建一次）
	protected static Logger log = LoggerFactory.getLogger(DbUtil.class);
	private static final String DB_URL; // 数据库连接URL
	private static final String DB_USERNAME; // 数据库用户名
	private static final String DB_PASSWORD; // 数据库密码
	private static final String DB_DIRVER; // 数据库驱动
	static {
		/*** 加载配置文件 */
		Properties pro = TacacsConfig.getTacacsConfig();
		if(null==pro) {
			log.error("加载数据库配置异常，数据库配置为空");
		}
		DB_URL = pro.getProperty("db.url");
		DB_USERNAME = pro.getProperty("db.username");
		DB_PASSWORD = pro.getProperty("db.password");
		DB_DIRVER = pro.getProperty("db.driver");
		;
	}

	/**
	 * 用来执行insert、update、delete语句
	 *
	 * @param sql
	 * @param parameters
	 * @throws SQLException
	 */
	public static void update(String sql, String... parameters) throws SQLException {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connection = getDruidConnection();
			ps = connection.prepareStatement(sql);
			for (int index = 1; index <= parameters.length; index++) {
				ps.setString(index, parameters[index - 1]);
			}
			int count = ps.executeUpdate(sql);
			log.info(">>>>>>>>>>>>> update data {}", count);
		} finally {
			// 切记!!! 一定要释放资源
			closeResource(connection, ps, resultSet);
		}
	}

	/**
	 * 执行SQL查询
	 * 
	 * @param querySql
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> executeQuery(String querySql, String... parameters) throws Exception {
		List<Map<String, Object>> resultList = new ArrayList<>();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connection = getDruidConnection();
			ps = connection.prepareStatement(querySql);
			for (int index = 1; index <= parameters.length; index++) {
				ps.setString(index, parameters[index - 1]);
			}
			resultSet = ps.executeQuery();
			ResultSetMetaData metaData = resultSet.getMetaData();
			while (resultSet.next()) {
				int columnCount = metaData.getColumnCount();
				Map<String, Object> resultMap = new LinkedHashMap<>();
				for (int i = 1; i <= columnCount; i++) {
					String columnName = metaData.getColumnName(i);// 字段名称
					Object columnValue = resultSet.getObject(columnName);// 字段值
					resultMap.put(columnName, columnValue);
				}
				resultList.add(resultMap);
			}
			log.info(">>>>>>>>>>>>data:{}", resultList);
		} finally {
			// 切记!!! 一定要释放资源
			closeResource(connection, ps, resultSet);
		}
		return resultList;
	}

	/**
	 * 获取Druid数据源
	 *
	 * @return
	 * @throws SQLException
	 */
	private static DruidDataSource getDruidDataSource() throws SQLException {
		// 保证Druid数据源在多线程下只创建一次
		if (druidDataSource == null) {
			synchronized (DbUtil.class) {
				if (druidDataSource == null) {
					druidDataSource = createDruidDataSource();
					return druidDataSource;
				}
			}
		}
		log.debug(">>>>>>>>>>> 复用Druid数据源:url={}, username={}, password={}", druidDataSource.getUrl(),
				druidDataSource.getUsername(), druidDataSource.getPassword());
		return druidDataSource;
	}

	/**
	 * 创建Druid数据源
	 *
	 * @return
	 * @throws SQLException
	 */
	private static DruidDataSource createDruidDataSource() throws SQLException {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setUrl(DB_URL);
		druidDataSource.setUsername(DB_USERNAME);
		druidDataSource.setPassword(DB_PASSWORD);
		druidDataSource.setDriverClassName(DB_DIRVER);
		/*----下面的具体配置参数自己根据项目情况进行调整----*/
		druidDataSource.setMaxActive(20);
		druidDataSource.setInitialSize(1);
		druidDataSource.setMinIdle(1);
		druidDataSource.setMaxWait(60000);
		druidDataSource.setValidationQuery("select 1 from dual");
		druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
		druidDataSource.setMinEvictableIdleTimeMillis(300000);
		druidDataSource.setTestWhileIdle(true);
		druidDataSource.setTestOnBorrow(false);
		druidDataSource.setTestOnReturn(false);
		druidDataSource.setPoolPreparedStatements(true);
		druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
		druidDataSource.init();
		return druidDataSource;
	}

	/**
	 * 获取Druid连接
	 *
	 * @return
	 * @throws SQLException
	 */
	private static DruidPooledConnection getDruidConnection() throws SQLException {
		DruidDataSource druidDataSource = getDruidDataSource();
		DruidPooledConnection connection = druidDataSource.getConnection();
		return connection;
	}

	/**
	 * 释放资源
	 *
	 * @param connection
	 * @param statement
	 * @param resultSet
	 * @throws SQLException
	 */
	private static void closeResource(Connection connection, Statement statement, ResultSet resultSet)
			throws SQLException {
		// 注意资源释放顺序
		if (resultSet != null) {
			resultSet.close();
		}
		if (statement != null) {
			statement.close();
		}
		if (connection != null) {
			connection.close();
		}
	}
}