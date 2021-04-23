/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package maintenance.bin.src.DBConn;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.core.LoggedLabel;
import maintenance.bin.src.core.mainform;



/**
 * Manipulates Database Connection
 * 
 * @author Raymond A. Tiongco 1.0 22-11-2006
 */
public class DBConnect {
	public static Connection con;
	private static capture_ini ini = new capture_ini();
	public static boolean isConnected = false;

	private static String dbPassword;
	private static String dbUser;
	private static String dbServer;
	private static String dbPort;
	public static String dbName;
	public static String userid = "";

	public static final String LEFT_JOIN = "LEFT";
	public static final String RIGHT_JOIN = "RIGHT";
	public static final String INNER_JOIN = "INNER";
	public static final String OUTER_JOIN = "OUTER";
	public static final String RIGHT_OUTER_JOIN = "RIGHT OUTER";

	//no need to invoke everytime u need it since its static block
	static {
		try {
			Class.forName(ini.driver);
		} 
		catch (ClassNotFoundException e) 
		{
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("DBConnect", "", traceWriter.toString());
		}
	}

	//method for establishing jdbc connection
	public static Connection getConnection() throws SQLException {
		if (isConnected == false)  {
			try	{
				dbServer = mainform.ini.host;
				dbPort = mainform.ini.port;
				dbName = mainform.ini.dbase;

				dbUser = mainform.ini.user;
				dbPassword = mainform.ini.pass;
				String dbUrl = mainform.ini.url;
				con = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
				isConnected = true;
			}
			catch(SQLException e){
				/*StringWriter traceWriter = new StringWriter();
					PrintWriter printWriter = new PrintWriter(traceWriter, false);
					e.printStackTrace(printWriter);
					createINI.create("DBConnect", "Connection", traceWriter.toString());*/
			}
		}
		return con;		
	}

	//method for closing jdbc connection
	public static void terminateConnection() {
		con = null;
	}

	//method for status of jdbc connection 
	public static boolean isConnected() {
		return isConnected;
	}

	public boolean isEOF(ResultSet rst) {
		boolean is_eof = false;
		try{
			is_eof = rst.next();
		}catch(SQLException e)
		{
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("Mainform", "", traceWriter.toString());
			JOptionPane.showMessageDialog(null,"Error disconnecting from Database!","JDBC",JOptionPane.ERROR_MESSAGE);
		}//catch
		return (!is_eof);
	}//end isEOF


	/**
	 * @param first_query    e.g. DBConnect.Select(....)
	 * @param second_query	 e.g  DBConnect.Select(....)
	 * @return String SQL Union statement
	 */
	public static String Union(String first_query, String second_query) {
		String sql = first_query + " union " + second_query;
		return sql;	
	}

	/**
	 * @param first_query   e.g. DBConnect.Select(....)
	 * @param second_query  e.g. DBConnect.Select(....)
	 * @param third_query   e.g. DBConnect.Select(....)
	 * @return String SQL Union statement
	 */
	public static String Union(String first_query, String second_query, String third_query)	{
		String sql = first_query + " union " + second_query + " union " + third_query;
		return sql;
	}

	/**
	 * @param first_query   e.g. DBConnect.Select(....)
	 * @param second_query  e.g. DBConnect.Select(....)
	 * @param third_query   e.g. DBConnect.Select(....)
	 * @param fourth_query   e.g. DBConnect.Select(....)
	 * @return String SQL Union statement
	 */
	public static String Union(String first_query, String second_query, String third_query, String fourth_query) {
		String sql = first_query + " union " + second_query + " union " + third_query  + 
		" union " + fourth_query;
		return sql;
	}

	/**
	 * @param first_query   e.g. DBConnect.Select(....)
	 * @param second_query  e.g. DBConnect.Select(....)
	 * @param third_query   e.g. DBConnect.Select(....)
	 * @param fourth_query  e.g. DBConnect.Select(....)
	 * @param fifth_query   e.g. DBConnect.Select(....)
	 * @return String SQL Union statement
	 */
	public static String Union(String first_query, String second_query, String third_query, String fourth_query, String fifth_query) {
		String sql = first_query + " union " + second_query + " union " + third_query  + 
		" union " + fourth_query + " union " + fifth_query;
		return sql;
	}

	/**
	 * @param first_table    e.g. "tbl1"
	 * @param second_table   e.g. "tbl2"
	 * @param column_name    e.g. "col1,col2"
	 * @param join_type      e.g. DBConn.LEFT_JOIN or 
	 * @param reference_key  e.g. "tbl1.col1 = tbl2.col1"
	 * @param condition      e.g. "col1 = col2"
	 * @param order_by       e.g. "col1 ASC"
	 * @return String SQL Join Statement
	 */
	public static String Join(String first_table, String second_table, String column_name, String join_type, String reference_key, String condition, String order_by) {
		StringBuffer buffer = new StringBuffer(64);
		buffer.append("SELECT " + column_name + " ");
		buffer.append("FROM " + first_table + " ");
		buffer.append(join_type + " JOIN " + second_table + " ");
		buffer.append("ON " + reference_key + " ");

		if(null != condition) buffer.append("WHERE " + condition + " ");
		if(null != order_by) buffer.append("ORDER BY " + order_by);

		String sql = buffer.toString();

		return sql;
	}



	/**
	 * @param table_name  e.g. "table1,table2..."
	 * @param column_name e.g. "col1,col2..."
	 * @param condition   e.g. "col1 > 20 AND col1 <40..." or null
	 * @param group_by    e.g. "col1"
	 * @param order_by     e.g. "col1 desc..." or null
	 * 
	 * @return String SQL Select statement
	 */
	public static String Select(String table_name, String column_name, String condition, String orderBy) {
		StringBuffer buffer = new StringBuffer(64);
		buffer.append("SELECT " + column_name + " FROM ");
		buffer.append(table_name);
		if (null != condition) {
			buffer.append(" WHERE " + condition);
		}
		if (null != orderBy) {
			buffer.append(" ORDER BY " + orderBy);
		}
		String sql = buffer.toString();
		return sql;
	}	

	/**
	 * @param table_name  e.g. "table1,table2..."
	 * @param column_name e.g. "col1,col2..."
	 * @param condition   e.g. "col1 > 20 AND col1 <40..." or null
	 * 
	 * @return String SQL Select statement
	 */
	public static String SelectForUpdate(String table_name, String column_name, String condition) {
		StringBuffer buffer = new StringBuffer(64);
		buffer.append("SELECT " + column_name + " FROM ");
		buffer.append(table_name);
		if (null != condition) {
			buffer.append(" WHERE " + condition + " ");
		}
		buffer.append("FOR UPDATE");
		String sql = buffer.toString();
		return sql;
	}	


	/**
	 * @param table_name   e.g. "table1,table2..."
	 * @param column_name  e.g. "col1,col2..."
	 * @param column_value e.g. "'val1','val2'..."
	 * 
	 * @return String SQL Insert statement
	 */
	public static String Insert(String table_name, String column_name, String column_value) {
		String sql = "INSERT INTO " + table_name + " " + 
		"(" + column_name + ") " +
		"VALUES (" + column_value + ")";
		try {
			writelog(table_name,"Add record "+column_value.replace("'", ""));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(sql);

		return sql;
	}

	/**
	 * each parameter with 2 or more values can be passed 
	 * w/ comma delimeted(,) String
	 * 
	 * @param table_name   e.g. "table1,table2..."
	 * @param column_name  e.g. "col1='val1',col2='val2'..."
	 * @param condition    e.g. "col1 > 20 AND col1 <40..." or null
	 * 
	 * @return String SQL Update statement
	 */
	public static String Update(String table_name, String column_name_value, String condition) {	
		StringBuffer buffer = new StringBuffer();

		buffer.append("UPDATE " + table_name + " ");
		buffer.append("SET " + column_name_value + " ");
		if (null != condition) buffer.append("WHERE " + condition);
		String sql =  buffer.toString();
		try {
			writelog(table_name,"Edit record "+condition.replace("'", "")+" "+column_name_value.replace("'", ""));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(sql);

		return sql;

	}

	public static String Update(String table_name, String column_name_value) {	
		StringBuffer buffer = new StringBuffer();

		buffer.append("UPDATE " + table_name + " ");
		buffer.append("SET " + column_name_value + " ");
		String sql =  buffer.toString();
		try {
			writelog(table_name,"Edit record "+column_name_value.replace("'", ""));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//			System.out.println(sql);
		return sql;

	}


	/**
	 * @param table_name  e.g. "table1"
	 * @param condition   e.g  "col1 > 1" or null
	 * @return String sql delete statement
	 */
	public static String delete (String table_name, String condition) {
		StringBuffer buffer = new StringBuffer();

		buffer.append("DELETE FROM " + table_name);
		if (null != condition) buffer.append(" WHERE " + condition);

		String sql = buffer.toString();
		try {
			writelog(table_name,"Delete record "+condition.replace("'", ""));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(sql);
		return sql;
	}

	public static String delete (String table_name) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("DELETE FROM " + table_name);

		String sql = buffer.toString();
		try {
			writelog(table_name,"Delete record ");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(sql);
		return sql;
	}


	public static String dateFormat(String field) {
		return "date_format("+field+", '%M %d, %Y')";
	}

	public static String timeFormat(String field) {
		return "time_format("+field+"" +
		", '%h:%i:%s %p')";
	}

	public static String beginTransaction() {
		return "START TRANSACTION";	
	}

	public static String commitTransaction() {
		return "COMMIT";
	}

	public static String rollbackTransaction() {
		return "ROLLBACK";	
	}	

	/**
	 * @param table_name  e.g. "table1,table2..."
	 * @param column_name e.g. "col1,col2..."
	 * @param condition   e.g. "col1 > 20 AND col1 <40..." or null
	 * @param group_by    e.g. "col1"
	 * @param order_by     e.g. "col1 desc..." or null
	 * 
	 * @return String SQL Select statement
	 */
	public static String Select(String table_name, String column_name, String condition, String groupBy, String orderBy) {
		StringBuffer buffer = new StringBuffer(64);
		buffer.append("SELECT " + column_name + " FROM ");
		buffer.append(table_name);
		if (null != condition) {
			buffer.append(" WHERE " + condition);
		}
		if (null != groupBy) {
			buffer.append(" GROUP BY " + groupBy);
		}
		if (null != orderBy) {
			buffer.append(" ORDER BY " + orderBy);
		}
		String sql = buffer.toString();
		return sql;
	}	

	/**
	 * @return the dbName
	 */
	public static String getDbName() {
		return dbName;
	}

	/**
	 * @return the dbPort
	 */
	public static String getDbPort() {
		return dbPort;
	}

	/**
	 * @return the dbServer
	 */
	public static String getDbServer() {
		return dbServer;
	}

	/**
	 * @return the dbPassword
	 */
	public static String getDbPassword() {
		return dbPassword;
	}

	/**
	 * @return the dbUser
	 */
	public static String getDbUser() {
		return dbUser;
	}

	/**
	 * @return clean string
	 */
	public static String clean(String words) {
		String temp = "";
		if	(words != null){
			if	(words.contains("'") == true){
				if	(words.contains("\"") == true){
					temp = words.replaceAll("\"","\"");
				}
				if	(words.contains("\\") == true){
					temp = words.replaceAll("\\\\","\\\\\\\\");
					return temp.replaceAll("'","''");
				}
				else{
					return words.replaceAll("'","''");
				}
			}
			else if	(words.contains("\\\\") == true){
				return words.replaceAll("\\\\","\\\\\\\\");
			}
			else if	(words.contains("\\") == true){
				return words.replaceAll("\\\\","\\\\\\\\");
			}
			else {
				return words;
			}
		}
		return words;
	}

	/**
	 * @return sub select
	 */
	public static String subQuery(String table, String column) {
		String select = null;
		select = "(Select "+column+" from "+table+")";
		return select;
	}

	/**
	 * @return char_length
	 */
	public static String charLength(String column) {
		String length = null;
		length = "char_length("+column+")";
		return length;
	}

	/**
	 * @return substring
	 */
	public static String stringSub(String value, int from, int to) {
		String sub = null;
		sub = "substring("+value+","+from+","+to+")";
		return sub;
	}

	private static void writelog(String table,String msg) throws SQLException {
		try	{
			if	(userid.trim().length() == 0){
				userid = mainform.getUcode();
			}

			//LoggedLabel.getFormattedDate(),LoggedLabel.getFormattedTime(
			Statement stmt = DBConnect.getConnection().createStatement();
			String mssg = msg +" from table "+ table;
			String insertLog = "insert into syslog (date,time,msg,userid) values " +
			"('"+clean(LoggedLabel.getFormattedDate())+"', " +
			"'"+clean(LoggedLabel.getFormattedTime())+"', " +
			"'"+mssg+"', " +
			"'"+userid+"')";
			//System.out.println(insertLog);
			stmt.execute(insertLog);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}