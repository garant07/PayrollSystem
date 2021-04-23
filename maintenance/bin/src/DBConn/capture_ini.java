package maintenance.bin.src.DBConn;

  import java.io.*;

import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.createINI;
   // (The TextReader class must also be available to this program.)
   
   public class capture_ini {
   
         TextReader data;     // Character input stream for reading data.
         PrintWriter result;  // Character output stream for writing data.
         String number = new String();
         public String driver = null;
         public String url= null;
         public String user = null;
         public String pass = null;
         public String port = null;
         public String host = null;
         public String dbase = null;
         public String dbtype = null;
         
         public capture_ini (){                                             //   read from the file.

         int numberCt;  // Number of items actually stored in the array.
         
         try {  // Create the input stream.
        	 File file = new File("maintenance/mycon.dat");
     		 if (!file.exists()) {
     			new DbaseConnectorFrame();
           	    return;
     		 }
        	 data = new TextReader(new FileReader("maintenance/mycon.dat"));
         }
         catch (FileNotFoundException e) {
        	 Message.messageInfo(null,"Error log has been created");
        	 StringWriter traceWriter = new StringWriter();
        	 PrintWriter printWriter = new PrintWriter(traceWriter, false);
        	 e.printStackTrace(printWriter);
        	 createINI.create("capture_ini", "capture_ini", traceWriter.toString());
         }
         try {
         
             // Read the data from the input file.
        	 char[] testing = null;
             numberCt = 0;
             while (data.eof() == false) {  // Read until end-of-file.
                number = data.getln().toString();
                int num1 = number.indexOf(" ");
                testing = number.toCharArray();
                numberCt++;
                }

             int key_int = (char) testing[0];
             for (int i = 0; i < number.length() ; i++){
       	     if (testing[i] > 10){
       		   testing[i] = (char)(testing[i] - key_int - 128 );
       		  } else {
       		  testing[i] = (char)(testing[i]);
       		    }
             }
             String data_s= new String(testing);
              
             driver = data_s.substring(data_s.indexOf(key_int)+1,data_s.indexOf(key_int+1));
             url = data_s.substring(data_s.indexOf(key_int+1)+1,data_s.indexOf(key_int+2));
             user = data_s.substring(data_s.indexOf(key_int+2)+1,data_s.indexOf(key_int+3));
             pass = data_s.substring(data_s.indexOf(key_int+3)+1,data_s.indexOf(key_int+4));
             host = data_s.substring(data_s.indexOf(key_int+4)+1,data_s.indexOf(key_int+5));
             port = data_s.substring(data_s.indexOf(key_int+5)+1,data_s.indexOf(key_int+6));

             dbase = data_s.substring(data_s.indexOf(key_int+6)+1,data_s.indexOf(key_int+7));
             
             dbtype = data_s.substring(data_s.indexOf(key_int+7)+1,data_s.length());
   
          }
          catch (TextReader.Error e) {
        	  Message.messageInfo(null,"Error log has been created");
        	  StringWriter traceWriter = new StringWriter();
        	  PrintWriter printWriter = new PrintWriter(traceWriter, false);
        	  e.printStackTrace(printWriter);
        	  createINI.create("capture_ini", "capture_ini", traceWriter.toString());
          }
          catch (IndexOutOfBoundsException e) {
        	  Message.messageInfo(null,"Error log has been created");
        	  StringWriter traceWriter = new StringWriter();
        	  PrintWriter printWriter = new PrintWriter(traceWriter, false);
        	  e.printStackTrace(printWriter);
        	  createINI.create("capture_ini", "capture_ini", traceWriter.toString());
          }
          finally {
            data.close();
          }
         }
          public static void main(String[] args) {          
        	  new capture_ini();

      }  // end of main()
  
   } // end of class