package maintenance.bin.src.MaintEmployee;

/**
 * Author:  Norberto L. Silva
 * Date:    March 8, 2012
 * Company: Applied Ideas, Inc 
 * Program: Employee Government Contributions Maintenance ==-> Payroll System
 */


import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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

public class EmployeeGovtContMaint extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private JDesktopPane desk = mainform.desktop;

	public static JInternalFrame iFrame = null;

	static String DEFAULT_QUERY = "";

	private ResultSetTableModel tableEmployeeGovtCont;

	Container container;
	Message msg = new Message();

	//variable for buttons
	private JButton jbtnedit;
	private JButton jbtnclose;

	//variable for user entry
	JScrollPane scrollPane;
	private MyField txtTinNo = new MyField(true,15);
	private JComboBox jcbWTaxStatus = new JComboBox();
	private JComboBox jcbTinBCompCode = new JComboBox();
	private MyField txtSSSId = new MyField(true,15);
	private JComboBox jcbSSSBCompCode = new JComboBox();
	private MyField txtHDMFId = new MyField(true,15);
	private JComboBox jcbHDMFBCompCode = new JComboBox();
	private MyField txtPhilhealthId = new MyField(true,15);
	private JComboBox jcbPHHBCompCode = new JComboBox();
	private JComboBox jcbZeroSSSPhHdmf = new JComboBox();
	private JFormattedTextField txtAddtlHDMF = new JFormattedTextField(new DecimalFormat("#,##0.00"));

	private	JLabel lblEmployee = new JLabel();

	private String EmpID = "";
	private boolean recordExist = false;	
	private String WTaxStatus = "";
	private String WTaxType = "";
	private String SSSType = "";
	private String HDMFType = "";
	private String PhilHealthType = "";
	private String ZeroSSSPhHdmf = "";
	private String tmpAmount = "";
	
	public EmployeeGovtContMaint(String empid, String emp_name, int w, int h) {
		super ("Employee Government Contribution Maintenance", false, true, false, false);
		iFrame = this;
	    Dimension rootSize = Toolkit.getDefaultToolkit().getScreenSize();
		setEmployee(empid,emp_name);
		this.setName("EmployeeGovtContmaint");
		this.setResizable(false);
		this.setLocation(((rootSize.width + 165) - w) / 2,((rootSize.height + 80) - h) / 3);

		this.addInternalFrameListener(new InternalFrameListener(){
			public void internalFrameActivated(InternalFrameEvent arg0) {
				EmployeeMaint.subMain1 = 1;
				EmployeeMaint.jbtnadd.setEnabled(false);
				EmployeeMaint.jbtnedit.setEnabled(false);
				EmployeeMaint.jbtndelete.setEnabled(false);
				EmployeeMaint.jbtngovt.setEnabled(false);
				EmployeeMaint.jbtnprevemp.setEnabled(false);
			}
			public void internalFrameClosed(InternalFrameEvent arg0) {}
			public void internalFrameClosing(InternalFrameEvent arg0) {
				EmployeeMaint.subMain1 = 0;
				EmployeeMaint.jbtnadd.setEnabled(true);
				EmployeeMaint.jbtnedit.setEnabled(true);
				EmployeeMaint.jbtndelete.setEnabled(true);
				EmployeeMaint.jbtngovt.setEnabled(true);
				EmployeeMaint.jbtnprevemp.setEnabled(true);
			}
			public void internalFrameDeactivated(InternalFrameEvent arg0) {}
			public void internalFrameDeiconified(InternalFrameEvent arg0) {}
			public void internalFrameIconified(InternalFrameEvent arg0) {}
			public void internalFrameOpened(InternalFrameEvent arg0) {}
		});

		desk.setLayout(null);
		desk.add(this, new Integer(1));
		
		try {
			tableEmployeeGovtCont = new ResultSetTableModel( DEFAULT_QUERY );

			//this.setAlwaysOnTop(true);
			container = this.getContentPane();
			container.setLayout(null);

			//Set frame objects
			jbtnedit = new JButton("Edit");
			jbtnedit.setMnemonic('E');
			jbtnclose = new JButton("Close");
			jbtnclose.setMnemonic('C');
			
			JLabel lblTinNo = new JLabel("TIN No.");
			JLabel lblTaxStatus = new JLabel("Tax Status");
			JLabel lblBasisCompWTax = new JLabel("Basis Of Computation");
			JLabel lblSSSID = new JLabel("SSS ID");
			JLabel lblBasisCompSSS = new JLabel("Basis Of Computation");
			JLabel lblHDMFID = new JLabel("HDMF ID");
			JLabel lblBasisCompHDMF = new JLabel("Basis Of Computation");
			JLabel lblPhilhealthID = new JLabel("PhilHealth ID");
			JLabel lblBasisCompPhilhealth = new JLabel("Basis Of Computation");
			JLabel lblZeroSSSPhHdmf = new JLabel("Zero SSS/PhilHealth/HDMF");
			JLabel lblAddtlHdmf = new JLabel("Additional HDMF");

			String sql = "SELECT * FROM wtaxstatus ORDER By wtxstat_desc";
			tableEmployeeGovtCont.resultSet = tableEmployeeGovtCont.statement.executeQuery(sql);

			jcbWTaxStatus.setFont(new Font("",Font.PLAIN, 12));
			jcbWTaxStatus.setBackground(Color.white);
			jcbWTaxStatus.addItem("Choose");
			while (tableEmployeeGovtCont.resultSet.next()){
				jcbWTaxStatus.addItem(Formatter.formatString(100,' ','-',tableEmployeeGovtCont.resultSet.getString("wtxstat_desc"))+"-"+tableEmployeeGovtCont.resultSet.getString("wtxstat_code"));
			}//while (tableEmployeeGovtCont.resultSet.next())

			sql = "SELECT * FROM basiscomputationtype ORDER By bcomtype_desc";
			tableEmployeeGovtCont.resultSet = tableEmployeeGovtCont.statement.executeQuery(sql);

			jcbTinBCompCode.setFont(new Font("",Font.PLAIN, 12));
			jcbTinBCompCode.setBackground(Color.white);
			jcbTinBCompCode.addItem("Choose");
			while (tableEmployeeGovtCont.resultSet.next()){
				jcbTinBCompCode.addItem(Formatter.formatString(100,' ','-',tableEmployeeGovtCont.resultSet.getString("bcomtype_desc"))+"-"+tableEmployeeGovtCont.resultSet.getString("bcomtype_code"));
			}//while (tableEmployeeGovtCont.resultSet.next())

			sql = "SELECT * FROM basiscomputationtype ORDER By bcomtype_desc";
			tableEmployeeGovtCont.resultSet = tableEmployeeGovtCont.statement.executeQuery(sql);

			jcbSSSBCompCode.setFont(new Font("",Font.PLAIN, 12));
			jcbSSSBCompCode.setBackground(Color.white);
			jcbSSSBCompCode.addItem("Choose");
			while (tableEmployeeGovtCont.resultSet.next()){
				jcbSSSBCompCode.addItem(Formatter.formatString(100,' ','-',tableEmployeeGovtCont.resultSet.getString("bcomtype_desc"))+"-"+tableEmployeeGovtCont.resultSet.getString("bcomtype_code"));
			}//while (tableEmployeeGovtCont.resultSet.next())

			sql = "SELECT * FROM basiscomputationtype ORDER By bcomtype_desc";
			tableEmployeeGovtCont.resultSet = tableEmployeeGovtCont.statement.executeQuery(sql);

			jcbHDMFBCompCode.setFont(new Font("",Font.PLAIN, 12));
			jcbHDMFBCompCode.setBackground(Color.white);
			jcbHDMFBCompCode.addItem("Choose");
			while (tableEmployeeGovtCont.resultSet.next()){
				jcbHDMFBCompCode.addItem(Formatter.formatString(100,' ','-',tableEmployeeGovtCont.resultSet.getString("bcomtype_desc"))+"-"+tableEmployeeGovtCont.resultSet.getString("bcomtype_code"));
			}//while (tableEmployeeGovtCont.resultSet.next())

			sql = "SELECT * FROM basiscomputationtype ORDER By bcomtype_desc";
			tableEmployeeGovtCont.resultSet = tableEmployeeGovtCont.statement.executeQuery(sql);

			jcbPHHBCompCode.setFont(new Font("",Font.PLAIN, 12));
			jcbPHHBCompCode.setBackground(Color.white);
			jcbPHHBCompCode.addItem("Choose");
			while (tableEmployeeGovtCont.resultSet.next()) {
				jcbPHHBCompCode.addItem(Formatter.formatString(100,' ','-',tableEmployeeGovtCont.resultSet.getString("bcomtype_desc"))+"-"+tableEmployeeGovtCont.resultSet.getString("bcomtype_code"));
			}//while (tableEmployeeGovtCont.resultSet.next())

			jcbZeroSSSPhHdmf.setFont(new Font("",Font.PLAIN, 12));
			jcbZeroSSSPhHdmf.setBackground(Color.white);
			jcbZeroSSSPhHdmf.addItem("Choose");
			jcbZeroSSSPhHdmf.addItem("Yes");
			jcbZeroSSSPhHdmf.addItem("No");

			
			lblEmployee.setBounds(10,10,400,20);
			lblTaxStatus.setBounds(18,50,100,20);
			lblTinNo.setBounds(18,80,100,20);
			lblBasisCompWTax.setBounds(270,80,150,20);
			lblSSSID.setBounds(18,110,150,20);
			lblBasisCompSSS.setBounds(270,110,150,20);
			lblHDMFID.setBounds(18,140,150,20);
			lblBasisCompHDMF.setBounds(270,140,150,20);
			lblPhilhealthID.setBounds(18,170,150,20);
			lblBasisCompPhilhealth.setBounds(270,170,150,20);
			lblZeroSSSPhHdmf.setBounds(18,200,150,20);
			lblAddtlHdmf.setBounds(18,230,150,20);

			jcbWTaxStatus.setBounds(110,50,270,20);
			txtTinNo.setBounds(110,80,120,20);
			txtTinNo.setDisabledTextColor(new Color(139,113,113));
			jcbTinBCompCode.setBounds(422,80,250,20);
			txtSSSId.setBounds(110,110,120,20);
			txtSSSId.setDisabledTextColor(new Color(139,113,113));
			jcbSSSBCompCode.setBounds(422,110,250,20);
			txtHDMFId.setBounds(110,140,120,20);
			txtHDMFId.setDisabledTextColor(new Color(139,113,113));
			jcbHDMFBCompCode.setBounds(422,140,250,20);
			txtPhilhealthId.setBounds(110,170,120,20);
			txtPhilhealthId.setDisabledTextColor(new Color(139,113,113));
			jcbPHHBCompCode.setBounds(422,170,250,20);
			jcbZeroSSSPhHdmf.setBounds(190,200,80,20);
			txtAddtlHDMF.setBounds(120,230,80,20);
			txtAddtlHDMF.setDisabledTextColor(new Color(139,113,113));
			txtAddtlHDMF.setHorizontalAlignment(JTextField.TRAILING);
			
			jbtnedit.setBounds(160,270,80,22);
			jbtnclose.setBounds(460,270,80,22);

			tableEmployeeGovtCont.resultSet = tableEmployeeGovtCont.statement.executeQuery(DEFAULT_QUERY);
			
			setFieldValue();
			
			//add objects to container
			container.add(lblEmployee);
			container.add(lblTinNo);
			container.add(txtTinNo);
			container.add(lblTaxStatus);
			container.add(jcbWTaxStatus);
			container.add(lblBasisCompWTax);
			container.add(jcbTinBCompCode);
			container.add(lblSSSID);
			container.add(txtSSSId);
			container.add(lblBasisCompSSS);
			container.add(jcbSSSBCompCode);
			container.add(lblHDMFID);
			container.add(txtHDMFId);
			container.add(lblBasisCompHDMF);
			container.add(jcbHDMFBCompCode);
			container.add(lblPhilhealthID);
			container.add(txtPhilhealthId);
			container.add(lblBasisCompPhilhealth);
			container.add(jcbPHHBCompCode);
			container.add(lblZeroSSSPhHdmf);
			container.add(jcbZeroSSSPhHdmf);
			container.add(lblAddtlHdmf);
			container.add(txtAddtlHDMF);
			container.add(jbtnedit);
			container.add(jbtnclose);        

			//Disabled text fields
			disableFields();

			jcbWTaxStatus.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbWTaxStatus.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						WTaxStatus = "";
					}
					else {
						WTaxStatus = jcbWTaxStatus.getSelectedItem().toString().substring(101);
					}
				}
			});

			jcbTinBCompCode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbTinBCompCode.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						WTaxType = "";
					}
					else {
						WTaxType = jcbTinBCompCode.getSelectedItem().toString().substring(101);
					}
				}
			});

			jcbSSSBCompCode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbSSSBCompCode.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						SSSType = "";
					}
					else {
						SSSType = jcbSSSBCompCode.getSelectedItem().toString().substring(101);
					}
				}
			});

			jcbHDMFBCompCode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbHDMFBCompCode.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						HDMFType = "";
					}
					else {
						HDMFType = jcbHDMFBCompCode.getSelectedItem().toString().substring(101);
					}
				}
			});

			jcbPHHBCompCode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbPHHBCompCode.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						PhilHealthType = "";
					}
					else {
						PhilHealthType = jcbPHHBCompCode.getSelectedItem().toString().substring(101);
					}
				}
			});

			jcbZeroSSSPhHdmf.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbZeroSSSPhHdmf.getSelectedItem().toString().equalsIgnoreCase("Yes") == true) {
						txtAddtlHDMF.setText("");
						txtAddtlHDMF.setEnabled(false);
						ZeroSSSPhHdmf = "Y";
					}
					else if (jcbZeroSSSPhHdmf.getSelectedItem().toString().equalsIgnoreCase("No") == true) {
						txtAddtlHDMF.setText(tmpAmount);
						txtAddtlHDMF.setEnabled(true);
						ZeroSSSPhHdmf = "N";
					}
					else {
						txtAddtlHDMF.setText("");
						txtAddtlHDMF.setEnabled(false);
						ZeroSSSPhHdmf = "";
					}
				}
			});

			txtAddtlHDMF.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtAddtlHDMF.getText().indexOf(".") == -1) {
								if (txtAddtlHDMF.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtAddtlHDMF.getText().length() > (txtAddtlHDMF.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtAddtlHDMF.addFocusListener(new FocusListener() {				
				public void focusLost(FocusEvent arg0) {
					if (txtAddtlHDMF.getText().trim().length() > 0) {
						tmpAmount = txtAddtlHDMF.getText();
					}
				}
				
				public void focusGained(FocusEvent arg0) {}
			});
			
			//DELETE BUTTON LISTENER
			jbtnclose.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String dcaption = jbtnclose.getText();

					if (dcaption == "Cancel"){
						setCancel();
					}
					else if (dcaption == "Close") {
						EmployeeMaint.subMain1 = 0;
						EmployeeMaint.jbtnadd.setEnabled(true);
						EmployeeMaint.jbtnedit.setEnabled(true);
						EmployeeMaint.jbtndelete.setEnabled(true);
						EmployeeMaint.jbtngovt.setEnabled(true);
						EmployeeMaint.jbtnprevemp.setEnabled(true);
						EmployeeGovtContMaint.iFrame.dispose();
					}//else if (dcaption == "Close"){
				}//public void mouseClicked(MouseEvent e)
			});

			//EDIT BUTTON LISTENER
			jbtnedit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					String ecaption = jbtnedit.getText();
					if (ecaption == "Edit") {
						if (recordExist) {
							// start indicating lock
							LockHandler.initializeLock(container);

							//wait for lock indicator to invoke
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									try {
										//row level locking
										LockHandler.startLock(DBConnect.SelectForUpdate("empgovcont",
												"*",
												"empid = '"+EmpID+"'"));
										//refresh
										tableEmployeeGovtCont.setQuery(DEFAULT_QUERY);

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
						}
						jbtnedit.setText("Update");
						jbtnedit.setMnemonic('U');
						jbtnclose.setText("Cancel");
						jbtnclose.setMnemonic('C');

						enableFields();

						jcbWTaxStatus.grabFocus();

					}
					else if (ecaption == "Update") {
						String AddtlHDMF = "0";
						if (txtAddtlHDMF.getText().trim().length() > 0) {
							AddtlHDMF = txtAddtlHDMF.getText().trim().replaceAll(",","");
						}
						if (WTaxStatus.trim().length() == 0) {
							Message.messageError("Invalid input for W/Tax Status field.");
							jcbWTaxStatus.grabFocus();
						}
						else if (txtTinNo.getText().trim().length() == 0) {
							Message.messageError("Invalid input for TIN Number field.");
							txtTinNo.grabFocus();
						}
						else if (WTaxType.trim().length() == 0) {
							Message.messageError("Invalid input for W/Tax Basis of Computation field.");
							jcbTinBCompCode.grabFocus();
						}
						else if (txtSSSId.getText().trim().length() == 0) {
							Message.messageError("Invalid input for SSS ID field.");
							txtSSSId.grabFocus();
						}
						else if (SSSType.trim().length() == 0) {
							Message.messageError("Invalid input for SSS Basis of Computation field.");
							jcbSSSBCompCode.grabFocus();
						}
						else if (txtHDMFId.getText().trim().length() == 0) {
							Message.messageError("Invalid input for HDMF ID field.");
							txtHDMFId.grabFocus();
						}
						else if (HDMFType.trim().length() == 0) {
							Message.messageError("Invalid input for HDMF Basis of Computation field.");
							jcbHDMFBCompCode.grabFocus();
						}
						else if (txtPhilhealthId.getText().trim().length() == 0) {
							Message.messageError("Invalid input for PhilHealth ID field.");
							txtPhilhealthId.grabFocus();
						}
						else if (PhilHealthType.trim().length() == 0) {
							Message.messageError("Invalid input for PhilHealth Basis of Computation field.");
							jcbPHHBCompCode.grabFocus();
						}
						else if (ZeroSSSPhHdmf.trim().length() == 0) {
							Message.messageError("Invalid input for Zero SSS/PhilHealth/HDMF field.");
							jcbZeroSSSPhHdmf.grabFocus();
						}
						else {
							if (recordExist) {
								try {
									String e_sql = DBConnect.Update("empgovcont",
											"tin_no = '"+txtTinNo.getText().trim()+"', " +
											"wtxstat_code = '"+WTaxStatus+"', " +
											"tin_bcomp_code = '"+WTaxType+"', " +
											"sss_id = '"+txtSSSId.getText().trim()+"', " +
											"sss_bcomp_code = '"+SSSType+"', " +
											"hdmf_id = '"+txtHDMFId.getText().trim()+"', " +
											"hdmf_bcomp_code = '"+HDMFType+"', " +
											"philhealth_id = '"+txtPhilhealthId.getText().trim()+"', " +
											"phh_bcomp_code = '"+PhilHealthType+"', " +
											"zero_sssphhdmf = '"+ZeroSSSPhHdmf+"', " +
											"additional_hdmf = '"+AddtlHDMF+"'",
											"empid = '" +EmpID+"'"
											);
									//unlock mode then update
									LockHandler.removeLockAndUpdate(e_sql);
								} catch (SQLException e1) {
									e1.printStackTrace();
								}
							}//ENDof if (recordExist) {
							else {
								try {
									String save_sql = DBConnect.Insert("empgovcont",
											"empid,tin_no,wtxstat_code,tin_bcomp_code,"+
											"sss_id,sss_bcomp_code,hdmf_id,hdmf_bcomp_code,"+
											"philhealth_id,phh_bcomp_code,zero_sssphhdmf,additional_hdmf",
											"'"+DBConnect.clean(EmpID)+"', " +
											"'"+txtTinNo.getText().trim()+"', " +
											"'"+WTaxStatus+"', " +
											"'"+WTaxType+"', " +
											"'"+txtSSSId.getText().trim()+"', " +
											"'"+SSSType+"', " +
											"'"+txtHDMFId.getText().trim()+"', " +
											"'"+HDMFType+"', " +
											"'"+txtPhilhealthId.getText().trim()+"', " +
											"'"+PhilHealthType+"', " +
											"'"+ZeroSSSPhHdmf+"', " +
											"'"+AddtlHDMF+"' "
											);
									tableEmployeeGovtCont.setInsert(save_sql);
								} catch (IllegalStateException e1) {
									e1.printStackTrace();
								} catch (SQLException e1) {
									e1.printStackTrace();
								}
							}//ENDof else if (recordExist) {
							
							try {
								tableEmployeeGovtCont.setQuery(DEFAULT_QUERY);
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

			setSize(694,330);
//			setSize(433,430);
			setVisible(true);

		}//try
		catch ( ClassNotFoundException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("EmployeeGovtContMaint", "EmployeeGovtContMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch ( SQLException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("EmployeeGovtContMaint", "EmployeeGovtContMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch (Exception e) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("EmployeeGovtContMaint", "EmployeeGovtContMaint", traceWriter.toString());        
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
		jcbWTaxStatus.setEnabled(true);
		txtTinNo.setEnabled(true);
		jcbTinBCompCode.setEnabled(true);
		txtSSSId.setEnabled(true);
		jcbSSSBCompCode.setEnabled(true);
		txtHDMFId.setEnabled(true);
		jcbHDMFBCompCode.setEnabled(true);
		txtPhilhealthId.setEnabled(true);
		jcbPHHBCompCode.setEnabled(true);
		jcbZeroSSSPhHdmf.setEnabled(true);
		if (ZeroSSSPhHdmf.equalsIgnoreCase("N") == true) {
			txtAddtlHDMF.setEnabled(true);
		}
		else {
			txtAddtlHDMF.setText("");
			txtAddtlHDMF.setEnabled(false);
		}
	}//ENDof private void enableFields() {

	private void disableFields() {
		jcbWTaxStatus.setEnabled(false);
		txtTinNo.setEnabled(false);
		jcbTinBCompCode.setEnabled(false);
		txtSSSId.setEnabled(false);
		jcbSSSBCompCode.setEnabled(false);
		txtHDMFId.setEnabled(false);
		jcbHDMFBCompCode.setEnabled(false);
		txtPhilhealthId.setEnabled(false);
		jcbPHHBCompCode.setEnabled(false);
		jcbZeroSSSPhHdmf.setEnabled(false);
		txtAddtlHDMF.setEnabled(false);
	}//ENDof private void disableFields() {

	private void clearFields() {
		jcbWTaxStatus.setSelectedItem("Choose");
		txtTinNo.setText("");
		jcbTinBCompCode.setSelectedItem("Choose");
		txtSSSId.setText("");
		jcbSSSBCompCode.setSelectedItem("Choose");
		txtHDMFId.setText("");
		jcbHDMFBCompCode.setSelectedItem("Choose");
		txtPhilhealthId.setText("");
		jcbPHHBCompCode.setSelectedItem("Choose");
		jcbZeroSSSPhHdmf.setSelectedItem("Choose");
		txtAddtlHDMF.setText("");
		txtAddtlHDMF.setEnabled(false);
	}//ENDof private void clearFields() {

	private void setFieldValue() {
		try {
			tableEmployeeGovtCont.resultSet.last();
			if (tableEmployeeGovtCont.resultSet.getRow() > 0) {
				recordExist = true;
				tableEmployeeGovtCont.resultSet.beforeFirst();
				while (tableEmployeeGovtCont.resultSet.next()) {
					txtTinNo.setText(tableEmployeeGovtCont.resultSet.getString("tin_no"));
					WTaxStatus = "";
					jcbWTaxStatus.setSelectedIndex(0);
					for (int ii=0; ii<=jcbWTaxStatus.getItemCount()-1; ii++) {
						if (jcbWTaxStatus.getItemAt(ii).toString().length() > 50) {
							if (tableEmployeeGovtCont.resultSet.getString("wtxstat_code").equalsIgnoreCase(jcbWTaxStatus.getItemAt(ii).toString().substring(101)) == true) {
								WTaxStatus = tableEmployeeGovtCont.resultSet.getString("wtxstat_code");
								jcbWTaxStatus.setSelectedIndex(ii);
							}
						}
					}//ENDof for (int ii=0; ii<=jcbWTaxStatus.getItemCount()-1; ii++) {
					WTaxType = "";
					jcbTinBCompCode.setSelectedIndex(0);
					for (int ii=0; ii<=jcbTinBCompCode.getItemCount()-1; ii++) {
						if (jcbTinBCompCode.getItemAt(ii).toString().length() > 50) {
							if (tableEmployeeGovtCont.resultSet.getString("tin_bcomp_code").equalsIgnoreCase(jcbTinBCompCode.getItemAt(ii).toString().substring(101)) == true) {
								WTaxType = tableEmployeeGovtCont.resultSet.getString("tin_bcomp_code");
								jcbTinBCompCode.setSelectedIndex(ii);
							}
						}
					}//ENDof for (int ii=0; ii<=jcbTinBCompCode.getItemCount()-1; ii++) {
					txtSSSId.setText(tableEmployeeGovtCont.resultSet.getString("sss_id"));
					SSSType = "";
					jcbSSSBCompCode.setSelectedIndex(0);
					for (int ii=0; ii<=jcbSSSBCompCode.getItemCount()-1; ii++) {
						if (jcbSSSBCompCode.getItemAt(ii).toString().length() > 50) {
							if (tableEmployeeGovtCont.resultSet.getString("sss_bcomp_code").equalsIgnoreCase(jcbSSSBCompCode.getItemAt(ii).toString().substring(101)) == true) {
								SSSType = tableEmployeeGovtCont.resultSet.getString("sss_bcomp_code");
								jcbSSSBCompCode.setSelectedIndex(ii);
							}
						}
					}//ENDof for (int ii=0; ii<=jcbSSSBCompCode.getItemCount()-1; ii++) {
					txtHDMFId.setText(tableEmployeeGovtCont.resultSet.getString("hdmf_id"));
					HDMFType = "";
					jcbHDMFBCompCode.setSelectedIndex(0);
					for (int ii=0; ii<=jcbHDMFBCompCode.getItemCount()-1; ii++) {
						if (jcbHDMFBCompCode.getItemAt(ii).toString().length() > 50) {
							if (tableEmployeeGovtCont.resultSet.getString("hdmf_bcomp_code").equalsIgnoreCase(jcbHDMFBCompCode.getItemAt(ii).toString().substring(101)) == true) {
								HDMFType = tableEmployeeGovtCont.resultSet.getString("hdmf_bcomp_code");
								jcbHDMFBCompCode.setSelectedIndex(ii);
							}
						}
					}//ENDof for (int ii=0; ii<=jcbHDMFBCompCode.getItemCount()-1; ii++) {
					txtPhilhealthId.setText(tableEmployeeGovtCont.resultSet.getString("philhealth_id"));
					PhilHealthType = "";
					jcbPHHBCompCode.setSelectedIndex(0);
					for (int ii=0; ii<=jcbPHHBCompCode.getItemCount()-1; ii++) {
						if (jcbPHHBCompCode.getItemAt(ii).toString().length() > 50) {
							if (tableEmployeeGovtCont.resultSet.getString("phh_bcomp_code").equalsIgnoreCase(jcbPHHBCompCode.getItemAt(ii).toString().substring(101)) == true) {
								PhilHealthType = tableEmployeeGovtCont.resultSet.getString("phh_bcomp_code");
								jcbPHHBCompCode.setSelectedIndex(ii);
							}
						}
					}//ENDof for (int ii=0; ii<=jcbPHHBCompCode.getItemCount()-1; ii++) {
					if (tableEmployeeGovtCont.resultSet.getString("zero_sssphhdmf").equalsIgnoreCase("Y") == true) {
						txtAddtlHDMF.setEnabled(false);
						ZeroSSSPhHdmf = "Y";
						jcbZeroSSSPhHdmf.setSelectedItem("Yes");
					} 
					else if (tableEmployeeGovtCont.resultSet.getString("zero_sssphhdmf").equalsIgnoreCase("N") == true) {
						txtAddtlHDMF.setEnabled(true);
						ZeroSSSPhHdmf = "N";
						jcbZeroSSSPhHdmf.setSelectedItem("No");
					} 
					else {
						txtAddtlHDMF.setEnabled(false);
						ZeroSSSPhHdmf = "";
						jcbZeroSSSPhHdmf.setSelectedItem("Choose");
					}			
					txtAddtlHDMF.setText(tableEmployeeGovtCont.resultSet.getString("addtl_hdmf"));
					tmpAmount = tableEmployeeGovtCont.resultSet.getString("addtl_hdmf");
				}//while (tableEmployeeGovtCont.resultSet.next())
			}
			else {
				recordExist = false;
				clearFields();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void setFieldValue() {
	
	private void setEmployee(String empid, String emp_name) {
		lblEmployee.setText(empid+": "+emp_name);
		EmpID = empid;
		DEFAULT_QUERY = DBConnect.Select("empgovcont ",
							 "tin_no, "+					
							 "wtxstat_code, "+
							 "tin_bcomp_code, "+
							 "sss_id, "+
							 "sss_bcomp_code, "+
							 "hdmf_id, "+
							 "hdmf_bcomp_code, "+
							 "philhealth_id, "+
							 "phh_bcomp_code, "+
							 "zero_sssphhdmf, "+
							 "FORMAT(additional_hdmf,2) AS addtl_hdmf ",
							 "empid = '"+empid+"'",
							 null);
	}//ENDof private void setTaxTable(String empid, String emp_name) {

}//ENDof public class CustomerMaint extends JInternalFrame {
