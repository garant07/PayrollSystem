package maintenance.bin.src.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import maintenance.bin.src.DBConn.DBConnect;
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.core.mainform;

public class WriteLog {

  static Statement stmtLog = null;
  static ResultSet rsLog = null;
  static String MySql;
  
  public static int errCount = 0;
  
  public static void WriteLog( String msg ) {
	Date now = new Date();
    SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
    //System.out.println("Date is " + dformat.format(now));	  
    SimpleDateFormat tformat = new SimpleDateFormat("HH:mm:ss");
    //System.out.println("Time is " + tformat.format(now));	  

	try {
        MySql = "INSERT INTO syslog (syslog.date,syslog.time,syslog.msg,syslog.userid) " +
         		"  VALUES ('" + dformat.format(now) + "'," +
         		          "'" + tformat.format(now) + "'," +
         		          "'" + msg.trim() + "'," +
         		          "'" + mainform.getUcode().trim() + "')";
    	//Class.forName("com.mysql.jdbc.Driver");
    	//Connection con = DriverManager.getConnection("jdbc:mysql://192.168.0.8:3306/javapos","root","applied");
        //stmtLog = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
        //                           ResultSet.CONCUR_READ_ONLY);
        stmtLog = DBConnect.getConnection().createStatement();
        stmtLog.executeUpdate(MySql);
    } catch (Exception err) {
	    err.printStackTrace();
    }
  }
  
  /**
   * REPORT ERROR ON TEXT FILE UPLOADING PROCEDURE
   * 
   * @param data - Data on the text file
   * @param recLen - Length of the record per line
   * @param counter - Record counter every line in a file
   * @param isError - Is error encountered
   */
  public static void reportError(String data, int recLen, int counter, boolean isError){
	  String path = "log/error";
	  boolean gotErr = false;
	  
	  System.out.println(data.length()+"**"+recLen);
	  if (data.length() != recLen) {
		  gotErr = true;
		  errCount++;
	  }
	  
	  if (gotErr) {
		  //System.out.println("Error in record=\""+data+"\" on line "+counter);
		  write2file("Found Error on \""+data+"\" : " +
		  			"Line No. "+counter,
		  			path);
	  }
	  
  }

  
  public static void write2file(String message, String dir){
	  try {
		  PrintWriter writer = null;
		  SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMdd");
		  String date = dFormat.format(new Date());
		  File file = new File(dir);
		  
		  if (!file.exists()){
			  file.mkdir();
		  }
		  
		  if (file.exists()){
			  writer = new PrintWriter(new FileWriter(dir+"/"+date+"plu.txt", true));
			  writer.println(message);
			  writer.close();
		  }
	  }
	  catch(Exception e){
		  e.printStackTrace();
	  }
  }

}
