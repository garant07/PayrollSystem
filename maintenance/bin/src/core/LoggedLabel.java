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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;


public final class LoggedLabel extends JLabel implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static 	SimpleDateFormat 	dateFormat;
	private static 	Date             	date = new Date();
	
	private String 					 	user;
	private String 						currentDate;
	private String 						currentTime;
	
	public LoggedLabel() {
		this.setSize(100,80);	
	}
	
	
	
	public static String getFormattedTime() {
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		String sTime = dateFormat.format(date);
		return sTime;
	}
	
	public static String getFormattedDate() {
	    dateFormat = 
			new SimpleDateFormat("yyyy-M-d");
		String sDate = dateFormat.format(date);
		return sDate;
	}
	
	
	public void start() {
		this.run();
	}
	
	private void display() {
		try {

			//set name
			StringBuffer sBuffer = new StringBuffer();
			sBuffer.append(mainform.getFname());
			sBuffer.append(", ");
			sBuffer.append(mainform.getLname());
			setUser(sBuffer.toString());
	
			//set date
		    dateFormat = 
				new SimpleDateFormat("MMMMM d, yyyy");
			setCurrentDate(dateFormat.format(date));
			
			
			//set time
			DateFormat timeFormat = 
				DateFormat.getTimeInstance(DateFormat.DEFAULT);	
			setCurrentTime(timeFormat.format(date));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(true) {
			date = new Date();
			this.display();
			StringBuffer loggedData = new StringBuffer();
			loggedData.append("<HTML><TABLE>");
			loggedData.append("<TR><TD align='right'>User:</TD><TD><B>");
			loggedData.append(this.getUser());
			loggedData.append("</TD></TR>");
			loggedData.append("<TR><TD align='right'>Date:</TD><TD><B>");
			loggedData.append(this.getCurrentDate());
			loggedData.append("</TD></TR>");
			loggedData.append("<TR><TD align='right'>Time:</TD><TD><B>");
			loggedData.append(this.getCurrentTime());
			loggedData.append("</TD></TR>");
			
			this.setText(loggedData.toString());
			

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}

	private String getCurrentDate() {
		return currentDate;
	}

	private void setCurrentDate(String currentDate) throws Exception {
		if (currentDate == null) {
			throw new Exception("currentDate cannot be null");
		}
		this.currentDate = currentDate;
	}

	private String getCurrentTime() {
		return currentTime;
	}

	private void setCurrentTime(String currentTime) throws Exception {
		if (currentTime == null) {
			throw new Exception("currentTime cannot be null");
		}
		this.currentTime = currentTime;
	}

	private String getUser() {
		return user;
	}

	private void setUser(String user) throws Exception {
		if (user == null) {
			throw new Exception("user cannot be null");
		}
		this.user = user;
	}
	

}
