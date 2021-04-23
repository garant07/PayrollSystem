/* %W% %E% Raymond Tiongco
 * 
 * Copyright (c) 2006 Applied Ideas, Inc . All Rights Reserved. 
 * 
 * This software is the confidential and propietary information
 * of Applied Ideas Inc. You shall not disclose such 
 * Confidential Information and shall use it only in 
 * accordance with the terms of the licence agreement you
 * entered into Applied
 * 
 * APPLIED MAKES WARANTIES ONLY TO UPTO OUR SPECIFIED DATE
 * AGREEMENT. APPLIED SHALL NOT BE LIABLE FOR ANY DAMAGES 
 * SUFFERED BY LICENSEE AS A RESULT OF USING OR MODIFYING
 * AFTER OUR AGREEMENT EXPIRES
 * 
 */


package maintenance.bin.src.core;


import java.awt.Container;
import java.awt.Cursor;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import maintenance.bin.src.DBConn.DBConnect;

public final class LockHandler {

	//static Statement lockStat = mainform.stmt2;
	static Statement 	lockStat = mainform.stat;
	static String 		LOCK_STATUS_MESSAGE = "Trying to lock record.";
	static String 		LOCK_DIALOG_MESSAGE = "Someone is still editing that particular. Try again later.";


	public static void initializeLock(Container container) {
		//set lock property
		mainform.lblMessage.setText(LOCK_STATUS_MESSAGE);
		container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}
	
	public static void startLock(String SelectForUpdateQuery) throws SQLException {
		//parameter validation
		if ( SelectForUpdateQuery == null) {
			throw new SQLException("Select for update query cannot be null");
		}

		//lock implementation
		//System.out.println(DBConnect.beginTransaction());
		//System.out.println(SelectForUpdateQuery);
		lockStat.execute(DBConnect.beginTransaction());
		lockStat.executeQuery(SelectForUpdateQuery);

	}

	public static void removeLockAndUpdate(String e_sql) throws SQLException {
		//parameter validation
		if (e_sql == null) {
			throw new SQLException("e_sql cannot be null");
		}
		//System.out.println(e_sql);
		//System.out.println(DBConnect.commitTransaction());
		lockStat.execute(e_sql);
		lockStat.execute(DBConnect.commitTransaction());
	} 

	public static void LimitLockExceeded(Container container) throws IllegalStateException, SQLException {
		container.setCursor(Cursor.getDefaultCursor());
		mainform.lblMessage.setText("");
		JOptionPane.showMessageDialog(container,LOCK_DIALOG_MESSAGE,"Information",JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void finallyLimitLockExceeded(Container container) throws IllegalStateException, SQLException {
		container.setCursor(Cursor.getDefaultCursor());
		mainform.lblMessage.setText("");
	}	


	public static void removeLock() throws SQLException {
		//System.out.println(DBConnect.rollbackTransaction());
		lockStat.execute(DBConnect.rollbackTransaction());
	}

	public static void removeLockAndDelete(String del_sql) throws SQLException {
		//parameter validation
		if (del_sql == null) {
			throw new SQLException("del_sql cannot be null");
		}
		//unlock mode and delete
		lockStat.execute(del_sql);
		lockStat.execute(DBConnect.commitTransaction());
		mainform.lblMessage.setText("");
	}


}
