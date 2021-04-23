package maintenance.bin.src.Functions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class fileManager {
	public String version;
	
    public fileManager(){
		FileInputStream file;
		try {
			file = new FileInputStream("maintenance/version.txt");
			Properties prop = new Properties();
			prop.load(file);
			version = prop.getProperty("Payroll");
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}//cons
}