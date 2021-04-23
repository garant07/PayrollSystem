package maintenance.bin.src.MaintCompany;

/**
 * Author:  Norberto L. Silva
 * Date:    March 20, 2012
 * Company: Applied Ideas, Inc 
 * Program: Company Payroll Computation Maintenance ==-> Payroll System
 */


import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import maintenance.bin.src.DBConn.DBConnect;
import maintenance.bin.src.Functions.Formatter;
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.MyField;
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.ResultTableModel.ResultSetTableModel;
import maintenance.bin.src.core.LockHandler;
import maintenance.bin.src.core.mainform;

public class CompanyPayCompMaint extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private JDesktopPane desk = mainform.desktop;

	public static JInternalFrame iFrame = null;

	static String DEFAULT_QUERY = "";

	private ResultSetTableModel tableCompanyPayComp;

	Container container;
	Message msg = new Message();

	//variable for buttons
	private JButton jbtnedit;
	private JButton jbtnclose;

	//variable for user entry
	JScrollPane scrollPane;
	private MyField txtWorkDaysYear = new MyField(false,3);
	private MyField txtWorkDaysMonth = new MyField(false,3);
	private MyField txtWorkHoursDay = new MyField(false,2);
	private JComboBox jcbSSSFrequency = new JComboBox();
	private JComboBox jcbPHHFrequency = new JComboBox();
	private JComboBox jcbHDMFFrequency = new JComboBox();
	private JComboBox jcbWtaxFrequency = new JComboBox();
	
	private	JLabel lblCompany = new JLabel();

	private String CoCode = "";
	private String SSSFrequency = "";
	private String PhilHealthFrequency = "";
	private String HDMFFrequency = "";
	private String WTaxFrequency = "";
	
	public CompanyPayCompMaint(String cocode, String co_name, int w, int h) {
		super ("Company Payroll Computation Maintenance", false, true, false, false);
		iFrame = this;
	    Dimension rootSize = Toolkit.getDefaultToolkit().getScreenSize();
		setEmployee(cocode,co_name);
		this.setName("CompanyPayCompmaint");
		this.setResizable(false);
		this.setLocation(((rootSize.width + 285) - w) / 2,((rootSize.height + 80) - h) / 3);

		this.addInternalFrameListener(new InternalFrameListener(){
			public void internalFrameActivated(InternalFrameEvent arg0) {
				CompanyMaint.subMain1 = 1;
				CompanyMaint.jbtnadd.setEnabled(false);
				CompanyMaint.jbtnedit.setEnabled(false);
				CompanyMaint.jbtndelete.setEnabled(false);
				CompanyMaint.jbtnpaycomp.setEnabled(false);
				CompanyMaint.jbtnannul.setEnabled(false);
			}
			public void internalFrameClosed(InternalFrameEvent arg0) {}
			public void internalFrameClosing(InternalFrameEvent arg0) {
				CompanyMaint.subMain1 = 0;
				CompanyMaint.jbtnadd.setEnabled(true);
				CompanyMaint.jbtnedit.setEnabled(true);
				CompanyMaint.jbtndelete.setEnabled(true);
				CompanyMaint.jbtnpaycomp.setEnabled(true);
				CompanyMaint.jbtnannul.setEnabled(true);
			}
			public void internalFrameDeactivated(InternalFrameEvent arg0) {}
			public void internalFrameDeiconified(InternalFrameEvent arg0) {}
			public void internalFrameIconified(InternalFrameEvent arg0) {}
			public void internalFrameOpened(InternalFrameEvent arg0) {}
		});

		desk.setLayout(null);
		desk.add(this, new Integer(1));
		
		try {
			tableCompanyPayComp = new ResultSetTableModel( DEFAULT_QUERY );

			//this.setAlwaysOnTop(true);
			container = this.getContentPane();
			container.setLayout(null);

			//Set frame objects
			jbtnedit = new JButton("Edit");
			jbtnedit.setMnemonic('E');
			jbtnclose = new JButton("Close");
			jbtnclose.setMnemonic('C');
			
			JLabel lblWorkDaysYear = new JLabel("# of Working Days in a Year");
			JLabel lblWorkDaysMonth = new JLabel("# of Working Days in a Month");
			JLabel lblWorkHoursDay = new JLabel("# of Working Hours in a Day");
			JLabel lblSSSFrequency = new JLabel("SSS Frequency");
			JLabel lblPhilhealthFrequency = new JLabel("PhilHealth Frequency");
			JLabel lblHDMFFrequency = new JLabel("HDMF Frequency");
			JLabel lblWtaxFrequency = new JLabel("Wtax Frequency");

			String sql = "SELECT * FROM frequency ORDER By freq_desc";
			tableCompanyPayComp.resultSet = tableCompanyPayComp.statement.executeQuery(sql);

			jcbSSSFrequency.setFont(new Font("",Font.PLAIN, 12));
			jcbSSSFrequency.setBackground(Color.white);
			jcbSSSFrequency.addItem("Choose");
			while (tableCompanyPayComp.resultSet.next()){
				jcbSSSFrequency.addItem(Formatter.formatString(100,' ','-',tableCompanyPayComp.resultSet.getString("freq_desc"))+"-"+tableCompanyPayComp.resultSet.getString("freq_code"));
			}//while (tableCompanyPayComp.resultSet.next())

			sql = "SELECT * FROM frequency ORDER By freq_desc";
			tableCompanyPayComp.resultSet = tableCompanyPayComp.statement.executeQuery(sql);

			jcbPHHFrequency.setFont(new Font("",Font.PLAIN, 12));
			jcbPHHFrequency.setBackground(Color.white);
			jcbPHHFrequency.addItem("Choose");
			while (tableCompanyPayComp.resultSet.next()) {
				jcbPHHFrequency.addItem(Formatter.formatString(100,' ','-',tableCompanyPayComp.resultSet.getString("freq_desc"))+"-"+tableCompanyPayComp.resultSet.getString("freq_code"));
			}//while (tableCompanyPayComp.resultSet.next())

			sql = "SELECT * FROM frequency ORDER By freq_desc";
			tableCompanyPayComp.resultSet = tableCompanyPayComp.statement.executeQuery(sql);

			jcbHDMFFrequency.setFont(new Font("",Font.PLAIN, 12));
			jcbHDMFFrequency.setBackground(Color.white);
			jcbHDMFFrequency.addItem("Choose");
			while (tableCompanyPayComp.resultSet.next()){
				jcbHDMFFrequency.addItem(Formatter.formatString(100,' ','-',tableCompanyPayComp.resultSet.getString("freq_desc"))+"-"+tableCompanyPayComp.resultSet.getString("freq_code"));
			}//while (tableCompanyPayComp.resultSet.next())

			sql = "SELECT * FROM frequency ORDER By freq_desc";
			tableCompanyPayComp.resultSet = tableCompanyPayComp.statement.executeQuery(sql);

			jcbWtaxFrequency.setFont(new Font("",Font.PLAIN, 12));
			jcbWtaxFrequency.setBackground(Color.white);
			jcbWtaxFrequency.addItem("Choose");
			while (tableCompanyPayComp.resultSet.next()){
				jcbWtaxFrequency.addItem(Formatter.formatString(100,' ','-',tableCompanyPayComp.resultSet.getString("freq_desc"))+"-"+tableCompanyPayComp.resultSet.getString("freq_code"));
			}//while (tableCompanyPayComp.resultSet.next())

			lblCompany.setBounds(10,10,400,20);
			lblWorkDaysYear.setBounds(18,50,170,20);
			lblWorkDaysMonth.setBounds(18,80,170,20);
			lblWorkHoursDay.setBounds(18,110,170,20);
			lblSSSFrequency.setBounds(18,140,170,20);
			lblPhilhealthFrequency.setBounds(18,170,170,20);
			lblHDMFFrequency.setBounds(18,200,170,20);
			lblWtaxFrequency.setBounds(18,230,170,20);

			txtWorkDaysYear.setBounds(200,50,30,20);
			txtWorkDaysYear.setDisabledTextColor(new Color(139,113,113));
			txtWorkDaysMonth.setBounds(200,80,30,20);
			txtWorkDaysMonth.setDisabledTextColor(new Color(139,113,113));
			txtWorkHoursDay.setBounds(200,110,25,20);
			txtWorkHoursDay.setDisabledTextColor(new Color(139,113,113));
			jcbSSSFrequency.setBounds(200,140,200,20);
			jcbPHHFrequency.setBounds(200,170,200,20);
			jcbHDMFFrequency.setBounds(200,200,200,20);
			jcbWtaxFrequency.setBounds(200,230,200,20);
			
			jbtnedit.setBounds(110,270,80,22);
			jbtnclose.setBounds(400,270,80,22);

			tableCompanyPayComp.resultSet = tableCompanyPayComp.statement.executeQuery(DEFAULT_QUERY);
			
			setFieldValue();
			
			//add objects to container
			container.add(lblCompany);
			container.add(lblWorkDaysYear);
			container.add(txtWorkDaysYear);
			container.add(lblWorkDaysMonth);
			container.add(txtWorkDaysMonth);
			container.add(lblWorkHoursDay);
			container.add(txtWorkHoursDay);
			container.add(lblSSSFrequency);
			container.add(jcbSSSFrequency);
			container.add(lblPhilhealthFrequency);
			container.add(jcbPHHFrequency);
			container.add(lblHDMFFrequency);
			container.add(jcbHDMFFrequency);
			container.add(lblWtaxFrequency);
			container.add(jcbWtaxFrequency);
			container.add(jbtnedit);
			container.add(jbtnclose);        

			//Disabled text fields
			disableFields();

			jcbSSSFrequency.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbSSSFrequency.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						SSSFrequency = "";
					}
					else {
						SSSFrequency = jcbSSSFrequency.getSelectedItem().toString().substring(101);
					}
				}
			});

			jcbPHHFrequency.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbPHHFrequency.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						PhilHealthFrequency = "";
					}
					else {
						PhilHealthFrequency = jcbPHHFrequency.getSelectedItem().toString().substring(101);
					}
				}
			});

			jcbHDMFFrequency.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbHDMFFrequency.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						HDMFFrequency = "";
					}
					else {
						HDMFFrequency = jcbHDMFFrequency.getSelectedItem().toString().substring(101);
					}
				}
			});

			jcbWtaxFrequency.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbWtaxFrequency.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						WTaxFrequency = "";
					}
					else {
						WTaxFrequency = jcbWtaxFrequency.getSelectedItem().toString().substring(101);
					}
				}
			});

			//DELETE BUTTON LISTENER
			jbtnclose.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String dcaption = jbtnclose.getText();

					if (dcaption == "Cancel"){
						setCancel();
					}
					else if (dcaption == "Close") {
						CompanyMaint.subMain1 = 0;
						CompanyMaint.jbtnadd.setEnabled(true);
						CompanyMaint.jbtnedit.setEnabled(true);
						CompanyMaint.jbtndelete.setEnabled(true);
						CompanyMaint.jbtnpaycomp.setEnabled(true);
						CompanyMaint.jbtnannul.setEnabled(true);
						CompanyPayCompMaint.iFrame.dispose();
					}//else if (dcaption == "Close"){
				}//public void mouseClicked(MouseEvent e)
			});

			//EDIT BUTTON LISTENER
			jbtnedit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					String ecaption = jbtnedit.getText();
					if (ecaption == "Edit") {
						// start indicating lock
						LockHandler.initializeLock(container);

						//wait for lock indicator to invoke
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								try {
									//row level locking
									LockHandler.startLock(DBConnect.SelectForUpdate("company",
											"*",
											"co_code = '"+CoCode+"'"));
									//refresh
									tableCompanyPayComp.setQuery(DEFAULT_QUERY);

								}
								catch (SQLException e) {
									try {
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
						jbtnedit.setText("Update");
						jbtnedit.setMnemonic('U');
						jbtnclose.setText("Cancel");
						jbtnclose.setMnemonic('C');

						enableFields();

						txtWorkDaysYear.grabFocus();

					}
					else if (ecaption == "Update") {
						if (txtWorkDaysYear.getText().trim().length() == 0 ||
							Integer.parseInt(txtWorkDaysYear.getText().trim()) == 0) {
							Message.messageError("Invalid input for Working Days in a year field.");
							txtWorkDaysYear.grabFocus();
						}
						else if (txtWorkDaysMonth.getText().trim().length() == 0 ||
								Integer.parseInt(txtWorkDaysMonth.getText().trim()) == 0) {
							Message.messageError("Invalid input for Working Days in a month field.");
							txtWorkDaysMonth.grabFocus();
						}
						else if (txtWorkHoursDay.getText().trim().length() == 0 ||
								Integer.parseInt(txtWorkHoursDay.getText().trim()) == 0) {
							Message.messageError("Invalid input for Working Hours in a day field.");
							txtWorkHoursDay.grabFocus();
						}
						else if (SSSFrequency.trim().length() == 0) {
							Message.messageError("Invalid input for SSS Frequency field.");
							jcbSSSFrequency.grabFocus();
						}
						else if (PhilHealthFrequency.trim().length() == 0) {
							Message.messageError("Invalid input for PhilHealth Frequency field.");
							jcbPHHFrequency.grabFocus();
						}
						else if (HDMFFrequency.trim().length() == 0) {
							Message.messageError("Invalid input for HDMF Frequency field.");
							jcbHDMFFrequency.grabFocus();
						}
						else if (WTaxFrequency.trim().length() == 0) {
							Message.messageError("Invalid input for W/Tax Frequency field.");
							jcbWtaxFrequency.grabFocus();
						}
						else {
							try {
								String e_sql = DBConnect.Update("company",
										"co_work_days_year = '"+txtWorkDaysYear.getText().trim()+"', " +
										"co_work_days_month = '"+txtWorkDaysMonth.getText().trim()+"', " +
										"co_work_hours_day = '"+txtWorkHoursDay.getText().trim()+"', " +
										"co_sss_frequency = '"+SSSFrequency+"', " +
										"co_phh_frequency = '"+PhilHealthFrequency+"', " +
										"co_hdmf_frequency = '"+HDMFFrequency+"', " +
										"co_wtax_frequency = '"+WTaxFrequency+"'",
										"co_code = '" +CoCode+"'"
										);
								//unlock mode then update
								LockHandler.removeLockAndUpdate(e_sql);
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
							
							try {
								tableCompanyPayComp.setQuery(DEFAULT_QUERY);
							} catch (IllegalStateException e1) {
								e1.printStackTrace();
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
							jbtnclose.setText("Close");
							jbtnclose.setMnemonic('C');
							jbtnedit.setText("Edit");
							jbtnedit.setMnemonic('E');

							disableFields();

							Message.messageInfo(Message.messageUpdate);
						}//ENDof nested if						
					}
				}//public void mouseClicked(MouseEvent e)
			});

			setSize(574,330);
			setVisible(true);

		}//try
		catch ( ClassNotFoundException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("CompanyPayCompMaint", "CompanyPayCompMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch ( SQLException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("CompanyPayCompMaint", "CompanyPayCompMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch (Exception e) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("CompanyPayCompMaint", "CompanyPayCompMaint", traceWriter.toString());        
			dispose();
		}
	}//ENDof public CustomerMaint() {

	private void setCancel() {
		try {
			//rollback row level locking
			LockHandler.removeLock();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		jbtnedit.setText("Edit");
		jbtnclose.setText("Close");
		jbtnclose.setMnemonic('C');
		jbtnedit.setEnabled(true);
		setFieldValue();
		disableFields();
	}//ENDof private void setCancel() {

	private void enableFields() {
		txtWorkDaysYear.setEnabled(true);
		txtWorkDaysMonth.setEnabled(true);
		txtWorkHoursDay.setEnabled(true);
		jcbSSSFrequency.setEnabled(true);
		jcbPHHFrequency.setEnabled(true);
		jcbHDMFFrequency.setEnabled(true);
		jcbWtaxFrequency.setEnabled(true);
	}//ENDof private void enableFields() {

	private void disableFields() {
		txtWorkDaysYear.setEnabled(false);
		txtWorkDaysMonth.setEnabled(false);
		txtWorkHoursDay.setEnabled(false);
		jcbSSSFrequency.setEnabled(false);
		jcbPHHFrequency.setEnabled(false);
		jcbHDMFFrequency.setEnabled(false);
		jcbWtaxFrequency.setEnabled(false);
	}//ENDof private void disableFields() {

	private void clearFields() {
		txtWorkDaysYear.setText("");
		txtWorkDaysMonth.setText("");
		txtWorkHoursDay.setText("");
		jcbSSSFrequency.setSelectedItem("Choose");
		jcbPHHFrequency.setSelectedItem("Choose");
		jcbHDMFFrequency.setSelectedItem("Choose");
		jcbWtaxFrequency.setSelectedItem("Choose");
	}//ENDof private void clearFields() {

	private void setFieldValue() {
		try {
			tableCompanyPayComp.resultSet.last();
			if (tableCompanyPayComp.resultSet.getRow() > 0) {
				tableCompanyPayComp.resultSet.beforeFirst();
				while (tableCompanyPayComp.resultSet.next()) {
					txtWorkDaysYear.setText(tableCompanyPayComp.resultSet.getString("co_work_days_year"));
					txtWorkDaysMonth.setText(tableCompanyPayComp.resultSet.getString("co_work_days_month"));
					txtWorkHoursDay.setText(tableCompanyPayComp.resultSet.getString("co_work_hours_day"));
					SSSFrequency = "";
					jcbSSSFrequency.setSelectedIndex(0);
					for (int ii=0; ii<=jcbSSSFrequency.getItemCount()-1; ii++) {
						if (jcbSSSFrequency.getItemAt(ii).toString().length() > 50) {
							if (tableCompanyPayComp.resultSet.getString("co_sss_frequency").equalsIgnoreCase(jcbSSSFrequency.getItemAt(ii).toString().substring(101)) == true) {
								SSSFrequency = tableCompanyPayComp.resultSet.getString("co_sss_frequency");
								jcbSSSFrequency.setSelectedIndex(ii);
							}
						}
					}//ENDof for (int ii=0; ii<=jcbSSSFrequency.getItemCount()-1; ii++) {
					PhilHealthFrequency = "";
					jcbPHHFrequency.setSelectedIndex(0);
					for (int ii=0; ii<=jcbPHHFrequency.getItemCount()-1; ii++) {
						if (jcbPHHFrequency.getItemAt(ii).toString().length() > 50) {
							if (tableCompanyPayComp.resultSet.getString("co_phh_frequency").equalsIgnoreCase(jcbPHHFrequency.getItemAt(ii).toString().substring(101)) == true) {
								PhilHealthFrequency = tableCompanyPayComp.resultSet.getString("co_phh_frequency");
								jcbPHHFrequency.setSelectedIndex(ii);
							}
						}
					}//ENDof for (int ii=0; ii<=jcbPHHFrequency.getItemCount()-1; ii++) {
					HDMFFrequency = "";
					jcbHDMFFrequency.setSelectedIndex(0);
					for (int ii=0; ii<=jcbHDMFFrequency.getItemCount()-1; ii++) {
						if (jcbHDMFFrequency.getItemAt(ii).toString().length() > 50) {
							if (tableCompanyPayComp.resultSet.getString("co_hdmf_frequency").equalsIgnoreCase(jcbHDMFFrequency.getItemAt(ii).toString().substring(101)) == true) {
								HDMFFrequency = tableCompanyPayComp.resultSet.getString("co_hdmf_frequency");
								jcbHDMFFrequency.setSelectedIndex(ii);
							}
						}
					}//ENDof for (int ii=0; ii<=jcbHDMFFrequency.getItemCount()-1; ii++) {
					WTaxFrequency = "";
					jcbWtaxFrequency.setSelectedIndex(0);
					for (int ii=0; ii<=jcbWtaxFrequency.getItemCount()-1; ii++) {
						if (jcbWtaxFrequency.getItemAt(ii).toString().length() > 50) {
							if (tableCompanyPayComp.resultSet.getString("co_wtax_frequency").equalsIgnoreCase(jcbWtaxFrequency.getItemAt(ii).toString().substring(101)) == true) {
								WTaxFrequency = tableCompanyPayComp.resultSet.getString("co_wtax_frequency");
								jcbWtaxFrequency.setSelectedIndex(ii);
							}
						}
					}//ENDof for (int ii=0; ii<=jcbWtaxFrequency.getItemCount()-1; ii++) {
				}//while (tableCompanyPayComp.resultSet.next())
			}
			else {
				clearFields();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void setFieldValue() {
	
	private void setEmployee(String cocode, String co_name) {
		lblCompany.setText(cocode+": "+co_name);
		CoCode = cocode;
		DEFAULT_QUERY = DBConnect.Select("company ",
							 "co_work_days_year, "+					
							 "co_work_days_month, "+
							 "co_work_hours_day, "+
							 "co_sss_frequency, "+
							 "co_phh_frequency, "+
							 "co_hdmf_frequency, "+
							 "co_wtax_frequency ",
							 "co_code = '"+cocode+"'",
							 null);
	}//ENDof private void setTaxTable(String cocode, String co_name) {

}//ENDof public class CustomerMaint extends JInternalFrame {
