package maintenance.bin.src.Functions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import maintenance.bin.src.DBConn.DBConnect;
import maintenance.bin.src.core.mainform;

public class RecordCheck {
	
	public static boolean recordexist = false;
	private static Statement st = mainform.stat;
	private static ResultSet rs = null;
	
	
	public static boolean check(String table, String column, String key){
		String queryrecord = DBConnect.Select(table, "*", ""+column+" = '"+key+"'", null);
		try{
//			System.out.println(queryrecord);
			rs = st.executeQuery(queryrecord);
			while(rs.next()){
//				System.out.println(rs.getRow());
				if	(rs.getRow() >= 1)recordexist = true;
				else recordexist = false;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return recordexist;
	}
	
}