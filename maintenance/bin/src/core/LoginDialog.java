/**
 * Author       : Raymond A. Tiongco
 * Date Created : July 6, 2006
 * Description  : Log-in Frame w/ events
 * 
 * "WE CAN DO ALL THINGS THROUGH CHRIST WHO STRENGTHENS US...."
 */

package maintenance.bin.src.core;



import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import maintenance.bin.src.Component.WriteLog;
import maintenance.bin.src.DBConn.DBConnect;
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.createINI;


public final class LoginDialog extends JDialog implements ActionListener, WindowListener, KeyListener {
	//initialize class member variables
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//variables for the Panel
	private JPanel				panel;
	
	//variable for the layout
	private GridBagLayout 		layout;
	private GridBagConstraints 	constraint;
	
	//variable for the Labels 
	private JLabel 				lblUserName;
	private JLabel 				lblPassword;
	
	//variable for the data entry components
	JTextField 			txtUserName;
	private JPasswordField 		txtPassword;
	
	//variable for the buttons
	private JButton 			btnLogin;
	private JButton 			btnExit;
	
	//variable for class parent
	private mainform 			parent;

	private int 				attempt;
	private String 				query;
	
	
	//constructor
	public LoginDialog(mainform parent)
	{
		super(parent);
		this.parent = parent;
		initializeComponents();
		createGUI();
	}//end constructor

	private void createGUI()
	{
		//typical settings for frame
		setSize(400,200);
		setModal(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setTitle("User Log-in");
		setResizable(false);
		setVisible(false);
	}
	
	//user-defined method use to initialize gui components
	private void initializeComponents() 
	{
		//initialize the layout components
		layout = new GridBagLayout();
		constraint = new GridBagConstraints();
		panel = new JPanel();
		panel.setLayout(layout);
		
		//initialize label components
		lblUserName = new JLabel("Username");
		lblPassword = new JLabel("Password");
		
		//initialize data entry components
		txtUserName = new JTextField(15);
		txtPassword = new JPasswordField(15);
		
		//text font settings inside the textfield
		Font f = new Font("Verdana",Font.PLAIN,12);
		txtUserName.setFont(f);
		txtPassword.setFont(f);

		//initialize button components
		btnLogin = new JButton("Login");
		btnExit = new JButton("Exit");
		
		//set equal dimension size for buttons
		Dimension d = new Dimension(70,25);
		btnLogin.setPreferredSize(d);
		btnExit.setPreferredSize(d);
		
		//utilize the button event using keyboard
		btnExit.setMnemonic('E');
		btnLogin.setMnemonic('L');
		
		//-clicable identity
		Cursor c = new Cursor(Cursor.HAND_CURSOR);
		btnExit.setCursor(c);
		btnLogin.setCursor(c);
		
		//event listener registration
		btnLogin.addActionListener(this);
		btnExit.addActionListener(this);
		txtUserName.addKeyListener(this);
		txtPassword.addKeyListener(this);
		addWindowListener(this);
		
		//Setting and organizing gui components
		
		//General GridbagConstraints
		constraint.anchor = GridBagConstraints.WEST;//align left
		constraint.gridheight = 1;//occupies only one column
		constraint.insets = new Insets(9,3,9,3);//(Top,Left,Bottom,Right)space between components
		
		//Add Component in desired layout position
		addComponent(lblUserName,1,0,0);
		addComponent(txtUserName,2,1,0);
		addComponent(lblPassword,1,0,1);
		addComponent(txtPassword,2,1,1);
		constraint.insets = new Insets(5,3,3,3);//overwrite insets position
		addComponent(btnLogin,1,1,2);
		addComponent(btnExit,1,2,2);
		
		//add panel to thisainer
		getContentPane().add(panel);	
	}//end initialize

	//user-defined method use to add component to a panel
	public void addComponent(Component c,int gridwidth,
			                 int gridx, int gridy)
	{
		constraint.gridy = gridy;// column number
		constraint.gridx = gridx;// row number
		constraint.gridwidth = gridwidth;//occupies (constraint) row
		layout.setConstraints(c,constraint);
		panel.add(c);
	}//end addComponent
	
	
    private void exitApplication() {
    	int choice = Message.messageYesNo(mainform.desktop,"Are you sure?");

		if (choice == 0)
		{
			DBConnect dbconn = new DBConnect();
			dbconn.terminateConnection();
			System.exit(0);
		}
    }
    
    private void LoginTransaction() {
        try{
        	
        	 Statement stmt = mainform.stat;;
        	 ResultSet rs;
        	 DBConnect dbconn = new DBConnect();
        	 query = DBConnect.Select("user, userposition","*","user.position = userposition.code and user.userid = '" + txtUserName.getText().trim() + "'",null);

              rs = stmt.executeQuery(query);
              if (dbconn.isEOF(rs))
              {
            	 setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); 
            	 Message.messageWarning(this,"Username not found!");
            	 txtUserName.grabFocus();
            	 txtUserName.setText(null);
           	     txtPassword.setText(null);
           	     
           	     setAttempt(getAttempt()+1);
                 if (getAttempt() >=3)
                 {
                  Message.messageWarning(this,"You have reached the maximum allowable number of attempts for system login. System will now close!");
                  dbconn.terminateConnection();
                  System.exit(1);
                 }else{Message.messageWarning(this,"You have " + String.valueOf(3 - attempt) + " login attempt(s) remaining!");}
                 //rs.close();
                // stmt.close();
               }else //user exists: check password
                {
                     try{  
               	       query = DBConnect.Select("user, userposition","*","user.position = userposition.code and user.userid = '" + txtUserName.getText().trim() + "' AND user.password = '" + new String(txtPassword.getPassword()) + "'",null);
                           //query = "SELECT * FROM user,position WHERE  user.position = position.code and (user.userid LIKE '" + username + "' AND user.password LIKE '" + password + "')";
                           stmt = DBConnect.getConnection().createStatement();
                           rs = stmt.executeQuery(query);
                           if (dbconn.isEOF(rs))
                           {
                        	   setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        	   Message.messageWarning(this,"Invalid password!");
                        	   txtUserName.grabFocus();
                        	   txtUserName.setText(null);
                        	   txtPassword.setText(null);
                               
                        	   this.setAttempt(getAttempt()+1);
                        	   
                               
                               if (getAttempt() >=3)
		                              {
		                                Message.messageWarning(this,"You have reached the maximum allowable number of attempts for system login. System will now close!");
		                                dbconn.terminateConnection();
		                                System.exit(1);
		                               }else{Message.messageWarning(this,"You have " + String.valueOf(3 - attempt) + " login attempt(s) remaining!");}
                               rs.close();
                               stmt.close();
                            }else if(rs.getString(7).toString().equalsIgnoreCase("n") == true){
                            	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                         	    Message.messageWarning(this,"User not active!");
                         	    txtUserName.grabFocus();
                            }else if(rs.getString(8).toString().equalsIgnoreCase("y") == true){
                            	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                         	    Message.messageWarning(this,"Account already logged in!");
                         	    txtUserName.setText(null);
                         	    txtPassword.setText(null);
                         	    txtUserName.grabFocus();
                            }else{ //close rs then show mainform
                            	 String gcode = rs.getString(6);
                            	 String ucode = rs.getString(1);
                            	 String fname = rs.getString(3);
                            	 String lname = rs.getString(2);
                            	 
                            	 //set parameters logged data
                            	 new mainform(gcode,ucode,fname,lname);
                            	 
                            	 
                            	 
                            	 parent.setMenu(true);
                            	 String update = DBConnect.Update("user",
                            			 "loggedin = 'Y'",
                            			 "userid = '"+rs.getString(1)+"'");
                            	 DBConnect.userid = ucode;
                            	 
                            	 stmt.execute(update);
                            	 WriteLog.WriteLog("Logged on to back-end");
                          
                                 dispose();
                             }
                      }catch(SQLException sqlex)
                      {
                    	  Message.messageInfo(this,"Error log has been created");
                    	  StringWriter traceWriter = new StringWriter();
                    	  PrintWriter printWriter = new PrintWriter(traceWriter, false);
                    	  sqlex.printStackTrace(printWriter);
                    	  createINI.create("LoginDialog", "LoginTransaction", traceWriter.toString());
                      }
               }//end else
         }catch(SQLException sqlex)
         {
        	 Message.messageInfo(this,"Error log has been created");
        	 StringWriter traceWriter = new StringWriter();
        	 PrintWriter printWriter = new PrintWriter(traceWriter, false);
        	 sqlex.printStackTrace(printWriter);
        	 createINI.create("LoginDialog", "LoginTransaction", traceWriter.toString());
         }//end catch
	}
    
    private void setLogin() {
		//getting the values of a texfield and a passwordfield
		String uname = txtUserName.getText().trim();
		String pass = new String(txtPassword.getPassword());
		
		//checking if the fields are properly filled-up
		if (uname.trim().length() == 0)
		{
			Message.messageError(this,"Username cannot be left blank");
			
			//returning the focus to the textfield
			txtUserName.requestFocus();
		}
		else if (pass.trim().length() == 0)
		{
			Message.messageError(this,"Password cannot be left blank");
			txtPassword.requestFocus();
		}
		else
		{
			LoginTransaction();
		}
    }
	
	//event for btnLogin button
	private void btnLogin_actionPerformed(ActionEvent e)
	{
		setLogin();
	}
	
	//event for btnExit button
	private void btnExit_actionPerformed(ActionEvent e)
	{
		exitApplication();
	}
	
	//event handler
	public void actionPerformed(ActionEvent e) 
	{
		//obtaining the source of the event from the event object
		Object source = e.getSource();
		
		if (source == btnLogin) btnLogin_actionPerformed(e);
		else if (source == btnExit) btnExit_actionPerformed(e);
	}//end actionevent

	

	public void windowClosing(WindowEvent e) {
		exitApplication();
	}
	
	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	
	public void keyTyped(KeyEvent e) {
		if (e.getSource().equals(txtPassword)&& 
				(e.getKeyChar() == (char) KeyEvent.VK_ENTER)) {
			setLogin();
			//getRootPane().setDefaultButton(btnLogin);
		} else if (e.getSource().equals(txtUserName) && 
				(e.getKeyChar() == (char) KeyEvent.VK_ENTER)) {
			txtPassword.requestFocus();
		}
	}

	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}

	private int getAttempt() {
		return attempt;
	}

	private void setAttempt(int attempt) {
		this.attempt = attempt;
	}
	
}//end class
