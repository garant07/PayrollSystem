package maintenance.bin.src.RasterPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JDesktopPane;

import maintenance.bin.src.core.FileManager;




//class for the background image:this is reusable
public class RasterPanel extends JDesktopPane{

	private static final long serialVersionUID = 1L;
	final FileManager fmgr = new FileManager();
       BufferedImage bi, bi1, bi2;
       boolean flipped = false;

public RasterPanel() {
        setBackground(Color.WHITE);
        setSize(450, 400);
        Image image = getToolkit().getImage(fmgr.getMainFormImage());
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(image, 1);
        try {
                mt.waitForAll();
            } catch (Exception e) {
                System.out.println("Exception while loading image.");
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
            	
            	System.out.println(ex.getMessage());
            }
}//rasterpnlcons




public void paintComponent(Graphics g) {
  super.paintComponent(g);
  Graphics2D g2D = (Graphics2D) g;
  int height = Toolkit.getDefaultToolkit().getScreenSize().height;
  int width = Toolkit.getDefaultToolkit().getScreenSize().width;
  g2D.drawImage(bi, 0, 0,width,height+70, this);
}//pntcmpnt

}//classrstrpnl