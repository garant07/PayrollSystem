package maintenance.bin.src.GetGUI;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.core.mainform;



public class GetGUI {
	
	public static JDesktopPane pnl2;
	private int Limit = 0;
	
	int xx = 0;
	int yy = 0;
	JDesktopPane desk = mainform.desktop;
	BorderLayout BorderLay = new BorderLayout();
	public GetGUI(JDesktopPane pnl, String program){
		
		pnl2 = pnl;
		
		String imgLimit;
		
		try	{
		FileInputStream imgLoc =  new FileInputStream("maintenance/config");
		Properties prop = new Properties();
		prop.load(imgLoc);
		imgLimit = prop.getProperty("OpenForms");
		Limit = Integer.parseInt(imgLimit);
		}catch(Exception e){
			System.out.println(e.getMessage()+" ->> line 28");
		}
		try {
			final JInternalFrame iFrame;
			JInternalFrame[] in = pnl.getAllFrames();
			if	(in.length >= Limit){
				JOptionPane.showMessageDialog(pnl,"Try to close other forms to continue...","Information",JOptionPane.INFORMATION_MESSAGE);
			}else{
				 
				 iFrame =  (JInternalFrame) Class.forName(program.trim()).newInstance();
				 pnl.setDesktopManager( new NoDragDesktopManager() );
				 pnl.setLayout(null);
				 pnl.add(iFrame, new Integer(1));
				 
				 
				 //set center
				 Dimension size = iFrame.getSize();
			     Dimension rootSize = Toolkit.getDefaultToolkit().getScreenSize();
				 if	(size.width <= 500){
					 iFrame.setLocation(((rootSize.width + 235) - size.width) / 2,(rootSize.height - size.height) / 3);
					 xx = ((rootSize.width + 235) - size.width) / 2;
					 yy = (rootSize.height - size.height) / 3;
				 }else{
					 if	(rootSize.width <= 800){
						 iFrame.setLocation((rootSize.width - size.width) / 2,(rootSize.height - size.height) / 3);
						 xx = (rootSize.width - size.width) / 2;
						 yy = (rootSize.height - size.height) / 3;
					 }else	{
						 iFrame.setLocation(((rootSize.width + 235) - size.width) / 2,
					        		(rootSize.height - size.height) / 3);
						 xx = ((rootSize.width + 235) - size.width) / 2;
						 yy = (rootSize.height - size.height) / 3;
					 }
				 }
				 iFrame.setSelected(true);
				 iFrame.putClientProperty("dragMode", "fixed");
			     iFrame.setIconifiable(true);
				 if (in.length > 0){
					 for (int x = 0; x < in.length; x++){
						 if (iFrame.getName() == in[x].getName()){
							 iFrame.dispose();
							 in[x].setSelected(true);
						 }//3rd if statement
					 }//1st for loop
				 }//2nd if statement	 
			}//1st if-else Statement
			
		}//1st try statement
		catch (InstantiationException e1) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e1.printStackTrace(printWriter);
			createINI.create("GetGUI", "GetGUI", traceWriter.toString());
		} 
		catch (IllegalAccessException e1) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e1.printStackTrace(printWriter);
			createINI.create("GetGUI", "GetGUI", traceWriter.toString());
		} 
		catch (ClassNotFoundException e1) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e1.printStackTrace(printWriter);
			createINI.create("GetGUI", "GetGUI", traceWriter.toString());
		}
		catch (Exception e){
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("GetGUI", "GetGUI", traceWriter.toString());
		}
		
	}//public GetGUI(JDesktopPane pnl, Object program){
	private class NoDragDesktopManager extends DefaultDesktopManager
	{
		public void beginDraggingFrame(JComponent f)
		{
			if (!"fixed".equals(f.getClientProperty("dragMode")))
				super.beginDraggingFrame(f);
		}
		public void dragFrame(JComponent f, int newX, int newY)
		{
			if (!"fixed".equals(f.getClientProperty("dragMode")))
				super.dragFrame(f, newX, newY);
		}
		public void endDraggingFrame(JComponent f)
		{
			if (!"fixed".equals(f.getClientProperty("dragMode")))
				super.endDraggingFrame(f);
		}
	}

}//public class GetGUI {
