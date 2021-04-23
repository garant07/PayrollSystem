package maintenance.bin.src.PayrollProcessing;

/**
 * Author : Norberto L. Silva
 * Date   : March 21, 2012
 * Company: Applied Ideas, Inc 
 * Program: Payroll Processing ==-> Payroll System
 */

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import maintenance.bin.src.DBConn.DBConnect;
import maintenance.bin.src.Functions.ActionCursor;
import maintenance.bin.src.Functions.Formatter;
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.core.mainform;

public class PayrollProcessing extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	static String		MySql				= "";
	static Statement	stmnt				= null;
	static Statement	stmntTxn			= null;
	static Statement	stmntOut			= null;
	static Statement	stmntChk			= null;
	static Statement	stmntAttn			= null;
	static Statement	stmntOT				= null;
	static ResultSet	rs	 				= null;
	static ResultSet	rsTxn 				= null;
	static ResultSet	rsChk 				= null;
	static ResultSet	rsAttn 				= null;
	static ResultSet	rsOT 				= null;

	private JPanel		jContentPane		= null;
	private JLabel		lblPayrollPeriod	= null;
	private JLabel		lblFrequency		= null;
	private JLabel		lblEmployee			= null;

	private JComboBox	jcbPayrollPeriod	= null;
	private JComboBox	jcbFrequency		= null;
	private JComboBox	jcbEmployee			= null;

	private JButton		jbttnProcessPayroll = null;

	private JProgressBar progress = null;

	String	EmpID			= "";
	String	PayPeriod		= "";
	String	PayFrequency	= "";
	double	Amount			= 0;
	double	MonthlyRate		= 0;
	double	DailyRate		= 0;
	double	HourlyRate		= 0;

	NumberFormat	 num2Format	= new DecimalFormat("0.00");

	public PayrollProcessing() {
		super ("Payroll Processing", //title
				false, 				 //rezisable
				true, 				 //closable
				false, 				 //maximizable
				false); 			 //iconifiable
		this.setName("PayrollProcessing");
		initialize();
	}//ENDof public PayrollProcessing() {

	private void initialize() {
		this.setSize(503, 215);
		this.setContentPane(getJContentPane());
		this.setOpaque(true);
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		this.setClosable(true);
		this.setVisible(true);
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

	}//ENDof private void initialize() {

	public JPanel getJContentPane() {
		if (jContentPane == null) {
			lblPayrollPeriod = new JLabel();
			lblPayrollPeriod.setFont(new Font("Dialog", Font.BOLD, 12));
			lblPayrollPeriod.setHorizontalAlignment(SwingConstants.RIGHT);
			lblPayrollPeriod.setText("Payroll Period");
			lblPayrollPeriod.setBounds(new Rectangle(15,30,120,23));

			jcbPayrollPeriod = new JComboBox();
			jcbPayrollPeriod.setVisible(true);
			jcbPayrollPeriod.addItem("Choose");
			getPayrollPeriod();
			jcbPayrollPeriod.setBounds(new Rectangle(145,30,175,23));

			lblFrequency = new JLabel();
			lblFrequency.setFont(new Font("Dialog", Font.BOLD, 12));
			lblFrequency.setHorizontalAlignment(SwingConstants.RIGHT);
			lblFrequency.setText("Frequency");
			lblFrequency.setBounds(new Rectangle(15,65,120,23));

			jcbFrequency = new JComboBox();
			jcbFrequency.setVisible(true);
			jcbFrequency.addItem("Choose");
			getFrequency();
			jcbFrequency.setBounds(new Rectangle(145,65,140,23));

			lblEmployee = new JLabel();
			lblEmployee.setFont(new Font("Dialog", Font.BOLD, 12));
			lblEmployee.setHorizontalAlignment(SwingConstants.RIGHT);
			lblEmployee.setText("Employee");
			lblEmployee.setBounds(new Rectangle(15,100,120,23));

			jcbEmployee = new JComboBox();
			jcbEmployee.setVisible(true);
			jcbEmployee.addItem("All");
			getEmployee();
			jcbEmployee.setBounds(new Rectangle(145,100,300,23));

			progress = new JProgressBar();
			progress.setPreferredSize( new Dimension( 190, 20 ) );
			progress.setStringPainted(true);
			progress.setBounds(new Rectangle(0,165,503,20));
			progress.setValue( 0 );
		}
		jContentPane = new JPanel();
		jContentPane.setLayout(null);
		jContentPane.add(lblPayrollPeriod, null);
		jContentPane.add(jcbPayrollPeriod, null);
		jContentPane.add(lblFrequency, null);
		jContentPane.add(jcbFrequency, null);
		jContentPane.add(lblEmployee, null);
		jContentPane.add(jcbEmployee, null);
		jContentPane.add(getJBttnProcessPayroll(), null);
		jContentPane.add(progress, null);
		progress.setVisible(false);
		return jContentPane;
	}//ENDof public JPanel getJContentPane() {

	private void getPayrollPeriod() {
		String query  = DBConnect.Select("payperiod",
										 "payp_code, payp_desc", 
										 null, 
										 "payp_desc");
		try {
			Statement stmt = DBConnect.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()){
				jcbPayrollPeriod.addItem(Formatter.formatString(100,' ','-',rs.getString("payp_desc"))+"-"+rs.getString("payp_code"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void getPayrollPeriod() {

	private void getFrequency() {
		String query  = DBConnect.Select("frequency",
										 "freq_code, freq_desc", 
										 null, 
										 "freq_code");
		try {
			Statement stmt = DBConnect.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()){
				jcbFrequency.addItem(Formatter.formatString(100,' ','-',rs.getString("freq_desc"))+"-"+rs.getString("freq_code"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void getFrequency() {
	
	private void getEmployee() {
		String query  = DBConnect.Select("emppif",
				"CONCAT(lastname,', ',firstname,' ',middlename) as empname, empid",
				null, 
		"empname");
		try {
			Statement stmt = DBConnect.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()){
				jcbEmployee.addItem(Formatter.formatString(100,' ','-',rs.getString("empname"))+"-"+rs.getString("empid"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void getEmployee() {

	private JButton getJBttnProcessPayroll() {
		if (jbttnProcessPayroll == null) {
			jbttnProcessPayroll = new JButton();
			jbttnProcessPayroll.setText("Process Payroll");
			jbttnProcessPayroll.setMnemonic('G');
			jbttnProcessPayroll.setBounds(new Rectangle(170,135,160,23));
			jbttnProcessPayroll.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean proceed = false;
					if (jcbPayrollPeriod.getSelectedIndex() == 0) {
						proceed = false;
						Message.messageError("Please select payroll period to process.");
						jcbPayrollPeriod.grabFocus();
					}
					else if (jcbFrequency.getSelectedIndex() == 0) {
						proceed = false;
						Message.messageError("Please select payroll frequency.");
						jcbFrequency.grabFocus();
					}
					else if (chkFileIfEmpty() == false) {
						int x = Message.messageYesNo(jContentPane, "There is an unposted payroll transaction, overwrite?");
						if (x == JOptionPane.YES_OPTION) {
							proceed = true;
						} else {
							proceed = false;
						}
					}
					if (proceed) {
						ActionCursor cur = new ActionCursor();
						cur.startWaitCursor(jContentPane.getRootPane());

						payrollProcessing();

						cur.stopWaitCursor(jContentPane.getRootPane());
					}
				}	
			});
		}
		return jbttnProcessPayroll;
	}//ENDof private JButton getJBttnProcessPayroll() {


	private void payrollProcessing() {
		DecimalFormat deciFormat0 = new DecimalFormat("#,##0");

		int recCount = 0;
		int recCntr  = 0;
		String MySql = "";

		int NumDaysYear		= 0;
		int	NumDaysMonth	= 0;
		int NumHoursDay		= 0;

		try {
			stmnt	 = DBConnect.getConnection().createStatement();
			stmntTxn = DBConnect.getConnection().createStatement();
			stmntOut = DBConnect.getConnection().createStatement();
			stmntChk = DBConnect.getConnection().createStatement();
			stmntAttn= DBConnect.getConnection().createStatement();
			stmntOT	 = DBConnect.getConnection().createStatement();

			//company parameters
			MySql = "SELECT * FROM company";
			rsTxn = stmntTxn.executeQuery(MySql);
			while(rsTxn.next()) {
				NumDaysYear		= rsTxn.getInt("co_work_days_year");
				NumDaysMonth	= rsTxn.getInt("co_work_days_month");
				NumHoursDay		= rsTxn.getInt("co_work_hours_day");
			}
			rsTxn.close();

			//get OT rates
			MySql = "SELECT * FROM otrates";
			rsOT = stmntOT.executeQuery(MySql);

			if (jcbEmployee.getSelectedItem().toString().equalsIgnoreCase("All") == true) {
				MySql = "DELETE FROM payrolltxn";
				stmntOut.execute(MySql);
				MySql = "SELECT * FROM emppif ORDER By empid";
			}
			else {
				MySql = "SELECT * FROM emppif WHERE empid = '" +
				jcbEmployee.getSelectedItem().toString().substring(101)+"'";
			}

			rsTxn = stmntTxn.executeQuery(MySql);

			rsTxn.last();
			recCount = rsTxn.getRow();

			progress.setMinimum( 0 );
			progress.setMaximum( recCount );
			progress.setValue( 0 );
			progress.setVisible(true);

			rsTxn.beforeFirst();
			while (rsTxn.next()) {
				recCntr += 1;
				progress.setString("Processing Employee: "+deciFormat0.format(recCntr)+" of "+deciFormat0.format(recCount));
				progress.setValue( recCntr );
				Rectangle progressRect = progress.getBounds();
				progressRect.x = 0;
				progressRect.y = 0;
				progress.paintImmediately( progressRect );

				EmpID		= rsTxn.getString("empid");
				PayPeriod	= jcbPayrollPeriod.getSelectedItem().toString().substring(101).trim();
				PayFrequency= jcbFrequency.getSelectedItem().toString().substring(101).trim();
				
				if (rsTxn.getString("estat_code").equalsIgnoreCase("HIRE") == true) {
					MySql = "SELECT * " +
							" FROM attendance " +
							"WHERE empid = '"+EmpID+"' "+
							"  AND payp_code = '"+PayPeriod+"'";
					rsAttn = stmntAttn.executeQuery(MySql);

					if (rsTxn.getString("srtype_code").equalsIgnoreCase("MRTE") == true) {
						MonthlyRate = rsTxn.getDouble("salary");
						DailyRate	= MonthlyRate * 12 / NumDaysYear;
						HourlyRate	= DailyRate / NumHoursDay;
					}
					else {
						DailyRate	= rsTxn.getDouble("salary");
						MonthlyRate	= DailyRate / 12 * NumDaysYear;
						HourlyRate	= DailyRate / NumHoursDay;
					}

					//Create Basis Salary
					MySql = DBConnect.Select("compensation",
											 "comps_code",
											 null,
											 "comps_code");
					rs = stmnt.executeQuery(MySql);
					while(rs.next()) {
						Amount = 0;
						if (rsTxn.getString("srtype_code").equalsIgnoreCase("MRTE") == true) {
							Amount = MonthlyRate / 2;
						}
						else {
							if (rsAttn.first()) {
								Amount = HourlyRate * rsAttn.getInt("regular_hours_worked");
							}
						}
						writeRecord("A",EmpID,"C",rs.getString("comps_code"),PayPeriod,num2Format.format(Amount));
						rs.last();
					}//ENDof while(rs.next()) {
					rs.close();

					//******************************************************//
					//Compute Attendance OverTime,Absent,Tardiness,Undertime//
					//******************************************************//
					if (rsAttn.first()) {
						Amount = 0;
						//Regular Day
						if (rsAttn.getDouble("regular_day_ot") > 0) {
							computeOT(rsAttn.getString("regular_day_ot_otrates"),rsAttn.getDouble("regular_day_ot"));
						}
						//Regular Day in Excess of 8 hours
						if (rsAttn.getDouble("regular_day_ot_excess") > 0) {
							computeOT(rsAttn.getString("regular_day_ot_excess_otrates"),rsAttn.getDouble("regular_day_ot_excess"));
						}
						//Legal Holiday
						if (rsAttn.getDouble("legal_Holiday_ot") > 0) {
							computeOT(rsAttn.getString("legal_Holiday_ot_otrates"),rsAttn.getDouble("legal_Holiday_ot"));
						}
						//Legal Holiday in Excess of 8 hours
						if (rsAttn.getDouble("legal_Holiday_ot_excess") > 0) {
							computeOT(rsAttn.getString("legal_Holiday_ot_excess_otrates"),rsAttn.getDouble("legal_Holiday_ot_excess"));
						}
						//Special Holiday
						if (rsAttn.getDouble("special_holiday_ot") > 0) {
							computeOT(rsAttn.getString("special_holiday_ot_otrates"),rsAttn.getDouble("special_holiday_ot"));
						}
						//Special Holiday in Excess of 8 hours
						if (rsAttn.getDouble("special_holiday_ot_excess") > 0) {
							computeOT(rsAttn.getString("special_holiday_ot_excess_otrates"),rsAttn.getDouble("special_holiday_ot_excess"));
						}
						//Rest Day
						if (rsAttn.getDouble("rest_day_ot") > 0) {
							computeOT(rsAttn.getString("rest_day_ot_otrates"),rsAttn.getDouble("rest_day_ot"));
						}
						//Rest Day in Excess of 8 hours
						if (rsAttn.getDouble("rest_day_ot_excess") > 0) {
							computeOT(rsAttn.getString("rest_day_ot_excess_otrates"),rsAttn.getDouble("rest_day_ot_excess"));
						}
						//Rest Day Legal Holiday
						if (rsAttn.getDouble("rest_day_legal_holiday_ot") > 0) {
							computeOT(rsAttn.getString("rest_day_legal_holiday_ot_otrates"),rsAttn.getDouble("rest_day_legal_holiday_ot"));
						}
						//Rest Day Legal Holiday in Excess of 8 hours
						if (rsAttn.getDouble("rest_day_legal_holiday_ot_excess") > 0) {
							computeOT(rsAttn.getString("rest_day_legal_holiday_ot_excess_otrates"),rsAttn.getDouble("rest_day_legal_holiday_ot_excess"));
						}
						//Rest Day Special Holiday
						if (rsAttn.getDouble("rest_day_special_holiday_ot") > 0) {
							computeOT(rsAttn.getString("rest_day_special_holiday_ot_otrates"),rsAttn.getDouble("rest_day_special_holiday_ot"));
						}
						//Rest Day Special Holiday in Excess of 8 hours
						if (rsAttn.getDouble("rest_day_special_holiday_ot_excess") > 0) {
							computeOT(rsAttn.getString("rest_day_special_holiday_ot_excess_otrates"),rsAttn.getDouble("rest_day_special_holiday_ot_excess"));
						}
						//Night Differential 1
						if (rsAttn.getDouble("night_diff1") > 0) {
							computeOT(rsAttn.getString("night_diff1_otrates"),rsAttn.getDouble("night_diff1"));
						}
						//Night Differential 2
						if (rsAttn.getDouble("night_diff2") > 0) {
							computeOT(rsAttn.getString("night_diff2_otrates"),rsAttn.getDouble("night_diff2"));
						}
						//COMPUTE Deduction
						//Days Absent
						if (rsAttn.getDouble("days_absent") > 0) {
							computeDeduction(rsAttn.getString("days_absent_deduct"),rsAttn.getDouble("days_absent"));
						}
						//Tardiness
						if (rsAttn.getDouble("tardiness") > 0) {
							computeDeduction(rsAttn.getString("tardiness_deduct"),rsAttn.getDouble("tardiness"));
						}
						//Undertime
						if (rsAttn.getDouble("undertime") > 0) {
							computeDeduction(rsAttn.getString("undertime_deduct"),rsAttn.getDouble("undertime"));
						}
					}//ENDof if (rsAttn.first()) {
					//**********************************************************//
					//END Compute Attendance OverTime,Absent,Tardiness,Undertime//
					//**********************************************************//
					
					//****************************//
					//Compute Government Deduction//
					//****************************//
					MySql = DBConnect.Select("empgovcont",
							 				 "*",
							 				 "empid = '"+EmpID+"'",
							 				 null);
					rs = stmnt.executeQuery(MySql);
					if (rs.first()) {
						
						
						
						
					}//ENDof if (rs.first()) {
					rs.close();
					//********************************//
					//END Compute Government Deduction//
					//********************************//
					
					//****************************//
					//Other Compensation/Deduction//
					//****************************//
					MySql = DBConnect.Select("othercompsdeduct",
											 "comps_deduct,other_type,amount,freq_code",
											 "empid = '"+EmpID+"'",
											 null);
					rs = stmnt.executeQuery(MySql);
					while(rs.next()) {
						if (rs.getString("freq_code").equalsIgnoreCase(PayFrequency) == true) {
							writeRecord("U",EmpID,rs.getString("other_type"),rs.getString("comps_deduct"),
									PayPeriod,
									rs.getString("amount"));
						}
					}//ENDof while(rs.next()) {
					rs.close();
					//********************************//
					//END Other Compensation/Deduction//
					//********************************//

				}//ENDof if (rsTxn.getString("estat_code").equalsIgnoreCase("HIRE") == true) {
				
			}//ENDof while (rsOutTxt.next()) {

			progress.setValue( recCount );

			Message.messageInfo("Payroll processing completed.");

			progress.setVisible(false);

			rsTxn.close();
			rsAttn.close();
			rsOT.close();
			stmnt.close();
			stmntTxn.close();
			stmntOut.close();
			stmntChk.close();
			stmntAttn.close();
			stmntOT.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void processpayroll() {
 
	private void computeOT(String otCode, double otValue) {
		//System.out.println("HourlyRate="+HourlyRate);
		try {
			MySql = DBConnect.Select("otrates as ot, compensation as c",
					 				 "ot.comps_code,ot.otrates_rate,c.freq_code",
					 				 "c.comps_code = ot.comps_code AND "+
					 				 "otrates_code = '"+otCode+"'",
					 				 null);
			rs = stmnt.executeQuery(MySql);
			if (rs.first()) {
				if (rs.getString("freq_code").equalsIgnoreCase(PayFrequency) == true) {
					Amount = (HourlyRate * rs.getDouble("otrates_rate")) * otValue;
					writeRecord("A",EmpID,"C",rs.getString("comps_code"),PayPeriod,num2Format.format(Amount));
				}
			}//ENDof if (rs.first()) {
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void computeOT(String otcode) {
	
	private void computeDeduction(String deductCode, double deductValue) {
		//System.out.println("HourlyRate="+HourlyRate);
		try {
			MySql = DBConnect.Select("deduction",
					 				 "freq_code",
					 				 "deduct_code = '"+deductCode+"'",
					 				 null);
			rs = stmnt.executeQuery(MySql);
			if (rs.first()) {
				if (rs.getString("freq_code").equalsIgnoreCase(PayFrequency) == true) {
					Amount = HourlyRate * deductValue;
					writeRecord("A",EmpID,"D",deductCode,PayPeriod,num2Format.format(Amount));
				}
			}//ENDof if (rs.first()) {
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void computeDeduction() {
	
	private void writeRecord(String wtype, String empid, String txntype, String txncode, String payperiod, String amount) {
		try {
			MySql = DBConnect.Select("payrolltxn",
					"empid,amount",
					"empid = '"+empid+"' AND "+
					"txn_type = '"+txntype+"' AND "+
					"txn_code = '"+txncode+"'",
					null
			);
			rsChk = stmntChk.executeQuery(MySql);
			rsChk.last();
			if (rsChk.getRow() > 0) {
				String Amt = amount;
				if (wtype.equalsIgnoreCase("A") == false) {
					Amt = num2Format.format(rsChk.getDouble("amount")+Double.parseDouble(amount));
				}
				MySql = DBConnect.Update("payrolltxn",
						"payp_code = '"+payperiod+"',"+
						"amount = '"+Amt+"'",
						"empid = '"+empid+"' AND "+
						"txn_type = '"+txntype+"' AND "+
						"txn_code = '"+txncode+"'"
				);
			}//ENDof if (rsChk.getRow() > 0) {
			else {
				MySql = DBConnect.Insert("payrolltxn",
						"empid,txn_type,txn_code,payp_code,amount",
						"'"+empid+"',"+
						"'"+txntype+"',"+
						"'"+txncode+"',"+
						"'"+payperiod+"',"+
						"'"+amount+"'"
				);
			}//ENDof else if (rsChk.getRow() > 0) {
			stmntOut.execute(MySql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void writeRecord(String empid, String txntype, String txncode, String payperiod, double amount) {

	private boolean chkFileIfEmpty() {
		boolean FileIsEmpty = false;

		return FileIsEmpty;
	}//ENDof private boolean chkFileIfEmpty() {

	public boolean isNumeric( String sVal ) {
		try {
			Double.parseDouble(sVal);
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}//ENDof public boolean isNumeric( String sVal ) {

}//ENDof public class PayrollProcessing extends JInternalFrame {
