package maintenance.bin.src.DBConn;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.core.mainform;


	public class DbaseConnectorFrame extends JFrame implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private boolean isUpdate;
		
		//variable for the panel
		JPanel p;
		
		//variable for the layout
		GridBagLayout gbl;
		GridBagConstraints gbc;
		
		//variable for the labels
		JLabel lblHost;
		JLabel lblDatabaseType;
		JLabel lblDatabaseName;
		JLabel lblPort;
		JLabel lblUserName;
		JLabel lblPassword;
		
		//variable for the data entry components
		JTextField txtHost;
		JComboBox cboDatabaseType;
		JTextField txtDatabaseName;
		JTextField txtPort;
		JTextField txtUserName;
		JPasswordField txtPassword;
		
		//variable for the buttons
		JButton btnTestConnection;
		JButton btnSaveAndExit;
		
		//variable applicable to connection
		String driver = "";
		String url = "";
		
		//variable for Character IO Stream for reading and writing data
		TextReader reader;
		PrintWriter writer;
		
		
		public DbaseConnectorFrame() {
			initializeComponents();
			createAndShowGUI();
		}
		
		public DbaseConnectorFrame(String args) {
			initializeComponents();

			if (args.equals("update")) {
				
				File file = new File("maintenance/mycon.dat");
				
				this.setUpdate(true);

				if (file.exists()) {
					capture_ini ini = new capture_ini();
					txtDatabaseName.setText(ini.dbase);
					txtHost.setText(ini.host);
					txtPassword.setText(ini.pass);
					txtPort.setText(ini.port);
					txtUserName.setText(ini.user);
					cboDatabaseType.setSelectedItem(ini.dbtype);
				
				}
			}
			
			
			createAndShowGUI();
			
		}
		
		private void createAndShowGUI() {
			setSize(300,300);
			setTitle("Database Connection");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setResizable(false);
			this.addWindowListener(new WindowListener(){
				public void windowActivated(WindowEvent arg0) {}
				public void windowClosed(WindowEvent arg0) {}
				public void windowClosing(WindowEvent arg0) {
					/*try {
						Class.forName("maintenance.bin.src.core.mainform").newInstance();
					}
					catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					catch (InstantiationException e) {
						e.printStackTrace();
					}
					catch (ClassNotFoundException e) {
						e.printStackTrace();
					}*/
					//new mainform();
				}
				public void windowDeactivated(WindowEvent arg0) {}
				public void windowDeiconified(WindowEvent arg0) {}
				public void windowIconified(WindowEvent arg0) {}
				public void windowOpened(WindowEvent arg0) {}
			});
			setResizable(true);
			setLocationRelativeTo(null);
			setVisible(true);
		}
		
		private void initializeComponents() {
			//initialize layout components
			gbl = new GridBagLayout();
			gbc = new GridBagConstraints();
			p = new JPanel();
			p.setLayout(gbl);
			
			//initialize label components
			lblHost = new JLabel("Host");
			lblDatabaseType = new JLabel("Database Type");
			lblDatabaseName = new JLabel("Database Name");
			lblPort = new JLabel("Port");
			lblUserName = new JLabel("UserName");
			lblPassword = new JLabel("Password");
			
			//initialize data entry components
			txtHost = new JTextField(12);
			txtPort = new JTextField(5);
			txtUserName = new JTextField(12);
			txtPassword = new JPasswordField(12);
			txtDatabaseName = new JTextField(12);
			cboDatabaseType =  new JComboBox();
			cboDatabaseType.setFont(new Font("",Font.PLAIN,12));
			cboDatabaseType.addItem("--Select Database--");
			cboDatabaseType.addItem("MySQL");
			cboDatabaseType.addItem("Oracle");
			cboDatabaseType.addItem("SQL Server");		
			
			//initialize button components
			Dimension d = new Dimension(140,25);
			btnTestConnection = new JButton("Test Connection",new ImageIcon("images/businesssetup.png"));
			btnTestConnection.setPreferredSize(d);
			btnTestConnection.setMnemonic('T');
			btnSaveAndExit = new JButton("SaveAndExit",new ImageIcon("images/save.png"));
			btnSaveAndExit.setPreferredSize(d);
			btnSaveAndExit.setMnemonic('S');
			btnSaveAndExit.setEnabled(false);
			
			//event listener registration
			btnSaveAndExit.addActionListener(this);
			btnTestConnection.addActionListener(this);
			cboDatabaseType.addActionListener(this);
			
			//General GridbagConstraints
			gbc.anchor = GridBagConstraints.WEST;//align left
			gbc.gridheight = 1;//occupies only one column
			gbc.insets = new Insets(4,4,4,4);//(Top,Left,Bottom,Right)space between components
			
			//Add Component in desired layout position
			addComponent(lblHost,1,0,0);
			addComponent(txtHost,1,1,0);
			addComponent(lblDatabaseType,1,0,1);
			addComponent(cboDatabaseType,1,1,1);
			addComponent(lblDatabaseName,1,0,2);
			addComponent(txtDatabaseName,1,1,2);
			addComponent(lblPort,1,0,3);
			addComponent(txtPort,1,1,3);
			addComponent(lblUserName,1,0,4);
			addComponent(txtUserName,1,1,4);
			addComponent(lblPassword,1,0,5);
			addComponent(txtPassword,1,1,5);
			gbc.insets = new Insets(7,0,0,0);
			gbc.anchor = GridBagConstraints.CENTER;
			addComponent(btnTestConnection,2,0,6);
			addComponent(btnSaveAndExit,2,0,7);
			
			getContentPane().add(p);
		}
		
		public void addComponent(Component c,int gridwidth,int gridx, int gridy) {
			gbc.gridy = gridy;// column number
			gbc.gridx = gridx;// row number
			gbc.gridwidth = gridwidth;//occupies (gbc) row
			gbl.setConstraints(c,gbc);
			p.add(c);
		}


		public static void main(String args[]) {
			DbaseConnectorFrame frame = 
					new DbaseConnectorFrame(args[0]);
		}
		
		
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			int index = cboDatabaseType.getSelectedIndex();
			
			if (source == cboDatabaseType) {
				
				switch(index) {
					case 0:						
						this.driver = "";
						JOptionPane.showMessageDialog(this,"Please select Database Type item on the list.","Error",JOptionPane.ERROR_MESSAGE);
						break;
					case 1:
						this.driver = "com.mysql.jdbc.Driver";
						break;
					case 2:
						this.driver = "oracle.jdbc.driver.OracleDriver";
						break;
					case 3:
						this.driver = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
						break;
				}//switch
				return;
			}//if
			
			if (source == btnTestConnection) {
				try	{
					String host = txtHost.getText().trim();
					String port = txtPort.getText().trim();
					String uname = txtUserName.getText().trim();
					String pass = new String(txtPassword.getPassword());
					String dbase = txtDatabaseName.getText().trim();
					
					switch(index) {
						case 0:						
							this.url = "";
							break;
						case 1:
							this.url = "jdbc:mysql://"+host+":"+port+"/"+dbase+"";
							break;
						case 2:
							this.url = "jdbc:oracle:thin:@"+host+":"+port+":"+dbase+"";
							break;
						case 3:
							this.url = "jdbc:microsoft:sqlserver://"+host+":"+port+";DatabaseName="+dbase+"";
							break;

					}//switch
					
					
					Class.forName(this.driver).newInstance();
					Connection cnn = DriverManager.getConnection(this.url, uname, pass);
					cnn.createStatement();
					JOptionPane.showMessageDialog(this,"Connection Successful","Information",JOptionPane.INFORMATION_MESSAGE);
					btnSaveAndExit.setEnabled(true);
				}
				catch (Exception ex) {
					JOptionPane.showMessageDialog(this,"Connection Failed\nError log has been created","Information",JOptionPane.ERROR_MESSAGE);
//					Message.messageInfo("Error log has been created");
					StringWriter traceWriter = new StringWriter();
//					PrintWriter printWriter = new PrintWriter(traceWriter, false);
//					ex.printStackTrace(printWriter);
					createINI.create("DbaseConnectorFrame", "actionPerformed", traceWriter.toString());
					btnSaveAndExit.setEnabled(false);					
				}
				
				return;
			}
			
			if (source == btnSaveAndExit) {
				
				try {
					//create the output stream
					String filePath = "maintenance/mycon.dat";
					new FileManipulator(new File(filePath));
					writer = new PrintWriter(new FileWriter(filePath));
				}
				catch(IOException ioe) {
					System.out.println(ioe.getMessage());
					reader.close();
				}	
				
				try {
		        	 Random generator = new Random();
			         int key_int = generator.nextInt(3);
			         int key_org = key_int;
			         
			       //  String url = "jdbc:mysql://"+host+":"+port+"/"+dbase+"";
		        	 	
		             // Output the numbers in reverse order.
		        	 
		        	 char[] driver = this.driver.trim().toCharArray();
		        	 char[] url = this.url.trim().toCharArray();
		             char[] user = txtUserName.getText().trim().toCharArray();
		             char[] pass = txtPassword.getPassword();
		             
		             char[] host = txtHost.getText().trim().toCharArray();
		             char[] port = txtPort.getText().trim().toCharArray();
		             char[] dbase = txtDatabaseName.getText().trim().toCharArray();
		             char[] dbtype = cboDatabaseType.getSelectedItem().toString().trim().toCharArray();
		             
		             
		             //System.out.println(host);
		        	 writer.print((char)key_int);
		        	 key_int++;
		        	 
		             for (int i = 0; i < driver.length ; i++)
		             {
		            	 writer.print((char)(driver[i]+ key_org + 128));
		             }
		             writer.print((char)key_int);
		             key_int++;
		             for (int i = 0; i < url.length ; i++)
		             {
		            	 writer.print((char)(url[i]+ key_org + 128));
		             }
		             writer.print((char)key_int);
		             key_int++;	             
		             
		             for (int i = 0; i < user.length ; i++)
		             {	
		           	    writer.print((char)(user[i]+ key_org + 128));
		                    
		             }
		             writer.print((char)key_int);
		             key_int++;
		             for (int i = 0; i < pass.length ; i++)
		             {	
		           	    writer.print((char)(pass[i]+ key_org + 128));
		                    
		             }
		             writer.print((char)key_int);
		             key_int++;
		             for (int i = 0; i < host.length ; i++)
		             {	
		           	    writer.print((char)(host[i]+ key_org + 128));
		                    
		             }
		             writer.print((char)key_int);
		             key_int++;
		             for (int i = 0; i < port.length ; i++)
		             {	
		           	    writer.print((char)(port[i]+ key_org + 128));
		                    
		             }

		             writer.print((char)key_int);
		             key_int++;
		             for (int i = 0; i < dbase.length ; i++)
		             {	
		           	    writer.print((char)(dbase[i]+ key_org + 128));
		                    
		             }
		             writer.print((char)key_int);
		             key_int++;
		             for (int i = 0; i < dbtype.length ; i++)
		             {	
		           	    writer.print((char)(dbtype[i]+ key_org + 128));
		                    
		             }
		             
		             

		  
		          }
		          catch (TextReader.Error tre) {
		        	  Message.messageInfo("Error log has been created");
		        	  StringWriter traceWriter = new StringWriter();
		        	  PrintWriter printWriter = new PrintWriter(traceWriter, false);
		        	  tre.printStackTrace(printWriter);
		        	  createINI.create("DbaseConnectorFrame", "actionPerformed", traceWriter.toString());
		          }
		          catch (IndexOutOfBoundsException tre) {
		        	  Message.messageInfo("Error log has been created");
		        	  StringWriter traceWriter = new StringWriter();
		        	  PrintWriter printWriter = new PrintWriter(traceWriter, false);
		        	  tre.printStackTrace(printWriter);
		        	  createINI.create("DbaseConnectorFrame", "actionPerformed", traceWriter.toString());
		          }
		          finally {
		            writer.close();
		          }
		          
		          if (isUpdate) {
					  System.exit(0);
		          }
		          
		          dispose();
			}
			

		}//public

		/**
		 * @return the isUpdate
		 */
		private boolean getUpdate() {
			return isUpdate;
		}

		/**
		 * @param isUpdate the isUpdate to set
		 */
		private void setUpdate(boolean isUpdate) {
			this.isUpdate = isUpdate;
		}

	}//class
