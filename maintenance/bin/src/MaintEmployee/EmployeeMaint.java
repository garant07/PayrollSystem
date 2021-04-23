package maintenance.bin.src.MaintEmployee;

/**
 * Author : Norberto L. Silva
 * Date   : March 7, 2012
 * Company: Applied Ideas, Inc 
 * Program: Employee Maintenance ==-> Payroll System
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
import java.beans.PropertyVetoException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import maintenance.bin.src.Calendar.DateComboBox;
import maintenance.bin.src.DBConn.DBConnect;
import maintenance.bin.src.Functions.Formatter;
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.MyField;
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.ResultTableModel.ResultSetTableModel;
import maintenance.bin.src.core.LockHandler;
import maintenance.bin.src.core.mainform;

public class EmployeeMaint extends JInternalFrame {
	private static final long serialVersionUID = 1L;
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	static final String DEFAULT_QUERY = DBConnect.Select("emppif ",
										"emppif.lastname, "+								//0
										"emppif.firstname, "+								//1
										"emppif.middlename, "+								//2
										"emppif.empid, "+									//3
										"FORMAT(emppif.salary,2) AS samt, "+				//4
										"emppif.srtype_code, "+								//5
										"emppif.etype_code, "+								//6
										"emppif.rank_code, "+								//7
										"emppif.estat_code, "+								//8
										"emppif.cost_code, "+								//9
										"emppif.dept_code, "+								//10
										"emppif.postn_code, "+								//11
										"CONVERT(emppif.birth_date,CHAR) as bdate, "+		//12
										"CONVERT(emppif.empl_date,CHAR) as edate, "+		//13
										"CONVERT(emppif.term_date,CHAR) as tdate, "+		//14
										"CONVERT(emppif.pastpay_date,CHAR) as pdate, "+		//15
										"emppif.allowed_ot, "+								//16
										"FORMAT(fixed_taxallow,2) AS ftallow, "+			//17
										"FORMAT(fixed_nontaxallow,2) AS fnallow, "+			//18
										"emppif.pymnt_code, "+								//19
										"emppif.accnt_no ",									//20
										null, "lastname, firstname, middlename");

	public static int subMain1 = 0;
	public static int subMain2 = 0;

	private ResultSetTableModel tableEmployee;
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
	public static JButton jbtngovt;
	public static JButton jbtnprevemp;

	//variable for user entry
	JScrollPane scrollPane;
	private MyField txtempid = new MyField(true,10);
	private MyField txtLastname = new MyField(true,30);
	private MyField txtFirstname = new MyField(true,30);
	private MyField txtMiddlename = new MyField(true,30);
	private JFormattedTextField txtSalary = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JComboBox jcbSalaryRateType = new JComboBox();
	private JComboBox jcbEmpType = new JComboBox();
	private JComboBox jcbRank = new JComboBox();
	private JComboBox jcbEmpStatus = new JComboBox();
	private JComboBox jcbCostCenter = new JComboBox();
	private JComboBox jcbDepartment = new JComboBox();
	private JComboBox jcbPosition = new JComboBox();
	private MyField txtBirthDate = new MyField(true,15);	
	private DateComboBox cboBirthDate = new DateComboBox();
	private MyField txtEmpDate = new MyField(true,15);
	private DateComboBox cboEmpDate = new DateComboBox();
	private MyField txtTermDate = new MyField(true,15);
	private DateComboBox cboTermDate = new DateComboBox();
	private MyField txtLPayDate = new MyField(true,15);
	private DateComboBox cboLPayDate = new DateComboBox();
	private JComboBox jcbAllowedOT = new JComboBox();
	private JFormattedTextField txtFTaxAllow = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtFNTaxAllow = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JComboBox jcbPymntType = new JComboBox();
	private MyField txtAcctNo = new MyField(true,12);
	private JTextField txtsearch = new JTextField();

	SimpleDateFormat dteFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat strFormat = new SimpleDateFormat("MMMM dd, yyyy");

	String Salary		= "0";
	String RateType		= "";
	String EmpType		= "";
	String Rank			= "";
	String EmpStatus	= "";
	String CostCenter	= "";
	String Department	= "";
	String Position		= "";
	String BirthDate	= "0000-00-00";
	String EmpDate		= "0000-00-00";
	String TermDate		= "0000-00-00";
	String LPayDate		= "0000-00-00";
	String AllowedOT	= "";
	String FTaxAllow	= "";
	String FNTaxAllow	= "";
	String PymntType	= "";

	public EmployeeMaint() {
		super ("Employee Maintenance", false, true, false, false);
		this.setName("empmaint");
		this.setResizable(false);

		this.addInternalFrameListener(new InternalFrameListener(){
			public void internalFrameActivated(InternalFrameEvent arg0) {
				if (subMain1 == 1) {
					try {
						EmployeeGovtContMaint.iFrame.setSelected(true);
					}
					catch (PropertyVetoException e) {}
				}
				else if (subMain2 == 1) {
					try {
						EmployeePrevEmpMaint.iFrame.setSelected(true);
					}
					catch (PropertyVetoException e) {}
				}
				mainform.tree.setEnabled(false);
			}
			public void internalFrameClosed(InternalFrameEvent arg0) {}
			public void internalFrameClosing(InternalFrameEvent arg0) {
				if (subMain1 == 1) {
					EmployeeGovtContMaint.iFrame.dispose();
				}
				if (subMain2 == 1) {
					EmployeePrevEmpMaint.iFrame.dispose();
				}
				mainform.tree.setEnabled(true);
			}
			public void internalFrameDeactivated(InternalFrameEvent arg0) {}
			public void internalFrameDeiconified(InternalFrameEvent arg0) {
				if (subMain1 == 1) {
					try {
						EmployeeGovtContMaint.iFrame.setIcon(false);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
				if (subMain2 == 1) {
					try {
						EmployeePrevEmpMaint.iFrame.setIcon(false);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
			}
			public void internalFrameIconified(InternalFrameEvent arg0) {
				if (subMain1 == 1) {
					try {
						EmployeeGovtContMaint.iFrame.setIcon(true);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
				if (subMain2 == 1) {
					try {
						EmployeePrevEmpMaint.iFrame.setIcon(true);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
			}
			public void internalFrameOpened(InternalFrameEvent arg0) {}
		});

		try {
			tableEmployee = new ResultSetTableModel( DEFAULT_QUERY );

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
			jbtngovt = new JButton("Gov't Cont.");
			jbtngovt.setMnemonic('G');
			jbtngovt.setToolTipText("Government Contribution");
			jbtnprevemp = new JButton("Prev ER");
			jbtnprevemp.setMnemonic('P');
			jbtnprevemp.setToolTipText("Previous Employer");

			JLabel EmpNo = new JLabel("Employee No.");
			JLabel lblLastname = new JLabel("Last Name");
			JLabel lblFirstname = new JLabel("First Name");
			JLabel lblMiddlename = new JLabel("Middle Name");
			JLabel lblSalary = new JLabel("Salary");
			JLabel lblSalaryRateType = new JLabel("Rate Type");
			JLabel lblEmpType = new JLabel("Emp. Type");
			JLabel lblRank = new JLabel("Rank");
			JLabel lblEmpStatus = new JLabel("Emp. Status");
			JLabel lblCostCenter = new JLabel("Cost Center");
			JLabel lblDepartment = new JLabel("Department");
			JLabel lblPosition = new JLabel("Position");
			JLabel lblBirthDate = new JLabel("Date of Birth");
			JLabel lblEmpDate = new JLabel("Date Employed");
			JLabel lblTermDate = new JLabel("Date Terminated");
			JLabel lblLPayDate = new JLabel("Last CutOff Date");
			JLabel lblAllowOT = new JLabel("Allowed OT");
			JLabel lblFTaxAllow = new JLabel("Taxable Allow");
			JLabel lblFNTaxAllow = new JLabel("NonTaxable Allow");
			JLabel lblPymntType = new JLabel("Payment Type");
			JLabel lblAcctNo = new JLabel("Account No.");

			tbl = new JTable(tableEmployee);

			txtempid.setCapital(true);

			TableCellRenderer renderer = new TestRenderer();
			tbl.setDefaultRenderer(Object.class, renderer);
			scrollPane = new JScrollPane(tbl);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			tbl.setSelectionMode(0);

			//Salary Rate Type
			String sql = "SELECT * FROM salaryratetype ORDER By srtype_desc";
			tableEmployee.resultSet = tableEmployee.statement.executeQuery(sql);

			jcbSalaryRateType.setFont(new Font("",Font.PLAIN, 12));
			jcbSalaryRateType.setBackground(Color.white);
			jcbSalaryRateType.addItem("Choose");
			while (tableEmployee.resultSet.next()){
				jcbSalaryRateType.addItem(Formatter.formatString(50,' ','-',tableEmployee.resultSet.getString("srtype_desc"))+"-"+tableEmployee.resultSet.getString("srtype_code"));
			}//while (tableEmployee.resultSet.next())

			//Employment Type
			sql = "SELECT * FROM emptype ORDER By etype_desc";
			tableEmployee.resultSet = tableEmployee.statement.executeQuery(sql);

			jcbEmpType.setFont(new Font("",Font.PLAIN, 12));
			jcbEmpType.setBackground(Color.white);
			jcbEmpType.addItem("Choose");
			while (tableEmployee.resultSet.next()){
				jcbEmpType.addItem(Formatter.formatString(50,' ','-',tableEmployee.resultSet.getString("etype_desc"))+"-"+tableEmployee.resultSet.getString("etype_code"));
			}//while (tableEmployee.resultSet.next())

			//Rank
			sql = "SELECT * FROM rank ORDER By rank_desc";
			tableEmployee.resultSet = tableEmployee.statement.executeQuery(sql);

			jcbRank.setFont(new Font("",Font.PLAIN, 12));
			jcbRank.setBackground(Color.white);
			jcbRank.addItem("Choose");
			while (tableEmployee.resultSet.next()){
				jcbRank.addItem(Formatter.formatString(50,' ','-',tableEmployee.resultSet.getString("rank_desc"))+"-"+tableEmployee.resultSet.getString("rank_code"));
			}//while (tableEmployee.resultSet.next())

			//Employment Status
			sql = "SELECT * FROM empstatus ORDER By estat_desc";
			tableEmployee.resultSet = tableEmployee.statement.executeQuery(sql);

			jcbEmpStatus.setFont(new Font("",Font.PLAIN, 12));
			jcbEmpStatus.setBackground(Color.white);
			jcbEmpStatus.addItem("Choose");
			while (tableEmployee.resultSet.next()){
				jcbEmpStatus.addItem(Formatter.formatString(50,' ','-',tableEmployee.resultSet.getString("estat_desc"))+"-"+tableEmployee.resultSet.getString("estat_code"));
			}//while (tableEmployee.resultSet.next())

			//Cost Center
			sql = "SELECT * FROM costcenter ORDER By cost_desc";
			tableEmployee.resultSet = tableEmployee.statement.executeQuery(sql);

			jcbCostCenter.setFont(new Font("",Font.PLAIN, 12));
			jcbCostCenter.setBackground(Color.white);
			jcbCostCenter.addItem("Choose");
			while (tableEmployee.resultSet.next()){
				jcbCostCenter.addItem(Formatter.formatString(50,' ','-',tableEmployee.resultSet.getString("cost_desc"))+"-"+tableEmployee.resultSet.getString("cost_code"));
			}//while (tableEmployee.resultSet.next())

			//Department
			sql = "SELECT * FROM department ORDER By dept_desc";
			tableEmployee.resultSet = tableEmployee.statement.executeQuery(sql);

			jcbDepartment.setFont(new Font("",Font.PLAIN, 12));
			jcbDepartment.setBackground(Color.white);
			jcbDepartment.addItem("Choose");
			while (tableEmployee.resultSet.next()){
				jcbDepartment.addItem(Formatter.formatString(50,' ','-',tableEmployee.resultSet.getString("dept_desc"))+"-"+tableEmployee.resultSet.getString("dept_code"));
			}//while (tableEmployee.resultSet.next())

			//Position
			sql = "SELECT * FROM position ORDER By post_desc";
			tableEmployee.resultSet = tableEmployee.statement.executeQuery(sql);

			jcbPosition.setFont(new Font("",Font.PLAIN, 12));
			jcbPosition.setBackground(Color.white);
			jcbPosition.addItem("Choose");
			while (tableEmployee.resultSet.next()){
				jcbPosition.addItem(Formatter.formatString(50,' ','-',tableEmployee.resultSet.getString("post_desc"))+"-"+tableEmployee.resultSet.getString("post_code"));
			}//while (tableEmployee.resultSet.next())

			jcbAllowedOT.setFont(new Font("",Font.PLAIN, 12));
			jcbAllowedOT.setBackground(Color.white);
			jcbAllowedOT.addItem("Choose");
			jcbAllowedOT.addItem("Yes");
			jcbAllowedOT.addItem("No");

			//Payment Type
			sql = "SELECT * FROM paymenttype ORDER By pymnt_desc";
			tableEmployee.resultSet = tableEmployee.statement.executeQuery(sql);

			jcbPymntType.setFont(new Font("",Font.PLAIN, 12));
			jcbPymntType.setBackground(Color.white);
			jcbPymntType.addItem("Choose");
			while (tableEmployee.resultSet.next()){
				jcbPymntType.addItem(Formatter.formatString(50,' ','-',tableEmployee.resultSet.getString("pymnt_desc"))+"-"+tableEmployee.resultSet.getString("pymnt_code"));
			}//while (tableEmployee.resultSet.next())

			tableEmployee.resultSet = tableEmployee.statement.executeQuery(DEFAULT_QUERY);
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
			scrollPane.setPreferredSize(new Dimension(600,170));
			jpan.setBounds(10,20,600,180);

			EmpNo.setBounds(28,100,100,280);
			lblLastname.setBounds(28,130,100,280);
			lblFirstname.setBounds(28,160,100,280);
			lblMiddlename.setBounds(28,190,100,280);
			lblSalary.setBounds(28,220,100,280);
			lblSalaryRateType.setBounds(320,220,100,280);
			lblEmpType.setBounds(28,250,210,280);
			lblRank.setBounds(320,250,210,280);
			lblEmpStatus.setBounds(28,280,210,280);
			lblCostCenter.setBounds(320,280,210,280);
			lblDepartment.setBounds(28,310,210,280);
			lblPosition.setBounds(320,310,210,280);			
			lblBirthDate.setBounds(28,340,210,280);
			lblEmpDate.setBounds(320,340,210,280);
			lblTermDate.setBounds(28,370,210,280);
			lblLPayDate.setBounds(320,370,210,280);
			lblAllowOT.setBounds(28,400,210,280);
			lblFTaxAllow.setBounds(28,430,210,280);
			lblFNTaxAllow.setBounds(320,430,210,280);
			lblPymntType.setBounds(28,460,210,280);
			lblAcctNo.setBounds(320,460,210,280);

			txtempid.setBounds(120,230,151,20);
			txtempid.setDisabledTextColor(new Color(139,113,113));
			txtLastname.setBounds(120,260,240,20);
			txtLastname.setDisabledTextColor(new Color(139,113,113));
			txtFirstname.setBounds(120,290,240,20);
			txtFirstname.setDisabledTextColor(new Color(139,113,113));
			txtMiddlename.setBounds(120,320,240,20);
			txtMiddlename.setDisabledTextColor(new Color(139,113,113));
			txtSalary.setBounds(120,350,80,20);
			txtSalary.setDisabledTextColor(new Color(139,113,113));
			txtSalary.setHorizontalAlignment(JTextField.TRAILING);
			jcbSalaryRateType.setBounds(420,350,170,20);
			jcbEmpType.setBounds(120,380,170,20);
			jcbRank.setBounds(420,380,170,20);
			jcbEmpStatus.setBounds(120,410,170,20);
			jcbCostCenter.setBounds(420,410,170,20);
			jcbDepartment.setBounds(120,440,170,20);
			jcbPosition.setBounds(420,440,170,20);
			txtBirthDate.setBounds(120,470,150,20);
			cboBirthDate.setBounds(new Rectangle(265, 469, 23, 23));
			txtEmpDate.setBounds(420,470,150,20);
			cboEmpDate.setBounds(new Rectangle(565, 469, 23, 23));
			txtTermDate.setBounds(120,500,150,20);
			cboTermDate.setBounds(new Rectangle(265, 499, 23, 23));
			txtLPayDate.setBounds(420,500,150,20);
			cboLPayDate.setBounds(new Rectangle(565, 499, 23, 23));
			jcbAllowedOT.setBounds(120, 530, 100, 20);
			txtFTaxAllow.setBounds(120,560,80,20);
			txtFTaxAllow.setDisabledTextColor(new Color(139,113,113));
			txtFTaxAllow.setHorizontalAlignment(JTextField.TRAILING);
			txtFNTaxAllow.setBounds(420,560,80,20);
			txtFNTaxAllow.setDisabledTextColor(new Color(139,113,113));
			txtFNTaxAllow.setHorizontalAlignment(JTextField.TRAILING);
			jcbPymntType.setBounds(120,590,170,20);
			txtAcctNo.setBounds(420,590,130,20);
			txtAcctNo.setDisabledTextColor(new Color(139,113,113));

			jbtnadd.setBounds(30,200,100,22);
			jbtnedit.setBounds(145,200,100,22);
			jbtndelete.setBounds(260,200,100,22);
			jbtngovt.setBounds(375,200,100,22);
			jbtnprevemp.setBounds(490,200,100,22);

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
			container.add(EmpNo);
			container.add(txtempid);
			container.add(lblFirstname);
			container.add(txtFirstname);
			container.add(lblLastname);
			container.add(txtLastname);
			container.add(lblMiddlename);
			container.add(txtMiddlename);
			container.add(lblSalary);
			container.add(txtSalary);
			container.add(lblSalaryRateType);
			container.add(jcbSalaryRateType);
			container.add(lblEmpType);
			container.add(jcbEmpType);
			container.add(lblRank);
			container.add(jcbRank);
			container.add(lblEmpStatus);
			container.add(jcbEmpStatus);
			container.add(lblCostCenter);
			container.add(jcbCostCenter);
			container.add(lblDepartment);
			container.add(jcbDepartment);
			container.add(lblPosition);
			container.add(jcbPosition);
			container.add(lblBirthDate);
			container.add(txtBirthDate);
			container.add(cboBirthDate);			
			container.add(lblEmpDate);
			container.add(txtEmpDate);
			container.add(cboEmpDate);			
			container.add(lblTermDate);
			container.add(txtTermDate);
			container.add(cboTermDate);			
			container.add(lblLPayDate);
			container.add(txtLPayDate);
			container.add(cboLPayDate);			
			container.add(lblAllowOT);
			container.add(jcbAllowedOT);
			container.add(lblFTaxAllow);
			container.add(txtFTaxAllow);
			container.add(lblFNTaxAllow);
			container.add(txtFNTaxAllow);
			container.add(lblPymntType);
			container.add(jcbPymntType);
			container.add(lblAcctNo);
			container.add(txtAcctNo);
			container.add(jbtnadd);
			container.add(jbtnedit);
			container.add(jbtndelete);        
			container.add(jbtngovt);        
			container.add(jbtnprevemp);        
			container.add(txtsearch);

			//Disabled text fields
			disableFields();

			txtSalary.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtSalary.getText().indexOf(".") == -1) {
								if (txtSalary.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtSalary.getText().length() > (txtSalary.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			jcbSalaryRateType.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbSalaryRateType.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						RateType = "";
					}
					else {
						RateType = jcbSalaryRateType.getSelectedItem().toString().substring(51);
					}
				}
			});

			jcbEmpType.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbEmpType.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						EmpType = "";
					}
					else {
						EmpType = jcbEmpType.getSelectedItem().toString().substring(51);
					}
				}
			});

			jcbRank.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbRank.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						Rank = "";
					}
					else {
						Rank = jcbRank.getSelectedItem().toString().substring(51);
					}
				}
			});

			jcbEmpStatus.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbEmpStatus.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						EmpStatus = "";
					}
					else {
						EmpStatus = jcbEmpStatus.getSelectedItem().toString().substring(51);
					}
				}
			});

			jcbCostCenter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbCostCenter.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						CostCenter = "";
					}
					else {
						CostCenter = jcbCostCenter.getSelectedItem().toString().substring(51);
					}
				}
			});

			jcbDepartment.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbDepartment.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						Department = "";
					}
					else {
						Department = jcbDepartment.getSelectedItem().toString().substring(51);
					}
				}
			});

			jcbPosition.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbPosition.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						Position = "";
					}
					else {
						Position = jcbPosition.getSelectedItem().toString().substring(51);
					}
				}
			});

			cboBirthDate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (cboBirthDate.getSelectedIndex() != -1) {
						txtBirthDate.setText(cboBirthDate.getSelectedItem().toString());
					}
				}
			});
			
			cboEmpDate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (cboEmpDate.getSelectedIndex() != -1) {
						txtEmpDate.setText(cboEmpDate.getSelectedItem().toString());
					}
				}
			});
			
			cboTermDate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (cboTermDate.getSelectedIndex() != -1) {
						txtTermDate.setText(cboTermDate.getSelectedItem().toString());
					}
				}
			});
			
			cboLPayDate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (cboLPayDate.getSelectedIndex() != -1) {
						txtLPayDate.setText(cboLPayDate.getSelectedItem().toString());
					}
				}
			});
			
			jcbAllowedOT.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbAllowedOT.getSelectedItem().toString().equalsIgnoreCase("Yes") == true) {
						AllowedOT = "Y";
					}
					else if (jcbAllowedOT.getSelectedItem().toString().equalsIgnoreCase("No") == true) {
						AllowedOT = "N";
					}
					else {
						AllowedOT = "";
					}
				}
			});

			txtFTaxAllow.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtFTaxAllow.getText().indexOf(".") == -1) {
								if (txtFTaxAllow.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtFTaxAllow.getText().length() > (txtFTaxAllow.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtFNTaxAllow.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtFNTaxAllow.getText().indexOf(".") == -1) {
								if (txtFNTaxAllow.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtFNTaxAllow.getText().length() > (txtFNTaxAllow.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			jcbPymntType.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbPymntType.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						PymntType = "";
					}
					else {
						PymntType = jcbPymntType.getSelectedItem().toString().substring(51);
					}
				}
			});

			//add button listener
			jbtnadd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String caption = jbtnadd.getText();
					String xcode = txtempid.getText().trim();

					if (caption == "Add"){
						ListSelectionModel selectionModel = tbl.getSelectionModel();
						selectionModel.clearSelection();
						tbl.setEnabled(false);

						enableFields();

						clearFields();
						txtempid.grabFocus();

						jbtnadd.setText("Save");
						jbtnadd.setMnemonic('S');
						jbtndelete.setText("Cancel");
						jbtndelete.setMnemonic('C');
						jbtnedit.setEnabled(false);
						jbtngovt.setEnabled(false);
						jbtnprevemp.setEnabled(false);
					}
					else if (caption == "Save") {
						Salary = "0";
						if (txtSalary.getText().trim().length() > 0) {
							Salary = txtSalary.getText().trim().replaceAll(",","");
						}
						if (txtBirthDate.getText().trim().length() > 0) {
							try {
								BirthDate = dteFormat.format(strFormat.parse(txtBirthDate.getText().trim()));
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
						}
						else {
							BirthDate = "0000-00-00";
						}
						if (txtEmpDate.getText().trim().length() > 0) {
							try {
								EmpDate = dteFormat.format(strFormat.parse(txtEmpDate.getText().trim()));
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
						}
						else {
							EmpDate = "0000-00-00";
						}
						if (txtTermDate.getText().trim().length() > 0) {
							try {
								TermDate = dteFormat.format(strFormat.parse(txtTermDate.getText().trim()));
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
						}
						else {
							TermDate = "0000-00-00";
						}
						if (txtLPayDate.getText().trim().length() > 0) {
							try {
								LPayDate = dteFormat.format(strFormat.parse(txtLPayDate.getText().trim()));
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
						}
						else {
							LPayDate = "0000-00-00";
						}
						FTaxAllow = "0";
						if (txtFTaxAllow.getText().trim().length() > 0) {
							FTaxAllow = txtFTaxAllow.getText().trim().replaceAll(",","");
						}
						FNTaxAllow = "0";
						if (txtFNTaxAllow.getText().trim().length() > 0) {
							FNTaxAllow = txtFNTaxAllow.getText().trim().replaceAll(",","");
						}

						int iColumn = 0;
						String save_sql = DBConnect.Insert("emppif",
								"empid,lastname,firstname,middlename,salary,srtype_code,etype_code,rank_code,estat_code," +
								"cost_code,dept_code,postn_code,birth_date,empl_date,term_date,pastpay_date,allowed_ot," +
								"fixed_taxallow,fixed_nontaxallow,pymnt_code,accnt_no",
								"'"+DBConnect.clean(xcode)+"', " +
								"'"+DBConnect.clean(txtLastname.getText().trim())+"', " +
								"'"+DBConnect.clean(txtFirstname.getText().trim())+"', " +
								"'"+DBConnect.clean(txtMiddlename.getText().trim())+"', " +
								"'"+Salary+"', " +
								"'"+RateType+"', " +
								"'"+EmpType+"', " +
								"'"+Rank+"', " +
								"'"+EmpStatus+"', " +
								"'"+CostCenter+"', " +
								"'"+Department+"', " +
								"'"+Position+"', " +
								"'"+BirthDate+"', " +
								"'"+EmpDate+"', " +
								"'"+TermDate+"', " +
								"'"+LPayDate+"', " +
								"'"+AllowedOT+"', " +
								"'"+FTaxAllow+"', " +
								"'"+FNTaxAllow+"', " +
								"'"+PymntType+"', " +
								"'"+txtAcctNo.getText().trim()+"' "
						);

						tbl.setEnabled(false);
						try {
							if (xcode.length() == 0) {   
								Message.messageError("Invalid input for Employee Number.");
								txtempid.grabFocus();
							}
							else if(txtLastname.getText().trim().length() == 0) {
								Message.messageError("Invalid input for Last Name field.");
								txtLastname.grabFocus();
							}
							else if(txtFirstname.getText().trim().length() == 0) {
								Message.messageError("Invalid input for First Name field.");
								txtFirstname.grabFocus();
							}
							else if(txtMiddlename.getText().trim().length() == 0) {
								Message.messageError("Invalid input for Middle Name field.");
								txtMiddlename.grabFocus();
							}
							else if(Double.parseDouble(Salary) == 0) {
								Message.messageError("Invalid input for Salary field.");
								txtSalary.grabFocus();
							}
							else if(jcbSalaryRateType.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Salary Rate Type field.");
								jcbSalaryRateType.grabFocus();
							}
							else if(jcbEmpType.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Employment Type field.");
								jcbEmpType.grabFocus();
							}
							else if(jcbRank.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Rank field.");
								jcbRank.grabFocus();
							}
							else if(jcbEmpStatus.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Employment Status field.");
								jcbEmpStatus.grabFocus();
							}
							else if(jcbCostCenter.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Cost Center field.");
								jcbCostCenter.grabFocus();
							}
							else if(jcbDepartment.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Department field.");
								jcbDepartment.grabFocus();
							}
							else if(jcbPosition.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Position field.");
								jcbPosition.grabFocus();
							}
							else if (BirthDate.equals("0000-00-00") == true) {
								Message.messageError("Invalid input for Date of Birth field.");
								cboBirthDate.grabFocus();
							}
							else if (EmpDate.equals("0000-00-00") == true) {
								Message.messageError("Invalid input for Date Employed field.");
								cboEmpDate.grabFocus();
							}
							else if (LPayDate.equals("0000-00-00") == true) {
								Message.messageError("Invalid input for Last CutOff Date field.");
								cboLPayDate.grabFocus();
							}
							else if(jcbAllowedOT.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Allowed OT field.");
								jcbAllowedOT.grabFocus();
							}
							else if(jcbPymntType.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Payment Type field.");
								jcbPymntType.grabFocus();
							}
							else{
								tableEmployee.setInsert(save_sql);

								tableEmployee.setQuery(DEFAULT_QUERY);
								tbl.getColumnModel().getColumn(iColumn).setMaxWidth(0);
								tbl.getColumnModel().getColumn(iColumn).setMinWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(iColumn).setMaxWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(iColumn).setMinWidth(0);
								tbl.getColumnModel().getColumn(0).setMaxWidth(147);
								tbl.getColumnModel().getColumn(0).setMinWidth(147);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(147);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(147);
								tbl.getColumnModel().getColumn(1).setMaxWidth(147);
								tbl.getColumnModel().getColumn(1).setMinWidth(147);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(147);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(147);
								tbl.getColumnModel().getColumn(2).setMaxWidth(147);
								tbl.getColumnModel().getColumn(2).setMinWidth(147);
								tbl.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(147);
								tbl.getTableHeader().getColumnModel().getColumn(2).setMinWidth(147);
								tbl.getColumnModel().getColumn(3).setMaxWidth(143);
								tbl.getColumnModel().getColumn(3).setMinWidth(143);
								tbl.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(143);
								tbl.getTableHeader().getColumnModel().getColumn(3).setMinWidth(143);
								tbl.getColumnModel().getColumn(0).setHeaderValue("Last Name");
								tbl.getColumnModel().getColumn(1).setHeaderValue("First Name");
								tbl.getColumnModel().getColumn(2).setHeaderValue("Middle Name");
								tbl.getColumnModel().getColumn(3).setHeaderValue("Employee No.");

								curRecord = txtempid.getText().trim();
								
								clearFields();

								Message.messageInfo(Message.messageAdd);
								//tbl.scrollRectToVisible(tbl.getCellRect(tbl.getRowCount()-1, tbl.getColumnCount(), true));
								tbl.scrollRectToVisible(tbl.getCellRect(setCurRecord(), tbl.getColumnCount(), true));
								txtempid.grabFocus();
							}
						}
						catch(SQLException se){
							try{
								tableEmployee.setQuery(DEFAULT_QUERY);
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
									String xcode = txtempid.getText();

									//row level locking
									LockHandler.startLock(DBConnect.SelectForUpdate("emppif",
											"*",
											"empid='"+DBConnect.clean(tbl.getValueAt(tbl.getSelectedRow(),0).toString())+"'"));

									String del_sql = DBConnect.delete("emppif","empid = '"+DBConnect.clean(xcode)+"'");
									String del_sql1 = DBConnect.delete("empgovcont","empid = '"+DBConnect.clean(xcode)+"'");
									String del_sql2 = DBConnect.delete("emphist","empid = '"+DBConnect.clean(xcode)+"'");
									//System.out.println(del_sql);
									
									txtsearch.setVisible(false);
									mainform.lblMessage.setText("");
									//int setDialog = JOptionPane.showConfirmDialog(container, "Are you sure ?",  "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
									int setDialog = Message.messageYesNo("Are you sure you want to delete\n" + txtempid.getText()+ ": "+ txtFirstname.getText().toString() + " "+txtMiddlename.getText().toString() + " "+ txtLastname.getText().toString()+ " ?");
									if (JOptionPane.YES_OPTION == setDialog) {
										try {
											// unlock mode and delete 
											LockHandler.removeLockAndDelete(del_sql2);
											LockHandler.removeLockAndDelete(del_sql1);
											LockHandler.removeLockAndDelete(del_sql);
											//tableEmployee.setInsert(del_sql);
											//																												
											//tableEmployee.setQuery(DEFAULT_QUERY);
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
										tableEmployee.setQuery(DEFAULT_QUERY);
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
					String xcode = txtempid.getText().trim();

					int currentr = tbl.getSelectedRow();
					int currentc = tbl.getSelectedColumn();	
					if (ecaption == "Edit"){
						if (txtempid.getText().toString().length() >= 1){
							currentr = tbl.getSelectedRow();
							currentc = tbl.getSelectedColumn();	

							// start indicating lock
							LockHandler.initializeLock(container);

							//wait for lock indicator to invoke
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									try {
										//row level locking
										LockHandler.startLock(DBConnect.SelectForUpdate("emppif",
												"*",
												"empid='"+tbl.getValueAt(tbl.getSelectedRow(),0).toString()+"'"));
										//refresh
										tableEmployee.setQuery(DEFAULT_QUERY);
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
										jbtngovt.setEnabled(false);
										jbtnprevemp.setEnabled(false);

										enableFields();

										txtempid.setEnabled(false);
										txtLastname.grabFocus();

									}
									catch (SQLException e) {
										try {
											//refresh and remove lock mode
											tableEmployee.setQuery(DEFAULT_QUERY);
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
							Salary = "0";
							if (txtSalary.getText().trim().length() > 0) {
								Salary = txtSalary.getText().trim().replaceAll(",","");
							}
							if (txtBirthDate.getText().trim().length() > 0) {
								try {
									BirthDate = dteFormat.format(strFormat.parse(txtBirthDate.getText().trim()));
								} catch (ParseException e1) {
									e1.printStackTrace();
								}
							}
							else {
								BirthDate = "0000-00-00";
							}
							if (txtEmpDate.getText().trim().length() > 0) {
								try {
									EmpDate = dteFormat.format(strFormat.parse(txtEmpDate.getText().trim()));
								} catch (ParseException e1) {
									e1.printStackTrace();
								}
							}
							else {
								EmpDate = "0000-00-00";
							}
							if (txtTermDate.getText().trim().length() > 0) {
								try {
									TermDate = dteFormat.format(strFormat.parse(txtTermDate.getText().trim()));
								} catch (ParseException e1) {
									e1.printStackTrace();
								}
							}
							else {
								TermDate = "0000-00-00";
							}
							if (txtLPayDate.getText().trim().length() > 0) {
								try {
									LPayDate = dteFormat.format(strFormat.parse(txtLPayDate.getText().trim()));
								} catch (ParseException e1) {
									e1.printStackTrace();
								}
							}
							else {
								LPayDate = "0000-00-00";
							}
							FTaxAllow = "0";
							if (txtFTaxAllow.getText().trim().length() > 0) {
								FTaxAllow = txtFTaxAllow.getText().trim().replaceAll(",","");
							}
							FNTaxAllow = "0";
							if (txtFNTaxAllow.getText().trim().length() > 0) {
								FNTaxAllow = txtFNTaxAllow.getText().trim().replaceAll(",","");
							}
							
							String e_sql = 
								DBConnect.Update("emppif",
										"lastname = '"+DBConnect.clean(txtLastname.getText().trim())+"', " +
										"firstname = '"+DBConnect.clean(txtFirstname.getText().trim())+"', " +
										"middlename = '"+DBConnect.clean(txtMiddlename.getText().trim())+"', " +
										"salary = '"+Salary+"', " +
										"srtype_code = '"+RateType+"', " +
										"etype_code = '"+EmpType+"', " +
										"rank_code = '"+Rank+"', " +
										"estat_code = '"+EmpStatus+"', " +
										"cost_code = '"+CostCenter+"', " +
										"dept_code = '"+Department+"', " +
										"postn_code = '"+Position+"', " +
										"birth_date = '"+BirthDate+"', " +
										"empl_date = '"+EmpDate+"', " +
										"term_date = '"+TermDate+"', " +
										"pastpay_date = '"+LPayDate+"', " +
										"allowed_ot = '"+AllowedOT+"', " +
										"fixed_taxallow = '"+FTaxAllow+"', " +
										"fixed_nontaxallow = '"+FNTaxAllow+"', " +
										"pymnt_code = '"+PymntType+"', " +
										"accnt_no = '"+txtAcctNo.getText().trim()+"'",
										"empid = '" +xcode+"'");
							
							if(txtLastname.getText().trim().length() == 0) {
								Message.messageError("Invalid input for Last Name field.");
								txtLastname.grabFocus();
							}
							else if(txtFirstname.getText().trim().length() == 0) {
								Message.messageError("Invalid input for First Name field.");
								txtFirstname.grabFocus();
							}
							else if(txtMiddlename.getText().trim().length() == 0) {
								Message.messageError("Invalid input for Middle Name field.");
								txtMiddlename.grabFocus();
							}
							else if(Double.parseDouble(Salary) == 0) {
								Message.messageError("Invalid input for Salary field.");
								txtSalary.grabFocus();
							}
							else if(jcbSalaryRateType.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Salary Rate Type field.");
								jcbSalaryRateType.grabFocus();
							}
							else if(jcbEmpType.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Employment Type field.");
								jcbEmpType.grabFocus();
							}
							else if(jcbRank.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Rank field.");
								jcbRank.grabFocus();
							}
							else if(jcbEmpStatus.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Employment Status field.");
								jcbEmpStatus.grabFocus();
							}
							else if(jcbCostCenter.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Cost Center field.");
								jcbCostCenter.grabFocus();
							}
							else if(jcbDepartment.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Department field.");
								jcbDepartment.grabFocus();
							}
							else if(jcbPosition.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Position field.");
								jcbPosition.grabFocus();
							}
							else if (BirthDate.equals("0000-00-00") == true) {
								Message.messageError("Invalid input for Date of Birth field.");
								cboBirthDate.grabFocus();
							}
							else if (EmpDate.equals("0000-00-00") == true) {
								Message.messageError("Invalid input for Date Employed field.");
								cboEmpDate.grabFocus();
							}
							else if (LPayDate.equals("0000-00-00") == true) {
								Message.messageError("Invalid input for Last CutOff Date field.");
								cboLPayDate.grabFocus();
							}
							else if(jcbAllowedOT.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Allowed OT field.");
								jcbAllowedOT.grabFocus();
							}
							else if(jcbPymntType.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Payment Type field.");
								jcbPymntType.grabFocus();
							}
							else {
								//unlock mode then update
								LockHandler.removeLockAndUpdate(e_sql);

								tableEmployee.setQuery(DEFAULT_QUERY);
								tbl.setEnabled(true);
								jbtnadd.setText("Add");
								jbtndelete.setText("Delete");
								jbtndelete.setMnemonic('D');
								jbtnedit.setText("Edit");
								jbtnedit.setMnemonic('E');
								jbtnadd.setEnabled(true);
								jbtngovt.setEnabled(true);
								jbtnprevemp.setEnabled(true);
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

			//Government Contribution button listener
			jbtngovt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (tbl.getSelectedRow() >= 0) {
						int getrow = tbl.getSelectedRow();
						new EmployeeGovtContMaint(tbl.getValueAt(getrow,3).toString(),
								tbl.getValueAt(getrow,0).toString().trim()+", "+
								tbl.getValueAt(getrow,1).toString().trim()+" "+
								tbl.getValueAt(getrow,2).toString().trim(),
								Width(),Height());
					}
				}
			});

			//Previous Employer button listener
			jbtnprevemp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (tbl.getSelectedRow() >= 0) {
						int getrow = tbl.getSelectedRow();
						new EmployeePrevEmpMaint(tbl.getValueAt(getrow,3).toString(),
								tbl.getValueAt(getrow,0).toString().trim()+", "+
								tbl.getValueAt(getrow,1).toString().trim()+" "+
								tbl.getValueAt(getrow,2).toString().trim(),
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
							txtsearch.setText("LastName:" + " " + e.getKeyChar());
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

			setSize(620,660);
			setVisible(true);

		}//try
		catch ( ClassNotFoundException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("EmployeeMaint", "EmployeeMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch ( SQLException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("EmployeeMaint", "EmployeeMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch (Exception e) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("EmployeeMaint", "EmployeeMaint", traceWriter.toString());        
			dispose();
		}
	}//ENDof public EmployeeMaint() {

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
		jbtngovt.setEnabled(true);
		jbtnprevemp.setEnabled(true);
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
			tableEmployee.setQuery(DEFAULT_QUERY);
			tbl.getColumnModel().getColumn(0).setMaxWidth(0);
			tbl.getColumnModel().getColumn(0).setMinWidth(0);
			tbl.getColumnModel().getColumn(1).setMaxWidth(0);
			tbl.getColumnModel().getColumn(1).setMinWidth(0);
			tbl.getColumnModel().getColumn(2).setMaxWidth(0);
			tbl.getColumnModel().getColumn(2).setMinWidth(0);
			tbl.getColumnModel().getColumn(3).setMaxWidth(0);
			tbl.getColumnModel().getColumn(3).setMinWidth(0);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(147);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(147);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(147);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(147);
			tbl.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(147);
			tbl.getTableHeader().getColumnModel().getColumn(2).setMinWidth(147);
			tbl.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(143);
			tbl.getTableHeader().getColumnModel().getColumn(3).setMinWidth(143);

			//set column header width (to avoid the ... sign)
			tbl.getColumnModel().getColumn(0).setHeaderValue("Last Name");
			tbl.getColumnModel().getColumn(1).setHeaderValue("First Name");
			tbl.getColumnModel().getColumn(2).setHeaderValue("Middle Name");
			tbl.getColumnModel().getColumn(3).setHeaderValue("Employee No.");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void reload() {

	private void enableFields() {
		txtempid.setEnabled(true);
		txtLastname.setEnabled(true);
		txtFirstname.setEnabled(true);
		txtMiddlename.setEnabled(true);
		txtSalary.setEnabled(true);
		jcbSalaryRateType.setEnabled(true);
		jcbEmpType.setEnabled(true);
		jcbRank.setEnabled(true);
		jcbEmpStatus.setEnabled(true);
		jcbCostCenter.setEnabled(true);
		jcbDepartment.setEnabled(true);
		jcbPosition.setEnabled(true);
		txtBirthDate.setEnabled(true);
		txtBirthDate.setFocusable(false);
		cboBirthDate.setEnabled(true);
		txtEmpDate.setEnabled(true);
		txtEmpDate.setFocusable(false);
		cboEmpDate.setEnabled(true);
		txtTermDate.setEnabled(true);
		txtTermDate.setFocusable(false);
		cboTermDate.setEnabled(true);
		txtLPayDate.setEnabled(true);
		txtLPayDate.setFocusable(false);
		cboLPayDate.setEnabled(true);
		jcbAllowedOT.setEnabled(true);
		txtFTaxAllow.setEnabled(true);
		txtFNTaxAllow.setEnabled(true);
		jcbPymntType.setEnabled(true);
		txtAcctNo.setEnabled(true);
	}//ENDof private void enableFields() {

	private void disableFields() {
		txtempid.setEnabled(false);
		txtLastname.setEnabled(false);
		txtFirstname.setEnabled(false);
		txtMiddlename.setEnabled(false);
		txtSalary.setEnabled(false);
		jcbSalaryRateType.setEnabled(false);
		jcbEmpType.setEnabled(false);
		jcbRank.setEnabled(false);
		jcbEmpStatus.setEnabled(false);
		jcbCostCenter.setEnabled(false);
		jcbDepartment.setEnabled(false);
		jcbPosition.setEnabled(false);
		txtBirthDate.setEnabled(false);
		cboBirthDate.setEnabled(false);
		txtEmpDate.setEnabled(false);
		cboEmpDate.setEnabled(false);
		txtTermDate.setEnabled(false);
		cboTermDate.setEnabled(false);
		txtLPayDate.setEnabled(false);
		cboLPayDate.setEnabled(false);
		jcbAllowedOT.setEnabled(false);
		txtFTaxAllow.setEnabled(false);
		txtFNTaxAllow.setEnabled(false);
		jcbPymntType.setEnabled(false);
		txtAcctNo.setEnabled(false);
	}//ENDof private void disableFields() {

	private void clearFields() {
		txtempid.setText("");
		txtLastname.setText("");
		txtFirstname.setText("");
		txtMiddlename.setText("");
		txtSalary.setText("");
		jcbSalaryRateType.setSelectedItem("Choose");
		jcbEmpType.setSelectedItem("Choose");
		jcbRank.setSelectedItem("Choose");
		jcbEmpStatus.setSelectedItem("Choose");
		jcbCostCenter.setSelectedItem("Choose");
		jcbDepartment.setSelectedItem("Choose");
		jcbPosition.setSelectedItem("Choose");
		cboBirthDate.setSelectedItem(strFormat.format(new Date()));
		txtBirthDate.setText("");
		cboEmpDate.setSelectedItem(strFormat.format(new Date()));
		txtEmpDate.setText("");
		cboTermDate.setSelectedItem(strFormat.format(new Date()));
		txtTermDate.setText("");
		cboLPayDate.setSelectedItem(strFormat.format(new Date()));
		txtLPayDate.setText("");
		jcbAllowedOT.setSelectedItem("Choose");
		txtFTaxAllow.setText("");
		txtFNTaxAllow.setText("");
		jcbPymntType.setSelectedItem("Choose");
		txtAcctNo.setText("");
	}//ENDof private void clearFields() {

	private void setFieldValue() {
		if (tbl.getSelectedRow() >= 0) {
			int getrow = tbl.getSelectedRow();
			txtempid.setText(tbl.getValueAt(getrow,3).toString());
			txtLastname.setText(tbl.getValueAt(getrow,0).toString());
			txtFirstname.setText(tbl.getValueAt(getrow,1).toString());
			txtMiddlename.setText(tbl.getValueAt(getrow,2).toString());
			txtSalary.setText(tbl.getValueAt(getrow,4).toString());
			jcbSalaryRateType.setSelectedIndex(0);
			for (int ii=0; ii<=jcbSalaryRateType.getItemCount()-1; ii++) {
				if (jcbSalaryRateType.getItemAt(ii).toString().length() > 50) {
					if (tbl.getValueAt(getrow,5).toString().equalsIgnoreCase(jcbSalaryRateType.getItemAt(ii).toString().substring(51)) == true) {
						jcbSalaryRateType.setSelectedIndex(ii);
					}
				}
			}//ENDof for (int ii=0; ii<=jcbSalaryRateType.getItemCount()-1; ii++) {
			jcbEmpType.setSelectedIndex(0);
			for (int ii=0; ii<=jcbEmpType.getItemCount()-1; ii++) {
				if (jcbEmpType.getItemAt(ii).toString().length() > 50) {
					if (tbl.getValueAt(getrow,6).toString().equalsIgnoreCase(jcbEmpType.getItemAt(ii).toString().substring(51)) == true) {
						jcbEmpType.setSelectedIndex(ii);
					}
				}
			}//ENDof for (int ii=0; ii<=jcbEmpType.getItemCount()-1; ii++) {
			jcbRank.setSelectedIndex(0);
			for (int ii=0; ii<=jcbRank.getItemCount()-1; ii++) {
				if (jcbRank.getItemAt(ii).toString().length() > 50) {
					if (tbl.getValueAt(getrow,7).toString().equalsIgnoreCase(jcbRank.getItemAt(ii).toString().substring(51)) == true) {
						jcbRank.setSelectedIndex(ii);
					}
				}
			}//ENDof for (int ii=0; ii<=jcbRank.getItemCount()-1; ii++) {
			jcbEmpStatus.setSelectedIndex(0);
			for (int ii=0; ii<=jcbEmpStatus.getItemCount()-1; ii++) {
				if (jcbEmpStatus.getItemAt(ii).toString().length() > 50) {
					if (tbl.getValueAt(getrow,8).toString().equalsIgnoreCase(jcbEmpStatus.getItemAt(ii).toString().substring(51)) == true) {
						jcbEmpStatus.setSelectedIndex(ii);
					}
				}
			}//ENDof for (int ii=0; ii<=jcbEmpStatus.getItemCount()-1; ii++) {
			jcbCostCenter.setSelectedIndex(0);
			for (int ii=0; ii<=jcbCostCenter.getItemCount()-1; ii++) {
				if (jcbCostCenter.getItemAt(ii).toString().length() > 50) {
					if (tbl.getValueAt(getrow,9).toString().equalsIgnoreCase(jcbCostCenter.getItemAt(ii).toString().substring(51)) == true) {
						jcbCostCenter.setSelectedIndex(ii);
					}
				}
			}//ENDof for (int ii=0; ii<=jcbCostCenter.getItemCount()-1; ii++) {
			jcbDepartment.setSelectedIndex(0);
			for (int ii=0; ii<=jcbDepartment.getItemCount()-1; ii++) {
				if (jcbDepartment.getItemAt(ii).toString().length() > 50) {
					if (tbl.getValueAt(getrow,10).toString().equalsIgnoreCase(jcbDepartment.getItemAt(ii).toString().substring(51)) == true) {
						jcbDepartment.setSelectedIndex(ii);
					}
				}
			}//ENDof for (int ii=0; ii<=jcbDepartment.getItemCount()-1; ii++) {
			jcbPosition.setSelectedIndex(0);
			for (int ii=0; ii<=jcbPosition.getItemCount()-1; ii++) {
				if (jcbPosition.getItemAt(ii).toString().length() > 50) {
					if (tbl.getValueAt(getrow,11).toString().equalsIgnoreCase(jcbPosition.getItemAt(ii).toString().substring(51)) == true) {
						jcbPosition.setSelectedIndex(ii);
					}
				}
			}//ENDof for (int ii=0; ii<=jcbDepartment.getItemCount()-1; ii++) {
			if (tbl.getValueAt(getrow,12).toString().equals("0000-00-00") == false) {
				try {
					cboBirthDate.setSelectedItem(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,12).toString())));
					txtBirthDate.setText(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,12).toString())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			else {
				cboBirthDate.setSelectedItem(strFormat.format(new Date()));
				txtBirthDate.setText("");
			}
			if (tbl.getValueAt(getrow,13).toString().equals("0000-00-00") == false) {
				try {
					cboEmpDate.setSelectedItem(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,13).toString())));
					txtEmpDate.setText(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,13).toString())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			else {
				cboEmpDate.setSelectedItem(strFormat.format(new Date()));
				txtEmpDate.setText("");
			}
			if (tbl.getValueAt(getrow,14).toString().equals("0000-00-00") == false) {
				try {
					cboTermDate.setSelectedItem(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,14).toString())));
					txtTermDate.setText(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,14).toString())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			else {
				cboTermDate.setSelectedItem(strFormat.format(new Date()));
				txtTermDate.setText("");
			}
			if (tbl.getValueAt(getrow,15).toString().equals("0000-00-00") == false) {
				try {
					cboLPayDate.setSelectedItem(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,15).toString())));
					txtLPayDate.setText(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,15).toString())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			else {
				cboLPayDate.setSelectedItem(strFormat.format(new Date()));
				txtLPayDate.setText("");
			}
			if (tbl.getValueAt(getrow,16).toString().equalsIgnoreCase("Y") == true) {
				jcbAllowedOT.setSelectedItem("Yes");
			} 
			else if (tbl.getValueAt(getrow,16).toString().equalsIgnoreCase("N") == true) {
				jcbAllowedOT.setSelectedItem("No");
			} 
			else {
				jcbAllowedOT.setSelectedItem("Choose");
			}			
			txtFTaxAllow.setText(tbl.getValueAt(getrow,17).toString());
			txtFNTaxAllow.setText(tbl.getValueAt(getrow,18).toString());
			jcbPymntType.setSelectedIndex(0);
			for (int ii=0; ii<=jcbPymntType.getItemCount()-1; ii++) {
				if (jcbPymntType.getItemAt(ii).toString().length() > 50) {
					if (tbl.getValueAt(getrow,19).toString().equalsIgnoreCase(jcbPymntType.getItemAt(ii).toString().substring(51)) == true) {
						jcbPymntType.setSelectedIndex(ii);
					}
				}
			}//ENDof for (int ii=0; ii<=jcbDepartment.getItemCount()-1; ii++) {
			txtAcctNo.setText(tbl.getValueAt(getrow,20).toString());
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
					if (tbl.getValueAt(i,3).toString().equalsIgnoreCase(curRecord) == true) {
						tbl.setRowSelectionInterval(i,i);
						tempR = i;
						break;
					}
				}//ENDof for (int i=0; i<=tbl.getRowCount()-1; i++) {
			}//ENDof if (curRecord.trim().length() > 0) {
		}//ENDof if (tbl.getRowCount() > 0) {
		return i;
	}//ENDof private int setCurRecord() {
	
}//ENDof public class EmployeeMaint extends JInternalFrame {
