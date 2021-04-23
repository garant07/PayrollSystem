package maintenance.bin.src.MaintCompany;

/**
 * Author : Norberto L. Silva
 * Date   : February 27, 2012
 * Company: Applied Ideas, Inc 
 * Program: Company Maintenance ==-> Payroll System
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import maintenance.bin.src.core.LockHandler;
import maintenance.bin.src.core.mainform;

public class CompanyMaint extends JInternalFrame {
	private static final long serialVersionUID = 1L;
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	static final String DEFAULT_QUERY = 
		DBConnect.Select("company ",
						 "company.co_name, "+				//0
						 "company.co_code, "+				//1
						 "company.co_initial, "+			//2
						 "company.co_addr1, "+				//3
						 "company.co_addr2, "+				//4
						 "company.co_contact_person, "+		//5
						 "company.co_contact_number, "+		//6
						 "company.co_email_address, "+		//7
						 "company.co_fax_no, "+				//8
						 "company.co_sss_no, "+				//9
						 "company.co_tin_no, "+				//10
						 "company.co_pagibig_no, "+			//11
						 "company.co_philhealth_no, "+		//12
						 "company.co_extra_pay_holiday ",	//13
						 null, "co_name");

	public static int subMain1 = 0;
	public static int subMain2 = 0;

	private ResultSetTableModel tableCompany;
	Container container;
	String VtxtUsed;
	String cboExtraPayHoliday;
	Message msg = new Message();
	JTable tbl;

	//variable current cell selected
	private int tempR;
	private int tempC;
	String curRecord = "";

	//variable for buttons
	static JButton jbtnadd;
	static JButton jbtnedit;
	static JButton jbtndelete;
	static JButton jbtnpaycomp;
	static JButton jbtnannul;

	//variable for user entry
	JScrollPane scrollPane;
	private MyField txtco_code = new MyField(true,4);
	private MyField txtco_name = new MyField(true,50);
	private MyField txtco_initial = new MyField(true,10);
	private MyField txtco_addr1 = new MyField(true,50);
	private MyField txtco_addr2 = new MyField(true,50);
	private MyField txtco_contact_person = new MyField(true,30);
	private MyField txtco_contact_number = new MyField(true,15);
	private MyField txtco_email_address = new MyField(true,30);
	private MyField txtco_fax_no = new MyField(true,15);
	private MyField txtco_sss_no = new MyField(true,15);
	private MyField txtco_tin_no = new MyField(true,15);
	private MyField txtco_pagibig_no = new MyField(true,15);
	private MyField txtco_philhealth_no = new MyField(true,15);
	private JComboBox jcbco_extra_pay_holiday = new JComboBox();
	private JTextField txtsearch = new JTextField();

	SimpleDateFormat dteFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat strFormat = new SimpleDateFormat("MMMM dd, yyyy");

	public CompanyMaint() {
		//super ("Item Maintenance", resizable, close, maximize, iconifiable);
		super ("Company Maintenance", false, true, false, false);
		this.setName("Custmaint");
		this.setResizable(false);

		this.addInternalFrameListener(new InternalFrameListener(){
			public void internalFrameActivated(InternalFrameEvent arg0) {
				if (subMain1 == 1) {
					try {
						CompanyPayCompMaint.iFrame.setSelected(true);
					}
					catch (PropertyVetoException e) {}
				}
				else if (subMain2 == 1) {
					try {
						CompanyAnnualtnMaint.iFrame.setSelected(true);
					}
					catch (PropertyVetoException e) {}
				}
				mainform.tree.setEnabled(false);
			}
			public void internalFrameClosed(InternalFrameEvent arg0) {}
			public void internalFrameClosing(InternalFrameEvent arg0) {
				if (subMain1 == 1) {
					CompanyPayCompMaint.iFrame.dispose();
				}
				if (subMain2 == 1) {
					CompanyAnnualtnMaint.iFrame.dispose();
				}
				mainform.tree.setEnabled(true);
			}
			public void internalFrameDeactivated(InternalFrameEvent arg0) {}
			public void internalFrameDeiconified(InternalFrameEvent arg0) {
				if (subMain1 == 1) {
					try {
						CompanyPayCompMaint.iFrame.setIcon(false);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
				if (subMain2 == 1) {
					try {
						CompanyAnnualtnMaint.iFrame.setIcon(false);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
			}
			public void internalFrameIconified(InternalFrameEvent arg0) {
				if (subMain1 == 1) {
					try {
						CompanyPayCompMaint.iFrame.setIcon(true);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
				if (subMain2 == 1) {
					try {
						CompanyAnnualtnMaint.iFrame.setIcon(true);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
			}
			public void internalFrameOpened(InternalFrameEvent arg0) {}
		});

		try {
			tableCompany = new ResultSetTableModel( DEFAULT_QUERY );

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
			jbtnpaycomp = new JButton("Pay Comp");
			jbtnpaycomp.setMnemonic('P');
			jbtnpaycomp.setToolTipText("Payroll Computation");
			jbtnannul = new JButton("Annual");
			jbtnannul.setMnemonic('N');
			jbtnannul.setToolTipText("Annualization Parameter");
			
			JLabel lblco_code = new JLabel("Code");
			JLabel lblco_name = new JLabel("Name");
			JLabel lblco_initial = new JLabel("Initial");
			JLabel lblco_address = new JLabel("Address");
			JLabel lblco_contact_person = new JLabel("Contact Person");
			JLabel lblco_contact_number = new JLabel("Contact Number");
			JLabel lblco_fax_no = new JLabel("Fax No.");
			JLabel lblco_email_address = new JLabel("EMail");
			JLabel lblco_sss_no = new JLabel("SSS No.");
			JLabel lblco_tin_no = new JLabel("TIN No.");
			JLabel lblco_pagibig_no = new JLabel("Pag-Ibig No.");
			JLabel lblco_philhealth_no = new JLabel("PhilHealth No.");
			JLabel lblco_extra_pay_holiday = new JLabel("Extra PayHoliday");

			tbl = new JTable(tableCompany);

			txtco_code.setCapital(true);

			TableCellRenderer renderer = new TestRenderer();
			tbl.setDefaultRenderer(Object.class, renderer);
			scrollPane = new JScrollPane(tbl);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			tbl.setSelectionMode(0);

			tableCompany.resultSet = tableCompany.statement.executeQuery(DEFAULT_QUERY);
			int width0 = 143;
			int width1 = 143;
			int width2 = 143;
			int width3 = 100;
			TableColumn col0 = tbl.getColumnModel().getColumn(0);
			TableColumn col1 = tbl.getColumnModel().getColumn(1);
			TableColumn col2 = tbl.getColumnModel().getColumn(2);
			TableColumn col3 = tbl.getColumnModel().getColumn(3);

			col0.setMaxWidth(width0);
			col1.setMaxWidth(width1);
			col2.setMaxWidth(width2);
			col3.setMaxWidth(width3);
			col0.setMinWidth(width0);
			col1.setMinWidth(width1);
			col2.setMinWidth(width2);
			col3.setMinWidth(width3);

			//set column width
			reload();

			tbl.getTableHeader().setResizingAllowed(false);
			tbl.getTableHeader().setMaximumSize(new Dimension(23,56));
			tbl.setPreferredScrollableViewportSize(new Dimension(430,125));

			jcbco_extra_pay_holiday.setFont(new Font("",Font.PLAIN, 12));
			jcbco_extra_pay_holiday.setBackground(Color.white);
			jcbco_extra_pay_holiday.addItem("Choose");
			jcbco_extra_pay_holiday.addItem("Yes");
			jcbco_extra_pay_holiday.addItem("No");

			tbl.getTableHeader().setReorderingAllowed(false); //disabled column dragging

			//set display size
			scrollPane.setPreferredSize(new Dimension(600,170));
			jpan.setBounds(10,20,600,180);

			lblco_code.setBounds(28,100,100,280);
			lblco_name.setBounds(28,130,100,280);
			lblco_initial.setBounds(28,160,100,280);
			lblco_address.setBounds(28,190,100,280);
			lblco_contact_person.setBounds(28,250,100,280);
			lblco_contact_number.setBounds(28,280,210,280);
			lblco_fax_no.setBounds(320,280,210,280);
			lblco_email_address.setBounds(28,310,210,280);
			lblco_sss_no.setBounds(28,340,210,280);
			lblco_tin_no.setBounds(320,340,210,280);
			lblco_pagibig_no.setBounds(28,370,210,280);
			lblco_philhealth_no.setBounds(320,370,210,280);
			lblco_extra_pay_holiday.setBounds(28,400,210,280);

			txtco_code.setBounds(120,230,50,20);
			txtco_code.setDisabledTextColor(new Color(139,113,113));
			txtco_name.setBounds(120,260,240,20);
			txtco_name.setDisabledTextColor(new Color(139,113,113));
			txtco_initial.setBounds(120,290,100,20);
			txtco_initial.setDisabledTextColor(new Color(139,113,113));
			txtco_addr1.setBounds(120,320,305,20);
			txtco_addr1.setDisabledTextColor(new Color(139,113,113));
			txtco_addr2.setBounds(120,350,305,20);
			txtco_addr2.setDisabledTextColor(new Color(139,113,113));
			txtco_contact_person.setBounds(120,380,300,20);
			txtco_contact_person.setDisabledTextColor(new Color(139,113,113));
			txtco_contact_number.setBounds(120,410,140,20);
			txtco_contact_number.setDisabledTextColor(new Color(139,113,113));
			txtco_fax_no.setBounds(410,410,140,20);
			txtco_fax_no.setDisabledTextColor(new Color(139,113,113));
			txtco_email_address.setBounds(120,440,300,20);
			txtco_email_address.setDisabledTextColor(new Color(139,113,113));
			txtco_sss_no.setBounds(120,470,140,20);
			txtco_sss_no.setDisabledTextColor(new Color(139,113,113));
			txtco_tin_no.setBounds(410,470,140,20);
			txtco_tin_no.setDisabledTextColor(new Color(139,113,113));
			txtco_pagibig_no.setBounds(120,500,140,20);
			txtco_pagibig_no.setDisabledTextColor(new Color(139,113,113));
			txtco_philhealth_no.setBounds(410,500,140,20);
			txtco_philhealth_no.setDisabledTextColor(new Color(139,113,113));
			jcbco_extra_pay_holiday.setBounds(120,530,90,20);
			
			jbtnadd.setBounds(30,200,100,22);
			jbtnedit.setBounds(145,200,100,22);
			jbtndelete.setBounds(260,200,100,22);
			jbtnpaycomp.setBounds(375,200,100,22);
			jbtnannul.setBounds(490,200,100,22);

			txtsearch.setBounds(10,3,400,20);
			txtsearch.setBackground(new Color(255,255,225));
			txtsearch.setVisible(false);
			txtsearch.setBorder(BorderFactory.createEmptyBorder());

			jpan.add(scrollPane, BorderLayout.CENTER);

			if (tbl.getRowCount() >= 1){
				tbl.setRowSelectionInterval(0,0);
				tbl.setColumnSelectionInterval(0,0);
				setFieldValue();
			}

			//add objects to container
			container.add(jpan);
			container.add(lblco_code);
			container.add(txtco_code);
			container.add(lblco_initial);
			container.add(txtco_initial);
			container.add(lblco_name);
			container.add(txtco_name);
			container.add(lblco_address);
			container.add(txtco_addr1);
			container.add(txtco_addr2);
			container.add(lblco_contact_person);
			container.add(txtco_contact_person);
			container.add(lblco_contact_number);
			container.add(txtco_contact_number);
			container.add(lblco_fax_no);
			container.add(txtco_fax_no);
			container.add(lblco_email_address);
			container.add(txtco_email_address);
			container.add(lblco_sss_no);
			container.add(txtco_sss_no);
			container.add(lblco_tin_no);
			container.add(txtco_tin_no);
			container.add(lblco_pagibig_no);
			container.add(txtco_pagibig_no);
			container.add(lblco_philhealth_no);
			container.add(txtco_philhealth_no);
			container.add(lblco_extra_pay_holiday);
			container.add(jcbco_extra_pay_holiday);
			container.add(jbtnadd);
			container.add(jbtnedit);
			container.add(jbtndelete);        
			container.add(jbtnpaycomp);        
			container.add(jbtnannul);        
			container.add(txtsearch);

			//Disabled text fields
			disableFields();

			jcbco_extra_pay_holiday.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbco_extra_pay_holiday.getSelectedItem().toString().equalsIgnoreCase("Yes") == true) {
						cboExtraPayHoliday = "Y";
					}
					else {
						cboExtraPayHoliday = "N";
					}
				}
			});

			//add button listener
			jbtnadd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String caption = jbtnadd.getText();
					String xcode = txtco_code.getText().trim();

					if (caption == "Add"){
						ListSelectionModel selectionModel = tbl.getSelectionModel();
						selectionModel.clearSelection();
						tbl.setEnabled(false);

						enableFields();

						clearFields();
						txtco_code.grabFocus();

						jbtnadd.setText("Save");
						jbtnadd.setMnemonic('S');
						jbtndelete.setText("Cancel");
						jbtndelete.setMnemonic('C');
						jbtnedit.setEnabled(false);
					}
					else if (caption == "Save") {
						int iColumn = 0;
						String save_sql = DBConnect.Insert("company",
								"co_code,co_name,co_initial,co_addr1,co_addr2,co_contact_person,co_contact_number,co_email_address,"+
								"co_fax_no,co_sss_no,co_tin_no,co_pagibig_no,co_philhealth_no,co_extra_pay_holiday",
								"'"+DBConnect.clean(xcode)+"', " +
								"'"+DBConnect.clean(txtco_name.getText().trim())+"', " +
								"'"+DBConnect.clean(txtco_initial.getText().trim())+"', " +
								"'"+DBConnect.clean(txtco_addr1.getText().trim())+"', " +
								"'"+DBConnect.clean(txtco_addr2.getText().trim())+"', " +
								"'"+DBConnect.clean(txtco_contact_person.getText().trim())+"', " +
								"'"+DBConnect.clean(txtco_contact_number.getText().trim())+"', " +
								"'"+DBConnect.clean(txtco_email_address.getText().trim())+"', " +
								"'"+DBConnect.clean(txtco_fax_no.getText().trim())+"', " +
								"'"+DBConnect.clean(txtco_sss_no.getText().trim())+"', " +
								"'"+DBConnect.clean(txtco_tin_no.getText().trim())+"', " +
								"'"+DBConnect.clean(txtco_pagibig_no.getText().trim())+"', " +
								"'"+DBConnect.clean(txtco_philhealth_no.getText().trim())+"', " +
								"'"+DBConnect.clean(cboExtraPayHoliday)+"' "
						);

						tbl.setEnabled(false);
						try {
							if (xcode.length() == 0) {   
								Message.messageError("Invalid input for Company Code.");
								txtco_code.grabFocus();
							}
							else if(txtco_name.getText().trim().length() == 0) {
								Message.messageError("Invalid input for company name field.");
								txtco_name.grabFocus();
							}
							else if(jcbco_extra_pay_holiday.getSelectedIndex() == 0){
								Message.messageError("Invalid input for status field.");
								jcbco_extra_pay_holiday.grabFocus();
							}
							else{
								tableCompany.setInsert(save_sql);

								tableCompany.setQuery(DEFAULT_QUERY);
								tbl.getColumnModel().getColumn(iColumn).setMaxWidth(0);
								tbl.getColumnModel().getColumn(iColumn).setMinWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(iColumn).setMaxWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(iColumn).setMinWidth(0);
								tbl.getColumnModel().getColumn(0).setMaxWidth(147);
								tbl.getColumnModel().getColumn(0).setMinWidth(147);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(194);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(194);
								tbl.getColumnModel().getColumn(1).setMaxWidth(147);
								tbl.getColumnModel().getColumn(1).setMinWidth(147);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(50);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(50);
								tbl.getColumnModel().getColumn(2).setMaxWidth(147);
								tbl.getColumnModel().getColumn(2).setMinWidth(147);
								tbl.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(80);
								tbl.getTableHeader().getColumnModel().getColumn(2).setMinWidth(80);
								tbl.getColumnModel().getColumn(3).setMaxWidth(143);
								tbl.getColumnModel().getColumn(3).setMinWidth(143);
								tbl.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(264);
								tbl.getTableHeader().getColumnModel().getColumn(3).setMinWidth(264);
								tbl.getColumnModel().getColumn(0).setHeaderValue("Company Name");
								tbl.getColumnModel().getColumn(1).setHeaderValue("Code");
								tbl.getColumnModel().getColumn(2).setHeaderValue("Initial");
								tbl.getColumnModel().getColumn(3).setHeaderValue("Address");

								curRecord = txtco_code.getText().trim();

								clearFields();

								Message.messageInfo(Message.messageAdd);								
								//tbl.scrollRectToVisible(tbl.getCellRect(tbl.getRowCount()-1, tbl.getColumnCount(), true));
								tbl.scrollRectToVisible(tbl.getCellRect(setCurRecord(), tbl.getColumnCount(), true));								
								txtco_code.grabFocus();
							}
						}
						catch(SQLException se){
							try{
								tableCompany.setQuery(DEFAULT_QUERY);
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
									String xcode = txtco_code.getText();

									//row level locking
									LockHandler.startLock(DBConnect.SelectForUpdate("company",
											"*",
											"co_code='"+DBConnect.clean(tbl.getValueAt(tbl.getSelectedRow(),1).toString())+"'"));

									String del_sql = DBConnect.delete("company","co_code = '"+DBConnect.clean(xcode)+"'");
									//System.out.println(del_sql);
									
									txtsearch.setVisible(false);
									mainform.lblMessage.setText("");
									//int setDialog = JOptionPane.showConfirmDialog(container, "Are you sure ?",  "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
									int setDialog = Message.messageYesNo("Are you sure you want to delete\n" + txtco_code.getText()+ ": "+ txtco_initial.getText().toString() + " "+txtco_addr1.getText().toString() + " "+ txtco_name.getText().toString()+ " ?");
									if (JOptionPane.YES_OPTION == setDialog) {
										try {
											// unlock mode and delete 
											LockHandler.removeLockAndDelete(del_sql);
											//tableCompany.setInsert(del_sql);
											//																												
											//tableCompany.setQuery(DEFAULT_QUERY);
											ListSelectionModel selectionModel = tbl.getSelectionModel();
											selectionModel.setSelectionInterval(0, 0);
											//}//end if

											clearFields();

											reload();
											tbl.getSelectedRow();
											tbl.grabFocus();

											Message.messageInfo(Message.messageDelete);
										}
										catch(SQLException se) {
											System.out.println("*******" + se.getMessage());
										}
									}//if(setDialog == JOptionPane.YES_OPTION){
									tbl.grabFocus();

								} catch (SQLException e) {

									try {
										//refresh and remove lock mode
										tableCompany.setQuery(DEFAULT_QUERY);
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
			jbtnedit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					String ecaption = jbtnedit.getText();
					String xcode = txtco_code.getText().trim();
					int currentr = tbl.getSelectedRow();
					int currentc = tbl.getSelectedColumn();	
					if (ecaption == "Edit"){
						if (txtco_code.getText().toString().length() >= 1){
							currentr = tbl.getSelectedRow();
							currentc = tbl.getSelectedColumn();	

							// start indicating lock
							LockHandler.initializeLock(container);

							//wait for lock indicator to invoke
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									try {
										//row level locking
										LockHandler.startLock(DBConnect.SelectForUpdate("company",
												"*",
												"co_code='"+tbl.getValueAt(tbl.getSelectedRow(),1).toString()+"'"));
										//refresh
										tableCompany.setQuery(DEFAULT_QUERY);
										reload();

										//populate records
										tbl.setRowSelectionInterval(tempR,tempR);
										tbl.setColumnSelectionInterval(tempC,tempC);
										setFieldValue();

										tbl.grabFocus();	
										tbl.setEnabled(false);
										jbtnedit.setText("Update");
										jbtnedit.setMnemonic('U');
										jbtndelete.setText("Cancel");
										jbtndelete.setMnemonic('C');
										jbtnadd.setEnabled(false);

										enableFields();

										txtco_code.setEnabled(false);
										txtco_name.grabFocus();

									}
									catch (SQLException e) {
										try {
											//refresh and remove lock mode
											tableCompany.setQuery(DEFAULT_QUERY);
											reload();
											setCancel();
											LockHandler.LimitLockExceeded(container);

										} catch (IllegalStateException e1) {
											e1.printStackTrace();
										} catch (SQLException e1) {
											e1.printStackTrace();
										}
										e.printStackTrace();
									} 
									finally {
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
					else if (ecaption == "Update") {
						try {							
							String e_sql = 
								DBConnect.Update("company",
										"co_name= '"+DBConnect.clean(txtco_name.getText().trim())+"', " +
										"co_initial = '"+DBConnect.clean(txtco_initial.getText().trim())+"', " +
										"co_addr1 = '"+DBConnect.clean(txtco_addr1.getText().trim())+"', " +
										"co_addr2 = '"+DBConnect.clean(txtco_addr2.getText().trim())+"', " +
										"co_contact_person = '"+DBConnect.clean(txtco_contact_person.getText().trim())+"', " +
										"co_contact_number = '"+DBConnect.clean(txtco_contact_number.getText().trim())+"', " +
										"co_email_address = '"+txtco_email_address.getText().trim()+"', " +
										"co_fax_no = '"+txtco_fax_no.getText().trim()+"', " +
										"co_sss_no = '"+txtco_sss_no.getText().trim()+"', " +
										"co_tin_no = '"+txtco_tin_no.getText().trim()+"', " +
										"co_pagibig_no = '"+txtco_pagibig_no.getText().trim()+"', " +
										"co_philhealth_no = '"+txtco_philhealth_no.getText().trim()+"', " +
										"co_extra_pay_holiday = '"+cboExtraPayHoliday+"'",
										"co_code = '" +xcode+"'");
							if(txtco_name.getText().trim().length() == 0) {
								Message.messageError("Invalid input for company name field.");
								txtco_name.grabFocus();
							}
							else if(txtco_initial.getText().trim().length() == 0) {
								Message.messageError("Invalid input for initial field.");
								txtco_initial.grabFocus();
							}
							else if(jcbco_extra_pay_holiday.getSelectedIndex() == 0){
								Message.messageError("Invalid input for extra pay holiday field.");
								jcbco_extra_pay_holiday.grabFocus();
							}
							else {
								//unlock mode then update
								LockHandler.removeLockAndUpdate(e_sql);

								tableCompany.setQuery(DEFAULT_QUERY);
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

								setFieldValue();
								disableFields();

								tbl.grabFocus();

								Message.messageInfo(Message.messageUpdate);

								try {
									tbl.setRowSelectionInterval(currentr,currentr);
									tbl.setColumnSelectionInterval(currentc,currentc);
								}
								catch(Exception e3){
									System.out.println(e3.getMessage());
								}    
								tbl.grabFocus();
							}

						}
						catch(SQLException se){
							System.out.println(se.getMessage());
						}
					}
				}//public void mouseClicked(MouseEvent e)
			});

			//Payroll Computation button listener
			jbtnpaycomp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (tbl.getSelectedRow() >= 0) {
						int getrow = tbl.getSelectedRow();
						new CompanyPayCompMaint(tbl.getValueAt(getrow,1).toString(),
								tbl.getValueAt(getrow,0).toString().trim(),
								Width(),Height());
					}
				}
			});

			//Annualization button listener
			jbtnannul.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (tbl.getSelectedRow() >= 0) {
						int getrow = tbl.getSelectedRow();
						new CompanyAnnualtnMaint(tbl.getValueAt(getrow,1).toString(),
								tbl.getValueAt(getrow,0).toString().trim(),
								Width(),Height());
					}
				}
			});

			//Add Key Listener
			tbl.addKeyListener(new KeyAdapter(){
				public void keyReleased(KeyEvent e){
					tempR = tbl.getSelectedRow();
					tempC = tbl.getSelectedColumn();
					setFieldValue();
				}
			});        

			//table mouse click listener
			tbl.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					txtsearch.setVisible(false);
					txtsearch.setText(null);
					if (jbtndelete.getText().equals("Cancel") == false) {
						//get last selected cell
						tempR = tbl.getSelectedRow();
						tempC = tbl.getSelectedColumn();

						setFieldValue();

					}//if (jbtndelete.getLabel() != "Cancel")
				}//public void mouseClicked(MouseEvent e)
			}//new MouseAdapter()
			);//tbl.addMouseListener(

			//table key listener
			tbl.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					try {
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
								setFieldValue();
							}
							tbl.setRowSelectionInterval(currentr,currentr);
							tbl.setColumnSelectionInterval(currentc,currentc);
						}
						if ((e.getKeyCode() >= 65) && (e.getKeyCode() <= 105) && (e.isAltDown() != true)){
							txtsearch.setVisible(true);
							txtsearch.grabFocus();
							txtsearch.setText("CompanyName:" + " " + e.getKeyChar());
						}}
					catch(Exception ef){
						System.out.println("Error sa JTable Key Listener; Pakicheck n lang!!");
					}
				}//public void keyPressed(KeyEvent e){
			}//new KeyAdapter()
			);//tbl.addKeyListener(

			//txtsearch key listener
			txtsearch.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					if (eventKey == 8 && txtsearch.getText().length() == 13) {
						e.consume();
					}
				}//ENDof public void keyPressed(KeyEvent e) {
				
				public void keyReleased(KeyEvent e) {
					int eventKey = e.getKeyChar();
					if (eventKey == 10 || eventKey == 27) {
						txtsearch.setVisible(false);
						tbl.grabFocus();
					}
					
					String sKey = txtsearch.getText().substring(9).trim();
					if (sKey.trim().length() > 0) {
						for (int row = 0; row < tbl.getRowCount(); row++) {
							if (tbl.getValueAt(row,0).toString().length() >= sKey.length()) {
								if (tbl.getValueAt(row,0).toString().substring(0,sKey.length()).equalsIgnoreCase(sKey) == true) {
									tbl.setRowSelectionInterval(row,row);
									setFieldValue();
									break;
								}
							}
						}
					}//ENDof if (sKey.trim().length() > 0) {
					else {
						tbl.setRowSelectionInterval(0,0);
						setFieldValue();
					}//ENDof else if (sKey.trim().length() > 0) {
				}//public void keyReleased(KeyEvent e){
			}//new KeyAdapter(){
			);//txtsearch.addKeyListener(

			setSize(620,600);
			setVisible(true);
		}//try
		catch ( ClassNotFoundException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("CompanyMaint", "CompanyMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch ( SQLException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("CompanyMaint", "CompanyMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch (Exception e) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("CompanyMaint", "CompanyMaint", traceWriter.toString());        
			dispose();
		}
	}//ENDof public CompanyMaint() {

	private class TestRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			/*if (column == 5)
				setHorizontalAlignment( LEFT );
			else
				setHorizontalAlignment( LEFT );*/
			setBorder(BorderFactory.createLineBorder(getBackground(), 1));

			return this;
		}
	}//ENDof private class TestRenderer extends DefaultTableCellRenderer {

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
		//Select row in table 
		if (tbl.getSelectedRow() == -1){
			ListSelectionModel selectionModel = tbl.getSelectionModel();
			selectionModel.setSelectionInterval(0, 0);
		}//end if
		if (tbl.getRowCount() >= 1){
			//rollbacck current cell
			tbl.setRowSelectionInterval(tempR,tempR);
			tbl.setColumnSelectionInterval(tempC,tempC);
			setFieldValue();
		}else{
			clearFields();
		}
		tbl.getSelectedRow();
		tbl.grabFocus();
		disableFields();
	}//ENDof private void setCancel() {

	private void reload() {
		try {
			tableCompany.setQuery(DEFAULT_QUERY);
			tbl.getColumnModel().getColumn(0).setMaxWidth(0);
			tbl.getColumnModel().getColumn(0).setMinWidth(0);
			tbl.getColumnModel().getColumn(1).setMaxWidth(0);
			tbl.getColumnModel().getColumn(1).setMinWidth(0);
			tbl.getColumnModel().getColumn(2).setMaxWidth(0);
			tbl.getColumnModel().getColumn(2).setMinWidth(0);
			tbl.getColumnModel().getColumn(3).setMaxWidth(0);
			tbl.getColumnModel().getColumn(3).setMinWidth(0);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(194);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(194);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(50);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(50);
			tbl.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(80);
			tbl.getTableHeader().getColumnModel().getColumn(2).setMinWidth(80);
			tbl.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(264);
			tbl.getTableHeader().getColumnModel().getColumn(3).setMinWidth(264);

			//set column header width (to avoid the ... sign)
			tbl.getColumnModel().getColumn(0).setHeaderValue("Company Name");
			tbl.getColumnModel().getColumn(1).setHeaderValue("Code");
			tbl.getColumnModel().getColumn(2).setHeaderValue("Initial");
			tbl.getColumnModel().getColumn(3).setHeaderValue("Address");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void reload() {

	private void enableFields() {
		txtco_code.setEnabled(true);
		txtco_name.setEnabled(true);
		txtco_initial.setEnabled(true);
		txtco_addr1.setEnabled(true);
		txtco_addr2.setEnabled(true);
		txtco_contact_person.setEnabled(true);
		txtco_contact_number.setEnabled(true);
		txtco_email_address.setEnabled(true);
		txtco_fax_no.setEnabled(true);
		txtco_sss_no.setEnabled(true);
		txtco_tin_no.setEnabled(true);
		txtco_pagibig_no.setEnabled(true);
		txtco_philhealth_no.setEnabled(true);
		jcbco_extra_pay_holiday.setEnabled(true);
	}//ENDof private void enableFields() {

	private void disableFields() {
		txtco_code.setEnabled(false);
		txtco_name.setEnabled(false);
		txtco_initial.setEnabled(false);
		txtco_addr1.setEnabled(false);
		txtco_addr2.setEnabled(false);
		txtco_contact_person.setEnabled(false);
		txtco_contact_number.setEnabled(false);
		txtco_email_address.setEnabled(false);
		txtco_fax_no.setEnabled(false);
		txtco_sss_no.setEnabled(false);
		txtco_tin_no.setEnabled(false);
		txtco_pagibig_no.setEnabled(false);
		txtco_philhealth_no.setEnabled(false);
		jcbco_extra_pay_holiday.setEnabled(false);
	}//ENDof private void disableFields() {

	private void clearFields() {
		txtco_code.setText("");
		txtco_name.setText("");
		txtco_initial.setText("");
		txtco_addr1.setText("");
		txtco_addr2.setText("");
		txtco_contact_person.setText("");
		txtco_contact_number.setText("");
		txtco_email_address.setText("");
		txtco_fax_no.setText("");
		txtco_sss_no.setText("");
		txtco_tin_no.setText("");
		txtco_pagibig_no.setText("");
		txtco_philhealth_no.setText("");
		jcbco_extra_pay_holiday.setSelectedItem("Choose");
	}//ENDof private void clearFields() {

	private void setFieldValue() {
		if (tbl.getSelectedRow() >= 0) {
			int getrow = tbl.getSelectedRow();
			txtco_code.setText(tbl.getValueAt(getrow,1).toString());
			txtco_name.setText(tbl.getValueAt(getrow,0).toString());
			txtco_initial.setText(tbl.getValueAt(getrow,2).toString());
			txtco_addr1.setText(tbl.getValueAt(getrow,3).toString());
			txtco_addr2.setText(tbl.getValueAt(getrow,4).toString());
			txtco_contact_person.setText(tbl.getValueAt(getrow,5).toString());
			txtco_contact_number.setText(tbl.getValueAt(getrow,6).toString());
			txtco_email_address.setText(tbl.getValueAt(getrow,7).toString());
			txtco_fax_no.setText(tbl.getValueAt(getrow,8).toString());
			txtco_sss_no.setText(tbl.getValueAt(getrow,9).toString());
			txtco_tin_no.setText(tbl.getValueAt(getrow,10).toString());
			txtco_pagibig_no.setText(tbl.getValueAt(getrow,11).toString());
			txtco_philhealth_no.setText(tbl.getValueAt(getrow,12).toString());
			if (tbl.getValueAt(getrow,13).toString().equalsIgnoreCase("Y") == true) {
				jcbco_extra_pay_holiday.setSelectedItem("Yes");
			} 
			else if (tbl.getValueAt(getrow,13).toString().equalsIgnoreCase("N") == true) {
				jcbco_extra_pay_holiday.setSelectedItem("No");
			} 
			else {
				jcbco_extra_pay_holiday.setSelectedItem("Choose");
			}
		}//ENDof if (tbl.getSelectedRow() >= 0) {
	}//ENDof private void getFieldValue() {
	
	private int Width(){
		return this.getBounds().width;
	}
	private int Height(){
		return this.getBounds().height;
	}
	
	private int setCurRecord() {
		int i = 0;
		if (tbl.getRowCount() > 0) {
			if (curRecord.trim().length() > 0) {
				for (i=0; i<=tbl.getRowCount()-1; i++) {
					if (tbl.getValueAt(i,1).toString().equalsIgnoreCase(curRecord) == true) {
						tbl.setRowSelectionInterval(i,i);
						tempR = i;
						break;
					}
				}//ENDof for (int i=0; i<=tbl.getRowCount()-1; i++) {
			}//ENDof if (curRecord.trim().length() > 0) {
		}//ENDof if (tbl.getRowCount() > 0) {
		return i;
	}//ENDof private int setCurRecord() {
	
}//ENDof public class CompanyMaint extends JInternalFrame {
