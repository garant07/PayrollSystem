package maintenance.bin.src.Functions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author  Angelo Mercado
 * @purpose Create text file for error log
 */
public class createINI{
	
	static boolean okna;
	static File file;
	
	/**
	 * @param Filename - filename
	 * @param rowTitle - eg. rowTitle = 
	 * @param message - eg.  rowTitle = message
	 */
	public static void create(String Filename,String rowTitle,String message){
		try	{
//			System.out.println(Filename+"-"+rowTitle+"-"+message);
			SimpleDateFormat dFormat = new SimpleDateFormat("Mdyyyyhhmmss");
			String date = dFormat.format(new Date());
			String fileOUT = "Error/"+Filename+"("+date+").txt";
			file = new File(fileOUT);
			okna = file.createNewFile();
			if	(okna)write(fileOUT,rowTitle,message);
			else write(fileOUT,rowTitle,message);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	private static void write(String Filename,String rowTitle,String message){
		Properties prop = new Properties();
		try	{
			prop.load(new FileInputStream(Filename));
			prop.put(rowTitle,message);
			FileOutputStream out = new FileOutputStream(Filename);
		    prop.store(out, "Date Created");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
}
