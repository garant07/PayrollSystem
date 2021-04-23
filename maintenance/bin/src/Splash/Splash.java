package maintenance.bin.src.Splash;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import maintenance.bin.src.DBConn.DBConnect;
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.core.FileManager;
import maintenance.bin.src.core.mainform;

public class Splash extends JWindow {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Container cont;
	JLabel lbl = new JLabel();
	String imgLogo;
	RasterPanel cont = new RasterPanel();
	public static JProgressBar jpbConn;
	public static Timer timer;
	public static int TimeValue = 0;
	boolean RETURN = false;
	public static boolean show = false;
	Statement stmt;
	ResultSet RS;
	public static int timeValue = 0;
	public static int val = 1;
	public static Thread showForm = null;
	
	public Splash(){

		this.getContentPane().add(cont);
		cont.setLayout(null);
		cont.setBorder(BorderFactory.createLineBorder(Color.black));
		cont.setBackground(new Color(102,176,245));
	    try {
	    	FileInputStream imgLoc =  new FileInputStream("maintenance/config");
	    	Properties prop = new Properties();
	    	prop.load(imgLoc);
	    	imgLogo = prop.getProperty("Splash");
	    	
	    }catch(Exception e){}
	    
	    jpbConn = new JProgressBar();
	    jpbConn.setBounds(0,280,400,20);
	    jpbConn.setStringPainted(true);
	    
	    
	    lbl.setIcon(new ImageIcon(imgLogo));
    	//lbl.setPreferredSize(new Dimension(260,120));
    	lbl.setBounds(70,100,270,107);
    	lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    	lbl.setText("");
    	//cont.add(lbl);
    	cont.add(jpbConn);
    	timeValue = 500;
    	timer = new Timer(timeValue, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	jpbConn.setValue(TimeValue);
            	TimeValue += val;
            	jpbConn.setString("Connecting To Database...");
            	//connect();
            	if(jpbConn.getValue() == 60)timer.setDelay(250);
            	else if(jpbConn.getValue() == 80)timer.setDelay(180);
            	else if(jpbConn.getValue() == 50)timer.setDelay(300);
            	if ( jpbConn.getValue() == 100 ){
            		if (show == true){
            			String query = "show tables";
            			try {RETURN = mainform.stat.execute(query);}
            			catch (SQLException e) {}
            		    if (RETURN == true){
            		    	dis();
            		    	showForm = new Thread(new Runnable(){
								public void run() {
									mainform.frame.setVisible(true);
				            		mainform.dialog.setVisible(true);
								}
            		    	});
            		    	showForm.run();
            		    }
            		    else{
            		    	Message.messageError(cont,"Connection failed");
            		    	System.exit(1);
            		    }
            		    timer.stop();
            		}else {
            			Message.messageError(cont,"Connection failed");
            			System.exit(1);
            			timer.stop();
            		}
            	}
            	
            	
            }
        });
    	timer.start();
		setSize(400,300);
		setLocationRelativeTo(null);
		//setAlwaysOnTop(true);
		setVisible(true);
		
		
	}
	
	
	public static int setValue(int val2){
//		/val = val2;
		timeValue = val2;
		return timeValue;
	}
	
	private void dis(){
		this.dispose();
	}
	
	 //class for the background image:this is reusable
    private class RasterPanel extends JDesktopPane{

		private static final long serialVersionUID = 1L;
		final FileManager fmgr = new FileManager();
           BufferedImage bi, bi1, bi2;
           boolean flipped = false;

    RasterPanel() {
            setBackground(Color.WHITE);
            setSize(450, 400);
            Image image = getToolkit().getImage(fmgr.getPreloaderImage());
            MediaTracker mt = new MediaTracker(this);
            mt.addImage(image, 1);
            try {
                    mt.waitForAll();
                } catch (Exception e) {
                    //System.out.println("Exception while loading image.");
            }//catch
                try{
               /* if (image.getWidth(this) == -1) {
                    System.out.println("No jpg file");
                    System.exit(0);
                }//if*/

                bi1 = new BufferedImage(image.getWidth(this),( image.getHeight(this)+70),
                        BufferedImage.TYPE_INT_ARGB);
                        Graphics2D big = bi1.createGraphics();
                        big.drawImage(image, 0, 0, this);
                        bi = bi1;
                }
                catch(Exception ex){
                	setBackground(Color.LIGHT_GRAY);
                	
                	//System.out.println(ex.getMessage());
                }
    }//rasterpnlcons

   

    
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2D = (Graphics2D) g;
      g2D.drawImage(bi, 0, 0, this);
    }//pntcmpnt

   }//classrstrpnl


	
}
