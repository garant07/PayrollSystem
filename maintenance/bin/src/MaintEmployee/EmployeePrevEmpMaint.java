package maintenance.bin.src.MaintEmployee;

/**
 * Author:  Norberto L. Silva
 * Date:    March 12, 2012
 * Company: Applied Ideas, Inc 
 * Program: Employee Previous Employer Maintenance ==-> Payroll System
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
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
import javax.swing.JDesktopPane;
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
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.MyField;
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.ResultTableModel.ResultSetTableModel;
import maintenance.bin.src.core.LockHandler;
import maintenance.bin.src.core.mainform;

public class EmployeePrevEmpMaint extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private JDesktopPane desk = mainform.desktop;

	public static JInternalFrame iFrame = null;

	static String DEFAULT_QUERY = "";

	private ResultSetTableModel tableEmployeePrevEmp;

	Container container;
	Message msg = new Message();
	JTable tbl;

	//variable current cell selected
	private int tempR;
	private int tempC;
	String curRecord = "";

	//variable for buttons
	private JButton jbtnadd;
	private JButton jbtnedit;
	private JButton jbtndelete;

	//variable for user entry
	JScrollPane scrollPane;
	private MyField txtEmplyrName = new MyField(true,50);
	private MyField txtEmpDate = new MyField(true,15);
	private DateComboBox cboEmpDate = new DateComboBox();
	private MyField txtTinNo = new MyField(true,15);
	private MyField txtAddress = new MyField(true,100);
	private JFormattedTextField txtGAmount = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtWHeld_Amount = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtSSS_Amount = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtPhilHealth_Amount = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtHDMF_Amount = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtTaxBonus = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtTaxOthIncome = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtNTaxOthIncome = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtTaxSalary = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtNTaxSalary = new JFormattedTextField(new DecimalFormat("#,##0.00"));

	SimpleDateFormat dteFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat strFormat = new SimpleDateFormat("MMMM dd, yyyy");

	private	JLabel lblEmployee = new JLabel();
	private static String EmpID = "";
	
	public EmployeePrevEmpMaint(String wtax_code, String wtax_desc, int w, int h) {
		//super ("Item Maintenance", resizable, close, maximize, iconifiable);
		super ("Employee Previous Employer Maintenance", false, true, false, false);
		iFrame = this;
	    Dimension rootSize = Toolkit.getDefaultToolkit().getScreenSize();
	    setEmployee(wtax_code,wtax_desc);
		this.setName("EmployeePrevEmpmaint");
		this.setResizable(false);
		this.setLocation(((rootSize.width + 310) - w) / 2,((rootSize.height + 80) - h) / 3);

		this.addInternalFrameListener(new InternalFrameListener(){
			public void internalFrameActivated(InternalFrameEvent arg0) {
				EmployeeMaint.subMain2 = 1;
				EmployeeMaint.jbtnadd.setEnabled(false);
				EmployeeMaint.jbtnedit.setEnabled(false);
				EmployeeMaint.jbtndelete.setEnabled(false);
				EmployeeMaint.jbtngovt.setEnabled(false);
				EmployeeMaint.jbtnprevemp.setEnabled(false);
			}
			public void internalFrameClosed(InternalFrameEvent arg0) {}
			public void internalFrameClosing(InternalFrameEvent arg0) {
				EmployeeMaint.subMain2 = 0;
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
			tableEmployeePrevEmp = new ResultSetTableModel( DEFAULT_QUERY );

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
			
			JLabel lblEmplyrName = new JLabel("Employer Name");
			JLabel lblEmpDate = new JLabel("Date Employed");
			JLabel lblTinNo = new JLabel("TIN No.");
			JLabel lblAddress = new JLabel("Address");
			JLabel lblGamount = new JLabel("Gross Amount");
			JLabel lblWithHeld = new JLabel("WithHeld Amount");
			JLabel lblSSSAmount = new JLabel("SSS");
			JLabel lblPhilHealth = new JLabel("PhilHealth");
			JLabel lblHDMF = new JLabel("HDMF");
			JLabel lblTaxBonus = new JLabel("Taxed 13th/Bonus");
			JLabel lblTaxed = new JLabel("Taxed");
			JLabel lblNTaxed = new JLabel("Non-Taxed");
			JLabel lblOthIncome = new JLabel("Other Income");
			JLabel lblSalary = new JLabel("Salary");

			tbl = new JTable(tableEmployeePrevEmp);


			TableCellRenderer renderer = new TestRenderer();
			tbl.setDefaultRenderer(Object.class, renderer);
			scrollPane = new JScrollPane(tbl);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			tbl.setSelectionMode(0);

			tableEmployeePrevEmp.resultSet = tableEmployeePrevEmp.statement.executeQuery(DEFAULT_QUERY);
			int width0 = 143;
			int width1 = 143;
			int width2 = 143;
			TableColumn col0 = tbl.getColumnModel().getColumn(0);
			TableColumn col1 = tbl.getColumnModel().getColumn(1);
			TableColumn col2 = tbl.getColumnModel().getColumn(2);

			col0.setMaxWidth(width0);
			col1.setMaxWidth(width1);
			col2.setMaxWidth(width2);
			col0.setMinWidth(width0);
			col1.setMinWidth(width1);
			col2.setMinWidth(width2);

			//set column width
			reload();

			tbl.getTableHeader().setResizingAllowed(false);
			tbl.getTableHeader().setMaximumSize(new Dimension(23,56));
			tbl.setPreferredScrollableViewportSize(new Dimension(430,125));

			tbl.getTableHeader().setReorderingAllowed(false); //disabled column dragging

			//set display size
			scrollPane.setPreferredSize(new Dimension(517,170));
			jpan.setBounds(10,20,517,180);

			lblEmployee.setBounds(10,3,400,20);
			lblEmplyrName.setBounds(28,230,100,20);
			lblEmpDate.setBounds(28,260,100,20);
			lblTinNo.setBounds(28,290,100,20);
			lblAddress.setBounds(28,320,100,20);
			lblGamount.setBounds(28,350,100,20);
			lblWithHeld.setBounds(280,350,100,20);
			lblSSSAmount.setBounds(28,380,100,20);
			lblPhilHealth.setBounds(280,380,100,20);
			lblHDMF.setBounds(28,410,100,20);
			lblTaxBonus.setBounds(280,410,100,20);
			lblTaxed.setBounds(150,440,100,20);
			lblNTaxed.setBounds(250,440,100,20);
			lblOthIncome.setBounds(28,470,100,20);
			lblSalary.setBounds(28,500,100,20);

			txtEmplyrName.setBounds(130,230,300,20);
			txtEmplyrName.setDisabledTextColor(new Color(139,113,113));
			txtEmpDate.setBounds(130,260,150,20);
			txtEmpDate.setDisabledTextColor(new Color(139,113,113));
			cboEmpDate.setBounds(new Rectangle(275, 259, 23, 23));
			txtTinNo.setBounds(130,290,120,20);
			txtTinNo.setDisabledTextColor(new Color(139,113,113));
			txtAddress.setBounds(130,320,350,20);
			txtAddress.setDisabledTextColor(new Color(139,113,113));
			txtGAmount.setBounds(130,350,80,20);
			txtGAmount.setDisabledTextColor(new Color(139,113,113));
			txtGAmount.setHorizontalAlignment(JTextField.TRAILING);
			txtWHeld_Amount.setBounds(400,350,80,20);
			txtWHeld_Amount.setDisabledTextColor(new Color(139,113,113));
			txtWHeld_Amount.setHorizontalAlignment(JTextField.TRAILING);
			txtSSS_Amount.setBounds(130,380,80,20);
			txtSSS_Amount.setDisabledTextColor(new Color(139,113,113));
			txtSSS_Amount.setHorizontalAlignment(JTextField.TRAILING);
			txtPhilHealth_Amount.setBounds(400,380,80,20);
			txtPhilHealth_Amount.setDisabledTextColor(new Color(139,113,113));
			txtPhilHealth_Amount.setHorizontalAlignment(JTextField.TRAILING);
			txtHDMF_Amount.setBounds(130,410,80,20);
			txtHDMF_Amount.setDisabledTextColor(new Color(139,113,113));
			txtHDMF_Amount.setHorizontalAlignment(JTextField.TRAILING);
			txtTaxBonus.setBounds(400,410,80,20);
			txtTaxBonus.setDisabledTextColor(new Color(139,113,113));
			txtTaxBonus.setHorizontalAlignment(JTextField.TRAILING);
			txtTaxOthIncome.setBounds(130,470,80,20);
			txtTaxOthIncome.setDisabledTextColor(new Color(139,113,113));
			txtTaxOthIncome.setHorizontalAlignment(JTextField.TRAILING);
			txtNTaxOthIncome.setBounds(240,470,80,20);
			txtNTaxOthIncome.setDisabledTextColor(new Color(139,113,113));
			txtNTaxOthIncome.setHorizontalAlignment(JTextField.TRAILING);
			txtTaxSalary.setBounds(130,500,80,20);
			txtTaxSalary.setDisabledTextColor(new Color(139,113,113));
			txtTaxSalary.setHorizontalAlignment(JTextField.TRAILING);
			txtNTaxSalary.setBounds(240,500,80,20);
			txtNTaxSalary.setDisabledTextColor(new Color(139,113,113));
			txtNTaxSalary.setHorizontalAlignment(JTextField.TRAILING);
			
			jbtnadd.setBounds(80,200,80,22);
			jbtnedit.setBounds(230,200,80,22);
			jbtndelete.setBounds(380,200,80,22);

			jpan.add(scrollPane, BorderLayout.CENTER);

			if (tbl.getRowCount() >= 1){
				tbl.setRowSelectionInterval(0,0);
				tbl.setColumnSelectionInterval(0,0);
				setFieldValue();
			}

			//add objects to container
			container.add(jpan);
			container.add(lblEmployee);
			container.add(lblEmplyrName);
			container.add(txtEmplyrName);
			container.add(lblEmpDate);
			container.add(txtEmpDate);
			container.add(cboEmpDate);
			container.add(lblTinNo);
			container.add(txtTinNo);
			container.add(lblAddress);
			container.add(txtAddress);
			container.add(lblGamount);
			container.add(txtGAmount);
			container.add(lblWithHeld);
			container.add(txtWHeld_Amount);
			container.add(lblSSSAmount);
			container.add(txtSSS_Amount);
			container.add(lblPhilHealth);
			container.add(txtPhilHealth_Amount);
			container.add(lblHDMF);
			container.add(txtHDMF_Amount);
			container.add(txtTaxBonus);
			container.add(lblTaxBonus);
			container.add(lblTaxed);
			container.add(lblNTaxed);
			container.add(lblOthIncome);
			container.add(txtTaxOthIncome);
			container.add(txtNTaxOthIncome);
			container.add(lblSalary);
			container.add(txtTaxSalary);
			container.add(txtNTaxSalary);
			container.add(jbtnadd);
			container.add(jbtnedit);
			container.add(jbtndelete);        

			//Disabled text fields
			disableFields();

			cboEmpDate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (cboEmpDate.getSelectedIndex() != -1) {
						txtEmpDate.setText(cboEmpDate.getSelectedItem().toString());
					}
				}
			});
			
			txtGAmount.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtGAmount.getText().indexOf(".") == -1) {
								if (txtGAmount.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtGAmount.getText().length() > (txtGAmount.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtWHeld_Amount.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtWHeld_Amount.getText().indexOf(".") == -1) {
								if (txtWHeld_Amount.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtWHeld_Amount.getText().length() > (txtWHeld_Amount.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtSSS_Amount.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtSSS_Amount.getText().indexOf(".") == -1) {
								if (txtSSS_Amount.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtSSS_Amount.getText().length() > (txtSSS_Amount.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtPhilHealth_Amount.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtPhilHealth_Amount.getText().indexOf(".") == -1) {
								if (txtPhilHealth_Amount.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtPhilHealth_Amount.getText().length() > (txtPhilHealth_Amount.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtHDMF_Amount.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtHDMF_Amount.getText().indexOf(".") == -1) {
								if (txtHDMF_Amount.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtHDMF_Amount.getText().length() > (txtHDMF_Amount.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtTaxBonus.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtTaxBonus.getText().indexOf(".") == -1) {
								if (txtTaxBonus.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtTaxBonus.getText().length() > (txtTaxBonus.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtTaxOthIncome.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtTaxOthIncome.getText().indexOf(".") == -1) {
								if (txtTaxOthIncome.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtTaxOthIncome.getText().length() > (txtTaxOthIncome.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtNTaxOthIncome.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtNTaxOthIncome.getText().indexOf(".") == -1) {
								if (txtNTaxOthIncome.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtNTaxOthIncome.getText().length() > (txtNTaxOthIncome.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtTaxSalary.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtTaxSalary.getText().indexOf(".") == -1) {
								if (txtTaxSalary.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtTaxSalary.getText().length() > (txtTaxSalary.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtNTaxSalary.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtNTaxSalary.getText().indexOf(".") == -1) {
								if (txtNTaxSalary.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtNTaxSalary.getText().length() > (txtNTaxSalary.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			//add button listener
			jbtnadd.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					String caption = jbtnadd.getText();
					if (caption == "Add") {
						ListSelectionModel selectionModel = tbl.getSelectionModel();
						selectionModel.clearSelection();
						tbl.setEnabled(false);

						enableFields();

						clearFields();
						txtEmplyrName.grabFocus();

						jbtnadd.setText("Save");
						jbtnadd.setMnemonic('S');
						jbtndelete.setText("Cancel");
						jbtndelete.setMnemonic('C');
						jbtnedit.setEnabled(false);
					}
					else if (caption == "Save") {
						String xcode = txtEmplyrName.getText().trim();
						String gamt = "0";
						String wamt = "0";
						String sss = "0";
						String philhealth = "0";
						String hdmf = "0";
						String taxbonus = "0";
						String taxothinc = "0";
						String ntaxothinc = "0";
						String taxsalary = "0";
						String ntaxsalary = "0";
						String EmpDate = "0000-00-00";
						if (txtEmpDate.getText().trim().length() > 0) {
							try {
								EmpDate = dteFormat.format(strFormat.parse(txtEmpDate.getText().trim()));
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
						}
						if (txtGAmount.getText().trim().length() > 0){
							gamt = txtGAmount.getText().trim().replaceAll(",","");
						}
						if (txtWHeld_Amount.getText().trim().length() > 0){
							wamt = txtWHeld_Amount.getText().trim().replaceAll(",","");
						}
						if (txtSSS_Amount.getText().trim().length() > 0){
							sss = txtSSS_Amount.getText().trim().replaceAll(",","");
						}
						if (txtPhilHealth_Amount.getText().trim().length() > 0){
							philhealth = txtPhilHealth_Amount.getText().trim().replaceAll(",","");
						}
						if (txtHDMF_Amount.getText().trim().length() > 0){
							hdmf = txtHDMF_Amount.getText().trim().replaceAll(",","");
						}
						if (txtTaxBonus.getText().trim().length() > 0){
							taxbonus = txtTaxBonus.getText().trim().replaceAll(",","");
						}
						if (txtTaxOthIncome.getText().trim().length() > 0){
							taxothinc = txtTaxOthIncome.getText().trim().replaceAll(",","");
						}
						if (txtNTaxOthIncome.getText().trim().length() > 0){
							ntaxothinc = txtNTaxOthIncome.getText().trim().replaceAll(",","");
						}
						if (txtTaxSalary.getText().trim().length() > 0){
							taxsalary = txtTaxSalary.getText().trim().replaceAll(",","");
						}
						if (txtNTaxSalary.getText().trim().length() > 0){
							ntaxsalary = txtNTaxSalary.getText().trim().replaceAll(",","");
						}

						int iColumn = 0;
						String save_sql = DBConnect.Insert("emphist",
								"empid,employer_name,employment_date,employer_tin,"+
								"addr,gamount_prev_emplyr,wamount_prev_emplyr,sss_amount,"+
								"philhealth_amount,hdmf_amount,taxed_bonus,taxed_other_income,"+
								"non_tax_other_income,taxed_salary,non_tax_salary",
								"'"+DBConnect.clean(EmpID)+"', " +
								"'"+DBConnect.clean(xcode)+"', " +
								"'"+EmpDate+"', " +
								"'"+txtTinNo.getText().trim()+"', " +
								"'"+txtAddress.getText().trim()+"', " +
								"'"+gamt+"', " +
								"'"+wamt+"', " +
								"'"+sss+"', " +
								"'"+philhealth+"', " +
								"'"+hdmf+"', " +
								"'"+taxbonus+"', " +
								"'"+taxothinc+"', " +
								"'"+ntaxothinc+"', " +
								"'"+taxsalary+"', " +
								"'"+ntaxsalary+"' "
						);

						tbl.setEnabled(false);
						try {
							if (txtEmplyrName.getText().trim().length() == 0) {
								Message.messageError("Invalid input for Employer Name field.");
								txtEmplyrName.grabFocus();
							}
							else{
								tableEmployeePrevEmp.setInsert(save_sql);

								tableEmployeePrevEmp.setQuery(DEFAULT_QUERY);
								tbl.getColumnModel().getColumn(iColumn).setMaxWidth(0);
								tbl.getColumnModel().getColumn(iColumn).setMinWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(iColumn).setMaxWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(iColumn).setMinWidth(0);
								tbl.getColumnModel().getColumn(0).setMaxWidth(0);
								tbl.getColumnModel().getColumn(0).setMinWidth(0);
								tbl.getColumnModel().getColumn(1).setMaxWidth(0);
								tbl.getColumnModel().getColumn(1).setMinWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(350);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(350);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(150);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(150);
								tbl.getColumnModel().getColumn(0).setHeaderValue("Employer Name");
								tbl.getColumnModel().getColumn(1).setHeaderValue("Date Employed");

								curRecord = txtEmplyrName.getText().trim();
								
								clearFields();

								Message.messageInfo(Message.messageAdd);
								//tbl.scrollRectToVisible(tbl.getCellRect(tbl.getRowCount()-1, tbl.getColumnCount(), true));
								tbl.scrollRectToVisible(tbl.getCellRect(setCurRecord(), tbl.getColumnCount(), true));
								txtEmplyrName.grabFocus();
							}
						}
						catch (SQLException se) {
							try {
								tableEmployeePrevEmp.setQuery(DEFAULT_QUERY);
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
									String xcode = txtEmplyrName.getText().trim();

									//row level locking
									LockHandler.startLock(DBConnect.SelectForUpdate("emphist",
											"*",
											"empid = '"+EmpID+"' "+
											"AND employer_name = '"+xcode+"'"));

									String del_sql = DBConnect.delete("emphist","empid = '"+EmpID+"' AND employer_name = '"+xcode+"'");
									//System.out.println(del_sql);
									
									mainform.lblMessage.setText("");
									//int setDialog = JOptionPane.showConfirmDialog(container, "Are you sure ?",  "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
									int setDialog = Message.messageYesNo("Are you sure you want to delete\n" + EmpID + ": "+ txtEmplyrName.getText().trim()+ " ?");
									if (JOptionPane.YES_OPTION == setDialog) {
										try {
											// unlock mode and delete 
											LockHandler.removeLockAndDelete(del_sql);
											//tableEmployeePrevEmp.setInsert(del_sql);
											//																												
											//tableEmployeePrevEmp.setQuery(DEFAULT_QUERY);
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
										tableEmployeePrevEmp.setQuery(DEFAULT_QUERY);
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
						if (txtEmplyrName.getText().trim().length() >= 1){
							currentr = tbl.getSelectedRow();
							currentc = tbl.getSelectedColumn();	

							// start indicating lock
							LockHandler.initializeLock(container);

							//wait for lock indicator to invoke
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									try {
										//row level locking
										LockHandler.startLock(DBConnect.SelectForUpdate("emphist",
												"*",
												"empid = '"+EmpID+"' "+
												"AND employer_name = '"+tbl.getValueAt(tbl.getSelectedRow(),0).toString()+"'"));
										//refresh
										tableEmployeePrevEmp.setQuery(DEFAULT_QUERY);
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

										txtEmplyrName.setEnabled(false);
										cboEmpDate.grabFocus();

									}
									catch (SQLException e) {
										try {
											//refresh and remove lock mode
											tableEmployeePrevEmp.setQuery(DEFAULT_QUERY);
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
						String xcode = txtEmplyrName.getText().trim();
						String gamt = "0";
						String wamt = "0";
						String sss = "0";
						String philhealth = "0";
						String hdmf = "0";
						String taxbonus = "0";
						String taxothinc = "0";
						String ntaxothinc = "0";
						String taxsalary = "0";
						String ntaxsalary = "0";
						String EmpDate = "0000-00-00";
						if (txtEmpDate.getText().trim().length() > 0) {
							try {
								EmpDate = dteFormat.format(strFormat.parse(txtEmpDate.getText().trim()));
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
						}
						if (txtGAmount.getText().trim().length() > 0){
							gamt = txtGAmount.getText().trim().replaceAll(",","");
						}
						if (txtWHeld_Amount.getText().trim().length() > 0){
							wamt = txtWHeld_Amount.getText().trim().replaceAll(",","");
						}
						if (txtSSS_Amount.getText().trim().length() > 0){
							sss = txtSSS_Amount.getText().trim().replaceAll(",","");
						}
						if (txtPhilHealth_Amount.getText().trim().length() > 0){
							philhealth = txtPhilHealth_Amount.getText().trim().replaceAll(",","");
						}
						if (txtHDMF_Amount.getText().trim().length() > 0){
							hdmf = txtHDMF_Amount.getText().trim().replaceAll(",","");
						}
						if (txtTaxBonus.getText().trim().length() > 0){
							taxbonus = txtTaxBonus.getText().trim().replaceAll(",","");
						}
						if (txtTaxOthIncome.getText().trim().length() > 0){
							taxothinc = txtTaxOthIncome.getText().trim().replaceAll(",","");
						}
						if (txtNTaxOthIncome.getText().trim().length() > 0){
							ntaxothinc = txtNTaxOthIncome.getText().trim().replaceAll(",","");
						}
						if (txtTaxSalary.getText().trim().length() > 0){
							taxsalary = txtTaxSalary.getText().trim().replaceAll(",","");
						}
						if (txtNTaxSalary.getText().trim().length() > 0){
							ntaxsalary = txtNTaxSalary.getText().trim().replaceAll(",","");
						}

						try {
							String e_sql = 
								DBConnect.Update("emphist",
										"employment_date = '"+EmpDate+"', " +
										"employer_tin = '"+txtTinNo.getText().trim()+"', " +
										"addr = '"+txtAddress.getText().trim()+"', " +
										"gamount_prev_emplyr = '"+gamt+"', " +
										"wamount_prev_emplyr = '"+wamt+"', " +
										"sss_amount = '"+sss+"', " +
										"philhealth_amount = '"+philhealth+"', " +
										"hdmf_amount = '"+hdmf+"', " +
										"taxed_bonus = '"+taxbonus+"', " +
										"taxed_other_income = '"+taxothinc+"', " +
										"non_tax_other_income = '"+ntaxothinc+"', " +
										"taxed_salary = '"+taxsalary+"', " +
										"non_tax_salary = '"+ntaxsalary+"' ",
										"empid = '" +EmpID+"' AND employer_name = '"+xcode+"'");
							//unlock mode then update
							LockHandler.removeLockAndUpdate(e_sql);

							tableEmployeePrevEmp.setQuery(DEFAULT_QUERY);
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
					}
					catch(Exception ef){
						System.out.println("Error sa JTable Key Listener; Pakicheck n lang!!");
					}
				}//public void keyPressed(KeyEvent e){
			}//new KeyAdapter()
			);//tbl.addKeyListener(

			setSize(539,570);
			setVisible(true);

		}//try
		catch ( ClassNotFoundException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("EmployeePrevEmpMaint", "EmployeePrevEmpMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch ( SQLException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("EmployeePrevEmpMaint", "EmployeePrevEmpMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch (Exception e) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("EmployeePrevEmpMaint", "EmployeePrevEmpMaint", traceWriter.toString());        
			dispose();
		}
	}//ENDof public CustomerMaint() {

	private class TestRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

//			if (column == 0) {
//				setHorizontalAlignment( LEFT );
//			}
//			else {
//				setHorizontalAlignment( RIGHT );
//			}
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
			tableEmployeePrevEmp.setQuery(DEFAULT_QUERY);
			tbl.getColumnModel().getColumn(0).setMaxWidth(0);
			tbl.getColumnModel().getColumn(0).setMinWidth(0);
			tbl.getColumnModel().getColumn(1).setMaxWidth(0);
			tbl.getColumnModel().getColumn(1).setMinWidth(0);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(350);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(350);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(150);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(150);

			//set column header width (to avoid the ... sign)
			tbl.getColumnModel().getColumn(0).setHeaderValue("Employer Name");
			tbl.getColumnModel().getColumn(1).setHeaderValue("Date Employed");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void reload() {

	private void enableFields() {
		txtEmplyrName.setEnabled(true);
		txtEmpDate.setEnabled(true);
		cboEmpDate.setEnabled(true);
		txtTinNo.setEnabled(true);
		txtAddress.setEnabled(true);
		txtGAmount.setEnabled(true);
		txtWHeld_Amount.setEnabled(true);
		txtSSS_Amount.setEnabled(true);
		txtPhilHealth_Amount.setEnabled(true);
		txtHDMF_Amount.setEnabled(true);
		txtTaxBonus.setEnabled(true);
		txtTaxOthIncome.setEnabled(true);
		txtNTaxOthIncome.setEnabled(true);
		txtTaxSalary.setEnabled(true);
		txtNTaxSalary.setEnabled(true);
	}//ENDof private void enableFields() {

	private void disableFields() {
		txtEmplyrName.setEnabled(false);
		txtEmpDate.setEnabled(false);
		cboEmpDate.setEnabled(false);
		txtTinNo.setEnabled(false);
		txtAddress.setEnabled(false);
		txtGAmount.setEnabled(false);
		txtWHeld_Amount.setEnabled(false);
		txtSSS_Amount.setEnabled(false);
		txtPhilHealth_Amount.setEnabled(false);
		txtHDMF_Amount.setEnabled(false);
		txtTaxBonus.setEnabled(false);
		txtTaxOthIncome.setEnabled(false);
		txtNTaxOthIncome.setEnabled(false);
		txtTaxSalary.setEnabled(false);
		txtNTaxSalary.setEnabled(false);
	}//ENDof private void disableFields() {

	private void clearFields() {
		txtEmplyrName.setText("");
		cboEmpDate.setSelectedItem(strFormat.format(new Date()));
		txtEmpDate.setText("");
		txtTinNo.setText("");
		txtAddress.setText("");
		txtGAmount.setText("");
		txtWHeld_Amount.setText("");
		txtSSS_Amount.setText("");
		txtPhilHealth_Amount.setText("");
		txtHDMF_Amount.setText("");
		txtTaxBonus.setText("");
		txtTaxOthIncome.setText("");
		txtNTaxOthIncome.setText("");
		txtTaxSalary.setText("");
		txtNTaxSalary.setText("");
	}//ENDof private void clearFields() {

	private void setFieldValue() {
		if (tbl.getSelectedRow() >= 0) {
			int getrow = tbl.getSelectedRow();
			txtEmplyrName.setText(tbl.getValueAt(getrow,0).toString());
			if (tbl.getValueAt(getrow,14).toString().equals("0000-00-00") == false) {
				try {
					cboEmpDate.setSelectedItem(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,14).toString())));
					txtEmpDate.setText(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,14).toString())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			else {
				cboEmpDate.setSelectedItem(strFormat.format(new Date()));
				txtEmpDate.setText("");
			}
			txtTinNo.setText(tbl.getValueAt(getrow,2).toString());
			txtAddress.setText(tbl.getValueAt(getrow,3).toString());
			txtGAmount.setText(tbl.getValueAt(getrow,4).toString());
			txtWHeld_Amount.setText(tbl.getValueAt(getrow,5).toString());
			txtSSS_Amount.setText(tbl.getValueAt(getrow,6).toString());
			txtPhilHealth_Amount.setText(tbl.getValueAt(getrow,7).toString());
			txtHDMF_Amount.setText(tbl.getValueAt(getrow,8).toString());
			txtTaxBonus.setText(tbl.getValueAt(getrow,9).toString());
			txtTaxOthIncome.setText(tbl.getValueAt(getrow,10).toString());
			txtNTaxOthIncome.setText(tbl.getValueAt(getrow,11).toString());
			txtTaxSalary.setText(tbl.getValueAt(getrow,12).toString());
			txtNTaxSalary.setText(tbl.getValueAt(getrow,13).toString());
		}//ENDof if (tbl.getSelectedRow() >= 0) {
	}//ENDof private void getFieldValue() {
	
	private void setEmployee(String empid, String emp_name) {
		lblEmployee.setText(empid+": "+emp_name);
		EmpID = empid;
		DEFAULT_QUERY = DBConnect.Select("emphist ",
							 "employer_name, "+					
							 "DATE_FORMAT(employment_date,'%M %e, %Y') as edate, "+
							 "employer_tin, "+
							 "addr, "+
							 "gamount_prev_emplyr, "+
							 "wamount_prev_emplyr, "+
							 "FORMAT(sss_amount,2) AS sss_amt, "+
							 "FORMAT(philhealth_amount,2) AS philhealth_amt, "+
							 "FORMAT(hdmf_amount,2) AS philhealth_amt, "+
							 "FORMAT(taxed_bonus,2) AS tax_bonus, "+
							 "FORMAT(taxed_other_income,2) AS tax_oth_income, "+
							 "FORMAT(non_tax_other_income,2) AS ntax_oth_income, "+
							 "FORMAT(taxed_salary,2) AS tax_salary, "+
							 "FORMAT(non_tax_salary,2) AS ntax_salary, "+
							 "CONVERT(employment_date,CHAR) as empdate, "+
							 "employment_date ",
							 "empid = '"+empid+"'",
							 "employment_date");
	}//ENDof private void setTaxTable(String empid, String emp_name) {

	private int setCurRecord() {
		int i = 0;
		if (tbl.getRowCount() > 0) {
			if (curRecord.trim().length() > 0) {
				for (i=0; i<=tbl.getRowCount()-1; i++) {
					if (tbl.getValueAt(i,0).toString().equalsIgnoreCase(curRecord) == true) {
						tbl.setRowSelectionInterval(i,i);
						tempR = i;
						break;
					}
				}//ENDof for (int i=0; i<=tbl.getRowCount()-1; i++) {
			}//ENDof if (curRecord.trim().length() > 0) {
		}//ENDof if (tbl.getRowCount() > 0) {
		return i;
	}//ENDof private int setCurRecord() {
	
}//ENDof public class CustomerMaint extends JInternalFrame {
