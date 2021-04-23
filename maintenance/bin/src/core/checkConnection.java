package maintenance.bin.src.core;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import maintenance.bin.src.DBConn.DBConnect;
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.removeButton;


public class checkConnection {
	
	private Statement checkState = mainform.stat;
	private String query = "show tables";
	private boolean connected;
	
	public checkConnection(){
			try	{
				if	(DBConnect.isConnected == true)connected = checkState.execute(query);
			}
			catch(Exception e){
				DBConnect.isConnected = false;
				connected = false;
				try {
					reconnect();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
	}
	
	//reconnectFrame fr;
	private void reconnect() throws SQLException{
		if	(mainform.dialog.isVisible() == true){
			Message.messageError(mainform.frame,"Connection Interuppted...\n\t\t System will try to  reconnect");
		}else{
			Message.messageError("Connection Interuppted...\n\t\t System will reconnect");
		}
		closeIFrame();
		try	{
			mainform.stat = DBConnect.getConnection().createStatement();
			//fr = new reconnectFrame(mainform.frame);
			/*SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					try {
						mainform.stat = DBConnect.getConnection().createStatement();
						fr.dispose();
					} catch (SQLException e) {
						if	(mainform.dialog.isVisible() == true){
							Message.messageError(mainform.frame,"Cant Reconnect...\n\t\t System will now exit");
						}else{
							Message.messageError("Cant Reconnect...\n\t\t System will now exit");
						}
						System.exit(0);
					}
				}
			});*/
			
			
		}catch(Exception e1){
			if	(mainform.dialog.isVisible() == true){
				Message.messageError(mainform.frame,"Cant Reconnect...\n\t\t System will now exit");
			}else{
				Message.messageError("Cant Reconnect...\n\t\t System will now exit");
			}
			System.exit(0);
		}
	}
	
	private void closeIFrame(){
		JInternalFrame[] iFrames = mainform.desktop.getAllFrames();
		for	(int x = 0; x < iFrames.length; x++){
			mainform.tree.setEnabled(true);
			iFrames[x].dispose();			
		}
	}
	
	
}
