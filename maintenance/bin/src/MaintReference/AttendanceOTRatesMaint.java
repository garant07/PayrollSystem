package maintenance.bin.src.MaintReference;

/**
 * Author:  Norberto L. Silva
 * Date:    March 22, 2012
 * Company: Applied Ideas, Inc 
 * Program: Attendance OT Rates Maintenance ==-> Payroll System
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

public class AttendanceOTRatesMaint extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	static String DEFAULT_QUERY = "SELECT column_name, data_type, column_type, column_default "+
								  "  FROM information_schema.columns "+
								  " WHERE table_schema = '"+DBConnect.dbName+"'"+
								  "   AND table_name = 'attendance'"+
								  " ORDER By ordinal_position";

	private ResultSetTableModel tableAttendanceOTRates;

	Container container;
	Message msg = new Message();

	JInternalFrame j;
	
	//variable for buttons
	private JButton jbtnedit;
	private JButton jbtncancel;

	//variable for user entry
	private JComboBox jcbRegularDay						= new JComboBox();
	private JComboBox jcbRegularDayExcess				= new JComboBox();
	private JComboBox jcbLegalHoliday					= new JComboBox();
	private JComboBox jcbLegalHolidayExcess				= new JComboBox();
	private JComboBox jcbSpecialHoliday					= new JComboBox();
	private JComboBox jcbSpecialHolidayExcess			= new JComboBox();
	private JComboBox jcbRestDay						= new JComboBox();
	private JComboBox jcbRestDayExcess					= new JComboBox();
	private JComboBox jcbRestDayLegalHoliday			= new JComboBox();
	private JComboBox jcbRestDayLegalHolidayExcess		= new JComboBox();
	private JComboBox jcbRestDaySpecialHoliday			= new JComboBox();
	private JComboBox jcbRestDaySpecialHolidayExcess	= new JComboBox();
	private JComboBox jcbNightDiff1						= new JComboBox();
	private JComboBox jcbNightDiff2						= new JComboBox();
	private JComboBox jcbDaysAbsent						= new JComboBox();
	private JComboBox jcbTardiness						= new JComboBox();
	private JComboBox jcbUndertime						= new JComboBox();

	public AttendanceOTRatesMaint() {
		super ("Attendance OT Rates Maintenance", false, true, false, false);
		this.setName("AttendanceOTRatesmaint");
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
			tableAttendanceOTRates = new ResultSetTableModel( DEFAULT_QUERY );

			//this.setAlwaysOnTop(true);
			container = this.getContentPane();
			container.setLayout(null);

			//Set frame objects
			jbtnedit = new JButton("Edit");
			jbtnedit.setMnemonic('E');
			jbtncancel = new JButton("Cancel");
			jbtncancel.setMnemonic('C');
			
			JLabel lblRegularDay = new JLabel("Regular Day");
			JLabel lblRegularDayExcess = new JLabel("Regular Day in Excess of 8 Hours");
			JLabel lblLegalHoliday = new JLabel("Legal Holiday");
			JLabel lblLegalHolidayExcess = new JLabel("Legal Holiday in Excess of 8 Hours");
			JLabel lblSpecialHoliday = new JLabel("Special Holiday");
			JLabel lblSpecialHolidayExcess = new JLabel("Special Holiday in Excess of 8 Hours");
			JLabel lblRestDay = new JLabel("Rest Day");
			JLabel lblRestDayExcess = new JLabel("Rest Day in Excess of 8 Hours");
			JLabel lblRestDayLegalHoliday = new JLabel("Rest Day on a Legal Holiday");
			JLabel lblRestDayLegalHolidayExcess = new JLabel("Rest Day on a Legal Holiday in Excess of 8 Hours");
			JLabel lblRestDaySpecialHoliday = new JLabel("Rest Day on a Special Holiday");
			JLabel lblRestDaySpecialHolidayExcess = new JLabel("Rest Day on a Special Holiday in Excess of 8 Hours");
			JLabel lblNightDiff1 = new JLabel("Night Differential 1");
			JLabel lblNightDiff2 = new JLabel("Night Differential 2");
			JLabel lblDaysAbsent = new JLabel("Days Absent");
			JLabel lblTardiness = new JLabel("Tardiness");
			JLabel lblUndertime = new JLabel("Undertime");
			
			
			String sql = "SELECT * FROM otrates ORDER By otrates_desc";
			tableAttendanceOTRates.resultSet = tableAttendanceOTRates.statement.executeQuery(sql);

			jcbRegularDay.setFont(new Font("",Font.PLAIN, 12));
			jcbRegularDay.setBackground(Color.white);
			jcbRegularDay.addItem("Choose");
			while (tableAttendanceOTRates.resultSet.next()){
				jcbRegularDay.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("otrates_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("otrates_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			jcbRegularDayExcess.setFont(new Font("",Font.PLAIN, 12));
			jcbRegularDayExcess.setBackground(Color.white);
			jcbRegularDayExcess.addItem("Choose");
			tableAttendanceOTRates.resultSet.beforeFirst();
			while (tableAttendanceOTRates.resultSet.next()){
				jcbRegularDayExcess.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("otrates_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("otrates_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			jcbLegalHoliday.setFont(new Font("",Font.PLAIN, 12));
			jcbLegalHoliday.setBackground(Color.white);
			jcbLegalHoliday.addItem("Choose");
			tableAttendanceOTRates.resultSet.beforeFirst();
			while (tableAttendanceOTRates.resultSet.next()){
				jcbLegalHoliday.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("otrates_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("otrates_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			jcbLegalHolidayExcess.setFont(new Font("",Font.PLAIN, 12));
			jcbLegalHolidayExcess.setBackground(Color.white);
			jcbLegalHolidayExcess.addItem("Choose");
			tableAttendanceOTRates.resultSet.beforeFirst();
			while (tableAttendanceOTRates.resultSet.next()){
				jcbLegalHolidayExcess.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("otrates_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("otrates_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			jcbSpecialHoliday.setFont(new Font("",Font.PLAIN, 12));
			jcbSpecialHoliday.setBackground(Color.white);
			jcbSpecialHoliday.addItem("Choose");
			tableAttendanceOTRates.resultSet.beforeFirst();
			while (tableAttendanceOTRates.resultSet.next()){
				jcbSpecialHoliday.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("otrates_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("otrates_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			jcbSpecialHolidayExcess.setFont(new Font("",Font.PLAIN, 12));
			jcbSpecialHolidayExcess.setBackground(Color.white);
			jcbSpecialHolidayExcess.addItem("Choose");
			tableAttendanceOTRates.resultSet.beforeFirst();
			while (tableAttendanceOTRates.resultSet.next()){
				jcbSpecialHolidayExcess.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("otrates_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("otrates_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			jcbRestDay.setFont(new Font("",Font.PLAIN, 12));
			jcbRestDay.setBackground(Color.white);
			jcbRestDay.addItem("Choose");
			tableAttendanceOTRates.resultSet.beforeFirst();
			while (tableAttendanceOTRates.resultSet.next()){
				jcbRestDay.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("otrates_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("otrates_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			jcbRestDayExcess.setFont(new Font("",Font.PLAIN, 12));
			jcbRestDayExcess.setBackground(Color.white);
			jcbRestDayExcess.addItem("Choose");
			tableAttendanceOTRates.resultSet.beforeFirst();
			while (tableAttendanceOTRates.resultSet.next()){
				jcbRestDayExcess.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("otrates_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("otrates_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			jcbRestDayLegalHoliday.setFont(new Font("",Font.PLAIN, 12));
			jcbRestDayLegalHoliday.setBackground(Color.white);
			jcbRestDayLegalHoliday.addItem("Choose");
			tableAttendanceOTRates.resultSet.beforeFirst();
			while (tableAttendanceOTRates.resultSet.next()){
				jcbRestDayLegalHoliday.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("otrates_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("otrates_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			jcbRestDayLegalHolidayExcess.setFont(new Font("",Font.PLAIN, 12));
			jcbRestDayLegalHolidayExcess.setBackground(Color.white);
			jcbRestDayLegalHolidayExcess.addItem("Choose");
			tableAttendanceOTRates.resultSet.beforeFirst();
			while (tableAttendanceOTRates.resultSet.next()){
				jcbRestDayLegalHolidayExcess.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("otrates_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("otrates_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			jcbRestDaySpecialHoliday.setFont(new Font("",Font.PLAIN, 12));
			jcbRestDaySpecialHoliday.setBackground(Color.white);
			jcbRestDaySpecialHoliday.addItem("Choose");
			tableAttendanceOTRates.resultSet.beforeFirst();
			while (tableAttendanceOTRates.resultSet.next()){
				jcbRestDaySpecialHoliday.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("otrates_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("otrates_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			jcbRestDaySpecialHolidayExcess.setFont(new Font("",Font.PLAIN, 12));
			jcbRestDaySpecialHolidayExcess.setBackground(Color.white);
			jcbRestDaySpecialHolidayExcess.addItem("Choose");
			tableAttendanceOTRates.resultSet.beforeFirst();
			while (tableAttendanceOTRates.resultSet.next()){
				jcbRestDaySpecialHolidayExcess.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("otrates_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("otrates_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			jcbNightDiff1.setFont(new Font("",Font.PLAIN, 12));
			jcbNightDiff1.setBackground(Color.white);
			jcbNightDiff1.addItem("Choose");
			tableAttendanceOTRates.resultSet.beforeFirst();
			while (tableAttendanceOTRates.resultSet.next()){
				jcbNightDiff1.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("otrates_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("otrates_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			jcbNightDiff2.setFont(new Font("",Font.PLAIN, 12));
			jcbNightDiff2.setBackground(Color.white);
			jcbNightDiff2.addItem("Choose");
			tableAttendanceOTRates.resultSet.beforeFirst();
			while (tableAttendanceOTRates.resultSet.next()){
				jcbNightDiff2.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("otrates_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("otrates_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			sql = "SELECT * FROM deduction ORDER By deduct_desc";
			tableAttendanceOTRates.resultSet = tableAttendanceOTRates.statement.executeQuery(sql);

			jcbDaysAbsent.setFont(new Font("",Font.PLAIN, 12));
			jcbDaysAbsent.setBackground(Color.white);
			jcbDaysAbsent.addItem("Choose");
			while (tableAttendanceOTRates.resultSet.next()){
				jcbDaysAbsent.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("deduct_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("deduct_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			jcbTardiness.setFont(new Font("",Font.PLAIN, 12));
			jcbTardiness.setBackground(Color.white);
			jcbTardiness.addItem("Choose");
			tableAttendanceOTRates.resultSet.beforeFirst();
			while (tableAttendanceOTRates.resultSet.next()){
				jcbTardiness.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("deduct_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("deduct_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			jcbUndertime.setFont(new Font("",Font.PLAIN, 12));
			jcbUndertime.setBackground(Color.white);
			jcbUndertime.addItem("Choose");
			tableAttendanceOTRates.resultSet.beforeFirst();
			while (tableAttendanceOTRates.resultSet.next()){
				jcbUndertime.addItem(Formatter.formatString(100,' ','-',tableAttendanceOTRates.resultSet.getString("deduct_desc"))+"-"+tableAttendanceOTRates.resultSet.getString("deduct_code"));
			}//while (tableAttendanceOTRates.resultSet.next())

			lblRegularDay.setBounds(18,20,300,20);
			lblRegularDayExcess.setBounds(18,50,300,20);
			lblLegalHoliday.setBounds(18,80,300,20);
			lblLegalHolidayExcess.setBounds(18,110,300,20);
			lblSpecialHoliday.setBounds(18,140,300,20);
			lblSpecialHolidayExcess.setBounds(18,170,300,20);
			lblRestDay.setBounds(18,200,300,20);
			lblRestDayExcess.setBounds(18,230,300,20);
			lblRestDayLegalHoliday.setBounds(18,260,300,20);
			lblRestDayLegalHolidayExcess.setBounds(18,290,300,20);
			lblRestDaySpecialHoliday.setBounds(18,320,300,20);
			lblRestDaySpecialHolidayExcess.setBounds(18,350,300,20);
			lblNightDiff1.setBounds(18,380,300,20);
			lblNightDiff2.setBounds(18,410,300,20);
			lblDaysAbsent.setBounds(18,440,300,20);
			lblTardiness.setBounds(18,470,300,20);
			lblUndertime.setBounds(18,500,300,20);

			jcbRegularDay.setBounds(320,20,370,20);
			jcbRegularDayExcess.setBounds(320,50,370,20);
			jcbLegalHoliday.setBounds(320,80,370,20);
			jcbLegalHolidayExcess.setBounds(320,110,370,20);
			jcbSpecialHoliday.setBounds(320,140,370,20);
			jcbSpecialHolidayExcess.setBounds(320,170,370,20);
			jcbRestDay.setBounds(320,200,370,20);
			jcbRestDayExcess.setBounds(320,230,370,20);
			jcbRestDayLegalHoliday.setBounds(320,260,370,20);
			jcbRestDayLegalHolidayExcess.setBounds(320,290,370,20);
			jcbRestDaySpecialHoliday.setBounds(320,320,370,20);
			jcbRestDaySpecialHolidayExcess.setBounds(320,350,370,20);
			jcbNightDiff1.setBounds(320,380,370,20);
			jcbNightDiff2.setBounds(320,410,370,20);
			jcbDaysAbsent.setBounds(320,440,370,20);
			jcbTardiness.setBounds(320,470,370,20);
			jcbUndertime.setBounds(320,500,370,20);
						
			jbtnedit.setBounds(180,540,80,22);
			jbtncancel.setBounds(460,540,80,22);

			tableAttendanceOTRates.resultSet = tableAttendanceOTRates.statement.executeQuery(DEFAULT_QUERY);
			
			setFieldValue();
			
			//add objects to container
			container.add(lblRegularDay);
			container.add(jcbRegularDay);
			container.add(lblRegularDayExcess);
			container.add(jcbRegularDayExcess);
			container.add(lblLegalHoliday);
			container.add(jcbLegalHoliday);
			container.add(lblLegalHolidayExcess);
			container.add(jcbLegalHolidayExcess);
			container.add(lblSpecialHoliday);
			container.add(jcbSpecialHoliday);
			container.add(lblSpecialHolidayExcess);
			container.add(jcbSpecialHolidayExcess);
			container.add(lblRestDay);
			container.add(jcbRestDay);
			container.add(lblRestDayExcess);
			container.add(jcbRestDayExcess);
			container.add(lblRestDayLegalHoliday);
			container.add(jcbRestDayLegalHoliday);
			container.add(lblRestDayLegalHolidayExcess);
			container.add(jcbRestDayLegalHolidayExcess);
			container.add(lblRestDaySpecialHoliday);
			container.add(jcbRestDaySpecialHoliday);
			container.add(lblRestDaySpecialHolidayExcess);
			container.add(jcbRestDaySpecialHolidayExcess);
			container.add(lblNightDiff1);
			container.add(jcbNightDiff1);
			container.add(lblNightDiff2);
			container.add(jcbNightDiff2);
			container.add(lblDaysAbsent);
			container.add(jcbDaysAbsent);
			container.add(lblTardiness);
			container.add(jcbTardiness);
			container.add(lblUndertime);
			container.add(jcbUndertime);

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
									tableAttendanceOTRates.setQuery(DEFAULT_QUERY);
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

						jcbRegularDay.grabFocus();

					}
					else if (ecaption == "Update") {
						if (jcbRegularDay.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for Regular Day field.");
							jcbRegularDay.grabFocus();
						}
						else if (jcbRegularDayExcess.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for Regular Day in Excess of 8 Hours field.");
							jcbRegularDayExcess.grabFocus();
						}
						else if (jcbLegalHoliday.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for Legal Holiday field.");
							jcbLegalHoliday.grabFocus();
						}
						else if (jcbLegalHolidayExcess.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for Legal Holiday in Excess of 8 Hours field.");
							jcbLegalHolidayExcess.grabFocus();
						}
						else if (jcbSpecialHoliday.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for Special Holiday field.");
							jcbSpecialHolidayExcess.grabFocus();
						}
						else if (jcbSpecialHolidayExcess.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for Special Holiday in Excess of 8 Hours field.");
							jcbSpecialHolidayExcess.grabFocus();
						}
						else if (jcbRestDay.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for Rest Day field.");
							jcbRestDay.grabFocus();
						}
						else if (jcbRestDayExcess.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for Rest Day in Excess of 8 Hours field.");
							jcbRestDayExcess.grabFocus();
						}
						else if (jcbRestDayLegalHoliday.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for Rest Day on a Legal Holiday field.");
							jcbRestDayLegalHoliday.grabFocus();
						}
						else if (jcbRestDayLegalHolidayExcess.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for Rest Day on a Legal Holiday in Excess of 8 Hours field.");
							jcbRestDayLegalHolidayExcess.grabFocus();
						}
						else if (jcbRestDaySpecialHoliday.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for Rest Day on a Special Holiday field.");
							jcbRestDaySpecialHoliday.grabFocus();
						}
						else if (jcbRestDaySpecialHolidayExcess.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for Rest Day on a Special Holiday in Excess of 8 Hours field.");
							jcbRestDaySpecialHolidayExcess.grabFocus();
						}
						else if (jcbNightDiff1.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for Night Differential 1 field.");
							jcbNightDiff1.grabFocus();
						}
						else if (jcbNightDiff2.getSelectedIndex() == 0) {
							Message.messageError("Invalid input for Night Differential 2 field.");
							jcbNightDiff2.grabFocus();
						}
						else {
							
							try {
								Statement stmnt = DBConnect.getConnection().createStatement();
								String MySql = "ALTER TABLE attendance " +
											   "ALTER COLUMN regular_day_ot_otrates SET DEFAULT '"+jcbRegularDay.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN regular_day_ot_excess_otrates SET DEFAULT '"+jcbRegularDayExcess.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN legal_Holiday_ot_otrates SET DEFAULT '"+jcbLegalHoliday.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN legal_holiday_ot_excess_otrates SET DEFAULT '"+jcbLegalHolidayExcess.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN special_holiday_ot_otrates SET DEFAULT '"+jcbSpecialHoliday.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN special_holiday_ot_excess_otrates SET DEFAULT '"+jcbSpecialHolidayExcess.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN rest_day_ot_otrates SET DEFAULT '"+jcbRestDay.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN rest_day_ot_excess_otrates SET DEFAULT '"+jcbRestDayExcess.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN rest_day_legal_holiday_ot_otrates SET DEFAULT '"+jcbRestDayLegalHoliday.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN rest_day_legal_holiday_ot_excess_otrates SET DEFAULT '"+jcbRestDayLegalHolidayExcess.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN rest_day_special_holiday_ot_otrates SET DEFAULT '"+jcbRestDaySpecialHoliday.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN rest_day_special_holiday_ot_excess_otrates SET DEFAULT '"+jcbRestDaySpecialHolidayExcess.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN night_diff1_otrates SET DEFAULT '"+jcbNightDiff1.getSelectedItem().toString().substring(101).trim()+"',"+
											   "ALTER COLUMN night_diff2_otrates SET DEFAULT '"+jcbNightDiff2.getSelectedItem().toString().substring(101).trim()+"',"+
								   			   "ALTER COLUMN days_absent_deduct SET DEFAULT '"+jcbDaysAbsent.getSelectedItem().toString().substring(101).trim()+"',"+
								   			   "ALTER COLUMN tardiness_deduct SET DEFAULT '"+jcbTardiness.getSelectedItem().toString().substring(101).trim()+"',"+
								   			   "ALTER COLUMN undertime_deduct SET DEFAULT '"+jcbUndertime.getSelectedItem().toString().substring(101).trim()+"'";
								
								stmnt.execute(MySql);
								stmnt.close();
							} catch (SQLException e2) {
								e2.printStackTrace();
							}

							try {
								tableAttendanceOTRates.setQuery(DEFAULT_QUERY);
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

			setSize(710,600);
			setVisible(true);

		}//try
		catch ( ClassNotFoundException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("AttendanceOTRatesMaint", "AttendanceOTRatesMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch ( SQLException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("AttendanceOTRatesMaint", "AttendanceOTRatesMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch (Exception e) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("AttendanceOTRatesMaint", "AttendanceOTRatesMaint", traceWriter.toString());        
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
		jcbRegularDay.setEnabled(true);
		jcbRegularDayExcess.setEnabled(true);
		jcbLegalHoliday.setEnabled(true);
		jcbLegalHolidayExcess.setEnabled(true);
		jcbSpecialHoliday.setEnabled(true);
		jcbSpecialHolidayExcess.setEnabled(true);
		jcbRestDay.setEnabled(true);
		jcbRestDayExcess.setEnabled(true);
		jcbRestDayLegalHoliday.setEnabled(true);
		jcbRestDayLegalHolidayExcess.setEnabled(true);
		jcbRestDaySpecialHoliday.setEnabled(true);
		jcbRestDaySpecialHolidayExcess.setEnabled(true);
		jcbNightDiff1.setEnabled(true);
		jcbNightDiff2.setEnabled(true);
		jcbDaysAbsent.setEnabled(true);
		jcbTardiness.setEnabled(true);
		jcbUndertime.setEnabled(true);
	}//ENDof private void enableFields() {

	private void disableFields() {
		jcbRegularDay.setEnabled(false);
		jcbRegularDayExcess.setEnabled(false);
		jcbLegalHoliday.setEnabled(false);
		jcbLegalHolidayExcess.setEnabled(false);
		jcbSpecialHoliday.setEnabled(false);
		jcbSpecialHolidayExcess.setEnabled(false);
		jcbRestDay.setEnabled(false);
		jcbRestDayExcess.setEnabled(false);
		jcbRestDayLegalHoliday.setEnabled(false);
		jcbRestDayLegalHolidayExcess.setEnabled(false);
		jcbRestDaySpecialHoliday.setEnabled(false);
		jcbRestDaySpecialHolidayExcess.setEnabled(false);
		jcbNightDiff1.setEnabled(false);
		jcbNightDiff2.setEnabled(false);
		jcbDaysAbsent.setEnabled(false);
		jcbTardiness.setEnabled(false);
		jcbUndertime.setEnabled(false);
	}//ENDof private void disableFields() {

	private void clearFields() {
		jcbRegularDay.setSelectedItem("Choose");
		jcbRegularDayExcess.setSelectedItem("Choose");
		jcbLegalHoliday.setSelectedItem("Choose");
		jcbLegalHolidayExcess.setSelectedItem("Choose");
		jcbSpecialHoliday.setSelectedItem("Choose");
		jcbSpecialHolidayExcess.setSelectedItem("Choose");
		jcbRestDay.setSelectedItem("Choose");
		jcbRestDayExcess.setSelectedItem("Choose");
		jcbRestDayLegalHoliday.setSelectedItem("Choose");
		jcbRestDayLegalHolidayExcess.setSelectedItem("Choose");
		jcbRestDaySpecialHoliday.setSelectedItem("Choose");
		jcbRestDaySpecialHolidayExcess.setSelectedItem("Choose");
		jcbNightDiff1.setSelectedItem("Choose");
		jcbNightDiff2.setSelectedItem("Choose");
		jcbDaysAbsent.setSelectedItem("Choose");
		jcbTardiness.setSelectedItem("Choose");
		jcbUndertime.setSelectedItem("Choose");
	}//ENDof private void clearFields() {

	private void setFieldValue() {
		try {
			tableAttendanceOTRates.resultSet.last();
			if (tableAttendanceOTRates.resultSet.getRow() > 0) {
				tableAttendanceOTRates.resultSet.beforeFirst();
				while (tableAttendanceOTRates.resultSet.next()) {
					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("regular_day_ot_otrates") == true) {
						jcbRegularDay.setSelectedIndex(0);
						for (int ii=0; ii<=jcbRegularDay.getItemCount()-1; ii++) {
							if (jcbRegularDay.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbRegularDay.getItemAt(ii).toString().substring(101)) == true) {
										jcbRegularDay.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbRegularDay.getItemCount()-1; ii++) {
					}

					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("regular_day_ot_excess_otrates") == true) {
						jcbRegularDayExcess.setSelectedIndex(0);
						for (int ii=0; ii<=jcbRegularDayExcess.getItemCount()-1; ii++) {
							if (jcbRegularDayExcess.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbRegularDayExcess.getItemAt(ii).toString().substring(101)) == true) {
										jcbRegularDayExcess.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbRegularDayExcess.getItemCount()-1; ii++) {
					}

					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("legal_Holiday_ot_otrates") == true) {
						jcbLegalHoliday.setSelectedIndex(0);
						for (int ii=0; ii<=jcbLegalHoliday.getItemCount()-1; ii++) {
							if (jcbLegalHoliday.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbLegalHoliday.getItemAt(ii).toString().substring(101)) == true) {
										jcbLegalHoliday.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbLegalHoliday.getItemCount()-1; ii++) {
					}

					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("legal_holiday_ot_excess_otrates") == true) {
						jcbLegalHolidayExcess.setSelectedIndex(0);
						for (int ii=0; ii<=jcbLegalHolidayExcess.getItemCount()-1; ii++) {
							if (jcbLegalHolidayExcess.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbLegalHolidayExcess.getItemAt(ii).toString().substring(101)) == true) {
										jcbLegalHolidayExcess.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbLegalHolidayExcess.getItemCount()-1; ii++) {
					}

					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("special_holiday_ot_otrates") == true) {
						jcbSpecialHoliday.setSelectedIndex(0);
						for (int ii=0; ii<=jcbSpecialHoliday.getItemCount()-1; ii++) {
							if (jcbSpecialHoliday.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbSpecialHoliday.getItemAt(ii).toString().substring(101)) == true) {
										jcbSpecialHoliday.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbSpecialHoliday.getItemCount()-1; ii++) {
					}

					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("special_holiday_ot_excess_otrates") == true) {
						jcbSpecialHolidayExcess.setSelectedIndex(0);
						for (int ii=0; ii<=jcbSpecialHolidayExcess.getItemCount()-1; ii++) {
							if (jcbSpecialHolidayExcess.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbSpecialHolidayExcess.getItemAt(ii).toString().substring(101)) == true) {
										jcbSpecialHolidayExcess.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbSpecialHolidayExcess.getItemCount()-1; ii++) {
					}

					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("rest_day_ot_otrates") == true) {
						jcbRestDay.setSelectedIndex(0);
						for (int ii=0; ii<=jcbRestDay.getItemCount()-1; ii++) {
							if (jcbRestDay.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbRestDay.getItemAt(ii).toString().substring(101)) == true) {
										jcbRestDay.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbRestDay.getItemCount()-1; ii++) {
					}

					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("rest_day_ot_excess_otrates") == true) {
						jcbRestDayExcess.setSelectedIndex(0);
						for (int ii=0; ii<=jcbRestDayExcess.getItemCount()-1; ii++) {
							if (jcbRestDayExcess.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbRestDayExcess.getItemAt(ii).toString().substring(101)) == true) {
										jcbRestDayExcess.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbRestDayExcess.getItemCount()-1; ii++) {
					}

					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("rest_day_legal_holiday_ot_otrates") == true) {
						jcbRestDayLegalHoliday.setSelectedIndex(0);
						for (int ii=0; ii<=jcbRestDayLegalHoliday.getItemCount()-1; ii++) {
							if (jcbRestDayLegalHoliday.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbRestDayLegalHoliday.getItemAt(ii).toString().substring(101)) == true) {
										jcbRestDayLegalHoliday.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbRestDayLegalHoliday.getItemCount()-1; ii++) {
					}

					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("rest_day_legal_holiday_ot_excess_otrates") == true) {
						jcbRestDayLegalHolidayExcess.setSelectedIndex(0);
						for (int ii=0; ii<=jcbRestDayLegalHolidayExcess.getItemCount()-1; ii++) {
							if (jcbRestDayLegalHolidayExcess.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbRestDayLegalHolidayExcess.getItemAt(ii).toString().substring(101)) == true) {
										jcbRestDayLegalHolidayExcess.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbRestDayLegalHolidayExcess.getItemCount()-1; ii++) {
					}

					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("rest_day_special_holiday_ot_otrates") == true) {
						jcbRestDaySpecialHoliday.setSelectedIndex(0);
						for (int ii=0; ii<=jcbRestDaySpecialHoliday.getItemCount()-1; ii++) {
							if (jcbRestDaySpecialHoliday.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbRestDaySpecialHoliday.getItemAt(ii).toString().substring(101)) == true) {
										jcbRestDaySpecialHoliday.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbRestDaySpecialHoliday.getItemCount()-1; ii++) {
					}

					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("rest_day_special_holiday_ot_excess_otrates") == true) {
						jcbRestDaySpecialHolidayExcess.setSelectedIndex(0);
						for (int ii=0; ii<=jcbRestDaySpecialHolidayExcess.getItemCount()-1; ii++) {
							if (jcbRestDaySpecialHolidayExcess.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbRestDaySpecialHolidayExcess.getItemAt(ii).toString().substring(101)) == true) {
										jcbRestDaySpecialHolidayExcess.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbRestDaySpecialHolidayExcess.getItemCount()-1; ii++) {
					}

					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("night_diff1_otrates") == true) {
						jcbNightDiff1.setSelectedIndex(0);
						for (int ii=0; ii<=jcbNightDiff1.getItemCount()-1; ii++) {
							if (jcbNightDiff1.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbNightDiff1.getItemAt(ii).toString().substring(101)) == true) {
										jcbNightDiff1.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbNightDiff1.getItemCount()-1; ii++) {
					}

					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("night_diff2_otrates") == true) {
						jcbNightDiff2.setSelectedIndex(0);
						for (int ii=0; ii<=jcbNightDiff2.getItemCount()-1; ii++) {
							if (jcbNightDiff2.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbNightDiff2.getItemAt(ii).toString().substring(101)) == true) {
										jcbNightDiff2.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbNightDiff2.getItemCount()-1; ii++) {
					}

					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("days_absent_deduct") == true) {
						jcbDaysAbsent.setSelectedIndex(0);
						for (int ii=0; ii<=jcbDaysAbsent.getItemCount()-1; ii++) {
							if (jcbDaysAbsent.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbDaysAbsent.getItemAt(ii).toString().substring(101)) == true) {
										jcbDaysAbsent.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbDaysAbsent.getItemCount()-1; ii++) {
					}

					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("tardiness_deduct") == true) {
						jcbTardiness.setSelectedIndex(0);
						for (int ii=0; ii<=jcbTardiness.getItemCount()-1; ii++) {
							if (jcbTardiness.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbTardiness.getItemAt(ii).toString().substring(101)) == true) {
										jcbTardiness.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbTardiness.getItemCount()-1; ii++) {
					}

					if (tableAttendanceOTRates.resultSet.getString("column_name").equalsIgnoreCase("undertime_deduct") == true) {
						jcbUndertime.setSelectedIndex(0);
						for (int ii=0; ii<=jcbUndertime.getItemCount()-1; ii++) {
							if (jcbUndertime.getItemAt(ii).toString().length() > 50) {
								if (tableAttendanceOTRates.resultSet.getString("column_default") != null) {
									if (tableAttendanceOTRates.resultSet.getString("column_default").equalsIgnoreCase(jcbUndertime.getItemAt(ii).toString().substring(101)) == true) {
										jcbUndertime.setSelectedIndex(ii);
									}
								}
							}
						}//ENDof for (int ii=0; ii<=jcbUndertime.getItemCount()-1; ii++) {
					}

				}//while (tableAttendanceOTRates.resultSet.next())
			}
			else {
				clearFields();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void setFieldValue() {
	
}//ENDof public class CustomerMaint extends JInternalFrame {
