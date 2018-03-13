package com.xcgmym.jiajuwulian;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLTimeoutException;

import java.util.Timer;
import java.util.TimerTask;

public class ShuJuKu
{
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/jiajuwulian";

	private static final String USER = "jiajuwulian";
	private static final String PASS = "jiajuwulian";

	private Connection conn = null;
	private Timer timer = null;

	public ShuJuKu()
	{
		try
		{
			Class.forName(JDBC_DRIVER);
		}catch(ClassNotFoundException cnfe)
		{
			System.out.println("不能找到加载到数据库驱动类");
			System.out.println(cnfe.toString());
			return;
		}

		try
		{
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("链接到数据库");
		}catch(SQLException sqle)
		{
			System.out.println("连接数据库失败:"+sqle.toString());
			return;
		}

		/**
 		 * 心跳
 		 **/
		timer = new Timer("ShuJuKu", true);
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				
			}
		}, 10000, 3600*1000);
	}
}
