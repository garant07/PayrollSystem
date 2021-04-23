package maintenance.bin.src.MaintCompany;

/**
 * Author:  Norberto L. Silva
 * Date:    March 20, 2012
 * Company: Applied Ideas, Inc 
 * Program: Company Annualization Maintenance ==-> Payroll System
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
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.MyField;
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.ResultTableModel.ResultSetTableModel;
import maintenance.bin.src.core.LockHandler;
import maintenance.bin.src.core.mainform;

public class CompanyAnnualtnMaint extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private JDesktopPane desk = mainform.desktop;

	public static JInternalFrame iFrame = null;

	static String DEFAULT_QUERY = "";

	private ResultSetTableModel tableCompanyAnnualtn;

	Container container;
	Message msg = new Message();

	//variable for buttons
	private JButton jbtnedit;
	private JButton jbtnclose;

	//variable for user entry
	JScrollPane scrollPane;
	private MyField txtNumPayPeriod = new MyField(false,3);
	private MyField txtNumBonus = new MyField(false,3);
	private MyField txtNumDaysWeek = new MyField(false,2);
	private JComboBox jcbWithBonus = new JComboBox();
	
	private	JLabel lblCompany = new JLabel();

	private String CoCode = "";
	private String WithBonus = "";
	
	public CompanyAnnualtnMaint(String cocode, String co_name, int w, int h) {
		super ("Company Annualization Maintenance", false, true, false, false);
		iFrame = this;
	    Dimension rootSize = Toolkit.getDefaultToolkit().getScreenSize();
		setEmployee(cocode,co_name);
		this.setName("CompanyAnnualtnmaint");
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
			tableCompanyAnnualtn = new ResultSetTableModel( DEFAULT_QUERY );

			//this.setAlwaysOnTop(true);
			container = this.getContentPane();
			container.setLayout(null);

			//Set frame objects
			jbtnedit = new JButton("Edit");
			jbtnedit.setMnemonic('E');
			jbtnclose = new JButton("Close");
			jbtnclose.setMnemonic('C');
			
			JLabel lblNumPayPeriod = new JLabel("# of Payroll Period in a Year");
			JLabel lblNumBonus = new JLabel("# of Bonuses for the Year");
			JLabel lblNumDaysWeek = new JLabel("# of Days in a Week");
			JLabel lblWithBonus = new JLabel("With Bonus");

			jcbWithBonus.setFont(new Font("",Font.PLAIN, 12));
			jcbWithBonus.setBackground(Color.white);
			jcbWithBonus.addItem("Choose");
			jcbWithBonus.addItem("Yes");
			jcbWithBonus.addItem("No");

			lblCompany.setBounds(10,10,400,20);
			lblNumPayPeriod.setBounds(18,50,170,20);
			lblNumBonus.setBounds(18,80,170,20);
			lblNumDaysWeek.setBounds(18,110,170,20);
			lblWithBonus.setBounds(18,140,170,20);

			txtNumPayPeriod.setBounds(200,50,25,20);
			txtNumPayPeriod.setDisabledTextColor(new Color(139,113,113));
			txtNumBonus.setBounds(200,80,25,20);
			txtNumBonus.setDisabledTextColor(new Color(139,113,113));
			txtNumDaysWeek.setBounds(200,110,25,20);
			txtNumDaysWeek.setDisabledTextColor(new Color(139,113,113));
			jcbWithBonus.setBounds(200,140,80,20);
			
			jbtnedit.setBounds(110,180,80,22);
			jbtnclose.setBounds(400,180,80,22);

			tableCompanyAnnualtn.resultSet = tableCompanyAnnualtn.statement.executeQuery(DEFAULT_QUERY);
			
			setFieldValue();
			
			//add objects to container
			container.add(lblCompany);
			container.add(lblNumPayPeriod);
			container.add(txtNumPayPeriod);
			container.add(lblNumBonus);
			container.add(txtNumBonus);
			container.add(lblNumDaysWeek);
			container.add(txtNumDaysWeek);
			container.add(lblWithBonus);
			container.add(jcbWithBonus);
			container.add(jbtnedit);
			container.add(jbtnclose);        

			//Disabled text fields
			disableFields();

			jcbWithBonus.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbWithBonus.getSelectedItem().toString().equalsIgnoreCase("Yes") == true) {
						WithBonus = "Y";
					}
					else {
						WithBonus = "N";
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
						CompanyAnnualtnMaint.iFrame.dispose();
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
									tableCompanyAnnualtn.setQuery(DEFAULT_QUERY);

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

						txtNumPayPeriod.grabFocus();

					}
					else if (ecaption == "Update") {
						if (txtNumPayPeriod.getText().trim().length() == 0 ||
							Integer.parseInt(txtNumPayPeriod.getText().trim()) == 0) {
							Message.messageError("Invalid input for Number of Payroll Period in a year field.");
							txtNumPayPeriod.grabFocus();
						}
						else if (txtNumDaysWeek.getText().trim().length() == 0 ||
								Integer.parseInt(txtNumDaysWeek.getText().trim()) == 0) {
							Message.messageError("Invalid input for Number of Days in a week field.");
							txtNumDaysWeek.grabFocus();
						}
						else if (jcbWithBonus.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for with bonus field.");
							jcbWithBonus.grabFocus();
						}
						else {
							try {
								String e_sql = DBConnect.Update("company",
										"co_num_payroll_period = '"+txtNumPayPeriod.getText().trim()+"', " +
										"co_num_bonuses = '"+txtNumBonus.getText().trim()+"', " +
										"co_num_days_week = '"+txtNumDaysWeek.getText().trim()+"', " +
										"co_with_bonus = '"+WithBonus+"'",
										"co_code = '" +CoCode+"'"
										);
								//unlock mode then update
								LockHandler.removeLockAndUpdate(e_sql);
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
							
							try {
								tableCompanyAnnualtn.setQuery(DEFAULT_QUERY);
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

			setSize(574,240);
			setVisible(true);

		}//try
		catch ( ClassNotFoundException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("CompanyAnnualtnMaint", "CompanyAnnualtnMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch ( SQLException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("CompanyAnnualtnMaint", "CompanyAnnualtnMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch (Exception e) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("CompanyAnnualtnMaint", "CompanyAnnualtnMaint", traceWriter.toString());        
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
		txtNumPayPeriod.setEnabled(true);
		txtNumBonus.setEnabled(true);
		txtNumDaysWeek.setEnabled(true);
		jcbWithBonus.setEnabled(true);
	}//ENDof private void enableFields() {

	private void disableFields() {
		txtNumPayPeriod.setEnabled(false);
		txtNumBonus.setEnabled(false);
		txtNumDaysWeek.setEnabled(false);
		jcbWithBonus.setEnabled(false);
	}//ENDof private void disableFields() {

	private void clearFields() {
		txtNumPayPeriod.setText("");
		txtNumBonus.setText("");
		txtNumDaysWeek.setText("");
		jcbWithBonus.setSelectedItem("Choose");
	}//ENDof private void clearFields() {

	private void setFieldValue() {
		try {
			tableCompanyAnnualtn.resultSet.last();
			if (tableCompanyAnnualtn.resultSet.getRow() > 0) {
				tableCompanyAnnualtn.resultSet.beforeFirst();
				while (tableCompanyAnnualtn.resultSet.next()) {
					txtNumPayPeriod.setText(tableCompanyAnnualtn.resultSet.getString("co_num_payroll_period"));
					txtNumBonus.setText(tableCompanyAnnualtn.resultSet.getString("co_num_bonuses"));
					txtNumDaysWeek.setText(tableCompanyAnnualtn.resultSet.getString("co_num_days_week"));
					WithBonus = "";
					jcbWithBonus.setSelectedIndex(0);
					if (tableCompanyAnnualtn.resultSet.getString("co_with_bonus").equalsIgnoreCase("Y") == true) {
						WithBonus = "Y";
						jcbWithBonus.setSelectedItem("Yes");
					}
					else if (tableCompanyAnnualtn.resultSet.getString("co_with_bonus").equalsIgnoreCase("N") == true) {
						WithBonus = "N";
						jcbWithBonus.setSelectedItem("No");
					}
				}//while (tableCompanyAnnualtn.resultSet.next())
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
							 "co_num_payroll_period, "+					
							 "co_num_bonuses, "+
							 "co_num_days_week, "+
							 "co_with_bonus",
							 "co_code = '"+cocode+"'",
							 null);
	}//ENDof private void setTaxTable(String cocode, String co_name) {

}//ENDof public class CustomerMaint extends JInternalFrame {
