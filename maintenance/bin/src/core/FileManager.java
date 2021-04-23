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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.createINI;


/**
 * Managing image files from flat file config.file
 * 
 * @version		1.0 04 Oct 2006
 * @author		Raymond Tiongco
 *
 */
public class FileManager {
	/* You can access here the image used in Desktop
	 * such as mainForm and splash backgroud.
	 * You can modify or extend the functionality of
	 * this class if enhancement exists.
	 */
	
	/** Background image name instance variables. */ 
	private String mainFormImage;
	private String preloaderImage;
	private String icon;

	/** Background image mutators */
    public FileManager(){
    	//Just Call this constructor before using accessors
		try {
			FileInputStream fis = 
					new FileInputStream("maintenance/config");
			Properties prop = new Properties();
			prop.load(fis);
			this.mainFormImage = prop.getProperty("mainFormImage");
			this.icon = prop.getProperty("icon");
			this.preloaderImage = prop.getProperty("SImage");
		} catch (FileNotFoundException e) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("FileManager", "FileManager", traceWriter.toString());
		} catch (IOException ioe) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			ioe.printStackTrace(printWriter);
			createINI.create("Mainform", "FileManager", traceWriter.toString());
		}
	}//cons

    /**
     * @return Returns mainFormImage  
     */
	public String getMainFormImage() {
		return mainFormImage;
	}

    /**
     * @return Returns preloaderImage
     */
	public String getPreloaderImage() {
		return preloaderImage;
	}
	
	/**
     * @return Returns preloaderImage
     */
	public String getIcon() {
		return icon;
	}


}//end
