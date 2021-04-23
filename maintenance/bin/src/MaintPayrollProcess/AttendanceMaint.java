package maintenance.bin.src.MaintPayrollProcess;

/**
 * Author : Norberto L. Silva
 * Date   : March 16, 2012
 * Company: Applied Ideas, Inc 
 * Program: Attendance Maintenance ==-> Payroll System
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
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
import maintenance.bin.src.Functions.Formatter;
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.MyField;
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.ResultTableModel.ResultSetTableModel;
import maintenance.bin.src.core.LockHandler;
import maintenance.bin.src.core.mainform;

public class AttendanceMaint extends JInternalFrame {
	private static final long serialVersionUID = 1L;
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	static final String DEFAULT_QUERY = DBConnect.Select("attendance as at, emppif as e, payperiod as p ",
										"CONCAT(e.lastname,', ',e.firstname,' ',e.middlename) as empname, "+
										"p.payp_desc, "+
										"at.empid, "+
										"at.payp_code, "+
										"at.regular_hours_worked, "+
										"at.days_absent, "+
										"at.regular_day_ot, "+
										"at.regular_day_ot_excess, "+
										"at.legal_Holiday_ot, "+
										"at.legal_holiday_ot_excess, "+
										"at.special_holiday_ot, "+
										"at.special_holiday_ot_excess,"+
										"at.rest_day_ot, "+
										"at.rest_day_ot_excess, "+
										"at.rest_day_legal_holiday_ot, "+
										"at.rest_day_legal_holiday_ot_excess, "+
										"at.rest_day_special_holiday_ot, "+
										"at.rest_day_special_holiday_ot_excess, "+
										"at.night_diff1, "+
										"at.night_diff2, "+
										"at.tardiness, "+
										"at.undertime",
										"e.empid = at.empid AND " +
										"p.payp_code = at.payp_code",
										"payp_desc, empname");

	private ResultSetTableModel tableAttendance;
	Container container;
	Message msg = new Message();
	JTable tbl;

	//variable current cell selected
	private int tempR;
	private int tempC;
	String curRecord = "";

	//variable for buttons
	public static JButton jbtnadd;
	public static JButton jbtnedit;
	public static JButton jbtndelete;

	//variable for user entry
	JScrollPane scrollPane;
	private JComboBox jcbEmployee = new JComboBox();
	private JComboBox jcbPayPeriod = new JComboBox();
	private MyField txtRegHoursWorkedHr = new MyField(false,2);	
	private JFormattedTextField txtRegHoursWorkedMin = new JFormattedTextField(new DecimalFormat("00"));
	private JFormattedTextField txtDaysAbsent = new JFormattedTextField(new DecimalFormat("##0.00"));
	private MyField txtRegularDayHr = new MyField(false,2);	
	private JFormattedTextField txtRegularDayMin = new JFormattedTextField(new DecimalFormat("00"));
	private MyField txtRegularDayExcessHr = new MyField(false,2);	
	private JFormattedTextField txtRegularDayExcessMin = new JFormattedTextField(new DecimalFormat("00"));
	private MyField txtLegalHolidayHr = new MyField(false,2);	
	private JFormattedTextField txtLegalHolidayMin = new JFormattedTextField(new DecimalFormat("00"));
	private MyField txtLegalHolidayExcessHr = new MyField(false,2);	
	private JFormattedTextField txtLegalHolidayExcessMin = new JFormattedTextField(new DecimalFormat("00"));
	private MyField txtSpecialHolidayHr = new MyField(false,2);	
	private JFormattedTextField txtSpecialHolidayMin = new JFormattedTextField(new DecimalFormat("00"));
	private MyField txtSpecialHolidayExcessHr = new MyField(false,2);	
	private JFormattedTextField txtSpecialHolidayExcessMin = new JFormattedTextField(new DecimalFormat("00"));
	private MyField txtRestDayHr = new MyField(false,2);	
	private JFormattedTextField txtRestDayMin = new JFormattedTextField(new DecimalFormat("00"));
	private MyField txtRestDayExcessHr = new MyField(false,2);	
	private JFormattedTextField txtRestDayExcessMin = new JFormattedTextField(new DecimalFormat("00"));
	private MyField txtRestDayLegalHolidayHr = new MyField(false,2);	
	private JFormattedTextField txtRestDayLegalHolidayMin = new JFormattedTextField(new DecimalFormat("00"));
	private MyField txtRestDayLegalHolidayExcessHr = new MyField(false,2);	
	private JFormattedTextField txtRestDayLegalHolidayExcessMin = new JFormattedTextField(new DecimalFormat("00"));
	private MyField txtRestDaySpecialHolidayHr = new MyField(false,2);	
	private JFormattedTextField txtRestDaySpecialHolidayMin = new JFormattedTextField(new DecimalFormat("00"));
	private MyField txtRestDaySpecialHolidayExcessHr = new MyField(false,2);	
	private JFormattedTextField txtRestDaySpecialHolidayExcessMin = new JFormattedTextField(new DecimalFormat("00"));
	private MyField txtNightDiff1Hr = new MyField(false,2);	
	private JFormattedTextField txtNightDiff1Min = new JFormattedTextField(new DecimalFormat("00"));
	private MyField txtNightDiff2Hr = new MyField(false,2);	
	private JFormattedTextField txtNightDiff2Min = new JFormattedTextField(new DecimalFormat("00"));
	private MyField txtTardinessHr = new MyField(false,2);	
	private JFormattedTextField txtTardinessMin = new JFormattedTextField(new DecimalFormat("00"));
	private MyField txtUndertimeHr = new MyField(false,2);	
	private JFormattedTextField txtUndertimeMin = new JFormattedTextField(new DecimalFormat("00"));

	private JTextField txtsearch = new JTextField();

	SimpleDateFormat dteFormat 	= new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat strFormat 	= new SimpleDateFormat("MMMM dd, yyyy");
	NumberFormat	 numFormat 	= new DecimalFormat("00");
	NumberFormat	 num2Format	= new DecimalFormat("0.00");

	String Employee						= "";
	String PayPeriod					= "";
	double RegularHoursWorked			= 0; 
	double DaysAbsent					= 0; 
	double RegularDay					= 0;
	double RegularDayExcess				= 0;
	double LegalHoliday					= 0;
	double LegalHolidayExcess			= 0;
	double SpecialHoliday				= 0;
	double SpecialHolidayExcess			= 0;
	double RestDay						= 0;
	double RestDayExcess				= 0;
	double RestDayLegalHoliday			= 0;
	double RestDayLegalHolidayExcess	= 0;
	double RestDaySpecialHoliday		= 0;
	double RestDaySpecialHolidayExcess	= 0;
	double NightDifferential1			= 0;
	double NightDifferential2			= 0;
	double Tardiness					= 0; 
	double Undertime					= 0; 

	public AttendanceMaint() {
		super ("Attendance Maintenance", false, true, false, false);
		this.setName("attendancemaint");
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
			tableAttendance = new ResultSetTableModel( DEFAULT_QUERY );

			//this.setAlwaysOnTop(true);
			container = this.getContentPane();
			container.setLayout(null);

			//Set frame objects
			JPanel jpan = new JPanel();
			jbtnadd = new JButton("Add");
			jbtnadd.setMnemonic('A');
			jbtnadd.setToolTipText("Add Record");
			jbtnedit = new JButton("Edit");
			jbtnedit.setMnemonic('E');
			jbtnedit.setToolTipText("Edit Record");
			jbtndelete = new JButton("Delete");
			jbtndelete.setMnemonic('D');
			jbtndelete.setToolTipText("Delete Record");

			JLabel lblEmployee = new JLabel("Employee");
			JLabel lblPayPeriod = new JLabel("Cut-Off Date");
			JLabel lblRegHoursWorked = new JLabel("Regular Hours Worked");
			JLabel lblDaysAbsent = new JLabel("Days Absent");
			JLabel lblRegHoursWorkedColon = new JLabel("<html><B>:</B></html>");
			JLabel lblRegularDay = new JLabel("Regular Day OT");
			JLabel lblRegularDayColon = new JLabel("<html><B>:</B></html>");
			JLabel lblRegularDayExcess = new JLabel("Excess of 8 Hours");
			JLabel lblRegularDayExcessColon = new JLabel("<html><B>:</B></html>");
			JLabel lblLegalHoliday = new JLabel("Legal Holiday OT");
			JLabel lblLegalHolidayColon = new JLabel("<html><B>:</B></html>");
			JLabel lblLegalHolidayExcess = new JLabel("Excess of 8 Hours");
			JLabel lblLegalHolidayExcessColon = new JLabel("<html><B>:</B></html>");
			JLabel lblSpecialHoliday = new JLabel("Special Holiday OT");
			JLabel lblSpecialHolidayColon = new JLabel("<html><B>:</B></html>");
			JLabel lblSpecialHolidayExcess = new JLabel("Excess of 8 Hours");
			JLabel lblSpecialHolidayExcessColon = new JLabel("<html><B>:</B></html>");
			JLabel lblRestDay = new JLabel("Rest Day OT");
			JLabel lblRestDayColon = new JLabel("<html><B>:</B></html>");
			JLabel lblRestDayExcess = new JLabel("Excess of 8 Hours");
			JLabel lblRestDayExcessColon = new JLabel("<html><B>:</B></html>");
			JLabel lblRestDayLegalHoliday = new JLabel("Rest Day on a Legal Holiday OT");
			JLabel lblRestDayLegalHolidayColon = new JLabel("<html><B>:</B></html>");
			JLabel lblRestDayLegalHolidayExcess = new JLabel("Excess of 8 Hours");
			JLabel lblRestDayLegalHolidayExcessColon = new JLabel("<html><B>:</B></html>");
			JLabel lblRestDaySpecialHoliday = new JLabel("Rest Day on a Special Holiday OT");
			JLabel lblRestDaySpecialHolidayColon = new JLabel("<html><B>:</B></html>");
			JLabel lblRestDaySpecialHolidayExcess = new JLabel("Excess of 8 Hours");
			JLabel lblRestDaySpecialHolidayExcessColon = new JLabel("<html><B>:</B></html>");
			JLabel lblNightDiff1 = new JLabel("Night Differential 1");
			JLabel lblNightDiff1Colon = new JLabel("<html><B>:</B></html>");
			JLabel lblNightDiff2 = new JLabel("Night Differential 2");
			JLabel lblNightDiff2Colon = new JLabel("<html><B>:</B></html>");
			JLabel lblTardiness = new JLabel("Tardiness");
			JLabel lblTardinessColon = new JLabel("<html><B>:</B></html>");
			JLabel lblUndertime = new JLabel("Undertime");
			JLabel lblUndertimeColon = new JLabel("<html><B>:</B></html>");

			tbl = new JTable(tableAttendance);

			TableCellRenderer renderer = new TestRenderer();
			tbl.setDefaultRenderer(Object.class, renderer);
			scrollPane = new JScrollPane(tbl);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			tbl.setSelectionMode(0);

			//Employee
			String sql = "SELECT CONCAT(lastname,', ',firstname,' ',middlename) as empname, empid" +
			"  FROM emppif ORDER By empname";
			tableAttendance.resultSet = tableAttendance.statement.executeQuery(sql);

			jcbEmployee.setFont(new Font("",Font.PLAIN, 12));
			jcbEmployee.setBackground(Color.white);
			jcbEmployee.addItem("Choose");
			while (tableAttendance.resultSet.next()){
				jcbEmployee.addItem(Formatter.formatString(100,' ','-',tableAttendance.resultSet.getString("empname"))+"-"+tableAttendance.resultSet.getString("empid"));
			}//while (tableAttendance.resultSet.next())

			sql = "SELECT * FROM payperiod ORDER By payp_desc";
			tableAttendance.resultSet = tableAttendance.statement.executeQuery(sql);

			jcbPayPeriod.setFont(new Font("",Font.PLAIN, 12));
			jcbPayPeriod.setBackground(Color.white);
			jcbPayPeriod.addItem("Choose");
			while (tableAttendance.resultSet.next()){
				jcbPayPeriod.addItem(Formatter.formatString(100,' ','-',tableAttendance.resultSet.getString("payp_desc"))+"-"+tableAttendance.resultSet.getString("payp_code"));
			}//while (tableAttendance.resultSet.next())

			tableAttendance.resultSet = tableAttendance.statement.executeQuery(DEFAULT_QUERY);
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

			tbl.getTableHeader().setReorderingAllowed(false); //disabled column dragging

			//set display size
			scrollPane.setPreferredSize(new Dimension(580,170));
			jpan.setBounds(10,20,580,180);

			lblEmployee.setBounds(28,240,100,20);
			jcbEmployee.setBounds(120,240,270,20);
			lblPayPeriod.setBounds(28,270,100,20);
			jcbPayPeriod.setBounds(new Rectangle(120,270,200, 23));
			//Regular Hours Worked
			lblRegHoursWorked.setBounds(28,300,150,20);
			txtRegHoursWorkedHr.setBounds(225,300,25,20);
			txtRegHoursWorkedHr.setDisabledTextColor(new Color(139,113,113));
			txtRegHoursWorkedHr.setHorizontalAlignment(JTextField.TRAILING);
			lblRegHoursWorkedColon.setBounds(252,300,10,20);
			txtRegHoursWorkedMin.setBounds(256,300,25,20);
			txtRegHoursWorkedMin.setDisabledTextColor(new Color(139,113,113));
			txtRegHoursWorkedMin.setHorizontalAlignment(JTextField.TRAILING);
			//Days Absent
			lblDaysAbsent.setBounds(320,300,100,20);
			txtDaysAbsent.setBounds(435,300,50,20);
			txtDaysAbsent.setDisabledTextColor(new Color(139,113,113));
			txtDaysAbsent.setHorizontalAlignment(JTextField.TRAILING);
			//Regular Day OT
			lblRegularDay.setBounds(28,330,190,20);
			txtRegularDayHr.setBounds(225,330,25,20);
			txtRegularDayHr.setDisabledTextColor(new Color(139,113,113));
			txtRegularDayHr.setHorizontalAlignment(JTextField.TRAILING);
			lblRegularDayColon.setBounds(252,330,10,20);
			txtRegularDayMin.setBounds(256,330,25,20);
			txtRegularDayMin.setDisabledTextColor(new Color(139,113,113));
			txtRegularDayMin.setHorizontalAlignment(JTextField.TRAILING);
			lblRegularDayExcess.setBounds(320,330,100,20);
			txtRegularDayExcessHr.setBounds(435,330,25,20);
			txtRegularDayExcessHr.setDisabledTextColor(new Color(139,113,113));
			txtRegularDayExcessHr.setHorizontalAlignment(JTextField.TRAILING);
			lblRegularDayExcessColon.setBounds(462,330,10,20);
			txtRegularDayExcessMin.setBounds(466,330,25,20);
			txtRegularDayExcessMin.setDisabledTextColor(new Color(139,113,113));
			txtRegularDayExcessMin.setHorizontalAlignment(JTextField.TRAILING);
			//Legal Holiday OT
			lblLegalHoliday.setBounds(28,360,190,20);
			txtLegalHolidayHr.setBounds(225,360,25,20);
			txtLegalHolidayHr.setDisabledTextColor(new Color(139,113,113));
			txtLegalHolidayHr.setHorizontalAlignment(JTextField.TRAILING);
			lblLegalHolidayColon.setBounds(252,360,10,20);
			txtLegalHolidayMin.setBounds(256,360,25,20);
			txtLegalHolidayMin.setDisabledTextColor(new Color(139,113,113));
			txtLegalHolidayMin.setHorizontalAlignment(JTextField.TRAILING);
			lblLegalHolidayExcess.setBounds(320,360,100,20);
			txtLegalHolidayExcessHr.setBounds(435,360,25,20);
			txtLegalHolidayExcessHr.setDisabledTextColor(new Color(139,113,113));
			txtLegalHolidayExcessHr.setHorizontalAlignment(JTextField.TRAILING);
			lblLegalHolidayExcessColon.setBounds(462,360,10,20);
			txtLegalHolidayExcessMin.setBounds(466,360,25,20);
			txtLegalHolidayExcessMin.setDisabledTextColor(new Color(139,113,113));
			txtLegalHolidayExcessMin.setHorizontalAlignment(JTextField.TRAILING);
			//Special Holiday OT
			lblSpecialHoliday.setBounds(28,390,190,20);
			txtSpecialHolidayHr.setBounds(225,390,25,20);
			txtSpecialHolidayHr.setDisabledTextColor(new Color(139,113,113));
			txtSpecialHolidayHr.setHorizontalAlignment(JTextField.TRAILING);
			lblSpecialHolidayColon.setBounds(252,390,10,20);
			txtSpecialHolidayMin.setBounds(256,390,25,20);
			txtSpecialHolidayMin.setDisabledTextColor(new Color(139,113,113));
			txtSpecialHolidayMin.setHorizontalAlignment(JTextField.TRAILING);
			lblSpecialHolidayExcess.setBounds(320,390,100,20);
			txtSpecialHolidayExcessHr.setBounds(435,390,25,20);
			txtSpecialHolidayExcessHr.setDisabledTextColor(new Color(139,113,113));
			txtSpecialHolidayExcessHr.setHorizontalAlignment(JTextField.TRAILING);
			lblSpecialHolidayExcessColon.setBounds(462,390,10,20);
			txtSpecialHolidayExcessMin.setBounds(466,390,25,20);
			txtSpecialHolidayExcessMin.setDisabledTextColor(new Color(139,113,113));
			txtSpecialHolidayExcessMin.setHorizontalAlignment(JTextField.TRAILING);
			//Rest Day OT
			lblRestDay.setBounds(28,420,190,20);
			txtRestDayHr.setBounds(225,420,25,20);
			txtRestDayHr.setDisabledTextColor(new Color(139,113,113));
			txtRestDayHr.setHorizontalAlignment(JTextField.TRAILING);
			lblRestDayColon.setBounds(252,420,10,20);
			txtRestDayMin.setBounds(256,420,25,20);
			txtRestDayMin.setDisabledTextColor(new Color(139,113,113));
			txtRestDayMin.setHorizontalAlignment(JTextField.TRAILING);
			lblRestDayExcess.setBounds(320,420,100,20);
			txtRestDayExcessHr.setBounds(435,420,25,20);
			txtRestDayExcessHr.setDisabledTextColor(new Color(139,113,113));
			txtRestDayExcessHr.setHorizontalAlignment(JTextField.TRAILING);
			lblRestDayExcessColon.setBounds(462,420,10,20);
			txtRestDayExcessMin.setBounds(466,420,25,20);
			txtRestDayExcessMin.setDisabledTextColor(new Color(139,113,113));
			txtRestDayExcessMin.setHorizontalAlignment(JTextField.TRAILING);
			//Rest Day on a Legal Holiday OT
			lblRestDayLegalHoliday.setBounds(28,450,190,20);
			txtRestDayLegalHolidayHr.setBounds(225,450,25,20);
			txtRestDayLegalHolidayHr.setDisabledTextColor(new Color(139,113,113));
			txtRestDayLegalHolidayHr.setHorizontalAlignment(JTextField.TRAILING);
			lblRestDayLegalHolidayColon.setBounds(252,450,10,20);
			txtRestDayLegalHolidayMin.setBounds(256,450,25,20);
			txtRestDayLegalHolidayMin.setDisabledTextColor(new Color(139,113,113));
			txtRestDayLegalHolidayMin.setHorizontalAlignment(JTextField.TRAILING);
			lblRestDayLegalHolidayExcess.setBounds(320,450,100,20);
			txtRestDayLegalHolidayExcessHr.setBounds(435,450,25,20);
			txtRestDayLegalHolidayExcessHr.setDisabledTextColor(new Color(139,113,113));
			txtRestDayLegalHolidayExcessHr.setHorizontalAlignment(JTextField.TRAILING);
			lblRestDayLegalHolidayExcessColon.setBounds(462,450,10,20);
			txtRestDayLegalHolidayExcessMin.setBounds(466,450,25,20);
			txtRestDayLegalHolidayExcessMin.setDisabledTextColor(new Color(139,113,113));
			txtRestDayLegalHolidayExcessMin.setHorizontalAlignment(JTextField.TRAILING);			
			//Rest Day on a Special Holiday OT
			lblRestDaySpecialHoliday.setBounds(28,480,190,20);
			txtRestDaySpecialHolidayHr.setBounds(225,480,25,20);
			txtRestDaySpecialHolidayHr.setDisabledTextColor(new Color(139,113,113));
			txtRestDaySpecialHolidayHr.setHorizontalAlignment(JTextField.TRAILING);
			lblRestDaySpecialHolidayColon.setBounds(252,480,10,20);
			txtRestDaySpecialHolidayMin.setBounds(256,480,25,20);
			txtRestDaySpecialHolidayMin.setDisabledTextColor(new Color(139,113,113));
			txtRestDaySpecialHolidayMin.setHorizontalAlignment(JTextField.TRAILING);
			lblRestDaySpecialHolidayExcess.setBounds(320,480,100,20);
			txtRestDaySpecialHolidayExcessHr.setBounds(435,480,25,20);
			txtRestDaySpecialHolidayExcessHr.setDisabledTextColor(new Color(139,113,113));
			txtRestDaySpecialHolidayExcessHr.setHorizontalAlignment(JTextField.TRAILING);
			lblRestDaySpecialHolidayExcessColon.setBounds(462,480,10,20);
			txtRestDaySpecialHolidayExcessMin.setBounds(466,480,25,20);
			txtRestDaySpecialHolidayExcessMin.setDisabledTextColor(new Color(139,113,113));
			txtRestDaySpecialHolidayExcessMin.setHorizontalAlignment(JTextField.TRAILING);
			//Night Differential 1
			lblNightDiff1.setBounds(28,510,190,20);
			txtNightDiff1Hr.setBounds(225,510,25,20);
			txtNightDiff1Hr.setDisabledTextColor(new Color(139,113,113));
			txtNightDiff1Hr.setHorizontalAlignment(JTextField.TRAILING);
			lblNightDiff1Colon.setBounds(252,510,10,20);
			txtNightDiff1Min.setBounds(256,510,25,20);
			txtNightDiff1Min.setDisabledTextColor(new Color(139,113,113));
			txtNightDiff1Min.setHorizontalAlignment(JTextField.TRAILING);
			//Night Differential 2
			lblNightDiff2.setBounds(320,510,190,20);
			txtNightDiff2Hr.setBounds(435,510,25,20);
			txtNightDiff2Hr.setDisabledTextColor(new Color(139,113,113));
			txtNightDiff2Hr.setHorizontalAlignment(JTextField.TRAILING);
			lblNightDiff2Colon.setBounds(462,510,10,20);
			txtNightDiff2Min.setBounds(466,510,25,20);
			txtNightDiff2Min.setDisabledTextColor(new Color(139,113,113));
			txtNightDiff2Min.setHorizontalAlignment(JTextField.TRAILING);
			//Tardiness
			lblTardiness.setBounds(28,540,190,20);
			txtTardinessHr.setBounds(225,540,25,20);
			txtTardinessHr.setDisabledTextColor(new Color(139,113,113));
			txtTardinessHr.setHorizontalAlignment(JTextField.TRAILING);
			lblTardinessColon.setBounds(252,540,10,20);
			txtTardinessMin.setBounds(256,540,25,20);
			txtTardinessMin.setDisabledTextColor(new Color(139,113,113));
			txtTardinessMin.setHorizontalAlignment(JTextField.TRAILING);
			//Undertime
			lblUndertime.setBounds(320,540,190,20);
			txtUndertimeHr.setBounds(435,540,25,20);
			txtUndertimeHr.setDisabledTextColor(new Color(139,113,113));
			txtUndertimeHr.setHorizontalAlignment(JTextField.TRAILING);
			lblUndertimeColon.setBounds(462,540,10,20);
			txtUndertimeMin.setBounds(466,540,25,20);
			txtUndertimeMin.setDisabledTextColor(new Color(139,113,113));
			txtUndertimeMin.setHorizontalAlignment(JTextField.TRAILING);

			jbtnadd.setBounds(110,200,100,22);
			jbtnedit.setBounds(255,200,100,22);
			jbtndelete.setBounds(400,200,100,22);

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
			container.add(lblEmployee);
			container.add(jcbEmployee);
			container.add(lblPayPeriod);
			container.add(jcbPayPeriod);
			container.add(lblRegHoursWorked);
			container.add(txtRegHoursWorkedHr);
			container.add(lblRegHoursWorkedColon);
			container.add(txtRegHoursWorkedMin);
			//Regular Day OT
			container.add(lblRegularDay);
			container.add(txtRegularDayHr);
			container.add(lblRegularDayColon);
			container.add(txtRegularDayMin);
			container.add(lblRegularDayExcess);
			container.add(txtRegularDayExcessHr);
			container.add(lblRegularDayExcessColon);
			container.add(txtRegularDayExcessMin);
			//Days Absent
			container.add(lblDaysAbsent);
			container.add(txtDaysAbsent);
			//Legal Holiday OT
			container.add(lblLegalHoliday);
			container.add(txtLegalHolidayHr);
			container.add(lblLegalHolidayColon);
			container.add(txtLegalHolidayMin);
			container.add(lblLegalHolidayExcess);
			container.add(txtLegalHolidayExcessHr);
			container.add(lblLegalHolidayExcessColon);
			container.add(txtLegalHolidayExcessMin);
			//Special Holiday OT
			container.add(lblSpecialHoliday);
			container.add(txtSpecialHolidayHr);
			container.add(lblSpecialHolidayColon);
			container.add(txtSpecialHolidayMin);
			container.add(lblSpecialHolidayExcess);
			container.add(txtSpecialHolidayExcessHr);
			container.add(lblSpecialHolidayExcessColon);
			container.add(txtSpecialHolidayExcessMin);
			//Rest Day OT
			container.add(lblRestDay);
			container.add(txtRestDayHr);
			container.add(lblRestDayColon);
			container.add(txtRestDayMin);
			container.add(lblRestDayExcess);
			container.add(txtRestDayExcessHr);
			container.add(lblRestDayExcessColon);
			container.add(txtRestDayExcessMin);
			//Rest Day on a Legal Holiday OT
			container.add(lblRestDayLegalHoliday);
			container.add(txtRestDayLegalHolidayHr);
			container.add(lblRestDayLegalHolidayColon);
			container.add(txtRestDayLegalHolidayMin);
			container.add(lblRestDayLegalHolidayExcess);
			container.add(txtRestDayLegalHolidayExcessHr);
			container.add(lblRestDayLegalHolidayExcessColon);
			container.add(txtRestDayLegalHolidayExcessMin);
			//Rest Day on a Special Holiday OT
			container.add(lblRestDaySpecialHoliday);
			container.add(txtRestDaySpecialHolidayHr);
			container.add(lblRestDaySpecialHolidayColon);
			container.add(txtRestDaySpecialHolidayMin);
			container.add(lblRestDaySpecialHolidayExcess);
			container.add(txtRestDaySpecialHolidayExcessHr);
			container.add(lblRestDaySpecialHolidayExcessColon);
			container.add(txtRestDaySpecialHolidayExcessMin);			
			//Night Differential 1
			container.add(lblNightDiff1);
			container.add(txtNightDiff1Hr);
			container.add(lblNightDiff1Colon);
			container.add(txtNightDiff1Min);
			//Night Differential 2
			container.add(lblNightDiff2);
			container.add(txtNightDiff2Hr);
			container.add(lblNightDiff2Colon);
			container.add(txtNightDiff2Min);
			//Tardiness
			container.add(lblTardiness);
			container.add(txtTardinessHr);
			container.add(lblTardinessColon);
			container.add(txtTardinessMin);
			//Undertime
			container.add(lblUndertime);
			container.add(txtUndertimeHr);
			container.add(lblUndertimeColon);
			container.add(txtUndertimeMin);

			container.add(jbtnadd);
			container.add(jbtnedit);
			container.add(jbtndelete);        
			container.add(txtsearch);

			//Disabled text fields
			disableFields();

			txtRegHoursWorkedMin.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
				}//ENDof public void keyPressed(KeyEvent e) {

				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtRegHoursWorkedMin.getText().length() >= 2) {
								e.consume();
							}
							else {
								if (txtRegHoursWorkedMin.getText().length() > 0) {
									if (Integer.parseInt(txtRegHoursWorkedMin.getText()+c) > 59) e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {

				public void keyReleased(KeyEvent e) {
				}//ENDof public void keyReleased(KeyEvent e) {
			});

			txtDaysAbsent.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtDaysAbsent.getText().indexOf(".") == -1) {
								if (txtDaysAbsent.getText().length() >= 3) {
									e.consume();
								}
							}
							else {
								if (txtDaysAbsent.getText().length() > (txtDaysAbsent.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtRegularDayMin.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
				}//ENDof public void keyPressed(KeyEvent e) {

				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtRegularDayMin.getText().length() >= 2) {
								e.consume();
							}
							else {
								if (txtRegularDayMin.getText().length() > 0) {
									if (Integer.parseInt(txtRegularDayMin.getText()+c) > 59) e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {

				public void keyReleased(KeyEvent e) {
				}//ENDof public void keyReleased(KeyEvent e) {
			});

			txtRegularDayExcessMin.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
				}//ENDof public void keyPressed(KeyEvent e) {

				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtRegularDayExcessMin.getText().length() >= 2) {
								e.consume();
							}
							else {
								if (txtRegularDayExcessMin.getText().length() > 0) {
									if (Integer.parseInt(txtRegularDayExcessMin.getText()+c) > 59) e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {

				public void keyReleased(KeyEvent e) {
				}//ENDof public void keyReleased(KeyEvent e) {
			});

			txtLegalHolidayMin.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
				}//ENDof public void keyPressed(KeyEvent e) {

				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtLegalHolidayMin.getText().length() >= 2) {
								e.consume();
							}
							else {
								if (txtLegalHolidayMin.getText().length() > 0) {
									if (Integer.parseInt(txtLegalHolidayMin.getText()+c) > 59) e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {

				public void keyReleased(KeyEvent e) {
				}//ENDof public void keyReleased(KeyEvent e) {
			});

			txtLegalHolidayExcessMin.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
				}//ENDof public void keyPressed(KeyEvent e) {

				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtLegalHolidayExcessMin.getText().length() >= 2) {
								e.consume();
							}
							else {
								if (txtLegalHolidayExcessMin.getText().length() > 0) {
									if (Integer.parseInt(txtLegalHolidayExcessMin.getText()+c) > 59) e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {

				public void keyReleased(KeyEvent e) {
				}//ENDof public void keyReleased(KeyEvent e) {
			});

			txtSpecialHolidayMin.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
				}//ENDof public void keyPressed(KeyEvent e) {

				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtSpecialHolidayMin.getText().length() >= 2) {
								e.consume();
							}
							else {
								if (txtSpecialHolidayMin.getText().length() > 0) {
									if (Integer.parseInt(txtSpecialHolidayMin.getText()+c) > 59) e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {

				public void keyReleased(KeyEvent e) {
				}//ENDof public void keyReleased(KeyEvent e) {
			});

			txtSpecialHolidayExcessMin.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
				}//ENDof public void keyPressed(KeyEvent e) {

				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtSpecialHolidayExcessMin.getText().length() >= 2) {
								e.consume();
							}
							else {
								if (txtSpecialHolidayExcessMin.getText().length() > 0) {
									if (Integer.parseInt(txtSpecialHolidayExcessMin.getText()+c) > 59) e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {

				public void keyReleased(KeyEvent e) {
				}//ENDof public void keyReleased(KeyEvent e) {
			});

			txtRestDayMin.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
				}//ENDof public void keyPressed(KeyEvent e) {

				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtRestDayMin.getText().length() >= 2) {
								e.consume();
							}
							else {
								if (txtRestDayMin.getText().length() > 0) {
									if (Integer.parseInt(txtRestDayMin.getText()+c) > 59) e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {

				public void keyReleased(KeyEvent e) {
				}//ENDof public void keyReleased(KeyEvent e) {
			});

			txtRestDayExcessMin.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
				}//ENDof public void keyPressed(KeyEvent e) {

				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtRestDayExcessMin.getText().length() >= 2) {
								e.consume();
							}
							else {
								if (txtRestDayExcessMin.getText().length() > 0) {
									if (Integer.parseInt(txtRestDayExcessMin.getText()+c) > 59) e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {

				public void keyReleased(KeyEvent e) {
				}//ENDof public void keyReleased(KeyEvent e) {
			});

			txtRestDayLegalHolidayMin.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
				}//ENDof public void keyPressed(KeyEvent e) {

				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtRestDayLegalHolidayMin.getText().length() >= 2) {
								e.consume();
							}
							else {
								if (txtRestDayLegalHolidayMin.getText().length() > 0) {
									if (Integer.parseInt(txtRestDayLegalHolidayMin.getText()+c) > 59) e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {

				public void keyReleased(KeyEvent e) {
				}//ENDof public void keyReleased(KeyEvent e) {
			});

			txtRestDayLegalHolidayExcessMin.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
				}//ENDof public void keyPressed(KeyEvent e) {

				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtRestDayLegalHolidayExcessMin.getText().length() >= 2) {
								e.consume();
							}
							else {
								if (txtRestDayLegalHolidayExcessMin.getText().length() > 0) {
									if (Integer.parseInt(txtRestDayLegalHolidayExcessMin.getText()+c) > 59) e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {

				public void keyReleased(KeyEvent e) {
				}//ENDof public void keyReleased(KeyEvent e) {
			});

			txtRestDaySpecialHolidayMin.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
				}//ENDof public void keyPressed(KeyEvent e) {

				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtRestDaySpecialHolidayMin.getText().length() >= 2) {
								e.consume();
							}
							else {
								if (txtRestDaySpecialHolidayMin.getText().length() > 0) {
									if (Integer.parseInt(txtRestDaySpecialHolidayMin.getText()+c) > 59) e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {

				public void keyReleased(KeyEvent e) {
				}//ENDof public void keyReleased(KeyEvent e) {
			});

			txtRestDaySpecialHolidayExcessMin.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
				}//ENDof public void keyPressed(KeyEvent e) {

				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtRestDaySpecialHolidayExcessMin.getText().length() >= 2) {
								e.consume();
							}
							else {
								if (txtRestDaySpecialHolidayExcessMin.getText().length() > 0) {
									if (Integer.parseInt(txtRestDaySpecialHolidayExcessMin.getText()+c) > 59) e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {

				public void keyReleased(KeyEvent e) {
				}//ENDof public void keyReleased(KeyEvent e) {
			});

			txtNightDiff1Min.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
				}//ENDof public void keyPressed(KeyEvent e) {

				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtNightDiff1Min.getText().length() >= 2) {
								e.consume();
							}
							else {
								if (txtNightDiff1Min.getText().length() > 0) {
									if (Integer.parseInt(txtNightDiff1Min.getText()+c) > 59) e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {

				public void keyReleased(KeyEvent e) {
				}//ENDof public void keyReleased(KeyEvent e) {
			});

			txtNightDiff2Min.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
				}//ENDof public void keyPressed(KeyEvent e) {

				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtNightDiff2Min.getText().length() >= 2) {
								e.consume();
							}
							else {
								if (txtNightDiff2Min.getText().length() > 0) {
									if (Integer.parseInt(txtNightDiff2Min.getText()+c) > 59) e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {

				public void keyReleased(KeyEvent e) {
				}//ENDof public void keyReleased(KeyEvent e) {
			});

			//add button listener
			jbtnadd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String caption = jbtnadd.getText();

					if (caption == "Add"){
						ListSelectionModel selectionModel = tbl.getSelectionModel();
						selectionModel.clearSelection();
						tbl.setEnabled(false);

						enableFields();

						clearFields();
						jcbEmployee.grabFocus();

						jbtnadd.setText("Save");
						jbtnadd.setMnemonic('S');
						jbtndelete.setText("Cancel");
						jbtndelete.setMnemonic('C');
						jbtnedit.setEnabled(false);
					}
					else if (caption == "Save") {
					
						getFieldValue();
						
						String save_sql = DBConnect.Insert("attendance",
								"empid,payp_code,regular_hours_worked,days_absent,regular_day_ot,regular_day_ot_excess,legal_Holiday_ot," +
								"legal_holiday_ot_excess,special_holiday_ot,special_holiday_ot_excess,rest_day_ot,rest_day_ot_excess," +
								"rest_day_legal_holiday_ot,rest_day_legal_holiday_ot_excess,rest_day_special_holiday_ot," +
								"rest_day_special_holiday_ot_excess,night_diff1,night_diff2,tardiness,undertime",
								"'"+Employee+"', " +
								"'"+PayPeriod+"', " +
								"'"+num2Format.format(RegularHoursWorked)+"', " +
								"'"+num2Format.format(DaysAbsent)+"', " +
								"'"+num2Format.format(RegularDay)+"', " +
								"'"+num2Format.format(RegularDayExcess)+"', " +
								"'"+num2Format.format(LegalHoliday)+"', " +
								"'"+num2Format.format(LegalHolidayExcess)+"', " +
								"'"+num2Format.format(SpecialHoliday)+"', " +
								"'"+num2Format.format(SpecialHolidayExcess)+"', " +
								"'"+num2Format.format(RestDay)+"', " +
								"'"+num2Format.format(RestDayExcess)+"', " +
								"'"+num2Format.format(RestDayLegalHoliday)+"', " +
								"'"+num2Format.format(RestDayLegalHolidayExcess)+"', " +
								"'"+num2Format.format(RestDaySpecialHoliday)+"', " +
								"'"+num2Format.format(RestDaySpecialHolidayExcess)+"', " +
								"'"+num2Format.format(NightDifferential1)+"', " +
								"'"+num2Format.format(NightDifferential2)+"', " +
								"'"+num2Format.format(Tardiness)+"', " +
								"'"+num2Format.format(Undertime)+"' "
								);

						tbl.setEnabled(false);
						try {
							if (jcbEmployee.getSelectedIndex() == 0) {   
								Message.messageError("Invalid input for Employee field.");
								jcbEmployee.grabFocus();
							}
							else if (jcbPayPeriod.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Cut-Off Date field.");
								jcbPayPeriod.grabFocus();
							}
							else{
								tableAttendance.setInsert(save_sql);

								tableAttendance.setQuery(DEFAULT_QUERY);
								tbl.getColumnModel().getColumn(0).setMaxWidth(0);
								tbl.getColumnModel().getColumn(0).setMinWidth(0);
								tbl.getColumnModel().getColumn(1).setMaxWidth(0);
								tbl.getColumnModel().getColumn(1).setMinWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(364);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(364);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(200);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(200);
								tbl.getColumnModel().getColumn(0).setHeaderValue("Employee Name");
								tbl.getColumnModel().getColumn(1).setHeaderValue("Cut-Off Date");

								curRecord = Employee.trim()+PayPeriod.trim();

								clearFields();

								Message.messageInfo(Message.messageAdd);
								//tbl.scrollRectToVisible(tbl.getCellRect(tbl.getRowCount()-1, tbl.getColumnCount(), true));
								tbl.scrollRectToVisible(tbl.getCellRect(setCurRecord(), tbl.getColumnCount(), true));
								jcbEmployee.grabFocus();
							}
						}
						catch(SQLException se){
							try{
								tableAttendance.setQuery(DEFAULT_QUERY);
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
									//row level locking
									LockHandler.startLock(DBConnect.SelectForUpdate("attendance",
											"*",
											"empid='"+tbl.getValueAt(tbl.getSelectedRow(),2).toString()+"' "+
											"AND payp_code = '"+tbl.getValueAt(tbl.getSelectedRow(),3).toString()+"'"));

									String del_sql = DBConnect.delete("attendance","empid = '"+tbl.getValueAt(tbl.getSelectedRow(),2).toString()+"' "+
											"AND payp_code = '"+tbl.getValueAt(tbl.getSelectedRow(),3).toString()+"'");
									//System.out.println(del_sql);

									txtsearch.setVisible(false);
									mainform.lblMessage.setText("");
									//int setDialog = JOptionPane.showConfirmDialog(container, "Are you sure ?",  "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
									int setDialog = Message.messageYesNo("Are you sure you want to delete\n" + tbl.getValueAt(tbl.getSelectedRow(),0).toString().trim()+ ": "+ tbl.getValueAt(tbl.getSelectedRow(),1).toString().trim()+ " ?");
									if (JOptionPane.YES_OPTION == setDialog) {
										try {
											// unlock mode and delete 
											LockHandler.removeLockAndDelete(del_sql);
											//tableAttendance.setInsert(del_sql);
											//																												
											//tableAttendance.setQuery(DEFAULT_QUERY);
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
										tableAttendance.setQuery(DEFAULT_QUERY);
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

					int currentr = tbl.getSelectedRow();
					int currentc = tbl.getSelectedColumn();	
					if (ecaption == "Edit"){
						if (jcbEmployee.getSelectedItem().toString().substring(101).length() >= 1){
							currentr = tbl.getSelectedRow();
							currentc = tbl.getSelectedColumn();	

							// start indicating lock
							LockHandler.initializeLock(container);

							//wait for lock indicator to invoke
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									try {
										//row level locking
										LockHandler.startLock(DBConnect.SelectForUpdate("attendance",
												"*",
												"empid='"+tbl.getValueAt(tbl.getSelectedRow(),2).toString()+"' "+
												"AND payp_code = '"+tbl.getValueAt(tbl.getSelectedRow(),3).toString()+"'"));
										//refresh
										tableAttendance.setQuery(DEFAULT_QUERY);
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

										jcbEmployee.setEnabled(false);
										jcbPayPeriod.setEnabled(false);
										txtRegHoursWorkedHr.grabFocus();

									}
									catch (SQLException e) {
										try {
											//refresh and remove lock mode
											tableAttendance.setQuery(DEFAULT_QUERY);
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

							getFieldValue();
							
							String e_sql = 
								DBConnect.Update("attendance",
										"regular_hours_worked = '"+num2Format.format(RegularHoursWorked)+"', " +
										"days_absent = '"+num2Format.format(DaysAbsent)+"', " +
										"regular_day_ot = '"+num2Format.format(RegularDay)+"', " +
										"regular_day_ot_excess = '"+num2Format.format(RegularDayExcess)+"', " +
										"legal_Holiday_ot = '"+num2Format.format(LegalHoliday)+"', " +
										"legal_holiday_ot_excess = '"+num2Format.format(LegalHolidayExcess)+"', " +
										"special_holiday_ot = '"+num2Format.format(SpecialHoliday)+"', " +
										"special_holiday_ot_excess = '"+num2Format.format(SpecialHolidayExcess)+"', " +
										"rest_day_ot = '"+num2Format.format(RestDay)+"'",
										"empid='"+tbl.getValueAt(tbl.getSelectedRow(),2).toString()+"' "+
										"AND payp_code = '"+tbl.getValueAt(tbl.getSelectedRow(),3).toString()+"'"
										);
							String e_sql1 = 
								DBConnect.Update("attendance",
										"rest_day_ot_excess = '"+num2Format.format(RestDayExcess)+"', "+
										"rest_day_legal_holiday_ot = '"+num2Format.format(RestDayLegalHoliday)+"', "+
										"rest_day_legal_holiday_ot_excess = '"+num2Format.format(RestDayLegalHolidayExcess)+"', "+
										"rest_day_special_holiday_ot = '"+num2Format.format(RestDaySpecialHoliday)+"', "+
										"rest_day_special_holiday_ot_excess = '"+num2Format.format(RestDaySpecialHolidayExcess)+"', "+
										"night_diff1 = '"+num2Format.format(NightDifferential1)+"', "+
										"night_diff2 = '"+num2Format.format(NightDifferential2)+"', "+
										"tardiness = '"+num2Format.format(Tardiness)+"', "+
										"undertime = '"+num2Format.format(Undertime)+"'",
										"empid='"+tbl.getValueAt(tbl.getSelectedRow(),2).toString()+"' "+
										"AND payp_code = '"+tbl.getValueAt(tbl.getSelectedRow(),3).toString()+"'"
										);
							//unlock mode then update
							LockHandler.removeLockAndUpdate(e_sql);
							LockHandler.removeLockAndUpdate(e_sql1);

							tableAttendance.setQuery(DEFAULT_QUERY);
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

						catch(SQLException se){
							System.out.println(se.getMessage());
						}
					}
				}//public void mouseClicked(MouseEvent e)
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
							txtsearch.setText("Employee:" + " " + e.getKeyChar());
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
					if (eventKey == 8 && txtsearch.getText().length() == 10) {
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
						if (tbl.getRowCount() > 0) {
							for (int row = 0; row < tbl.getRowCount(); row++) {
								if (tbl.getValueAt(row,0).toString().length() >= sKey.length()) {
									if (tbl.getValueAt(row,0).toString().substring(0,sKey.length()).equalsIgnoreCase(sKey) == true) {
										tbl.setRowSelectionInterval(row,row);
										setFieldValue();
										break;
									}
								}
							}
						}
					}//ENDof if (sKey.trim().length() > 0) {
					else {
						if (tbl.getRowCount() > 0) {
							tbl.setRowSelectionInterval(0,0);
							setFieldValue();
						}
					}//ENDof else if (sKey.trim().length() > 0) {
				}//public void keyReleased(KeyEvent e){
			}//new KeyAdapter(){
			);//txtsearch.addKeyListener(

			setSize(600,610);
			setVisible(true);

		}//try
		catch ( ClassNotFoundException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("AttendanceMaint", "AttendanceMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch ( SQLException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("AttendanceMaint", "AttendanceMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch (Exception e) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("AttendanceMaint", "AttendanceMaint", traceWriter.toString());        
			dispose();
		}
	}//ENDof public AttendanceMaint() {

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
		if (tbl.getSelectedRow() == -1) {
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
			tableAttendance.setQuery(DEFAULT_QUERY);
			tbl.getColumnModel().getColumn(0).setMaxWidth(0);
			tbl.getColumnModel().getColumn(0).setMinWidth(0);
			tbl.getColumnModel().getColumn(1).setMaxWidth(0);
			tbl.getColumnModel().getColumn(1).setMinWidth(0);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(364);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(364);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(200);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(200);

			//set column header width (to avoid the ... sign)
			tbl.getColumnModel().getColumn(0).setHeaderValue("Employee Name");
			tbl.getColumnModel().getColumn(1).setHeaderValue("Cut-Off Date");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void reload() {

	private void enableFields() {
		jcbEmployee.setEnabled(true);
		jcbPayPeriod.setEnabled(true);
		txtRegHoursWorkedHr.setEnabled(true);
		txtRegHoursWorkedMin.setEnabled(true);
		txtDaysAbsent.setEnabled(true);
		txtRegularDayHr.setEnabled(true);
		txtRegularDayMin.setEnabled(true);
		txtRegularDayExcessHr.setEnabled(true);
		txtRegularDayExcessMin.setEnabled(true);
		txtLegalHolidayHr.setEnabled(true);
		txtLegalHolidayMin.setEnabled(true);
		txtLegalHolidayExcessHr.setEnabled(true);
		txtLegalHolidayExcessMin.setEnabled(true);
		txtSpecialHolidayHr.setEnabled(true);
		txtSpecialHolidayMin.setEnabled(true);
		txtSpecialHolidayExcessHr.setEnabled(true);
		txtSpecialHolidayExcessMin.setEnabled(true);
		txtRestDayHr.setEnabled(true);
		txtRestDayMin.setEnabled(true);
		txtRestDayExcessHr.setEnabled(true);
		txtRestDayExcessMin.setEnabled(true);
		txtRestDayLegalHolidayHr.setEnabled(true);
		txtRestDayLegalHolidayMin.setEnabled(true);
		txtRestDayLegalHolidayExcessHr.setEnabled(true);
		txtRestDayLegalHolidayExcessMin.setEnabled(true);
		txtRestDaySpecialHolidayHr.setEnabled(true);
		txtRestDaySpecialHolidayMin.setEnabled(true);
		txtRestDaySpecialHolidayExcessHr.setEnabled(true);
		txtRestDaySpecialHolidayExcessMin.setEnabled(true);
		txtNightDiff1Hr.setEnabled(true);
		txtNightDiff1Min.setEnabled(true);
		txtNightDiff2Hr.setEnabled(true);
		txtNightDiff2Min.setEnabled(true);
		txtTardinessHr.setEnabled(true);
		txtTardinessMin.setEnabled(true);
		txtUndertimeHr.setEnabled(true);
		txtUndertimeMin.setEnabled(true);
	}//ENDof private void enableFields() {

	private void disableFields() {
		jcbEmployee.setEnabled(false);
		jcbPayPeriod.setEnabled(false);
		txtRegHoursWorkedHr.setEnabled(false);
		txtRegHoursWorkedMin.setEnabled(false);
		txtDaysAbsent.setEnabled(false);
		txtRegularDayHr.setEnabled(false);
		txtRegularDayMin.setEnabled(false);
		txtRegularDayExcessHr.setEnabled(false);
		txtRegularDayExcessMin.setEnabled(false);
		txtLegalHolidayHr.setEnabled(false);
		txtLegalHolidayMin.setEnabled(false);
		txtLegalHolidayExcessHr.setEnabled(false);
		txtLegalHolidayExcessMin.setEnabled(false);
		txtSpecialHolidayHr.setEnabled(false);
		txtSpecialHolidayMin.setEnabled(false);
		txtSpecialHolidayExcessHr.setEnabled(false);
		txtSpecialHolidayExcessMin.setEnabled(false);
		txtRestDayHr.setEnabled(false);
		txtRestDayMin.setEnabled(false);
		txtRestDayExcessHr.setEnabled(false);
		txtRestDayExcessMin.setEnabled(false);
		txtRestDayLegalHolidayHr.setEnabled(false);
		txtRestDayLegalHolidayMin.setEnabled(false);
		txtRestDayLegalHolidayExcessHr.setEnabled(false);
		txtRestDayLegalHolidayExcessMin.setEnabled(false);
		txtRestDaySpecialHolidayHr.setEnabled(false);
		txtRestDaySpecialHolidayMin.setEnabled(false);
		txtRestDaySpecialHolidayExcessHr.setEnabled(false);
		txtRestDaySpecialHolidayExcessMin.setEnabled(false);
		txtNightDiff1Hr.setEnabled(false);
		txtNightDiff1Min.setEnabled(false);
		txtNightDiff2Hr.setEnabled(false);
		txtNightDiff2Min.setEnabled(false);
		txtTardinessHr.setEnabled(false);
		txtTardinessMin.setEnabled(false);
		txtUndertimeHr.setEnabled(false);
		txtUndertimeMin.setEnabled(false);
	}//ENDof private void disableFields() {

	private void clearFields() {
		jcbEmployee.setSelectedItem("Choose");
		jcbPayPeriod.setSelectedItem("Choose");
		txtRegHoursWorkedHr.setText("");
		txtRegHoursWorkedMin.setText("");
		txtDaysAbsent.setText("");
		txtRegularDayHr.setText("");
		txtRegularDayMin.setText("");
		txtRegularDayExcessHr.setText("");
		txtRegularDayExcessMin.setText("");
		txtLegalHolidayHr.setText("");
		txtLegalHolidayMin.setText("");
		txtLegalHolidayExcessHr.setText("");
		txtLegalHolidayExcessMin.setText("");
		txtSpecialHolidayHr.setText("");
		txtSpecialHolidayMin.setText("");
		txtSpecialHolidayExcessHr.setText("");
		txtSpecialHolidayExcessMin.setText("");
		txtRestDayHr.setText("");
		txtRestDayMin.setText("");
		txtRestDayExcessHr.setText("");
		txtRestDayExcessMin.setText("");
		txtRestDayLegalHolidayHr.setText("");
		txtRestDayLegalHolidayMin.setText("");
		txtRestDayLegalHolidayExcessHr.setText("");
		txtRestDayLegalHolidayExcessMin.setText("");
		txtRestDaySpecialHolidayHr.setText("");
		txtRestDaySpecialHolidayMin.setText("");
		txtRestDaySpecialHolidayExcessHr.setText("");
		txtRestDaySpecialHolidayExcessMin.setText("");
		txtNightDiff1Hr.setText("");
		txtNightDiff1Min.setText("");
		txtNightDiff2Hr.setText("");
		txtNightDiff2Min.setText("");
		txtTardinessHr.setText("");
		txtTardinessMin.setText("");
		txtUndertimeHr.setText("");
		txtUndertimeMin.setText("");
	}//ENDof private void clearFields() {

	private void setFieldValue() {
		if (tbl.getSelectedRow() >= 0) {
			int getrow = tbl.getSelectedRow();
			jcbEmployee.setSelectedIndex(0);
			for (int ii=0; ii<=jcbEmployee.getItemCount()-1; ii++) {
				if (jcbEmployee.getItemAt(ii).toString().length() > 50) {
					if (tbl.getValueAt(getrow,2).toString().equalsIgnoreCase(jcbEmployee.getItemAt(ii).toString().substring(101)) == true) {
						jcbEmployee.setSelectedIndex(ii);
					}
				}
			}//ENDof for (int ii=0; ii<=jcbEmployee.getItemCount()-1; ii++) {
			jcbPayPeriod.setSelectedIndex(0);
			for (int ii=0; ii<=jcbPayPeriod.getItemCount()-1; ii++) {
				if (jcbPayPeriod.getItemAt(ii).toString().length() > 50) {
					if (tbl.getValueAt(getrow,3).toString().equalsIgnoreCase(jcbPayPeriod.getItemAt(ii).toString().substring(101)) == true) {
						jcbPayPeriod.setSelectedIndex(ii);
					}
				}
			}//ENDof for (int ii=0; ii<=jcbPayPeriod.getItemCount()-1; ii++) {
			setHourMinute(tbl.getValueAt(getrow,4).toString(), txtRegHoursWorkedHr, txtRegHoursWorkedMin);
			txtDaysAbsent.setText(num2Format.format(Double.parseDouble(tbl.getValueAt(getrow,5).toString())));
			setHourMinute(tbl.getValueAt(getrow,6).toString(), txtRegularDayHr, txtRegularDayMin);
			setHourMinute(tbl.getValueAt(getrow,7).toString(), txtRegularDayExcessHr, txtRegularDayExcessMin);
			setHourMinute(tbl.getValueAt(getrow,8).toString(), txtLegalHolidayHr, txtLegalHolidayMin);
			setHourMinute(tbl.getValueAt(getrow,9).toString(), txtLegalHolidayExcessHr, txtLegalHolidayExcessMin);
			setHourMinute(tbl.getValueAt(getrow,10).toString(), txtSpecialHolidayHr, txtSpecialHolidayMin);
			setHourMinute(tbl.getValueAt(getrow,11).toString(), txtSpecialHolidayExcessHr, txtSpecialHolidayExcessMin);
			setHourMinute(tbl.getValueAt(getrow,12).toString(), txtRestDayHr, txtRestDayMin);
			setHourMinute(tbl.getValueAt(getrow,13).toString(), txtRestDayExcessHr, txtRestDayExcessMin);
			setHourMinute(tbl.getValueAt(getrow,14).toString(), txtRestDayLegalHolidayHr, txtRestDayLegalHolidayMin);
			setHourMinute(tbl.getValueAt(getrow,15).toString(), txtRestDayLegalHolidayExcessHr, txtRestDayLegalHolidayExcessMin);
			setHourMinute(tbl.getValueAt(getrow,16).toString(), txtRestDaySpecialHolidayHr, txtRestDaySpecialHolidayMin);
			setHourMinute(tbl.getValueAt(getrow,17).toString(), txtRestDaySpecialHolidayExcessHr, txtRestDaySpecialHolidayExcessMin);
			setHourMinute(tbl.getValueAt(getrow,18).toString(), txtNightDiff1Hr, txtNightDiff1Min);
			setHourMinute(tbl.getValueAt(getrow,19).toString(), txtNightDiff2Hr, txtNightDiff2Min);
			setHourMinute(tbl.getValueAt(getrow,20).toString(), txtTardinessHr, txtTardinessMin);
			setHourMinute(tbl.getValueAt(getrow,21).toString(), txtUndertimeHr, txtUndertimeMin);
		}//ENDof if (tbl.getSelectedRow() >= 0) {
	}//ENDof private void getFieldValue() {

	private void getFieldValue() {
		if (jcbEmployee.getSelectedIndex() != 0) {
			Employee = jcbEmployee.getSelectedItem().toString().substring(101);
		}
		else {
			Employee = "";
		}
		if (jcbPayPeriod.getSelectedIndex() != 0) {
			PayPeriod = jcbPayPeriod.getSelectedItem().toString().substring(101);
		}
		else {
			PayPeriod = "";
		}
		RegularHoursWorked = 0;
		if (txtRegHoursWorkedHr.getText().trim().length() > 0) {
			RegularHoursWorked = Double.parseDouble(txtRegHoursWorkedHr.getText().trim());
		}
		if (txtRegHoursWorkedMin.getText().trim().length() > 0) {
			RegularHoursWorked = RegularHoursWorked + (Double.parseDouble(txtRegHoursWorkedMin.getText().trim()) / 60);
		}
		DaysAbsent = 0;
		if (txtDaysAbsent.getText().trim().length() > 0) {
			DaysAbsent = Double.parseDouble(txtDaysAbsent.getText().trim());
		}
		RegularDay = 0;
		if (txtRegularDayHr.getText().trim().length() > 0) {
			RegularDay = Double.parseDouble(txtRegularDayHr.getText().trim());
		}
		if (txtRegularDayMin.getText().trim().length() > 0) {
			RegularDay = RegularDay + (Double.parseDouble(txtRegularDayMin.getText().trim()) / 60);
		}
		RegularDayExcess = 0;
		if (txtRegularDayExcessHr.getText().trim().length() > 0) {
			RegularDayExcess = Double.parseDouble(txtRegularDayExcessHr.getText().trim());
		}
		if (txtRegularDayExcessMin.getText().trim().length() > 0) {
			RegularDayExcess = RegularDayExcess + (Double.parseDouble(txtRegularDayExcessMin.getText().trim()) / 60);
		}
		LegalHoliday = 0;
		if (txtLegalHolidayHr.getText().trim().length() > 0) {
			LegalHoliday = Double.parseDouble(txtLegalHolidayHr.getText().trim());
		}
		if (txtLegalHolidayMin.getText().trim().length() > 0) {
			LegalHoliday = LegalHoliday + (Double.parseDouble(txtLegalHolidayMin.getText().trim()) / 60);
		}
		LegalHolidayExcess = 0;
		if (txtLegalHolidayExcessHr.getText().trim().length() > 0) {
			LegalHolidayExcess = Double.parseDouble(txtLegalHolidayExcessHr.getText().trim());
		}
		if (txtLegalHolidayExcessMin.getText().trim().length() > 0) {
			LegalHolidayExcess = LegalHolidayExcess + (Double.parseDouble(txtLegalHolidayExcessMin.getText().trim()) / 60);
		}
		SpecialHoliday = 0;
		if (txtSpecialHolidayHr.getText().trim().length() > 0) {
			SpecialHoliday = Double.parseDouble(txtSpecialHolidayHr.getText().trim());
		}
		if (txtSpecialHolidayMin.getText().trim().length() > 0) {
			SpecialHoliday = SpecialHoliday + (Double.parseDouble(txtSpecialHolidayMin.getText().trim()) / 60);
		}
		SpecialHolidayExcess = 0;
		if (txtSpecialHolidayExcessHr.getText().trim().length() > 0) {
			SpecialHolidayExcess = Double.parseDouble(txtSpecialHolidayExcessHr.getText().trim());
		}
		if (txtSpecialHolidayExcessMin.getText().trim().length() > 0) {
			SpecialHolidayExcess = SpecialHolidayExcess + (Double.parseDouble(txtSpecialHolidayExcessMin.getText().trim()) / 60);
		}
		RestDay = 0;
		if (txtRestDayHr.getText().trim().length() > 0) {
			RestDay = Double.parseDouble(txtRestDayHr.getText().trim());
		}
		if (txtRestDayMin.getText().trim().length() > 0) {
			RestDay = RestDay + (Double.parseDouble(txtRestDayMin.getText().trim()) / 60);
		}
		RestDayExcess = 0;
		if (txtRestDayExcessHr.getText().trim().length() > 0) {
			RestDayExcess = Double.parseDouble(txtRestDayExcessHr.getText().trim());
		}
		if (txtRestDayExcessMin.getText().trim().length() > 0) {
			RestDayExcess = RestDayExcess + (Double.parseDouble(txtRestDayExcessMin.getText().trim()) / 60);
		}
		RestDayLegalHoliday = 0;
		if (txtRestDayLegalHolidayHr.getText().trim().length() > 0) {
			RestDayLegalHoliday = Double.parseDouble(txtRestDayLegalHolidayHr.getText().trim());
		}
		if (txtRestDayLegalHolidayMin.getText().trim().length() > 0) {
			RestDayLegalHoliday = RestDayLegalHoliday + (Double.parseDouble(txtRestDayLegalHolidayMin.getText().trim()) / 60);
		}
		RestDayLegalHolidayExcess = 0;
		if (txtRestDayLegalHolidayExcessHr.getText().trim().length() > 0) {
			RestDayLegalHolidayExcess = Double.parseDouble(txtRestDayLegalHolidayExcessHr.getText().trim());
		}
		if (txtRestDayLegalHolidayExcessMin.getText().trim().length() > 0) {
			RestDayLegalHolidayExcess = RestDayLegalHolidayExcess + (Double.parseDouble(txtRestDayLegalHolidayExcessMin.getText().trim()) / 60);
		}
		RestDaySpecialHoliday = 0;
		if (txtRestDaySpecialHolidayHr.getText().trim().length() > 0) {
			RestDaySpecialHoliday = Double.parseDouble(txtRestDaySpecialHolidayHr.getText().trim());
		}
		if (txtRestDaySpecialHolidayMin.getText().trim().length() > 0) {
			RestDaySpecialHoliday = RestDaySpecialHoliday + (Double.parseDouble(txtRestDaySpecialHolidayMin.getText().trim()) / 60);
		}
		RestDaySpecialHolidayExcess = 0;
		if (txtRestDaySpecialHolidayExcessHr.getText().trim().length() > 0) {
			RestDaySpecialHolidayExcess = Double.parseDouble(txtRestDaySpecialHolidayExcessHr.getText().trim());
		}
		if (txtRestDaySpecialHolidayExcessMin.getText().trim().length() > 0) {
			RestDaySpecialHolidayExcess = RestDaySpecialHolidayExcess + (Double.parseDouble(txtRestDaySpecialHolidayExcessMin.getText().trim()) / 60);
		}
		NightDifferential1 = 0;
		if (txtNightDiff1Hr.getText().trim().length() > 0) {
			NightDifferential1 = Double.parseDouble(txtNightDiff1Hr.getText().trim());
		}
		if (txtNightDiff1Min.getText().trim().length() > 0) {
			NightDifferential1 = NightDifferential1 + (Double.parseDouble(txtNightDiff1Min.getText().trim()) / 60);
		}
		NightDifferential2 = 0;
		if (txtNightDiff2Hr.getText().trim().length() > 0) {
			NightDifferential2 = Double.parseDouble(txtNightDiff2Hr.getText().trim());
		}
		if (txtNightDiff2Min.getText().trim().length() > 0) {
			NightDifferential2 = NightDifferential2 + (Double.parseDouble(txtNightDiff2Min.getText().trim()) / 60);
		}
		Tardiness = 0;
		if (txtTardinessHr.getText().trim().length() > 0) {
			Tardiness = Double.parseDouble(txtTardinessHr.getText().trim());
		}
		if (txtTardinessMin.getText().trim().length() > 0) {
			Tardiness = Tardiness + (Double.parseDouble(txtTardinessMin.getText().trim()) / 60);
		}
		Undertime = 0;
		if (txtUndertimeHr.getText().trim().length() > 0) {
			Undertime = Double.parseDouble(txtUndertimeHr.getText().trim());
		}
		if (txtUndertimeMin.getText().trim().length() > 0) {
			Undertime = Undertime + (Double.parseDouble(txtUndertimeMin.getText().trim()) / 60);
		}
	}//ENDof private void getFieldValue() {
	
	private int setCurRecord() {
		int i = 0;
		if (tbl.getRowCount() > 0) {
			if (curRecord.trim().length() > 0) {
				for (i=0; i<=tbl.getRowCount()-1; i++) {
					if ((tbl.getValueAt(i,2).toString().trim()+tbl.getValueAt(i,3).toString().trim()).equalsIgnoreCase(curRecord) == true) {
						tbl.setRowSelectionInterval(i,i);
						tempR = i;
						break;
					}
				}//ENDof for (int i=0; i<=tbl.getRowCount()-1; i++) {
			}//ENDof if (curRecord.trim().length() > 0) {
		}//ENDof if (tbl.getRowCount() > 0) {
		return i;
	}//ENDof private int setCurRecord() {

	private void setHourMinute(String HrMinValue, MyField tHour, JFormattedTextField tMinute) {
		double time = Double.parseDouble(HrMinValue);
		long   hr	= (long)time;
		double min	= (time - hr) * 60;
		tHour.setText(hr+"");
		tMinute.setText(numFormat.format(min));
	}//ENDof private long getHour(String sHour) {
}//ENDof public class AttendanceMaint extends JInternalFrame {

