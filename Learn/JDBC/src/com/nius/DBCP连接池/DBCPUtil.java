package com.nius.DBCP连接池;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

public class DBCPUtil {
	
	// 创建一个连接池对象
	// 使用dbcp提供的工厂方法
	private static DataSource ds = null;
	
	static {
		try {
			Properties properties = new Properties();
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("dbcp.properties");
			properties.load(in);
			
			// 创建连接池对象
			ds = BasicDataSourceFactory.createDataSource(properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		try {
			// 从连接池中获取链接对象
			return ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
