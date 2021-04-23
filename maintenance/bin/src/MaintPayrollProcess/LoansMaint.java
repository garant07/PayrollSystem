package maintenance.bin.src.MaintPayrollProcess;

/**
 * Author : Norberto L. Silva
 * Date   : March 13, 2012
 * Company: Applied Ideas, Inc 
 * Program: Loans Maintenance ==-> Payroll System
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

public class LoansMaint extends JInternalFrame {
	private static final long serialVersionUID = 1L;
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	static final String DEFAULT_QUERY = DBConnect.Select("emppif as e, loan as l, loantype as t ",
										"CONCAT(e.lastname,', ',e.firstname,' ',e.middlename) as empname, "+
										"t.lntype_desc, "+
										"l.empid, "+
										"l.loan_type, "+
										"FORMAT(l.principal,2) AS principal, "+
										"FORMAT(l.interest,2) AS interest, "+
										"FORMAT(l.amortization,2) AS amortization, "+
										"FORMAT(l.balance,2) AS balance, "+
										"FORMAT(l.amount_paid,2) AS amount_paid, "+
										"CONVERT(l.start_date,CHAR) as sdate, "+
										"l.freq_code, "+
										"l.hold, "+
										"l.priority, "+
										"FORMAT(l.amortization1,2) AS amortization1, "+
										"FORMAT(l.amortization2,2) AS amortization2 ",
										"e.empid = l.empid "+
										"AND t.lntype_code = l.loan_type ",
										"empname, lntype_desc");

	private ResultSetTableModel tableLoans;
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
	private JComboBox jcbLoanType = new JComboBox();
	private JFormattedTextField txtPrincipal = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtInterest = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtAmortization = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtBalance = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtAmountPaid = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private MyField txtStartDate = new MyField(true,15);	
	private DateComboBox cboStartDate = new DateComboBox();
	private JComboBox jcbFrequency = new JComboBox();
	private JFormattedTextField txtAmortization1 = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtAmortization2 = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JComboBox jcbHold = new JComboBox();
	private MyField txtPriority = new MyField(false,2);

	private JTextField txtsearch = new JTextField();

	SimpleDateFormat dteFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat strFormat = new SimpleDateFormat("MMMM dd, yyyy");

	String Employee			= "";
	String LoanType			= "";
	String Principal		= "0";
	String Interest			= "0";
	String Amortization		= "0";
	String Balance			= "0";
	String AmountPaid		= "0";
	String StartDate		= "0000-00-00";
	String Frequency		= "";
	String Hold				= "";
	String Amortization1	= "0";
	String Amortization2	= "0";

	public LoansMaint() {
		super ("Loans Maintenance", false, true, false, false);
		this.setName("loansmaint");
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
			tableLoans = new ResultSetTableModel( DEFAULT_QUERY );

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
			JLabel lblLoanType = new JLabel("Loan Type");
			JLabel lblPrincipal = new JLabel("Principal");
			JLabel lblInterest = new JLabel("Interest");
			JLabel lblAmortization = new JLabel("Amortization");
			JLabel lblBalance = new JLabel("Balance");
			JLabel lblAmounPaid = new JLabel("Amount Paid");
			JLabel lblStartDate = new JLabel("Starting Date");
			JLabel lblFrequency = new JLabel("Frequency");
			JLabel lblAmortization1 = new JLabel("1st Half");
			JLabel lblAmortization2 = new JLabel("2nd Half");
			JLabel lblHold = new JLabel("Hold");
			JLabel lblPriority = new JLabel("Priority");

			tbl = new JTable(tableLoans);

			TableCellRenderer renderer = new TestRenderer();
			tbl.setDefaultRenderer(Object.class, renderer);
			scrollPane = new JScrollPane(tbl);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			tbl.setSelectionMode(0);

			//Employee
			String sql = "SELECT CONCAT(lastname,', ',firstname,' ',middlename) as empname, empid" +
						 "  FROM emppif ORDER By empname";
			tableLoans.resultSet = tableLoans.statement.executeQuery(sql);

			jcbEmployee.setFont(new Font("",Font.PLAIN, 12));
			jcbEmployee.setBackground(Color.white);
			jcbEmployee.addItem("Choose");
			while (tableLoans.resultSet.next()){
				jcbEmployee.addItem(Formatter.formatString(100,' ','-',tableLoans.resultSet.getString("empname"))+"-"+tableLoans.resultSet.getString("empid"));
			}//while (tableLoans.resultSet.next())

			//Loan Type
			sql = "SELECT * FROM loantype ORDER By lntype_desc";
			tableLoans.resultSet = tableLoans.statement.executeQuery(sql);

			jcbLoanType.setFont(new Font("",Font.PLAIN, 12));
			jcbLoanType.setBackground(Color.white);
			jcbLoanType.addItem("Choose");
			while (tableLoans.resultSet.next()){
				jcbLoanType.addItem(Formatter.formatString(100,' ','-',tableLoans.resultSet.getString("lntype_desc"))+"-"+tableLoans.resultSet.getString("lntype_code"));
			}//while (tableLoans.resultSet.next())

			//Frequency
			sql = "SELECT * FROM frequency ORDER By freq_desc";
			tableLoans.resultSet = tableLoans.statement.executeQuery(sql);

			jcbFrequency.setFont(new Font("",Font.PLAIN, 12));
			jcbFrequency.setBackground(Color.white);
			jcbFrequency.addItem("Choose");
			while (tableLoans.resultSet.next()){
				jcbFrequency.addItem(Formatter.formatString(100,' ','-',tableLoans.resultSet.getString("freq_desc"))+"-"+tableLoans.resultSet.getString("freq_code"));
			}//while (tableLoans.resultSet.next())

			jcbHold.setFont(new Font("",Font.PLAIN, 12));
			jcbHold.setBackground(Color.white);
			jcbHold.addItem("Choose");
			jcbHold.addItem("Yes");
			jcbHold.addItem("No");

			tableLoans.resultSet = tableLoans.statement.executeQuery(DEFAULT_QUERY);
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

			lblEmployee.setBounds(28,240,100,20);
			lblLoanType.setBounds(28,270,100,20);
			lblPrincipal.setBounds(28,300,100,20);
			lblInterest.setBounds(28,330,100,20);
			lblAmortization.setBounds(28,360,100,20);
			lblAmounPaid.setBounds(28,390,100,20);
			lblBalance.setBounds(28,420,100,20);
			lblStartDate.setBounds(28,450,100,20);
			lblFrequency.setBounds(28,480,100,20);
			lblAmortization1.setBounds(120,510,100,20);
			lblAmortization2.setBounds(290,510,100,20);
			lblHold.setBounds(28,540,100,20);
			lblPriority.setBounds(270,540,100,20);

			jcbEmployee.setBounds(120,240,270,20);
			jcbLoanType.setBounds(120,270,220,20);
			txtPrincipal.setBounds(120,300,80,20);
			txtPrincipal.setDisabledTextColor(new Color(139,113,113));
			txtPrincipal.setHorizontalAlignment(JTextField.TRAILING);
			txtInterest.setBounds(120,330,80,20);
			txtInterest.setDisabledTextColor(new Color(139,113,113));
			txtInterest.setHorizontalAlignment(JTextField.TRAILING);
			txtAmortization.setBounds(120,360,80,20);
			txtAmortization.setDisabledTextColor(new Color(139,113,113));
			txtAmortization.setHorizontalAlignment(JTextField.TRAILING);
			txtBalance.setBounds(120,390,80,20);
			txtBalance.setDisabledTextColor(new Color(139,113,113));
			txtBalance.setHorizontalAlignment(JTextField.TRAILING);
			txtAmountPaid.setBounds(120,420,80,20);
			txtAmountPaid.setDisabledTextColor(new Color(139,113,113));
			txtAmountPaid.setHorizontalAlignment(JTextField.TRAILING);
			txtStartDate.setBounds(120,450,150,20);
			cboStartDate.setBounds(new Rectangle(265, 449, 23, 23));
			jcbFrequency.setBounds(120,480,220,20);
			txtAmortization1.setBounds(180,510,80,20);
			txtAmortization1.setDisabledTextColor(new Color(139,113,113));
			txtAmortization1.setHorizontalAlignment(JTextField.TRAILING);
			txtAmortization2.setBounds(355,510,80,20);
			txtAmortization2.setDisabledTextColor(new Color(139,113,113));
			txtAmortization2.setHorizontalAlignment(JTextField.TRAILING);
			jcbHold.setBounds(120,540,100,20);
			txtPriority.setBounds(320,540,25,20);
			txtPriority.setDisabledTextColor(new Color(139,113,113));

			jbtnadd.setBounds(120,200,100,22);
			jbtnedit.setBounds(265,200,100,22);
			jbtndelete.setBounds(410,200,100,22);

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
			container.add(lblLoanType);
			container.add(jcbLoanType);
			container.add(lblPrincipal);
			container.add(txtPrincipal);
			container.add(lblInterest);
			container.add(txtInterest);
			container.add(lblAmortization);
			container.add(txtAmortization);
			container.add(lblBalance);
			container.add(txtBalance);
			container.add(lblAmounPaid);
			container.add(txtAmountPaid);
			container.add(lblStartDate);
			container.add(txtStartDate);
			container.add(cboStartDate);
			container.add(lblFrequency);
			container.add(jcbFrequency);
			container.add(lblAmortization1);
			container.add(txtAmortization1);
			container.add(lblAmortization2);
			container.add(txtAmortization2);
			container.add(lblHold);
			container.add(jcbHold);
			container.add(lblPriority);
			container.add(txtPriority);

			container.add(jbtnadd);
			container.add(jbtnedit);
			container.add(jbtndelete);        
			container.add(txtsearch);

			//Disabled text fields
			disableFields();

			txtPrincipal.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtPrincipal.getText().indexOf(".") == -1) {
								if (txtPrincipal.getText().length() >= 7) {
									e.consume();
								}
							}
							else {
								if (txtPrincipal.getText().length() > (txtPrincipal.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtInterest.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtInterest.getText().indexOf(".") == -1) {
								if (txtInterest.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtInterest.getText().length() > (txtInterest.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtAmortization.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtAmortization.getText().indexOf(".") == -1) {
								if (txtAmortization.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtAmortization.getText().length() > (txtAmortization.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtBalance.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtBalance.getText().indexOf(".") == -1) {
								if (txtBalance.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtBalance.getText().length() > (txtBalance.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtAmountPaid.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtAmountPaid.getText().indexOf(".") == -1) {
								if (txtAmountPaid.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtAmountPaid.getText().length() > (txtAmountPaid.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			cboStartDate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (cboStartDate.getSelectedIndex() != -1) {
						txtStartDate.setText(cboStartDate.getSelectedItem().toString());
					}
				}
			});
			
			jcbFrequency.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbFrequency.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						Frequency = "";
						if (jbtnadd.getText().equalsIgnoreCase("save") == true ||
							jbtnedit.getText().equalsIgnoreCase("update") == true) {
							txtAmortization1.setText("");
							txtAmortization2.setText("");
							txtAmortization1.setEnabled(false);
							txtAmortization2.setEnabled(false);
						}
					}
					else {
						Frequency = jcbFrequency.getSelectedItem().toString().substring(101);
						if (jbtnadd.getText().equalsIgnoreCase("save") == true ||
							jbtnedit.getText().equalsIgnoreCase("update") == true) {
							if (Frequency.equalsIgnoreCase("SEMI") == true) {
								txtAmortization1.setEnabled(true);
								txtAmortization2.setEnabled(true);
							}
							else {
								txtAmortization1.setText("");
								txtAmortization2.setText("");
								txtAmortization1.setEnabled(false);
								txtAmortization2.setEnabled(false);
							}
						}
					}
				}
			});

			txtAmortization1.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtAmortization1.getText().indexOf(".") == -1) {
								if (txtAmortization1.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtAmortization1.getText().length() > (txtAmortization1.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtAmortization2.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtAmortization2.getText().indexOf(".") == -1) {
								if (txtAmortization2.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtAmortization2.getText().length() > (txtAmortization2.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			jcbHold.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbHold.getSelectedItem().toString().equalsIgnoreCase("Yes") == true) {
						Hold = "Y";
					}
					else if (jcbHold.getSelectedItem().toString().equalsIgnoreCase("No") == true) {
						Hold = "N";
					}
					else {
						Hold = "";
					}
				}
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
						if (jcbEmployee.getSelectedIndex() != 0) {
							Employee = jcbEmployee.getSelectedItem().toString().substring(101);
						}
						else {
							Employee = "";
						}
						if (jcbLoanType.getSelectedIndex() != 0) {
							LoanType = jcbLoanType.getSelectedItem().toString().substring(101);
						}
						else {
							LoanType = "";
						}
						Principal = "0";
						if (txtPrincipal.getText().trim().length() > 0) {
							Principal = txtPrincipal.getText().trim().replaceAll(",","");
						}
						Interest = "0";
						if (txtInterest.getText().trim().length() > 0) {
							Interest = txtInterest.getText().trim().replaceAll(",","");
						}
						Amortization = "0";
						if (txtAmortization.getText().trim().length() > 0) {
							Amortization = txtAmortization.getText().trim().replaceAll(",","");
						}
						Balance = "0";
						if (txtBalance.getText().trim().length() > 0) {
							Balance = txtBalance.getText().trim().replaceAll(",","");
						}
						AmountPaid = "0";
						if (txtAmountPaid.getText().trim().length() > 0) {
							AmountPaid = txtAmountPaid.getText().trim().replaceAll(",","");
						}
						if (txtStartDate.getText().trim().length() > 0) {
							try {
								StartDate = dteFormat.format(strFormat.parse(txtStartDate.getText().trim()));
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
						}
						else {
							StartDate = "0000-00-00";
						}
						if (jcbFrequency.getSelectedIndex() != 0) {
							Frequency = jcbFrequency.getSelectedItem().toString().substring(101);
						}
						else {
							Frequency = "";
						}
						if (jcbHold.getSelectedItem().toString().trim().equalsIgnoreCase("Yes") == true) {
							Hold = "Y";
						}
						else if (jcbHold.getSelectedItem().toString().trim().equalsIgnoreCase("No") == true) {
							Hold = "N";
						}
						else {
							Hold = "";
						}
						Amortization1 = "0";
						if (txtAmortization1.getText().trim().length() > 0) {
							Amortization1 = txtAmortization1.getText().trim().replaceAll(",","");
						}
						Amortization2 = "0";
						if (txtAmortization2.getText().trim().length() > 0) {
							Amortization2 = txtAmortization2.getText().trim().replaceAll(",","");
						}
						String save_sql = DBConnect.Insert("loan",
								"empid,loan_type,principal,interest,amortization,balance," +
								"amount_paid,start_date,freq_code,hold,priority,amortization1,amortization2",
								"'"+Employee+"', " +
								"'"+LoanType+"', " +
								"'"+Principal+"', " +
								"'"+Interest+"', " +
								"'"+Amortization+"', " +
								"'"+Balance+"', " +
								"'"+AmountPaid+"', " +
								"'"+StartDate+"', " +
								"'"+Frequency+"', " +
								"'"+Hold+"', " +
								"'"+txtPriority.getText().trim()+"', " +
								"'"+Amortization1+"', " +
								"'"+Amortization2+"' "
								);

						tbl.setEnabled(false);
						try {
							if (jcbEmployee.getSelectedIndex() == 0) {   
								Message.messageError("Invalid input for Employee field.");
								jcbEmployee.grabFocus();
							}
							else if(jcbLoanType.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Loan Type field.");
								jcbLoanType.grabFocus();
							}
							else if(Double.parseDouble(Principal) == 0) {
								Message.messageError("Invalid input for Principal field.");
								txtPrincipal.grabFocus();
							}
							else if(Double.parseDouble(Amortization) == 0) {
								Message.messageError("Invalid input for Amortization field.");
								txtAmortization.grabFocus();
							}
							else if (StartDate.equals("0000-00-00") == true) {
								Message.messageError("Invalid input for Starting Date field.");
								cboStartDate.grabFocus();
							}
							else if(jcbFrequency.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Frequency field.");
								jcbFrequency.grabFocus();
							}
							else if(jcbHold.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Hold field.");
								jcbHold.grabFocus();
							}
							else if (txtPriority.getText().trim().length() == 0) {
								Message.messageError("Invalid input for Priority field.");
								txtPriority.grabFocus();
							}
							else if (Integer.parseInt(txtPriority.getText()) == 1 && 
									 jcbHold.getSelectedItem().toString().equalsIgnoreCase("Yes") == true) {
								Message.messageError("Cannot Hold loan if priority is 1.");
								jcbHold.grabFocus();
							}
							else if (Frequency.equalsIgnoreCase("SEMI") == true && 
									(Double.parseDouble(Amortization1)+Double.parseDouble(Amortization2)) > 0 &&
									(Double.parseDouble(Amortization1)+Double.parseDouble(Amortization2)) != Double.parseDouble(Amortization)) {
								Message.messageError("1st half and 2nd half amortization must be equal to the total amortization.");
								txtAmortization1.grabFocus();
							}
							else{
								tableLoans.setInsert(save_sql);

								tableLoans.setQuery(DEFAULT_QUERY);
								tbl.getColumnModel().getColumn(0).setMaxWidth(0);
								tbl.getColumnModel().getColumn(0).setMinWidth(0);
								tbl.getColumnModel().getColumn(1).setMaxWidth(0);
								tbl.getColumnModel().getColumn(1).setMinWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(350);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(350);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(234);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(234);
								tbl.getColumnModel().getColumn(0).setHeaderValue("Employee Name");
								tbl.getColumnModel().getColumn(1).setHeaderValue("Loan Type");

								curRecord = Employee.trim()+LoanType.trim();
								
								clearFields();

								Message.messageInfo(Message.messageAdd);
								//tbl.scrollRectToVisible(tbl.getCellRect(tbl.getRowCount()-1, tbl.getColumnCount(), true));
								tbl.scrollRectToVisible(tbl.getCellRect(setCurRecord(), tbl.getColumnCount(), true));
								jcbEmployee.grabFocus();
							}
						}
						catch(SQLException se){
							try{
								tableLoans.setQuery(DEFAULT_QUERY);
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
									LockHandler.startLock(DBConnect.SelectForUpdate("loan",
											"*",
											"empid='"+tbl.getValueAt(tbl.getSelectedRow(),2).toString()+"' "+
											"AND loan_type = '"+tbl.getValueAt(tbl.getSelectedRow(),3).toString()+"'"));

									String del_sql = DBConnect.delete("loan","empid = '"+tbl.getValueAt(tbl.getSelectedRow(),2).toString()+"' "+
																	  "AND loan_type = '"+tbl.getValueAt(tbl.getSelectedRow(),3).toString()+"'");
									//System.out.println(del_sql);
									
									txtsearch.setVisible(false);
									mainform.lblMessage.setText("");
									//int setDialog = JOptionPane.showConfirmDialog(container, "Are you sure ?",  "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
									int setDialog = Message.messageYesNo("Are you sure you want to delete\n" + tbl.getValueAt(tbl.getSelectedRow(),0).toString().trim()+ ": "+ tbl.getValueAt(tbl.getSelectedRow(),1).toString().trim()+ " ?");
									if (JOptionPane.YES_OPTION == setDialog) {
										try {
											// unlock mode and delete 
											LockHandler.removeLockAndDelete(del_sql);
											//tableLoans.setInsert(del_sql);
											//																												
											//tableLoans.setQuery(DEFAULT_QUERY);
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
										tableLoans.setQuery(DEFAULT_QUERY);
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
										LockHandler.startLock(DBConnect.SelectForUpdate("loan",
												"*",
												"empid='"+tbl.getValueAt(tbl.getSelectedRow(),2).toString()+"' "+
												"AND loan_type = '"+tbl.getValueAt(tbl.getSelectedRow(),3).toString()+"'"));
										//refresh
										tableLoans.setQuery(DEFAULT_QUERY);
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
										jcbLoanType.setEnabled(false);
										txtPrincipal.grabFocus();

									}
									catch (SQLException e) {
										try {
											//refresh and remove lock mode
											tableLoans.setQuery(DEFAULT_QUERY);
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
							Principal = "0";
							if (txtPrincipal.getText().trim().length() > 0) {
								Principal = txtPrincipal.getText().trim().replaceAll(",","");
							}
							Interest = "0";
							if (txtInterest.getText().trim().length() > 0) {
								Interest = txtInterest.getText().trim().replaceAll(",","");
							}
							Amortization = "0";
							if (txtAmortization.getText().trim().length() > 0) {
								Amortization = txtAmortization.getText().trim().replaceAll(",","");
							}
							Balance = "0";
							if (txtBalance.getText().trim().length() > 0) {
								Balance = txtBalance.getText().trim().replaceAll(",","");
							}
							AmountPaid = "0";
							if (txtAmountPaid.getText().trim().length() > 0) {
								AmountPaid = txtAmountPaid.getText().trim().replaceAll(",","");
							}
							if (txtStartDate.getText().trim().length() > 0) {
								try {
									StartDate = dteFormat.format(strFormat.parse(txtStartDate.getText().trim()));
								} catch (ParseException e1) {
									e1.printStackTrace();
								}
							}
							else {
								StartDate = "0000-00-00";
							}
							if (jcbFrequency.getSelectedIndex() != 0) {
								Frequency = jcbFrequency.getSelectedItem().toString().substring(101);
							}
							else {
								Frequency = "";
							}
							if (jcbHold.getSelectedItem().toString().trim().equalsIgnoreCase("Yes") == true) {
								Hold = "Y";
							}
							else if (jcbHold.getSelectedItem().toString().trim().equalsIgnoreCase("No") == true) {
								Hold = "N";
							}
							else {
								Hold = "";
							}
							Amortization1 = "0";
							if (txtAmortization1.getText().trim().length() > 0) {
								Amortization1 = txtAmortization1.getText().trim().replaceAll(",","");
							}
							Amortization2 = "0";
							if (txtAmortization2.getText().trim().length() > 0) {
								Amortization2 = txtAmortization2.getText().trim().replaceAll(",","");
							}
							
							String e_sql = 
								DBConnect.Update("loan",
										"principal = '"+Principal+"', " +
										"interest = '"+Interest+"', " +
										"amortization = '"+Amortization+"', " +
										"balance = '"+Balance+"', " +
										"amount_paid = '"+AmountPaid+"', " +
										"start_date = '"+StartDate+"', " +
										"freq_code = '"+Frequency+"', " +
										"hold = '"+Hold+"', " +
										"priority = '"+txtPriority.getText().trim()+"', "+
										"amortization1 = '"+Amortization1+"', "+
										"amortization2 = '"+Amortization2+"'",
										"empid='"+tbl.getValueAt(tbl.getSelectedRow(),2).toString()+"' "+
										"AND loan_type = '"+tbl.getValueAt(tbl.getSelectedRow(),3).toString()+"'"
										);
							
							if (Double.parseDouble(Principal) == 0) {
								Message.messageError("Invalid input for Principal field.");
								txtPrincipal.grabFocus();
							}
							else if(Double.parseDouble(Amortization) == 0) {
								Message.messageError("Invalid input for Amortization field.");
								txtAmortization.grabFocus();
							}
							else if (StartDate.equals("0000-00-00") == true) {
								Message.messageError("Invalid input for Starting Date field.");
								cboStartDate.grabFocus();
							}
							else if(jcbFrequency.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Frequency field.");
								jcbFrequency.grabFocus();
							}
							else if(jcbHold.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Hold field.");
								jcbHold.grabFocus();
							}
							else if (txtPriority.getText().trim().length() == 0) {
								Message.messageError("Invalid input for Priority field.");
								txtPriority.grabFocus();
							}
							else if (Integer.parseInt(txtPriority.getText()) == 1 && 
									 jcbHold.getSelectedItem().toString().equalsIgnoreCase("Yes") == true) {
								Message.messageError("Cannot Hold loan if priority is 1.");
								jcbHold.grabFocus();
							}
							else if (Frequency.equalsIgnoreCase("SEMI") == true && 
									(Double.parseDouble(Amortization1)+Double.parseDouble(Amortization2)) > 0 &&
									(Double.parseDouble(Amortization1)+Double.parseDouble(Amortization2)) != Double.parseDouble(Amortization)) {
								Message.messageError("1st half and 2nd half amortization must be equal to the total amortization.");
								txtAmortization1.grabFocus();
							}
							else {
								//unlock mode then update
								LockHandler.removeLockAndUpdate(e_sql);

								tableLoans.setQuery(DEFAULT_QUERY);
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

			setSize(620,610);
			setVisible(true);

		}//try
		catch ( ClassNotFoundException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("LoansMaint", "LoansMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch ( SQLException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("LoansMaint", "LoansMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch (Exception e) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("LoansMaint", "LoansMaint", traceWriter.toString());        
			dispose();
		}
	}//ENDof public LoansMaint() {

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
			tableLoans.setQuery(DEFAULT_QUERY);
			tbl.getColumnModel().getColumn(0).setMaxWidth(0);
			tbl.getColumnModel().getColumn(0).setMinWidth(0);
			tbl.getColumnModel().getColumn(1).setMaxWidth(0);
			tbl.getColumnModel().getColumn(1).setMinWidth(0);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(350);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(350);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(234);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(234);

			//set column header width (to avoid the ... sign)
			tbl.getColumnModel().getColumn(0).setHeaderValue("Employee Name");
			tbl.getColumnModel().getColumn(1).setHeaderValue("Loan Type");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void reload() {

	private void enableFields() {
		jcbEmployee.setEnabled(true);
		jcbLoanType.setEnabled(true);
		txtPrincipal.setEnabled(true);
		txtInterest.setEnabled(true);
		txtAmortization.setEnabled(true);
		txtBalance.setEnabled(true);
		txtAmountPaid.setEnabled(true);
		txtStartDate.setEnabled(true);
		txtStartDate.setFocusable(false);
		cboStartDate.setEnabled(true);
		jcbFrequency.setEnabled(true);
		if (jcbFrequency.getSelectedItem().toString().length() > 10) {
			if (jcbFrequency.getSelectedItem().toString().substring(101).equalsIgnoreCase("SEMI") == true) {
				txtAmortization1.setEnabled(true);
				txtAmortization2.setEnabled(true);
			}
			else {
				txtAmortization1.setEnabled(false);
				txtAmortization2.setEnabled(false);
			}
		}
		jcbHold.setEnabled(true);
		txtPriority.setEnabled(true);
	}//ENDof private void enableFields() {

	private void disableFields() {
		jcbEmployee.setEnabled(false);
		jcbLoanType.setEnabled(false);
		txtPrincipal.setEnabled(false);
		txtInterest.setEnabled(false);
		txtAmortization.setEnabled(false);
		txtBalance.setEnabled(false);
		txtAmountPaid.setEnabled(false);
		txtStartDate.setEnabled(false);
		cboStartDate.setEnabled(false);
		jcbFrequency.setEnabled(false);
		txtAmortization1.setEnabled(false);
		txtAmortization2.setEnabled(false);
		jcbHold.setEnabled(false);
		txtPriority.setEnabled(false);
	}//ENDof private void disableFields() {

	private void clearFields() {
		jcbEmployee.setSelectedItem("Choose");
		jcbLoanType.setSelectedItem("Choose");
		txtPrincipal.setText("");
		txtInterest.setText("");
		txtAmortization.setText("");
		txtBalance.setText("");
		txtAmountPaid.setText("");
		cboStartDate.setSelectedItem(strFormat.format(new Date()));
		txtStartDate.setText("");
		jcbFrequency.setSelectedItem("Choose");
		txtAmortization1.setText("");
		txtAmortization2.setText("");
		txtAmortization1.setEnabled(false);
		txtAmortization2.setEnabled(false);
		jcbHold.setSelectedItem("Choose");
		txtPriority.setText("");
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
			jcbLoanType.setSelectedIndex(0);
			for (int ii=0; ii<=jcbLoanType.getItemCount()-1; ii++) {
				if (jcbLoanType.getItemAt(ii).toString().length() > 50) {
					if (tbl.getValueAt(getrow,3).toString().equalsIgnoreCase(jcbLoanType.getItemAt(ii).toString().substring(101)) == true) {
						jcbLoanType.setSelectedIndex(ii);
					}
				}
			}//ENDof for (int ii=0; ii<=jcbLoanType.getItemCount()-1; ii++) {
			txtPrincipal.setText(tbl.getValueAt(getrow,4).toString());
			txtInterest.setText(tbl.getValueAt(getrow,5).toString());
			txtAmortization.setText(tbl.getValueAt(getrow,6).toString());
			txtBalance.setText(tbl.getValueAt(getrow,7).toString());
			txtAmountPaid.setText(tbl.getValueAt(getrow,8).toString());
			if (tbl.getValueAt(getrow,9).toString().equals("0000-00-00") == false) {
				try {
					cboStartDate.setSelectedItem(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,9).toString())));
					txtStartDate.setText(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,9).toString())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			else {
				cboStartDate.setSelectedItem(strFormat.format(new Date()));
				txtStartDate.setText("");
			}
			jcbFrequency.setSelectedIndex(0);
			for (int ii=0; ii<=jcbFrequency.getItemCount()-1; ii++) {
				if (jcbFrequency.getItemAt(ii).toString().length() > 50) {
					if (tbl.getValueAt(getrow,10).toString().equalsIgnoreCase(jcbFrequency.getItemAt(ii).toString().substring(101)) == true) {
						jcbFrequency.setSelectedIndex(ii);
					}
				}
			}//ENDof for (int ii=0; ii<=jcbFrequency.getItemCount()-1; ii++) {
			if (tbl.getValueAt(getrow,11).toString().equalsIgnoreCase("Y") == true) {
				jcbHold.setSelectedItem("Yes");
			} 
			else if (tbl.getValueAt(getrow,11).toString().equalsIgnoreCase("N") == true) {
				jcbHold.setSelectedItem("No");
			} 
			else {
				jcbHold.setSelectedItem("Choose");
			}			
			txtPriority.setText(tbl.getValueAt(getrow,12).toString());
			txtAmortization1.setText(tbl.getValueAt(getrow,13).toString());
			txtAmortization2.setText(tbl.getValueAt(getrow,14).toString());
		}//ENDof if (tbl.getSelectedRow() >= 0) {
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
	
}//ENDof public class LoansMaint extends JInternalFrame {
