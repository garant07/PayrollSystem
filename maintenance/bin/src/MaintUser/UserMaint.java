package maintenance.bin.src.MaintUser;
/**
 * Author:  Angelo Mercado
 * Date:    April 06, 2006
 * Company: Applied Ideas, Inc 
 * Program: User Maintenance
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import maintenance.bin.src.DBConn.DBConnect;
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.MyField;
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.ResultTableModel.ResultSetTableModel;
import maintenance.bin.src.TextReader.TextReader;
import maintenance.bin.src.core.LockHandler;
import maintenance.bin.src.core.mainform;

public class UserMaint extends JInternalFrame {
	private static final long serialVersionUID = 1L;
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	//static final String DEFAULT_QUERY = "Select user.code AS Code, user.password, user.lname AS Lastname,user.fname AS Firstname,user.mname AS Middlename,concat(user.position, ' - ',  position.desc) AS 'Code - Position' FROM user,position Where user.position = position.code order by user.lname ASC";

	static final String DEFAULT_QUERY = 
		DBConnect.Select("user, userposition ",
				"user.userid AS code, user.password, user.lname, user.fname, " +
				"user.mname, userposition.desc, user.active, user.loggedin ",
				"user.position = userposition.code ",
		"user.lname ASC");

	static final String DEFAULT_QUERY2 = 
		DBConnect.Select("user,userposition ",
				"user.position, userposition.desc ",
				"user.position = userposition.code ",
				null);
	//static final String DEFAULT_QUERY2 = "Select user.position, position.desc FROM user,position Where user.position = position.code order by position.code ASC";
	private ResultSetTableModel tableUser;
	private String cbopost; 
	Container container;
	String host;
	String port;
	String user;
	String pass;
	String conf;
	String db;
	String Fpath;
	TextReader Datacount;
	PrintWriter writeData;
	JFrame frame;
	int x;
	String VtxtUsed;
	String cboactive;
	String cbolog;
	Message msg = new Message();
	JTable tbl;

	//variable current cell selected
	private int tempR;
	private int tempC;

	//variable for buttons
	private JButton jbtnadd;
	private JButton jbtnedit;
	private JButton jbtndelete;

	//variable for user entry
	JScrollPane scrollPane;
	private JComboBox jcboPosition = new JComboBox();
	private MyField txtcode = new MyField(true,10);
	private JPasswordField txtpass = new JPasswordField();
	private JPasswordField txtconfirm = new JPasswordField();
	private MyField txtLastname = new MyField(true,30);
	private MyField txtFirstname = new MyField(true,30);
	private MyField txtMiddlename = new MyField(true,30);
	private JTextField txtsearch = new JTextField();
	private JComboBox jcboactive = new JComboBox();
	private JComboBox jcbolog = new JComboBox();

	public UserMaint(){
		//super ("Item Maintenance", resizable, close, maximize, iconifiable);
		super ("User Maintenance", false, true, false, false);
		this.setName("Usermaint");
		this.setResizable(false);

		this.addInternalFrameListener(new InternalFrameListener(){
			public void internalFrameActivated(InternalFrameEvent arg0) {
				mainform.tree.setEnabled(false);
			}
			public void internalFrameClosed(InternalFrameEvent arg0) {}
			public void internalFrameClosing(InternalFrameEvent arg0) {
				mainform.tree.setEnabled(true);
			}
			public void internalFrameDeactivated(InternalFrameEvent arg0) {}
			public void internalFrameDeiconified(InternalFrameEvent arg0) {}
			public void internalFrameIconified(InternalFrameEvent arg0) {}
			public void internalFrameOpened(InternalFrameEvent arg0) {}
		});

		try{
			tableUser = new ResultSetTableModel( DEFAULT_QUERY );

			//this.setAlwaysOnTop(true);
			container = this.getContentPane();
			container.setLayout(null);

			//Set frame objects
			JPanel jpan = new JPanel();
			jbtnadd = new JButton("Add");
			jbtnadd.setMnemonic('A');
			jbtnedit = new JButton("Edit");
			jbtnedit.setMnemonic('E');
			jbtndelete = new JButton("Delete");
			jbtndelete.setMnemonic('D');
			JLabel lblcode = new JLabel("Code");
			JLabel lblpass = new JLabel("Password");
			JLabel lblconfirm = new JLabel("Confirm");
			JLabel lblLastname = new JLabel("Lastname");
			JLabel lblFirstname = new JLabel("Firstname");
			JLabel lblMiddlename = new JLabel("Middlename");
			JLabel lblposition = new JLabel("Position");
			JLabel lblactive = new JLabel("Active");
			JLabel lbllog = new JLabel("Logged In");
			tbl = new JTable(tableUser);

			txtcode.setCapital(true);

			String ace = "select * from userposition";

			TableCellRenderer renderer = new TestRenderer();
			tbl.setDefaultRenderer(Object.class, renderer);
			scrollPane = new JScrollPane(tbl);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			tbl.setSelectionMode(0);

			//jcboPosition listener
			jcboPosition.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {    
							String getcbo = jcboPosition.getSelectedItem().toString();
							int nn = getcbo.indexOf("-");
							cbopost = getcbo.substring(0,nn);
						}
					}
			);

			try{  
				int count = 1;

				tableUser.resultSet = tableUser.statement.executeQuery(ace);

				//jcboPosition.setSelectedItem(tbl.getValueAt(1,4));
				jcboPosition.addItem("Code-Position");
				while (tableUser.resultSet.next()){
					String item = tableUser.resultSet.getString("Desc");
					String item2 = tableUser.resultSet.getString("Code");
					String fin;
					fin =  item2 +"-"+ item;
					jcboPosition.addItem(fin);
					++count;
				}//while (tableUser.resultSet.next())
			}//try
			catch(Exception e){
				System.out.println(e.getMessage());
			}//catch
			tableUser.resultSet = tableUser.statement.executeQuery(DEFAULT_QUERY);
			int iColumn = 0;
			int iColumn2 = 5;
			int colIndex1 = 1;
			int colIndex2 = 2;
			int colIndex3 = 3;
			//int width0 = 60;
			int width1 = 100;
			int width2 = 100;
			int width3 = 100;
			//TableColumn col0 = tbl.getColumnModel().getColumn(colIndex0);
			TableColumn col1 = tbl.getColumnModel().getColumn(colIndex1);
			TableColumn col2 = tbl.getColumnModel().getColumn(colIndex2);
			TableColumn col3 = tbl.getColumnModel().getColumn(colIndex3);

			col1.setMaxWidth(width1);
			col2.setMaxWidth(width2);
			col3.setMaxWidth(width3);
			col1.setMinWidth(100);
			col2.setMinWidth(100);
			col3.setMinWidth(100);

			//set column width
			reload();

			tbl.getTableHeader().setResizingAllowed(false);
			tbl.getTableHeader().setMaximumSize(new Dimension(23,56));

			tbl.setPreferredScrollableViewportSize(new Dimension(430,125));
			jcboPosition.setFont(new Font("",Font.PLAIN, 12));
			jcboactive.setFont(new Font("",Font.PLAIN, 12));
			jcboactive.setBackground(Color.white);
			jcboactive.addItem("Choose");
			jcboactive.addItem("Yes");
			jcboactive.addItem("No");

			jcbolog.setFont(new Font("",Font.PLAIN, 12));
			jcbolog.setBackground(Color.white);
			jcbolog.addItem("Choose");
			jcbolog.addItem("Yes");
			jcbolog.addItem("No");
			tbl.getTableHeader().setReorderingAllowed(false); //disabled column dragging
			//tbl.setGridColor(Color.WHITE);//set table grid color to white

			jpan.setBounds(-14,20,520,150);

			jcboPosition.setBackground(Color.WHITE);
			lblcode.setBounds(28,80,100,280);
			lblpass.setBounds(28,110,100,280);
			lblconfirm.setBounds(240, 110, 100, 280);
			lblLastname.setBounds(28,140,100,280);
			lblFirstname.setBounds(28,170,100,280);
			lblMiddlename.setBounds(28,200,100,280);
			lblposition.setBounds(28,230,210,280);
			lblactive.setBounds(28,260,210,280);
			lbllog.setBounds(240,260,100,280);

			txtcode.setBounds(120,210,110,20);
			txtcode.setDisabledTextColor(new Color(139,113,113));
			txtpass.setBounds(120,240,110,20);
			txtpass.setDisabledTextColor(new Color(139,113,113));
			txtconfirm.setBounds(310,240,110,20);
			txtconfirm.setDisabledTextColor(new Color(139,113,113));
			txtLastname.setBounds(120,270,240,20);
			txtLastname.setDisabledTextColor(new Color(139,113,113));
			txtFirstname.setBounds(120,300,240,20);
			txtFirstname.setDisabledTextColor(new Color(139,113,113));
			txtMiddlename.setBounds(120,330,240,20);
			txtMiddlename.setDisabledTextColor(new Color(139,113,113));
			jcboPosition.setBounds(120,360,240,20);
			jcboactive.setBounds(120,390,90,20);
			jcbolog.setBounds(310,390,90,20);

			jbtnadd.setBounds(85,180,80,22);
			jbtnedit.setBounds(195,180,80,22);
			jbtndelete.setBounds(310,180,80,22);

			txtsearch.setBounds(22,3,250,18);
			txtsearch.setBackground(new Color(255,255,225));
			txtsearch.setVisible(false);
			txtsearch.setBorder(BorderFactory.createEmptyBorder());

			jpan.add(scrollPane, BorderLayout.CENTER);

			if	(tbl.getRowCount() >= 1){
				tbl.setRowSelectionInterval(0,0);
				tbl.setColumnSelectionInterval(0,0);
				int getrow = tbl.getSelectedRow();
				Object getcode = tbl.getValueAt(getrow,0);
				Object getpass = tbl.getValueAt(getrow,1);
				Object getlast = tbl.getValueAt(getrow,2);
				Object getfirst = tbl.getValueAt(getrow,3);
				Object getmiddle = tbl.getValueAt(getrow,4);
				txtcode.setText((String) getcode);
				txtpass.setText((String) getpass);
				txtconfirm.setText((String) getpass);
				txtLastname.setText((String) getlast);
				txtFirstname.setText((String) getfirst);
				txtMiddlename.setText((String) getmiddle);
				for (int i=0; i < jcboPosition.getItemCount();i++){
					if (jcboPosition.getItemAt(i).toString().contains((CharSequence) tbl.getValueAt(getrow,5)) == true){
						jcboPosition.setSelectedIndex(i);
						break;
					}//end if
				}//for (int i=0; i < jcboPosition.getItemCount();i++){
				if(tbl.getValueAt(getrow,6).toString().equalsIgnoreCase("Y") == true){
					jcboactive.setSelectedItem("Yes");
				}else{
					jcboactive.setSelectedItem("No");
				}
				if(tbl.getValueAt(getrow,7).toString().equalsIgnoreCase("Y") == true){
					jcbolog.setSelectedItem("Yes");
				}else{
					jcbolog.setSelectedItem("No");
				} 
			}

			//add objects to container
			container.add(jpan);
			container.add(lblcode);
			container.add(txtcode);
			container.add(lblpass);
			container.add(txtpass);
			container.add(lblconfirm);
			container.add(txtconfirm);
			container.add(lblFirstname);
			container.add(txtFirstname);
			container.add(lblLastname);
			container.add(txtLastname);
			container.add(lblMiddlename);
			container.add(txtMiddlename);
			container.add(lblposition);
			container.add(jcboPosition);
			container.add(lblactive);
			container.add(jcboactive);
			container.add(lbllog);
			container.add(jcbolog);
			container.add(jbtnadd);
			container.add(jbtnedit);
			container.add(jbtndelete);        
			container.add(txtsearch);

			//Disabled text fields
			txtcode.setEnabled(false);
			txtpass.setEnabled(false);
			txtconfirm.setEnabled(false);
			txtLastname.setEnabled(false);
			txtFirstname.setEnabled(false);
			txtMiddlename.setEnabled(false);
			jcboPosition.setEnabled(false);
			jcboactive.setEnabled(false);
			jcbolog.setEnabled(false);

			//			jcbolog listener
			jcbolog.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if(jcbolog.getSelectedItem().toString().equals("Yes") == true){
								cbolog = "Y";
							}else{
								cbolog = "N";
							}
						}
					}
			);       

			//jcboactive listener
			jcboactive.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if(jcboactive.getSelectedItem().toString().equals("Yes") == true){
								cboactive = "Y";
							}else{
								cboactive = "N";
							}
						}
					}
			);

			//add button listener
			jbtnadd.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					String caption = jbtnadd.getText();
					String xcode = txtcode.getText().trim();
					String pass = txtpass.getText().trim();
					String conf = txtconfirm.getText().trim();
					String lname = " ";
					String fname = " ";
					String mname = " ";
					if	(txtLastname.getText().trim().length() == 0){
						lname = " ";
					}else{
						lname = txtLastname.getText();
					}
					if	(txtFirstname.getText().trim().length() == 0){
						fname = " ";
					}else{
						fname = txtFirstname.getText();
					}
					if	(txtMiddlename.getText().trim().length() == 0){
						mname = " ";
					}else{
						mname = txtMiddlename.getText();
					}

					if (caption == "Add"){
						ListSelectionModel selectionModel = tbl.getSelectionModel();
						selectionModel.clearSelection();
						tbl.setEnabled(false);
						txtcode.setEnabled(true);
						txtcode.grabFocus();
						txtpass.setEnabled(true);
						txtconfirm.setEnabled(true);
						txtLastname.setEnabled(true);
						txtFirstname.setEnabled(true);
						txtMiddlename.setEnabled(true);
						txtsearch.setVisible(false);
						jcboPosition.setEnabled(true);
						txtcode.setText(null);
						txtpass.setText(null);
						txtconfirm.setText(null);
						txtLastname.setText(null);
						txtFirstname.setText(null);
						txtMiddlename.setText(null);
						jcboPosition.setSelectedItem("Code-Position");
						jcboactive.setSelectedItem("Choose");
						jcboactive.setEnabled(true);
						jcbolog.setSelectedItem("No");
						jcbolog.setEnabled(false);
						jbtnadd.setText("Save");
						jbtnadd.setMnemonic('S');
						jbtndelete.setText("Cancel");
						jbtndelete.setMnemonic('C');
						jbtnedit.setEnabled(false);
					}
					else if (caption == "Save"){
						int iColumn = 0;
						String save_sql = DBConnect.Insert("user",
								"userid,password,lname,fname,mname,position,active,loggedin",
								"'"+DBConnect.clean(xcode)+"', " +
								"'"+DBConnect.clean(pass)+"', " +
								"'"+DBConnect.clean(lname)+"', " +
								"'"+DBConnect.clean(fname)+"', " +
								"'"+DBConnect.clean(mname)+"', " +
								"'"+DBConnect.clean(cbopost)+"', " +
								"'"+DBConnect.clean(cboactive)+"', " +
								"'"+DBConnect.clean(cbolog)+"' "
						);

						tbl.setEnabled(false);
						try{
							//String getcbo = (String) jcboPosition.getSelectedItem();

							if (xcode.length() == 0){   
								Message.messageError("Invalid input for code field.");
								txtcode.grabFocus();
							}
							else if(pass.length() == 0){
								Message.messageError("Invalid input for password field.");
								txtpass.grabFocus();
							}
							else if(conf.equals(pass) == false){
								Message.messageError("Password Confirm failed.");
								txtconfirm.grabFocus();
							}
							else if(lname.length() == 0){
								Message.messageError("Invalid input for lastname field.");
								txtLastname.grabFocus();
							}
							else if(fname.length() == 0){
								Message.messageError("Invalid input for firstname field.");
								txtFirstname.grabFocus();
							}
							else if(mname.length() == 0){
								Message.messageError("Invalid input for middlename field.");
								txtMiddlename.grabFocus();
							}
							else if(jcboPosition.getSelectedIndex() == 0){
								Message.messageError("Invalid input for position field.");
								jcboPosition.grabFocus();
							}
							else if(jcboactive.getSelectedIndex() == 0){
								Message.messageError("Invalid input for active field.");
								jcboactive.grabFocus();
							}
							else{
								tableUser.setInsert(save_sql);

								tableUser.setQuery(DEFAULT_QUERY);
								tbl.getColumnModel().getColumn(iColumn).setMaxWidth(0);
								tbl.getColumnModel().getColumn(iColumn).setMinWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(iColumn).setMaxWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(iColumn).setMinWidth(0);
								tbl.getColumnModel().getColumn(1).setMaxWidth(0);
								tbl.getColumnModel().getColumn(1).setMinWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(0);
								tbl.getColumnModel().getColumn(2).setMaxWidth(100);
								tbl.getColumnModel().getColumn(2).setMinWidth(100);
								tbl.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(100);
								tbl.getTableHeader().getColumnModel().getColumn(2).setMinWidth(100);
								tbl.getColumnModel().getColumn(3).setMaxWidth(100);
								tbl.getColumnModel().getColumn(3).setMinWidth(100);
								tbl.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(100);
								tbl.getTableHeader().getColumnModel().getColumn(3).setMinWidth(100);
								tbl.getColumnModel().getColumn(4).setMaxWidth(100);
								tbl.getColumnModel().getColumn(4).setMinWidth(100);
								tbl.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(100);
								tbl.getTableHeader().getColumnModel().getColumn(4).setMinWidth(100);
								tbl.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(150);
								tbl.getTableHeader().getColumnModel().getColumn(5).setMinWidth(150);
								tbl.getColumnModel().getColumn(2).setHeaderValue("Lastname");
								tbl.getColumnModel().getColumn(3).setHeaderValue("Firstname");
								tbl.getColumnModel().getColumn(4).setHeaderValue("Middlename");
								tbl.getColumnModel().getColumn(5).setHeaderValue("Position");
								txtcode.setText(null);
								txtpass.setText(null);
								txtconfirm.setText(null);
								txtLastname.setText(null);
								txtFirstname.setText(null);
								txtMiddlename.setText(null);
								jcboPosition.setSelectedItem("Code-Position");
								jcboactive.setSelectedItem("Choose");
								jcbolog.setSelectedItem("Choose");
								txtcode.grabFocus();
								Message.messageInfo(Message.messageAdd);
							}
						}
						catch(SQLException se){
							try{
								tableUser.setQuery(DEFAULT_QUERY);
								reload();
								JOptionPane.showMessageDialog(container,Message.messageExist+"\n"+se.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
								//System.out.println(se.getMessage());
							}catch(Exception exc){
								System.out.println(exc.getMessage());
							}
						}
					}				
				}
			});

			//DELETE BUTTON LISTENER
			jbtndelete.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String dcaption = jbtndelete.getText();

					if (dcaption == "Cancel"){
						setCancel();
					}
					else if (dcaption == "Delete"){
						// start indicating lock
						LockHandler.initializeLock(container);

						//wait for lock indicator to invoke
						SwingUtilities.invokeLater(new Runnable(){
							public void run() {
								try {
									String xcode = txtcode.getText();

									//row level locking
									LockHandler.startLock(DBConnect.SelectForUpdate("user",
											"*",
											"userid='"+DBConnect.clean(tbl.getValueAt(tbl.getSelectedRow(),0).toString())+"'"));

									String del_sql = DBConnect.delete("user","userid = '"+DBConnect.clean(xcode)+"'");
									//String del_sql = "Delete from user where userid = '"+xcode+"'";

									String getcha = (String) jcboPosition.getSelectedItem();//txtLastname.getText();
									if (getcha != "Code-Position"){
										txtsearch.setVisible(false);
										mainform.lblMessage.setText("");
										//int setDialog = JOptionPane.showConfirmDialog(container, "Are you sure ?",  "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
										int setDialog = Message.messageYesNo("Are you sure you want to delete\n" + txtcode.getText()+ ": "+ txtFirstname.getText().toString() + " "+txtMiddlename.getText().toString() + " "+ txtLastname.getText().toString()+ " ?");
										if(JOptionPane.YES_OPTION == setDialog){
											try{
												// unlock mode and delete 
												LockHandler.removeLockAndDelete(del_sql);
												//tableUser.setInsert(del_sql);
												//																												
												//tableUser.setQuery(DEFAULT_QUERY);
												int iColumn = 0;
												//if (xcode != "Code - Position"){
												ListSelectionModel selectionModel = tbl.getSelectionModel();
												selectionModel.setSelectionInterval(0, 0);
												//}//end if

												if (tbl.getRowCount() > 0){
													int getrow = tbl.getSelectedRow();
													Object getcode = tbl.getValueAt(getrow,0);
													Object getlast = tbl.getValueAt(getrow,2);
													Object getfirst = tbl.getValueAt(getrow,3);
													Object getmiddle = tbl.getValueAt(getrow,4);
//													txtcode.setText((String) getcode);
//													txtLastname.setText((String) getlast);
//													txtFirstname.setText((String) getfirst);
//													txtMiddlename.setText((String) getmiddle);
//													jcboPosition.setSelectedItem(tbl.getValueAt(getrow,5));
//													for (int i=0; i < jcboPosition.getItemCount();i++){
//														if (tbl.getValueAt(getrow,5).toString().equals(jcboPosition.getItemAt(i).toString().substring(5,jcboPosition.getItemAt(i).toString().length())) == true)
//														{//
//															jcboPosition.setSelectedIndex(i);
//															break;
//														}//if (tbl.getValueAt(getrow,5).toString().equals(jcboPosition.getItemAt(i).toString().substring(7,jcboPosition.getItemAt(i).toString().length())) == true)
//													}//for (int i=0; i < jcboPosition.getItemCount();i++){
//													if(tbl.getValueAt(getrow,6).toString().equalsIgnoreCase("Y") == true){
//														jcboactive.setSelectedItem("Yes");
//													}else{
//														jcboactive.setSelectedItem("No");
//													}
//													if(tbl.getValueAt(getrow,7).toString().equalsIgnoreCase("Y") == true){
//														jcbolog.setSelectedItem("Yes");
//													}else{
//														jcbolog.setSelectedItem("No");
//													}
													txtcode.setText("");
													txtpass.setText("");
													txtconfirm.setText("");
													txtLastname.setText("");
													txtFirstname.setText("");
													txtMiddlename.setText("");
													jcboPosition.setSelectedItem("Code-Position");
													jcboactive.setSelectedItem("Choose");
													jcbolog.setSelectedItem("Choose");
												}else{
													txtcode.setText(null);
													txtpass.setText(null);
													txtconfirm.setText(null);
													txtLastname.setText(null);
													txtFirstname.setText(null);
													txtMiddlename.setText(null);
													jcboPosition.setSelectedItem("Code-Position");
													jcboactive.setSelectedItem("Choose");
													jcbolog.setSelectedItem("Choose");
												}
												
												reload();
												tbl.getSelectedRow();
												tbl.grabFocus();

												Message.messageInfo(Message.messageDelete);
											}
											catch(SQLException se){
												System.out.println("*******" + se.getMessage());
											}
										}//if(setDialog == JOptionPane.YES_OPTION){
										tbl.grabFocus();
									}//if (txtLastname.getText() != null)

								} catch (SQLException e) {

									try {
										//refresh and remove lock mode
										tableUser.setQuery(DEFAULT_QUERY);
										reload();
										setCancel();
										LockHandler.LimitLockExceeded(container);

									} catch (IllegalStateException e1) {
										e1.printStackTrace();
									} catch (SQLException e1) {
										e1.printStackTrace();
									}
									e.printStackTrace();
								} finally {
									try {
										//remove lock indicator
										LockHandler.finallyLimitLockExceeded(container);
										tbl.requestFocus();
									} catch (IllegalStateException e) {
										e.printStackTrace();
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
							} 
						});// end SwingUtilities.invokeLater
					}//else if (dcaption == "Delete"){
				}//public void mouseClicked(MouseEvent e)
			});

			//EDIT BUTTON LISTENER
			jbtnedit.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String ecaption = jbtnedit.getText();
					String xcode = txtcode.getText().trim();
					String xpass = txtpass.getText().trim();
					String xconf = txtconfirm.getText().trim();
					String lname = " ";
					String fname = " ";
					String mname = " ";
					if	(txtLastname.getText().trim().length() == 0){
						lname = " ";
					}else{
						lname = txtLastname.getText();
					}
					if	(txtFirstname.getText().trim().length() == 0){
						fname = " ";
					}else{
						fname = txtFirstname.getText();
					}
					if	(txtMiddlename.getText().trim().length() == 0){
						mname = " ";
					}else{
						mname = txtMiddlename.getText();
					}
					int currentr = tbl.getSelectedRow();
					int currentc = tbl.getSelectedColumn();	
					String getcha = (String) jcboPosition.getSelectedItem();//txtLastname.getText();
					if (ecaption == "Edit"){
						if (txtcode.getText().toString().length() >= 1){
							currentr = tbl.getSelectedRow();
							currentc = tbl.getSelectedColumn();	

							// start indicating lock
							LockHandler.initializeLock(container);

							//wait for lock indicator to invoke
							SwingUtilities.invokeLater(new Runnable(){
								public void run() {
									try {
										//row level locking
										LockHandler.startLock(DBConnect.SelectForUpdate("user",
												"*",
												"userid='"+tbl.getValueAt(tbl.getSelectedRow(),0).toString()+"'"));
										//refresh
										tableUser.setQuery(DEFAULT_QUERY);
										reload();

										//populate records
										tbl.setRowSelectionInterval(tempR,tempR);
										tbl.setColumnSelectionInterval(tempC,tempC);
										setValue();

										tbl.grabFocus();	
										tbl.setEnabled(false);
										jbtnedit.setText("Update");
										jbtnedit.setMnemonic('U');
										jbtndelete.setText("Cancel");
										jbtndelete.setMnemonic('C');
										jbtnadd.setEnabled(false);
										txtpass.setEnabled(true);
										txtconfirm.setEnabled(true);
										txtLastname.setEnabled(true);
										txtFirstname.setEnabled(true);
										txtMiddlename.setEnabled(true);
										jcboPosition.setEnabled(true);
										jcboactive.setEnabled(true);
										jcbolog.setEnabled(true);
										txtsearch.setVisible(false);
										txtpass.grabFocus();
									}
									catch (SQLException e) {
										try {
											//refresh and remove lock mode
											tableUser.setQuery(DEFAULT_QUERY);
											reload();
											setCancel();
											LockHandler.LimitLockExceeded(container);

										} catch (IllegalStateException e1) {
											e1.printStackTrace();
										} catch (SQLException e1) {
											e1.printStackTrace();
										}
										e.printStackTrace();
									} finally {
										try {
											//remove lock indicator
											LockHandler.finallyLimitLockExceeded(container);
										} catch (IllegalStateException e) {
											e.printStackTrace();
										} catch (SQLException e) {
											e.printStackTrace();
										}
									}
								} 
							});
						}
					}
					else if (ecaption == "Update"){
						try{

							String e_sql = 
								DBConnect.Update("user",
										"password = '"+DBConnect.clean(xpass)+"', " +
										"lname = '"+DBConnect.clean(lname)+"', " +
										"fname = '"+DBConnect.clean(fname)+"', " +
										"mname = '"+DBConnect.clean(mname)+"', " +
										"position = '"+DBConnect.clean(cbopost)+"', " +
										"active = '"+DBConnect.clean(cboactive)+"', " +
										"loggedin = '"+DBConnect.clean(cbolog)+"' ",
										"userid = '"+DBConnect.clean(xcode)+"'" );

							if(xpass.length() == 0){
								Message.messageError("Invalid input for password field.");
								txtpass.grabFocus();
							}
							else if(xconf.equals(xpass) == false){
								Message.messageError("Password Confirm failed.");
								txtpass.grabFocus();
							}
							else if(lname.length() == 0)
							{
								Message.messageError("Invalid input for lastname field.");
								txtLastname.grabFocus();
							}
							else if(fname.length() == 0){
								Message.messageError("Invalid input for firstname field.");
								txtFirstname.grabFocus();
							}
							else if(mname.length() <= 0){
								Message.messageError("Invalid input for middlename field.");
								txtMiddlename.grabFocus();
							}
							else if(jcboPosition.getSelectedIndex() == 0){
								Message.messageError("Invalid input for position field.");
								jcboPosition.grabFocus();
							}
							else if(jcboactive.getSelectedIndex() == 0){
								Message.messageError("Invalid input for active field.");
								jcboactive.grabFocus();
							}
							else{
								//unlock mode then update
								LockHandler.removeLockAndUpdate(e_sql);

								tableUser.setQuery(DEFAULT_QUERY);
								tbl.setEnabled(true);
								jbtnadd.setText("Add");
								jbtndelete.setText("Delete");
								jbtndelete.setMnemonic('D');
								jbtnedit.setText("Edit");
								jbtnedit.setMnemonic('E');
								jbtnadd.setEnabled(true);
								ListSelectionModel selectionModel = tbl.getSelectionModel();
								selectionModel.setSelectionInterval(0, 0);
								reload();
								Object getcode = tbl.getValueAt(currentr,0);
								Object getlast = tbl.getValueAt(currentr,2);
								Object getfirst = tbl.getValueAt(currentr,3);
								Object getmiddle = tbl.getValueAt(currentr,4);
								txtcode.setText((String) getcode);
								txtLastname.setText((String) getlast);
								txtFirstname.setText((String) getfirst);
								txtMiddlename.setText((String) getmiddle);
								jcboPosition.setSelectedItem(tbl.getValueAt(currentr,4));
								if(tbl.getValueAt(currentr,6).toString().equalsIgnoreCase("Y") == true){
									jcboactive.setSelectedItem("Yes");
								}else{
									jcboactive.setSelectedItem("No");
								}
								if(tbl.getValueAt(currentr,7).toString().equalsIgnoreCase("Y") == true){
									jcbolog.setSelectedItem("Yes");
								}else{
									jcbolog.setSelectedItem("No");
								}
								//tbl.getSelectedRow();
								tbl.grabFocus();
								txtcode.setEnabled(false);
								txtpass.setEnabled(false);
								txtconfirm.setEnabled(false);
								txtLastname.setEnabled(false);
								txtFirstname.setEnabled(false);
								txtMiddlename.setEnabled(false);
								jcboPosition.setEnabled(false);
								txtsearch.setVisible(false);
								jcboactive.setEnabled(false);
								jcbolog.setEnabled(false);

								Message.messageInfo(Message.messageUpdate);

								try {
									tbl.setRowSelectionInterval(currentr,currentr);
									tbl.setColumnSelectionInterval(currentc,currentc);
								}
								catch(Exception e3){
									System.out.println(e3.getMessage());
								}    
							}

						}
						catch(SQLException se){
							System.out.println(se.getMessage());
						}
					}
				}//public void mouseClicked(MouseEvent e)
			});

			//Add Key Listener
			tbl.addKeyListener(new KeyAdapter(){
				public void keyReleased(KeyEvent e){
					//					int eventKey = e.getKeyCode();
					//					System.out.println("hello2 "+eventKey);
					tempR = tbl.getSelectedRow();
					tempC = tbl.getSelectedColumn();
					setValue();
				}
			});        


			//table mouse click listener
			tbl.addMouseListener(
					new MouseAdapter()
					{
						public void mouseClicked(MouseEvent e)	
						{
							txtsearch.setVisible(false);
							txtsearch.setText(null);
							if (jbtndelete.getText().equals("Cancel") == false){
								//get last selected cell
								tempR = tbl.getSelectedRow();
								tempC = tbl.getSelectedColumn();

								int getrow = tbl.getSelectedRow();
								Object getcode = tbl.getValueAt(getrow,0);
								Object getpass = tbl.getValueAt(getrow,1);
								Object getlast = tbl.getValueAt(getrow,2);
								Object getfirst = tbl.getValueAt(getrow,3);
								Object getmiddle = tbl.getValueAt(getrow,4);
								txtcode.setText((String) getcode);
								txtpass.setText((String) getpass);
								txtconfirm.setText((String) getpass);
								txtLastname.setText((String) getlast);
								txtFirstname.setText((String) getfirst);
								txtMiddlename.setText((String) getmiddle);
								for (int i=0; i < jcboPosition.getItemCount();i++){
									if (jcboPosition.getItemAt(i).toString().contains((CharSequence) tbl.getValueAt(getrow,5)) == true){
										jcboPosition.setSelectedIndex(i);
										break;
									}//end if
								}//for (int i=0; i < jcboPosition.getItemCount();i++){
								if(tbl.getValueAt(getrow,6).toString().equalsIgnoreCase("Y") == true){
									jcboactive.setSelectedItem("Yes");
								}else{
									jcboactive.setSelectedItem("No");
								}
								if(tbl.getValueAt(getrow,7).toString().equalsIgnoreCase("Y") == true){
									jcbolog.setSelectedItem("Yes");
								}else{
									jcbolog.setSelectedItem("No");
								}
							}//if (jbtndelete.getLabel() != "Cancel")
						}//public void mouseClicked(MouseEvent e)
					}//new MouseAdapter()
			);//tbl.addMouseListener(

			//table key listener
			tbl.addKeyListener(new KeyAdapter(){
				public void keyPressed(KeyEvent e){
					try{
						int eventKey = e.getKeyChar();
						int currentr = tbl.getSelectedRow();
						int currentc = tbl.getSelectedColumn();
						InputMap IMP = tbl.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
						KeyStroke ent = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
						IMP.put(ent, "none");
						if (eventKey == 10){
							if (jbtndelete.getText().toString().equals("Delete") == true){
								//get last selected cell
								tempR = tbl.getSelectedRow();
								tempC = tbl.getSelectedColumn();

								int getrow = tbl.getSelectedRow();
								Object getcode = tbl.getValueAt(getrow,0);
								Object getpass = tbl.getValueAt(getrow,1);
								Object getlast = tbl.getValueAt(getrow,2);
								Object getfirst = tbl.getValueAt(getrow,3);
								Object getmiddle = tbl.getValueAt(getrow,4);
								txtcode.setText((String) getcode);
								txtpass.setText((String) getpass);
								txtconfirm.setText((String) getpass);
								txtLastname.setText((String) getlast);
								txtFirstname.setText((String) getfirst);
								txtMiddlename.setText((String) getmiddle);
								for (int i=0; i < jcboPosition.getItemCount();i++){
									if (jcboPosition.getItemAt(i).toString().contains((CharSequence) tbl.getValueAt(getrow,5)) == true){
										jcboPosition.setSelectedIndex(i);
										break;
									}//end if
								}//for (int i=0; i < jcboPosition.getItemCount();i++){
								if(tbl.getValueAt(getrow,6).toString().equalsIgnoreCase("Y") == true){
									jcboactive.setSelectedItem("Yes");
								}else{
									jcboactive.setSelectedItem("No");
								}
								if(tbl.getValueAt(getrow,7).toString().equalsIgnoreCase("Y") == true){
									jcbolog.setSelectedItem("Yes");
								}else{
									jcbolog.setSelectedItem("No");
								} 
							}

							tbl.setRowSelectionInterval(currentr,currentr);
							tbl.setColumnSelectionInterval(currentc,currentc);
						}
						if ((e.getKeyCode() >= 65) && (e.getKeyCode() <= 105) && (e.isAltDown() != true)){
							txtsearch.setVisible(true);
							txtsearch.grabFocus();
							//Object text = e.getKeyChar();
							txtsearch.setText("Lastname:" + " " + e.getKeyChar());
						}}
					catch(Exception ef){
						System.out.println("Error sa JTable Key Listener; Pakicheck n lang!!");
					}

				}//public void keyPressed(KeyEvent e){
			}//new KeyAdapter()
			);//tbl.addKeyListener(

			//txtsearch key listener
			txtsearch.addKeyListener(new KeyAdapter(){
				public void keyReleased(KeyEvent e){
					int eventKey = e.getKeyChar();
					if(eventKey == 8){
						if( txtsearch.getText().length() == 9){
							txtsearch.setVisible(false);
							Rectangle bounds = tbl.getCellRect(tbl.getSelectedRow(),0,true);
							scrollPane.getViewport().setViewPosition(bounds.getLocation());
							scrollPane.getViewport().setOpaque(false);
							scrollPane.getViewport().setView(tbl);

						}	
					}
					try{

						if (txtsearch.getText().length() == 10) {
							txtsearch.setVisible(false);
						}//if (txtsearch.getText().length() == 0) {
						for(int col = 0; col < tbl.getRowCount(); col++){
							String getcode = tbl.getValueAt(col,2).toString();
							String[] getcode4 = txtsearch.getText().replaceFirst(" ", "-").split("-");
							String getcode2 = getcode4[1];
							if ((getcode.toUpperCase().equalsIgnoreCase(getcode2.toUpperCase())) && (txtsearch.getText().length() != 0)){

								ListSelectionModel selectionModel = tbl.getSelectionModel();
								selectionModel.removeSelectionInterval(0,tbl.getRowCount());
								selectionModel.addSelectionInterval(col,col);


								col = tbl.getRowCount();
								int getrow = tbl.getSelectedRow();
								Object getcode3 = tbl.getValueAt(getrow,0);
								Object getpass = tbl.getValueAt(getrow,1);
								Object getlast = tbl.getValueAt(getrow,2);
								Object getfirst = tbl.getValueAt(getrow,3);
								Object getmiddle = tbl.getValueAt(getrow,4);
								txtcode.setText((String) getcode3);
								txtpass.setText((String) getpass);
								txtconfirm.setText((String) getpass);
								txtLastname.setText((String) getlast);
								txtFirstname.setText((String) getfirst);
								txtMiddlename.setText((String) getmiddle);
								for (int i=0; i < jcboPosition.getItemCount();i++){
									if (jcboPosition.getItemAt(i).toString().contains((CharSequence) tbl.getValueAt(getrow,5)) == true){
										jcboPosition.setSelectedIndex(i);
										break;
									}//end if
								}//for (int i=0; i < jcboPosition.getItemCount();i++){
								if(tbl.getValueAt(getrow,6).toString().equalsIgnoreCase("Y") == true){
									jcboactive.setSelectedItem("Yes");
								}else{
									jcboactive.setSelectedItem("No");
								}
								if(tbl.getValueAt(getrow,7).toString().equalsIgnoreCase("Y") == true){
									jcbolog.setSelectedItem("Yes");
								}else{
									jcbolog.setSelectedItem("No");
								}
								Rectangle bounds = tbl.getCellRect(tbl.getSelectedRow(),0,true);
								scrollPane.getViewport().setViewPosition(bounds.getLocation());
								scrollPane.getViewport().setOpaque(false);
								scrollPane.getViewport().setView(tbl);

							}//if ((getcode.toUpperCase().equals(getcode2.toUpperCase())) && (txtsearch.getText().length() != 0)){
						}//for(int col = 0; col < tbl.getRowCount(); col++){
					}//try{
					catch(Exception xe){
						//System.out.println(xe.getMessage());
					}//catch(Exception xe){
				}//public void keyReleased(KeyEvent e){
			}//new KeyAdapter(){
			);//txtsearch.addKeyListener(

			setSize(500,450);
			//setLocation((790 + 500)/5,((540 - 420)/2));
			setVisible(true);

		}//try
		catch ( ClassNotFoundException e ) 
		{
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("UserMaint", "UserMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch ( SQLException e )
		{
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("UserMaint", "UserMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch(Exception e){
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("UserMaint", "UserMaint", traceWriter.toString());        
			dispose();
		}

	}


	private class TestRenderer extends DefaultTableCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			/*if (column == 5)
				setHorizontalAlignment( LEFT );
			else
				setHorizontalAlignment( LEFT );*/
			setBorder(BorderFactory.createLineBorder(getBackground(), 1));


			return this;
		}
	}

	private void setCancel() {
		try {
			//rollback row level locking
			LockHandler.removeLock();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		tbl.setEnabled(true);
		jbtnedit.setText("Edit");
		jbtnadd.setText("Add");
		jbtnadd.setMnemonic('A');
		jbtndelete.setText("Delete");
		jbtndelete.setMnemonic('D');
		jbtnadd.setEnabled(true);
		jbtnedit.setEnabled(true);
		jcboactive.setEnabled(false);
		jcbolog.setEnabled(false);
		//Select row in table 
		if (tbl.getSelectedRow() == -1){
			ListSelectionModel selectionModel = tbl.getSelectionModel();
			selectionModel.setSelectionInterval(0, 0);
		}//end if
		if (tbl.getRowCount() >= 1){
			//rollbacck current cell
			tbl.setRowSelectionInterval(tempR,tempR);
			tbl.setColumnSelectionInterval(tempC,tempC);
			setValue();

		}else{
			txtcode.setText(null);
			txtpass.setText(null);
			txtconfirm.setText(null);
			txtLastname.setText(null);
			txtFirstname.setText(null);
			txtMiddlename.setText(null);
			jcboPosition.setSelectedItem("Code-Position");
			jcboactive.setSelectedItem("Choose");
			jcbolog.setSelectedItem("Choose");
		}
		tbl.getSelectedRow();
		tbl.grabFocus();
		txtcode.setEnabled(false);
		txtpass.setEnabled(false);
		txtconfirm.setEnabled(false);
		txtLastname.setEnabled(false);
		txtFirstname.setEnabled(false);
		txtMiddlename.setEnabled(false);
		jcboPosition.setEnabled(false);
	}

	private void setValue() {
		if (tbl.getSelectedRow() >= 0) {
			int getrow = tbl.getSelectedRow();
			Object getcode = tbl.getValueAt(getrow,0);
			Object getpass = tbl.getValueAt(getrow,1);
			Object getlast = tbl.getValueAt(getrow,2);
			Object getfirst = tbl.getValueAt(getrow,3);
			Object getmiddle = tbl.getValueAt(getrow,4);
			txtcode.setText((String) getcode);
			txtpass.setText((String) getpass);
			txtconfirm.setText((String) getpass);
			txtLastname.setText((String) getlast);
			txtFirstname.setText((String) getfirst);
			txtMiddlename.setText((String) getmiddle);
			jcboPosition.setSelectedItem(tbl.getValueAt(getrow,5));
			//		txtcode.setText(tbl.getValueAt(tbl.getSelectedRow(),0).toString());
			//		txtpass.setText(tbl.getValueAt(tbl.getSelectedRow(),1).toString());
			//		txtconfirm.setText(tbl.getValueAt(tbl.getSelectedRow(),1).toString());
			//		txtLastname.setText(tbl.getValueAt(tbl.getSelectedRow(),2).toString());
			//		txtFirstname.setText(tbl.getValueAt(tbl.getSelectedRow(),3).toString());
			//		txtMiddlename.setText(tbl.getValueAt(tbl.getSelectedRow(),4).toString());
			//		jcboPosition.setSelectedItem(tbl.getValueAt(getrow,5));

			for (int i=0; i < jcboPosition.getItemCount();i++){
				if (jcboPosition.getItemAt(i).toString().contains((CharSequence) tbl.getValueAt(getrow,5)) == true){
					jcboPosition.setSelectedIndex(i);
					break;
				}//end if
			}//for (int i=0; i < jcboPosition.getItemCount();i++){
			if(tbl.getValueAt(getrow,6).toString().equalsIgnoreCase("Y") == true){
				jcboactive.setSelectedItem("Yes");
			}else{
				jcboactive.setSelectedItem("No");
			}
			if(tbl.getValueAt(getrow,7).toString().equalsIgnoreCase("Y") == true){
				jcbolog.setSelectedItem("Yes");
			}else{
				jcbolog.setSelectedItem("No");
			}
		}//ENDof if (tbl.getSelectedRow() >= 0) {
	}

	private void reload(){
		try {
			tableUser.setQuery(DEFAULT_QUERY);
			tbl.getColumnModel().getColumn(0).setMaxWidth(0);
			tbl.getColumnModel().getColumn(0).setMinWidth(0);
			tbl.getColumnModel().getColumn(1).setMaxWidth(0);
			tbl.getColumnModel().getColumn(1).setMinWidth(0);
			tbl.getColumnModel().getColumn(2).setMaxWidth(0);
			tbl.getColumnModel().getColumn(2).setMinWidth(0);
			tbl.getColumnModel().getColumn(3).setMaxWidth(0);
			tbl.getColumnModel().getColumn(3).setMinWidth(0);
			tbl.getColumnModel().getColumn(4).setMaxWidth(0);
			tbl.getColumnModel().getColumn(4).setMinWidth(0);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(0);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(0);
			tbl.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(100);
			tbl.getTableHeader().getColumnModel().getColumn(2).setMinWidth(100);
			tbl.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(100);
			tbl.getTableHeader().getColumnModel().getColumn(3).setMinWidth(100);
			tbl.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(100);
			tbl.getTableHeader().getColumnModel().getColumn(4).setMinWidth(100);
			tbl.getColumnModel().getColumn(5).setMaxWidth(150);
			tbl.getColumnModel().getColumn(5).setMinWidth(150);

			//set column header width (to avoid the ... sign)
			tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(0);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(0);
			tbl.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(150);
			tbl.getTableHeader().getColumnModel().getColumn(5).setMinWidth(150);
			tbl.getColumnModel().getColumn(2).setHeaderValue("Lastname");
			tbl.getColumnModel().getColumn(3).setHeaderValue("Firstname");
			tbl.getColumnModel().getColumn(4).setHeaderValue("Middlename");
			tbl.getColumnModel().getColumn(5).setHeaderValue("Position");
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}  //public class user_gui extends JFrame()
