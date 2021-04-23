package maintenance.bin.src.MaintEmployee;

/**
 * Author:  Norberto L. Silva
 * Date:    March 23, 2012
 * Company: Applied Ideas, Inc 
 * Program: Employee Government Code Maintenance ==-> Payroll System
 */


import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import maintenance.bin.src.DBConn.DBConnect;
import maintenance.bin.src.Functions.Formatter;
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.ResultTableModel.ResultSetTableModel;
import maintenance.bin.src.core.LockHandler;
import maintenance.bin.src.core.mainform;

public class EmployeeGovtCodeMaint extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	static String DEFAULT_QUERY = "SELECT column_name, data_type, column_type, column_default "+
								  "  FROM information_schema.columns "+
								  " WHERE table_schema = '"+DBConnect.dbName+"'"+
								  "   AND table_name = 'empgovcont'"+
								  " ORDER By ordinal_position";

	private ResultSetTableModel tableEmployeeGovtCode;

	Container container;
	Message msg = new Message();

	JInternalFrame j;
	
	//variable for buttons
	private JButton jbtnedit;
	private JButton jbtncancel;

	//variable for user entry
	private JComboBox jcbWTax			= new JComboBox();
	private JComboBox jcbSSS			= new JComboBox();
	private JComboBox jcbHDMF			= new JComboBox();
	private JComboBox jcbHDMFAdditional	= new JComboBox();
	private JComboBox jcbPhilHealth		= new JComboBox();

	public EmployeeGovtCodeMaint() {
		super ("Employee Government Code Maintenance", false, true, false, false);
		this.setName("EmployeeGovtCodemaint");
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

		try {
			tableEmployeeGovtCode = new ResultSetTableModel( DEFAULT_QUERY );

			//this.setAlwaysOnTop(true);
			container = this.getContentPane();
			container.setLayout(null);

			//Set frame objects
			jbtnedit = new JButton("Edit");
			jbtnedit.setMnemonic('E');
			jbtncancel = new JButton("Cancel");
			jbtncancel.setMnemonic('C');
			
			JLabel lblWTax = new JLabel("WTax");
			JLabel lblSSS = new JLabel("SSS");
			JLabel lblHDMF = new JLabel("HDMF");
			JLabel lblHDMFAdditional = new JLabel("HDMF Additional");
			JLabel lblPhilHealth = new JLabel("PhilHealth");
			
			String sql = "SELECT * FROM deduction ORDER By deduct_desc";
			tableEmployeeGovtCode.resultSet = tableEmployeeGovtCode.statement.executeQuery(sql);

			jcbWTax.setFont(new Font("",Font.PLAIN, 12));
			jcbWTax.setBackground(Color.white);
			jcbWTax.addItem("Choose");
			while (tableEmployeeGovtCode.resultSet.next()){
				jcbWTax.addItem(Formatter.formatString(100,' ','-',tableEmployeeGovtCode.resultSet.getString("deduct_desc"))+"-"+tableEmployeeGovtCode.resultSet.getString("deduct_code"));
			}//while (tableEmployeeGovtCode.resultSet.next())

			jcbSSS.setFont(new Font("",Font.PLAIN, 12));
			jcbSSS.setBackground(Color.white);
			jcbSSS.addItem("Choose");
			tableEmployeeGovtCode.resultSet.beforeFirst();
			while (tableEmployeeGovtCode.resultSet.next()){
				jcbSSS.addItem(Formatter.formatString(100,' ','-',tableEmployeeGovtCode.resultSet.getString("deduct_desc"))+"-"+tableEmployeeGovtCode.resultSet.getString("deduct_code"));
			}//while (tableEmployeeGovtCode.resultSet.next())

			jcbHDMF.setFont(new Font("",Font.PLAIN, 12));
			jcbHDMF.setBackground(Color.white);
			jcbHDMF.addItem("Choose");
			tableEmployeeGovtCode.resultSet.beforeFirst();
			while (tableEmployeeGovtCode.resultSet.next()){
				jcbHDMF.addItem(Formatter.formatString(100,' ','-',tableEmployeeGovtCode.resultSet.getString("deduct_desc"))+"-"+tableEmployeeGovtCode.resultSet.getString("deduct_code"));
			}//while (tableEmployeeGovtCode.resultSet.next())

			jcbHDMFAdditional.setFont(new Font("",Font.PLAIN, 12));
			jcbHDMFAdditional.setBackground(Color.white);
			jcbHDMFAdditional.addItem("Choose");
			tableEmployeeGovtCode.resultSet.beforeFirst();
			while (tableEmployeeGovtCode.resultSet.next()){
				jcbHDMFAdditional.addItem(Formatter.formatString(100,' ','-',tableEmployeeGovtCode.resultSet.getString("deduct_desc"))+"-"+tableEmployeeGovtCode.resultSet.getString("deduct_code"));
			}//while (tableEmployeeGovtCode.resultSet.next())

			jcbPhilHealth.setFont(new Font("",Font.PLAIN, 12));
			jcbPhilHealth.setBackground(Color.white);
			jcbPhilHealth.addItem("Choose");
			tableEmployeeGovtCode.resultSet.beforeFirst();
			while (tableEmployeeGovtCode.resultSet.next()){
				jcbPhilHealth.addItem(Formatter.formatString(100,' ','-',tableEmployeeGovtCode.resultSet.getString("deduct_desc"))+"-"+tableEmployeeGovtCode.resultSet.getString("deduct_code"));
			}//while (tableEmployeeGovtCode.resultSet.next())

			lblWTax.setBounds(18,20,100,20);
			lblSSS.setBounds(18,50,100,20);
			lblHDMF.setBounds(18,80,100,20);
			lblHDMFAdditional.setBounds(18,110,100,20);
			lblPhilHealth.setBounds(18,140,100,20);

			jcbWTax.setBounds(120,20,250,20);
			jcbSSS.setBounds(120,50,250,20);
			jcbHDMF.setBounds(120,80,250,20);
			jcbHDMFAdditional.setBounds(120,110,250,20);
			jcbPhilHealth.setBounds(120,140,250,20);
						
			jbtnedit.setBounds(75,180,80,22);
			jbtncancel.setBounds(240,180,80,22);

			tableEmployeeGovtCode.resultSet = tableEmployeeGovtCode.statement.executeQuery(DEFAULT_QUERY);
			
			setFieldValue();
			
			//add objects to container
			container.add(lblWTax);
			container.add(jcbWTax);
			container.add(lblSSS);
			container.add(jcbSSS);
			container.add(lblHDMF);
			container.add(jcbHDMF);
			container.add(lblHDMFAdditional);
			container.add(jcbHDMFAdditional);
			container.add(lblPhilHealth);
			container.add(jcbPhilHealth);

			container.add(jbtnedit);
			container.add(jbtncancel);        

			//Disabled text fields
			jbtncancel.setEnabled(false);
			disableFields();

			//Cancel BUTTON LISTENER
			jbtncancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setCancel();
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
									tableEmployeeGovtCode.setQuery(DEFAULT_QUERY);
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
						jbtncancel.setEnabled(true);

						enableFields();

						jcbWTax.grabFocus();

					}
					else if (ecaption == "Update") {
						if (jcbWTax.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for WTax field.");
							jcbWTax.grabFocus();
						}
						else if (jcbSSS.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for SSS field.");
							jcbSSS.grabFocus();
						}
						else if (jcbHDMF.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for HDMF field.");
							jcbHDMF.grabFocus();
						}
						else if (jcbHDMFAdditional.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for HDMF Additional field.");
							jcbHDMFAdditional.grabFocus();
						}
						else if (jcbPhilHealth.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for Philhealth field.");
							jcbPhilHealth.grabFocus();
						}
						else {
							
							try {
								Statement stmnt = DBConnect.getConnection().createStatement();
								String MySql = "ALTER TABLE empgovcont " +
											   "ALTER COLUMN wtax_deduct SET DEFAULT '"+jcbWTax.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN sss_deduct SET DEFAULT '"+jcbSSS.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN hdmf_deduct SET DEFAULT '"+jcbHDMF.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN hdmf_additional_deduct SET DEFAULT '"+jcbHDMFAdditional.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN philhealth_deduct SET DEFAULT '"+jcbPhilHealth.getSelectedItem().toString().substring(101).trim()+"'";
								stmnt.execute(MySql);
								MySql = "UPDATE empgovcont " +
										   "SET wtax_deduct = '"+jcbWTax.getSelectedItem().toString().substring(101).trim()+"',"+
										   "    sss_deduct = '"+jcbSSS.getSelectedItem().toString().substring(101).trim()+"',"+
										   "    hdmf_deduct = '"+jcbHDMF.getSelectedItem().toString().substring(101).trim()+"',"+
										   "    hdmf_additional_deduct = '"+jcbHDMFAdditional.getSelectedItem().toString().substring(101).trim()+"',"+
										   "    philhealth_deduct = '"+jcbPhilHealth.getSelectedItem().toString().substring(101).trim()+"'";
								stmnt.execute(MySql);
								stmnt.close();
							} catch (SQLException e2) {
								e2.printStackTrace();
							}

							try {
								tableEmployeeGovtCode.setQuery(DEFAULT_QUERY);
							} catch (IllegalStateException e1) {
								e1.printStackTrace();
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
							jbtnedit.setText("Edit");
							jbtnedit.setMnemonic('E');
							jbtncancel.setEnabled(false);
							
							disableFields();

							Message.messageInfo(Message.messageUpdate);
							jbtnedit.grabFocus();
						}//ENDof nested if						
					}
				}//public void mouseClicked(MouseEvent e)
			});

			setSize(390,240);
			setVisible(true);

		}//try
		catch ( ClassNotFoundException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("EmployeeGovtCodeMaint", "EmployeeGovtCodeMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch ( SQLException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("EmployeeGovtCodeMaint", "EmployeeGovtCodeMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch (Exception e) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("EmployeeGovtCodeMaint", "EmployeeGovtCodeMaint", traceWriter.toString());        
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
		jbtnedit.setEnabled(true);
		jbtncancel.setEnabled(false);
		setFieldValue();
		disableFields();
		jbtnedit.grabFocus();
	}//ENDof private void setCancel() {

	private void enableFields() {
		jcbWTax.setEnabled(true);
		jcbSSS.setEnabled(true);
		jcbHDMF.setEnabled(true);
		jcbHDMFAdditional.setEnabled(true);
		jcbPhilHealth.setEnabled(true);
	}//ENDof private void enableFields() {

	private void disableFields() {
		jcbWTax.setEnabled(false);
		jcbSSS.setEnabled(false);
		jcbHDMF.setEnabled(false);
		jcbHDMFAdditional.setEnabled(false);
		jcbPhilHealth.setEnabled(false);
	}//ENDof private void disableFields() {

	private void clearFields() {
		jcbWTax.setSelectedItem("Choose");
		jcbSSS.setSelectedItem("Choose");
		jcbHDMF.setSelectedItem("Choose");
		jcbHDMFAdditional.setSelectedItem("Choose");
		jcbPhilHealth.setSelectedItem("Choose");
	}//ENDof private void clearFields() {

	private void setFieldValue() {
		try {
			tableEmployeeGovtCode.resultSet.last();
			if (tableEmployeeGovtCode.resultSet.getRow() > 0) {
				tableEmployeeGovtCode.resultSet.beforeFirst();
				while (tableEmployeeGovtCode.resultSet.next()) {
					if (tableEmployeeGovtCode.resultSet.getString("column_name").equalsIgnoreCase("wtax_deduct") == true) {
						jcbWTax.setSelectedIndex(0);
						for (int ii=0; ii<=jcbWTax.getItemCount()-1; ii++) {
							if (jcbWTax.getItemAt(ii).toString().length() > 50) {
								if (tableEmployeeGovtCode.resultSet.getString("column_default") != null) {
									if (tableEmployeeGovtCode.resultSet.getString("column_default").equalsIgnoreCase(jcbWTax.getItemAt(ii).toString().substring(101)) == true) {
										jcbWTax.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbWTax.getItemCount()-1; ii++) {
					}

					if (tableEmployeeGovtCode.resultSet.getString("column_name").equalsIgnoreCase("sss_deduct") == true) {
						jcbSSS.setSelectedIndex(0);
						for (int ii=0; ii<=jcbSSS.getItemCount()-1; ii++) {
							if (jcbSSS.getItemAt(ii).toString().length() > 50) {
								if (tableEmployeeGovtCode.resultSet.getString("column_default") != null) {
									if (tableEmployeeGovtCode.resultSet.getString("column_default").equalsIgnoreCase(jcbSSS.getItemAt(ii).toString().substring(101)) == true) {
										jcbSSS.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbSSS.getItemCount()-1; ii++) {
					}

					if (tableEmployeeGovtCode.resultSet.getString("column_name").equalsIgnoreCase("hdmf_deduct") == true) {
						jcbHDMF.setSelectedIndex(0);
						for (int ii=0; ii<=jcbHDMF.getItemCount()-1; ii++) {
							if (jcbHDMF.getItemAt(ii).toString().length() > 50) {
								if (tableEmployeeGovtCode.resultSet.getString("column_default") != null) {
									if (tableEmployeeGovtCode.resultSet.getString("column_default").equalsIgnoreCase(jcbHDMF.getItemAt(ii).toString().substring(101)) == true) {
										jcbHDMF.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbHDMF.getItemCount()-1; ii++) {
					}

					if (tableEmployeeGovtCode.resultSet.getString("column_name").equalsIgnoreCase("hdmf_additional_deduct") == true) {
						jcbHDMFAdditional.setSelectedIndex(0);
						for (int ii=0; ii<=jcbHDMFAdditional.getItemCount()-1; ii++) {
							if (jcbHDMFAdditional.getItemAt(ii).toString().length() > 50) {
								if (tableEmployeeGovtCode.resultSet.getString("column_default") != null) {
									if (tableEmployeeGovtCode.resultSet.getString("column_default").equalsIgnoreCase(jcbHDMFAdditional.getItemAt(ii).toString().substring(101)) == true) {
										jcbHDMFAdditional.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbHDMF.getItemCount()-1; ii++) {
					}

					if (tableEmployeeGovtCode.resultSet.getString("column_name").equalsIgnoreCase("philhealth_deduct") == true) {
						jcbPhilHealth.setSelectedIndex(0);
						for (int ii=0; ii<=jcbPhilHealth.getItemCount()-1; ii++) {
							if (jcbPhilHealth.getItemAt(ii).toString().length() > 50) {
								if (tableEmployeeGovtCode.resultSet.getString("column_default") != null) {
									if (tableEmployeeGovtCode.resultSet.getString("column_default").equalsIgnoreCase(jcbPhilHealth.getItemAt(ii).toString().substring(101)) == true) {
										jcbPhilHealth.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbPhilHealth.getItemCount()-1; ii++) {
					}

				}//while (tableEmployeeGovtCode.resultSet.next())
			}
			else {
				clearFields();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void setFieldValue() {
	
}//ENDof public class CustomerMaint extends JInternalFrame {
