package maintenance.bin.src.Functions;

import java.text.DecimalFormat;
import java.util.Date;

public class Formatter {
	
	static DecimalFormat dFormat =  new DecimalFormat();

	public static String dateFormatter(){
		Date dt = new Date();
		
		//return String.format("%d-%02d-%02d",dt.getYear()+1900,dt.getMonth()+1,dt.getDate());
		return (dt.getYear()+1900)+"-"+formatInt(2,'0','+',(dt.getMonth()+1))+"-"+formatInt(2,'0','+',dt.getDate());
	}
	
	public static String dateFormatter2(){
		Date dt = new Date();
		
		String year = ""+(dt.getYear()+1900);
		
		//return String.format("%d-%02d-%02d",dt.getYear()+1900,dt.getMonth()+1,dt.getDate());
		return (formatInt(2,'0','+',(dt.getMonth()+1))+"/"+formatInt(2,'0','+',dt.getDate())+"/"+formatInt(2,'0','+',Integer.parseInt(year.substring((year.length()-2), year.length()))));
	}
	
	public static String dateFormatter1(){
		 Date dt = new Date();
 	    String mon_name = "";
 	    String day_week = "";
 	    
        if (dt.getDay() == 1) day_week = "Mon";
        if (dt.getDay() == 2) day_week = "Tue";
        if (dt.getDay() == 3) day_week = "Wed";
        if (dt.getDay() == 4) day_week = "Thr";
        if (dt.getDay() == 5) day_week = "Fri";
        if (dt.getDay() == 6) day_week = "Sat";
        if (dt.getDay() == 7) day_week = "Sun";
        if (dt.getMonth() == 0) mon_name = "JAN";
        if (dt.getMonth() == 1) mon_name = "FEB";
        if (dt.getMonth() == 2) mon_name = "MAR";
        if (dt.getMonth() == 3) mon_name = "APR";
        if (dt.getMonth() == 4) mon_name = "MAY";
        if (dt.getMonth() == 5) mon_name = "JUN";
        if (dt.getMonth() == 6) mon_name = "JUL";
        if (dt.getMonth() == 7) mon_name = "AUG";
        if (dt.getMonth() == 8) mon_name = "SEP";
        if (dt.getMonth() == 9) mon_name = "OCT";
        if (dt.getMonth() == 10) mon_name = "NOV";
        if (dt.getMonth() == 11) mon_name = "DEC";
        
        //String ddate = String.format("%s %s %d, %d",day_week,mon_name,dt.getDate(),dt.getYear() - 100 + 2000);
        String ddate = day_week+" "+mon_name+" "+dt.getDate()+", "+(dt.getYear() - 100 + 2000)+"";
        return ddate;
	}
	
	public static String timeFormatter1(){
		 Date dt = new Date();
	     String ddate = ""; 
	     return formatInt(2,'0','+',dt.getHours())+ ":" +formatInt(2,'0','+',dt.getMinutes());//+":"+formatInt(2,'0','+',dt.getSeconds());
	}
	
	public static String timeFormatter3(){
		 Date dt = new Date();
	     String ddate = ""; 
	     return formatInt(2,'0','+',dt.getHours())+ "" +formatInt(2,'0','+',dt.getMinutes());//+":"+formatInt(2,'0','+',dt.getSeconds());
	}
	
	public static String timeFormatter2(){
		 Date dt = new Date();
	     String ddate = ""; 
      if (dt.getHours() > 12) 
      	//ddate = String.format("%d:%02d:%02dpm",dt.getHours() - 12,dt.getMinutes(),dt.getSeconds());
      ddate = formatInt(2,' ','+',(dt.getHours()-12))+":"+formatInt(2,'0','+',dt.getMinutes())+":"+formatInt(2,'0','+',dt.getSeconds())+" PM";
      else if (dt.getHours() == 12) 
      	//ddate = String.format("%d:%02d:%02dpm",dt.getHours(),dt.getMinutes(),dt.getSeconds());
      ddate = formatInt(2,' ','+',dt.getHours())+":"+formatInt(2,'0','+',dt.getMinutes())+":"+formatInt(2,'0','+',dt.getSeconds())+" PM";
      else 
      	//ddate = String.format("%d:%02d:%02dam",dt.getHours(),dt.getMinutes(),dt.getSeconds());
      ddate = formatInt(2,' ','+',dt.getHours())+":"+formatInt(2,'0','+',dt.getMinutes())+":"+formatInt(2,'0','+',dt.getSeconds())+" AM";
      
	     
      return ddate;
	    // return formatInt(2,'0','+',dt.getHours())+ ":" +formatInt(2,'0','+',dt.getMinutes());//+":"+formatInt(2,'0','+',dt.getSeconds());
	}
	
	public static String timeFormatter(){
		Date dt = new Date();
		//return String.format("%02d:%02d:%02d",dt.getHours(),dt.getMinutes(),dt.getSeconds());
		return formatInt(2,'0','+',dt.getHours())+ ":" +formatInt(2,'0','+',dt.getMinutes())+":"+formatInt(2,'0','+',dt.getSeconds());
	}
	
	public static String float2Money(double total_){
		//String temp_totalp = String.format("%18.2f",total_);
		//Math.round(total_ * 100)/100f 
		String temp_totalp = dFormat.format(total_)+"";
		
		if (temp_totalp.indexOf('.') != (temp_totalp.length()-3)){
			if (temp_totalp.indexOf('.') == (temp_totalp.length()-2)){
				temp_totalp = temp_totalp+"0";
			}
			
		}
		String tp= "";
		if (temp_totalp.length() < 18){
			for (int i=0;i < (18 - temp_totalp.length());i++)
			tp +=' ';
		}
		temp_totalp = tp+temp_totalp;
		 
		 if ((total_ > 999.99) && (total_ < 1000000.00)){
		 temp_totalp = temp_totalp.substring(1,12) + "," + temp_totalp.substring(12,18);
		 }
		 else if (total_ > 999999.99){
			 temp_totalp = temp_totalp.substring(1,9) + "," + temp_totalp.substring(9,12) + "," + temp_totalp.substring(12,18);
		 }
		 temp_totalp.trim();
	return temp_totalp;	 
	}
	
	public static String float2Money1(double total_){
		//String temp_totalp = String.format("%18.2f",total_);
		//String temp_totalp = Math.round(total_ * 100)/100f +"";
		String temp_totalp = dFormat.format(total_)+"";
		
		if (temp_totalp.indexOf('.') != (temp_totalp.length()-3)){
			if (temp_totalp.indexOf('.') == (temp_totalp.length()-2)){
				temp_totalp = temp_totalp+"0";
			}
			
		}
		String tp= "";
		if (temp_totalp.length() < 18){
			for (int i=0;i < (18 - temp_totalp.length());i++)
			tp +=' ';
		}
		temp_totalp = tp+temp_totalp;
		 temp_totalp.trim();
	return temp_totalp;	 
	}
	
	
	/**
	 * @param args
	 */
	public static String trimString(int i,String s){
		s.trim();
		if (s.length() > i){
			s = s.substring(0,i);
			
		}
		return s;
	}
	/**
	 * @param args
	 */
	public static String formatString(int i, char fill,char left_right,String s){
		s.trim();
		if (s.length() > i){
			s = s.substring(0,i);
		}
		int diff = (i - s.length());
		String temp = "";
		if (left_right == '-'){
			temp = s;
			for (int n=0; n < diff;n++){
				temp += fill;
			}
			s = temp;
		} else {
			for (int n=0; n < diff;n++){
				temp += fill;
			}
			s = temp + s;
		}
		return s;
	}
	
	public static String formatInt(int i, char fill,char left_right,int s1){
		String s = s1+"";
		s.trim();
		if (s.length() > i){
			s = s.substring(0,i);
		}
		int diff = (i - s.length());
		String temp = "";
		if (left_right == '-'){
			temp = s;
			for (int n=0; n < diff;n++){
				temp += fill;
			}
			s = temp;
		} else {
			for (int n=0; n < diff;n++){
				temp += fill;
			}
			s = temp + s;
		}
		return s;
	}
	public static String formatInt(int i, char fill,char left_right,long s1){
		String s = s1+"";
		s.trim();
		if (s.length() > i){
			s = s.substring(0,i);
		}
		int diff = (i - s.length());
		String temp = "";
		if (left_right == '-'){
			temp = s;
			for (int n=0; n < diff;n++){
				temp += fill;
			}
			s = temp;
		} else {
			for (int n=0; n < diff;n++){
				temp += fill;
			}
			s = temp + s;
		}
		return s;
	}
	
	public static String formatInt(int i, char fill,char left_right,float s1){
		String s = s1+"";
		s.trim();
		if (s.length() > i){
			s = s.substring(0,i);
		}
		int diff = (i - s.length());
		String temp = "";
		if (left_right == '-'){
			temp = s;
			for (int n=0; n < diff;n++){
				temp += fill;
			}
			s = temp;
		} else {
			for (int n=0; n < diff;n++){
				temp += fill;
			}
			s = temp + s;
		}
		return s;
	}
	
	public static String formatInt(int i, char fill,char left_right,double s1){
		String s = s1+"";
		s.trim();
		if (s.length() > i){
			s = s.substring(0,i);
		}
		int diff = (i - s.length());
		String temp = "";
		if (left_right == '-'){
			temp = s;
			for (int n=0; n < diff;n++){
				temp += fill;
			}
			s = temp;
		} else {
			for (int n=0; n < diff;n++){
				temp += fill;
			}
			s = temp + s;
		}
		return s;
	}
	
	public static String reverse(String aData){
		String temp = null;
		if	(aData.length() == 1){
			return aData;
		}else{
			String lastchar = aData.substring(aData.length()-1, aData.length());
			String remaining = aData.substring(0, aData.length()-1);
			temp = lastchar + reverse(remaining);
			aData = temp;
		}
		return aData;
	}
	
	public static String reverseTrim(int trimlen, String trimchars) {
		trimchars = reverse(reverse(trimchars).substring(0, trimlen));
		return trimchars;
	}

	
}
