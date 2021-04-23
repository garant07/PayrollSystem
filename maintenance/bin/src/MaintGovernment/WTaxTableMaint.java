package maintenance.bin.src.MaintGovernment;

/**
 * Author:  Norberto L. Silva
 * Date:    February 29, 2012
 * Company: Applied Ideas, Inc 
 * Program: WithHolding Tax Table Maintenance ==-> Payroll System
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
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

import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

import maintenance.bin.src.DBConn.DBConnect;
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.ResultTableModel.ResultSetTableModel;
import maintenance.bin.src.core.LockHandler;
import maintenance.bin.src.core.mainform;

public class WTaxTableMaint extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private JDesktopPane desk = mainform.desktop;

	public static JInternalFrame iFrame = null;

	static String DEFAULT_QUERY = "";

	private ResultSetTableModel tableWTaxTable;

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
	private JComboBox jcbstatus = new JComboBox();
	private JFormattedTextField txtexempt = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtamount1 = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtamount2 = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtamount3 = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtamount4 = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtamount5 = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtamount6 = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtamount7 = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtamount8 = new JFormattedTextField(new DecimalFormat("#,##0.00"));

	private	JLabel lblWTaxTblDesc = new JLabel();

	private static String WTaxTbl_Code = "";
	
	public WTaxTableMaint(String wtax_code, String wtax_desc, int w, int h) {
		//super ("Item Maintenance", resizable, close, maximize, iconifiable);
		super ("WithHolding Tax Table Maintenance", false, true, false, false);
		iFrame = this;
	    Dimension rootSize = Toolkit.getDefaultToolkit().getScreenSize();
		setTaxTable(wtax_code,wtax_desc);
		this.setName("WTaxTablemaint");
		this.setResizable(false);
		//this.setLocation(((rootSize.width + 235) - w) / 2,(rootSize.height - h) / 3);
		this.setLocation(((rootSize.width + 5) - w) / 2,((rootSize.height - 150) - h) / 3);

		this.addInternalFrameListener(new InternalFrameListener(){
			public void internalFrameActivated(InternalFrameEvent arg0) {
				WTaxTypeMaint.subMain = 1;
			}
			public void internalFrameClosed(InternalFrameEvent arg0) {}
			public void internalFrameClosing(InternalFrameEvent arg0) {
				WTaxTypeMaint.subMain = 0;
			}
			public void internalFrameDeactivated(InternalFrameEvent arg0) {}
			public void internalFrameDeiconified(InternalFrameEvent arg0) {}
			public void internalFrameIconified(InternalFrameEvent arg0) {}
			public void internalFrameOpened(InternalFrameEvent arg0) {}
		});

		desk.setLayout(null);
		desk.add(this, new Integer(1));
		
		try {
			tableWTaxTable = new ResultSetTableModel( DEFAULT_QUERY );

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
			
			JLabel lblstatus = new JLabel("Status");
			JLabel lblexempt = new JLabel("Exempt");
			JLabel lblamountx1 = new JLabel("Amount");
			JLabel lblamount1 = new JLabel("1");
			JLabel lblamount2 = new JLabel("2");
			JLabel lblamount3 = new JLabel("3");
			JLabel lblamount4 = new JLabel("4");
			JLabel lblamountx2 = new JLabel("Amount");
			JLabel lblamount5 = new JLabel("5");
			JLabel lblamount6 = new JLabel("6");
			JLabel lblamount7 = new JLabel("7");
			JLabel lblamount8 = new JLabel("8");

			tbl = new JTable(tableWTaxTable);


			TableCellRenderer renderer = new TestRenderer();
			tbl.setDefaultRenderer(Object.class, renderer);
			scrollPane = new JScrollPane(tbl);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			tbl.setSelectionMode(0);

			String sql = "SELECT * FROM wtaxstatus";
			tableWTaxTable.resultSet = tableWTaxTable.statement.executeQuery(sql);

			jcbstatus.addItem("Choose");
			jcbstatus.addItem("Base");
			jcbstatus.addItem("Rate");
			while (tableWTaxTable.resultSet.next()){
				jcbstatus.addItem(tableWTaxTable.resultSet.getString("wtxstat_code"));
			}//while (tableWTaxTable.resultSet.next())

			tableWTaxTable.resultSet = tableWTaxTable.statement.executeQuery(DEFAULT_QUERY);
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
			scrollPane.setPreferredSize(new Dimension(672,170));
			jpan.setBounds(10,20,672,180);

			lblWTaxTblDesc.setBounds(10,3,400,20);
			lblstatus.setBounds(28,100,100,280);
			lblexempt.setBounds(28,130,100,280);
			lblamountx1.setBounds(28,180,100,280);
			lblamount1.setBounds(155,160,100,280);
			lblamount2.setBounds(265,160,100,280);
			lblamount3.setBounds(375,160,100,280);
			lblamount4.setBounds(485,160,100,280);
			lblamountx2.setBounds(28,230,100,280);
			lblamount5.setBounds(155,210,100,280);
			lblamount6.setBounds(265,210,100,280);
			lblamount7.setBounds(375,210,100,280);
			lblamount8.setBounds(485,210,100,280);

			jcbstatus.setBounds(120,230,80,20);
			txtexempt.setBounds(120,260,80,20);
			txtexempt.setDisabledTextColor(new Color(139,113,113));
			txtexempt.setHorizontalAlignment(JTextField.TRAILING);
			txtamount1.setBounds(120,310,80,20);
			txtamount1.setDisabledTextColor(new Color(139,113,113));
			txtamount1.setHorizontalAlignment(JTextField.TRAILING);
			txtamount2.setBounds(230,310,80,20);
			txtamount2.setDisabledTextColor(new Color(139,113,113));
			txtamount2.setHorizontalAlignment(JTextField.TRAILING);
			txtamount3.setBounds(340,310,80,20);
			txtamount3.setDisabledTextColor(new Color(139,113,113));
			txtamount3.setHorizontalAlignment(JTextField.TRAILING);
			txtamount4.setBounds(450,310,80,20);
			txtamount4.setDisabledTextColor(new Color(139,113,113));
			txtamount4.setHorizontalAlignment(JTextField.TRAILING);
			txtamount5.setBounds(120,360,80,20);
			txtamount5.setDisabledTextColor(new Color(139,113,113));
			txtamount5.setHorizontalAlignment(JTextField.TRAILING);
			txtamount6.setBounds(230,360,80,20);
			txtamount6.setDisabledTextColor(new Color(139,113,113));
			txtamount6.setHorizontalAlignment(JTextField.TRAILING);
			txtamount7.setBounds(340,360,80,20);
			txtamount7.setDisabledTextColor(new Color(139,113,113));
			txtamount7.setHorizontalAlignment(JTextField.TRAILING);
			txtamount8.setBounds(450,360,80,20);
			txtamount8.setDisabledTextColor(new Color(139,113,113));
			txtamount8.setHorizontalAlignment(JTextField.TRAILING);
			
			jbtnadd.setBounds(160,200,80,22);
			jbtnedit.setBounds(310,200,80,22);
			jbtndelete.setBounds(460,200,80,22);

			jpan.add(scrollPane, BorderLayout.CENTER);

			if (tbl.getRowCount() >= 1){
				tbl.setRowSelectionInterval(0,0);
				tbl.setColumnSelectionInterval(0,0);
				setFieldValue();
			}

			//add objects to container
			container.add(jpan);
			container.add(lblWTaxTblDesc);
			container.add(lblstatus);
			container.add(jcbstatus);
			container.add(lblexempt);
			container.add(txtexempt);
			container.add(lblamountx1);
			container.add(lblamount1);
			container.add(txtamount1);
			container.add(lblamount2);
			container.add(txtamount2);
			container.add(lblamount3);
			container.add(txtamount3);
			container.add(lblamount4);
			container.add(txtamount4);
			container.add(lblamountx2);
			container.add(lblamount5);
			container.add(txtamount5);
			container.add(lblamount6);
			container.add(txtamount6);
			container.add(lblamount7);
			container.add(txtamount7);
			container.add(lblamount8);
			container.add(txtamount8);
			container.add(jbtnadd);
			container.add(jbtnedit);
			container.add(jbtndelete);        

			//Disabled text fields
			disableFields();

			txtexempt.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) e.consume();
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtamount1.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) e.consume();
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtamount2.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) e.consume();
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtamount3.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) e.consume();
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtamount4.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) e.consume();
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtamount5.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) e.consume();
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtamount6.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) e.consume();
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtamount7.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) e.consume();
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtamount8.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) e.consume();
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			//add button listener
			jbtnadd.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					String caption = jbtnadd.getText();
					String xcode = jcbstatus.getSelectedItem().toString();
					String xamt = "0";
					String amt1 = "0";
					String amt2 = "0";
					String amt3 = "0";
					String amt4 = "0";
					String amt5 = "0";
					String amt6 = "0";
					String amt7 = "0";
					String amt8 = "0";
					if (txtexempt.getText().trim().length() > 0){
						xamt = txtexempt.getText().trim().replaceAll(",","");
					}
					if (txtamount1.getText().trim().length() > 0){
						amt1 = txtamount1.getText().trim().replaceAll(",","");
					}
					if (txtamount2.getText().trim().length() > 0){
						amt2 = txtamount2.getText().trim().replaceAll(",","");
					}
					if (txtamount3.getText().trim().length() > 0){
						amt3 = txtamount3.getText().trim().replaceAll(",","");
					}
					if (txtamount4.getText().trim().length() > 0){
						amt4 = txtamount4.getText().trim().replaceAll(",","");
					}
					if (txtamount5.getText().trim().length() > 0){
						amt5 = txtamount5.getText().trim().replaceAll(",","");
					}
					if (txtamount6.getText().trim().length() > 0){
						amt6 = txtamount6.getText().trim().replaceAll(",","");
					}
					if (txtamount7.getText().trim().length() > 0){
						amt7 = txtamount7.getText().trim().replaceAll(",","");
					}
					if (txtamount8.getText().trim().length() > 0){
						amt8 = txtamount8.getText().trim().replaceAll(",","");
					}

					if (caption == "Add") {
						ListSelectionModel selectionModel = tbl.getSelectionModel();
						selectionModel.clearSelection();
						tbl.setEnabled(false);

						enableFields();

						clearFields();
						jcbstatus.grabFocus();

						jbtnadd.setText("Save");
						jbtnadd.setMnemonic('S');
						jbtndelete.setText("Cancel");
						jbtndelete.setMnemonic('C');
						jbtnedit.setEnabled(false);
					}
					else if (caption == "Save") {
						int iColumn = 0;
						String save_sql = DBConnect.Insert("wtax",
								"wtax_code,wtax_seqno,wtax_status,wtax_exemptamt,"+
								"wtax_basesalary1,wtax_basesalary2,wtax_basesalary3,wtax_basesalary4,"+
								"wtax_basesalary5,wtax_basesalary6,wtax_basesalary7,wtax_basesalary8",
								"'"+DBConnect.clean(WTaxTbl_Code)+"', " +
								"'"+tbl.getRowCount()+"', " +
								"'"+DBConnect.clean(xcode)+"', " +
								"'"+xamt+"', " +
								"'"+amt1+"', " +
								"'"+amt2+"', " +
								"'"+amt3+"', " +
								"'"+amt4+"', " +
								"'"+amt5+"', " +
								"'"+amt6+"', " +
								"'"+amt7+"', " +
								"'"+amt8+"' "
						);

						tbl.setEnabled(false);
						try {
							if (jcbstatus.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Status field.");
								jcbstatus.grabFocus();
							}
							else if (tbl.getRowCount() == 0 && jcbstatus.getSelectedItem().toString().equalsIgnoreCase("Base") == false) {
								Message.messageError("Invalid input for Status field.\nFirst entry on the table should be 'Base'.");
								jcbstatus.grabFocus();
							}
							else if (tbl.getRowCount() == 1 && jcbstatus.getSelectedItem().toString().equalsIgnoreCase("Rate") == false) {
								Message.messageError("Invalid input for Status field.\nSecond entry on the table should be 'Rate'.");
								jcbstatus.grabFocus();
							}
							else{
								tableWTaxTable.setInsert(save_sql);

								tableWTaxTable.setQuery(DEFAULT_QUERY);
								tbl.getColumnModel().getColumn(iColumn).setMaxWidth(0);
								tbl.getColumnModel().getColumn(iColumn).setMinWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(iColumn).setMaxWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(iColumn).setMinWidth(0);
								tbl.getColumnModel().getColumn(0).setMaxWidth(0);
								tbl.getColumnModel().getColumn(0).setMinWidth(0);
								tbl.getColumnModel().getColumn(1).setMaxWidth(0);
								tbl.getColumnModel().getColumn(1).setMinWidth(0);
								tbl.getColumnModel().getColumn(2).setMaxWidth(0);
								tbl.getColumnModel().getColumn(2).setMinWidth(0);
								tbl.getColumnModel().getColumn(3).setMaxWidth(0);
								tbl.getColumnModel().getColumn(3).setMinWidth(0);
								tbl.getColumnModel().getColumn(4).setMaxWidth(0);
								tbl.getColumnModel().getColumn(4).setMinWidth(0);
								tbl.getColumnModel().getColumn(5).setMaxWidth(0);
								tbl.getColumnModel().getColumn(5).setMinWidth(0);
								tbl.getColumnModel().getColumn(6).setMaxWidth(0);
								tbl.getColumnModel().getColumn(6).setMinWidth(0);
								tbl.getColumnModel().getColumn(7).setMaxWidth(0);
								tbl.getColumnModel().getColumn(7).setMinWidth(0);
								tbl.getColumnModel().getColumn(8).setMaxWidth(0);
								tbl.getColumnModel().getColumn(8).setMinWidth(0);
								tbl.getColumnModel().getColumn(9).setMaxWidth(0);
								tbl.getColumnModel().getColumn(9).setMinWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(60);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(60);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(75);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(75);
								tbl.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(65);
								tbl.getTableHeader().getColumnModel().getColumn(2).setMinWidth(65);
								tbl.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(65);
								tbl.getTableHeader().getColumnModel().getColumn(3).setMinWidth(65);
								tbl.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(65);
								tbl.getTableHeader().getColumnModel().getColumn(4).setMinWidth(65);
								tbl.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(65);
								tbl.getTableHeader().getColumnModel().getColumn(5).setMinWidth(65);
								tbl.getTableHeader().getColumnModel().getColumn(6).setMaxWidth(65);
								tbl.getTableHeader().getColumnModel().getColumn(6).setMinWidth(65);
								tbl.getTableHeader().getColumnModel().getColumn(7).setMaxWidth(65);
								tbl.getTableHeader().getColumnModel().getColumn(7).setMinWidth(65);
								tbl.getTableHeader().getColumnModel().getColumn(8).setMaxWidth(65);
								tbl.getTableHeader().getColumnModel().getColumn(8).setMinWidth(65);
								tbl.getTableHeader().getColumnModel().getColumn(9).setMaxWidth(65);
								tbl.getTableHeader().getColumnModel().getColumn(9).setMinWidth(65);
								tbl.getColumnModel().getColumn(0).setHeaderValue("Status");
								tbl.getColumnModel().getColumn(1).setHeaderValue("Exempt");
								tbl.getColumnModel().getColumn(2).setHeaderValue("Amount 1");
								tbl.getColumnModel().getColumn(3).setHeaderValue("Amount 2");
								tbl.getColumnModel().getColumn(4).setHeaderValue("Amount 3");
								tbl.getColumnModel().getColumn(5).setHeaderValue("Amount 4");
								tbl.getColumnModel().getColumn(6).setHeaderValue("Amount 5");
								tbl.getColumnModel().getColumn(7).setHeaderValue("Amount 6");
								tbl.getColumnModel().getColumn(8).setHeaderValue("Amount 7");
								tbl.getColumnModel().getColumn(9).setHeaderValue("Amount 8");

								curRecord = jcbstatus.getSelectedItem().toString();
								
								clearFields();

								Message.messageInfo(Message.messageAdd);
								//tbl.scrollRectToVisible(tbl.getCellRect(tbl.getRowCount()-1, tbl.getColumnCount(), true));
								tbl.scrollRectToVisible(tbl.getCellRect(setCurRecord(), tbl.getColumnCount(), true));
								jcbstatus.grabFocus();
							}
						}
						catch (SQLException se) {
							try {
								tableWTaxTable.setQuery(DEFAULT_QUERY);
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
									String xcode = jcbstatus.getSelectedItem().toString();

									//row level locking
									LockHandler.startLock(DBConnect.SelectForUpdate("wtax",
											"*",
											"wtax_code = '"+WTaxTbl_Code+"' "+
											"AND wtax_status = '"+tbl.getValueAt(tbl.getSelectedRow(),0).toString()+"'"));

									String del_sql = DBConnect.delete("wtax","wtax_code = '"+WTaxTbl_Code+"' AND wtax_status = '"+DBConnect.clean(xcode)+"'");
									//System.out.println(del_sql);
									
									mainform.lblMessage.setText("");
									//int setDialog = JOptionPane.showConfirmDialog(container, "Are you sure ?",  "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
									int setDialog = Message.messageYesNo("Are you sure you want to delete\n" + WTaxTbl_Code + ": "+ jcbstatus.getSelectedItem().toString()+ " ?");
									if (JOptionPane.YES_OPTION == setDialog) {
										try {
											// unlock mode and delete 
											LockHandler.removeLockAndDelete(del_sql);
											//tableWTaxTable.setInsert(del_sql);
											//																												
											//tableWTaxTable.setQuery(DEFAULT_QUERY);
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
										tableWTaxTable.setQuery(DEFAULT_QUERY);
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
					String xcode = jcbstatus.getSelectedItem().toString();
					String xamt = "0";
					String amt1 = "0";
					String amt2 = "0";
					String amt3 = "0";
					String amt4 = "0";
					String amt5 = "0";
					String amt6 = "0";
					String amt7 = "0";
					String amt8 = "0";
					if (txtexempt.getText().trim().length() > 0){
						xamt = txtexempt.getText().trim().replaceAll(",","");
					}
					if (txtamount1.getText().trim().length() > 0){
						amt1 = txtamount1.getText().trim().replaceAll(",","");
					}
					if (txtamount2.getText().trim().length() > 0){
						amt2 = txtamount2.getText().trim().replaceAll(",","");
					}
					if (txtamount3.getText().trim().length() > 0){
						amt3 = txtamount3.getText().trim().replaceAll(",","");
					}
					if (txtamount4.getText().trim().length() > 0){
						amt4 = txtamount4.getText().trim().replaceAll(",","");
					}
					if (txtamount5.getText().trim().length() > 0){
						amt5 = txtamount5.getText().trim().replaceAll(",","");
					}
					if (txtamount6.getText().trim().length() > 0){
						amt6 = txtamount6.getText().trim().replaceAll(",","");
					}
					if (txtamount7.getText().trim().length() > 0){
						amt7 = txtamount7.getText().trim().replaceAll(",","");
					}
					if (txtamount8.getText().trim().length() > 0){
						amt8 = txtamount8.getText().trim().replaceAll(",","");
					}
					int currentr = tbl.getSelectedRow();
					int currentc = tbl.getSelectedColumn();	
					if (ecaption == "Edit"){
						if (jcbstatus.getSelectedItem().toString().length() >= 1){
							currentr = tbl.getSelectedRow();
							currentc = tbl.getSelectedColumn();	

							// start indicating lock
							LockHandler.initializeLock(container);

							//wait for lock indicator to invoke
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									try {
										//row level locking
										LockHandler.startLock(DBConnect.SelectForUpdate("wtax",
												"*",
												"wtax_code = '"+WTaxTbl_Code+"' "+
												"AND wtax_status = '"+tbl.getValueAt(tbl.getSelectedRow(),0).toString()+"'"));
										//refresh
										tableWTaxTable.setQuery(DEFAULT_QUERY);
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

										jcbstatus.setEnabled(false);
										txtexempt.grabFocus();

									}
									catch (SQLException e) {
										try {
											//refresh and remove lock mode
											tableWTaxTable.setQuery(DEFAULT_QUERY);
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
							String e_sql = 
								DBConnect.Update("wtax",
										"wtax_exemptamt = '"+xamt+"', " +
										"wtax_basesalary1 = '"+amt1+"', " +
										"wtax_basesalary2 = '"+amt2+"', " +
										"wtax_basesalary3 = '"+amt3+"', " +
										"wtax_basesalary4 = '"+amt4+"', " +
										"wtax_basesalary5 = '"+amt5+"', " +
										"wtax_basesalary6 = '"+amt6+"', " +
										"wtax_basesalary7 = '"+amt7+"', " +
										"wtax_basesalary8 = '"+amt8+"' ",
										"wtax_code = '" +WTaxTbl_Code+"' AND wtax_status = '"+xcode+"'");
							//unlock mode then update
							LockHandler.removeLockAndUpdate(e_sql);

							tableWTaxTable.setQuery(DEFAULT_QUERY);
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

			setSize(694,430);
			setVisible(true);

		}//try
		catch ( ClassNotFoundException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("WTaxTableMaint", "WTaxTableMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch ( SQLException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("WTaxTableMaint", "WTaxTableMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch (Exception e) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("WTaxTableMaint", "WTaxTableMaint", traceWriter.toString());        
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

			if (column == 0) {
				setHorizontalAlignment( LEFT );
			}
			else {
				setHorizontalAlignment( RIGHT );
			}
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
			tableWTaxTable.setQuery(DEFAULT_QUERY);
			tbl.getColumnModel().getColumn(0).setMaxWidth(0);
			tbl.getColumnModel().getColumn(0).setMinWidth(0);
			tbl.getColumnModel().getColumn(1).setMaxWidth(0);
			tbl.getColumnModel().getColumn(1).setMinWidth(0);
			tbl.getColumnModel().getColumn(2).setMaxWidth(0);
			tbl.getColumnModel().getColumn(2).setMinWidth(0);
			tbl.getColumnModel().getColumn(3).setMaxWidth(0);
			tbl.getColumnModel().getColumn(3).setMinWidth(0);
			tbl.getColumnModel().getColumn(4).setMaxWidth(0);
			tbl.getColumnModel().getColumn(4).setMinWidth(0);
			tbl.getColumnModel().getColumn(5).setMaxWidth(0);
			tbl.getColumnModel().getColumn(5).setMinWidth(0);
			tbl.getColumnModel().getColumn(6).setMaxWidth(0);
			tbl.getColumnModel().getColumn(6).setMinWidth(0);
			tbl.getColumnModel().getColumn(7).setMaxWidth(0);
			tbl.getColumnModel().getColumn(7).setMinWidth(0);
			tbl.getColumnModel().getColumn(8).setMaxWidth(0);
			tbl.getColumnModel().getColumn(8).setMinWidth(0);
			tbl.getColumnModel().getColumn(9).setMaxWidth(0);
			tbl.getColumnModel().getColumn(9).setMinWidth(0);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(60);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(60);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(75);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(75);
			tbl.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(65);
			tbl.getTableHeader().getColumnModel().getColumn(2).setMinWidth(65);
			tbl.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(65);
			tbl.getTableHeader().getColumnModel().getColumn(3).setMinWidth(65);
			tbl.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(65);
			tbl.getTableHeader().getColumnModel().getColumn(4).setMinWidth(65);
			tbl.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(65);
			tbl.getTableHeader().getColumnModel().getColumn(5).setMinWidth(65);
			tbl.getTableHeader().getColumnModel().getColumn(6).setMaxWidth(65);
			tbl.getTableHeader().getColumnModel().getColumn(6).setMinWidth(65);
			tbl.getTableHeader().getColumnModel().getColumn(7).setMaxWidth(65);
			tbl.getTableHeader().getColumnModel().getColumn(7).setMinWidth(65);
			tbl.getTableHeader().getColumnModel().getColumn(8).setMaxWidth(65);
			tbl.getTableHeader().getColumnModel().getColumn(8).setMinWidth(65);
			tbl.getTableHeader().getColumnModel().getColumn(9).setMaxWidth(65);
			tbl.getTableHeader().getColumnModel().getColumn(9).setMinWidth(65);

			//set column header width (to avoid the ... sign)
			tbl.getColumnModel().getColumn(0).setHeaderValue("Status");
			tbl.getColumnModel().getColumn(1).setHeaderValue("Exempt");
			tbl.getColumnModel().getColumn(2).setHeaderValue("Amount 1");
			tbl.getColumnModel().getColumn(3).setHeaderValue("Amount 2");
			tbl.getColumnModel().getColumn(4).setHeaderValue("Amount 3");
			tbl.getColumnModel().getColumn(5).setHeaderValue("Amount 4");
			tbl.getColumnModel().getColumn(6).setHeaderValue("Amount 5");
			tbl.getColumnModel().getColumn(7).setHeaderValue("Amount 6");
			tbl.getColumnModel().getColumn(8).setHeaderValue("Amount 7");
			tbl.getColumnModel().getColumn(9).setHeaderValue("Amount 8");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void reload() {

	private void enableFields() {
		jcbstatus.setEnabled(true);
		txtexempt.setEnabled(true);
		txtamount1.setEnabled(true);
		txtamount2.setEnabled(true);
		txtamount3.setEnabled(true);
		txtamount4.setEnabled(true);
		txtamount5.setEnabled(true);
		txtamount6.setEnabled(true);
		txtamount7.setEnabled(true);
		txtamount8.setEnabled(true);
	}//ENDof private void enableFields() {

	private void disableFields() {
		jcbstatus.setEnabled(false);
		txtexempt.setEnabled(false);
		txtamount1.setEnabled(false);
		txtamount2.setEnabled(false);
		txtamount3.setEnabled(false);
		txtamount4.setEnabled(false);
		txtamount5.setEnabled(false);
		txtamount6.setEnabled(false);
		txtamount7.setEnabled(false);
		txtamount8.setEnabled(false);
	}//ENDof private void disableFields() {

	private void clearFields() {
		jcbstatus.setSelectedItem("Choose");
		txtexempt.setText("");
		txtamount1.setText("");
		txtamount2.setText("");
		txtamount3.setText("");
		txtamount4.setText("");
		txtamount5.setText("");
		txtamount6.setText("");
		txtamount7.setText("");
		txtamount8.setText("");
	}//ENDof private void clearFields() {

	private void setFieldValue() {
		if (tbl.getSelectedRow() >= 0) {
			int getrow = tbl.getSelectedRow();
			jcbstatus.setSelectedItem(tbl.getValueAt(getrow,0).toString());
			txtexempt.setText(tbl.getValueAt(getrow,1).toString());
			txtamount1.setText(tbl.getValueAt(getrow,2).toString());
			txtamount2.setText(tbl.getValueAt(getrow,3).toString());
			txtamount3.setText(tbl.getValueAt(getrow,4).toString());
			txtamount4.setText(tbl.getValueAt(getrow,5).toString());
			txtamount5.setText(tbl.getValueAt(getrow,6).toString());
			txtamount6.setText(tbl.getValueAt(getrow,7).toString());
			txtamount7.setText(tbl.getValueAt(getrow,8).toString());
			txtamount8.setText(tbl.getValueAt(getrow,9).toString());
		}//ENDof if (tbl.getSelectedRow() >= 0) {
	}//ENDof private void getFieldValue() {
	
	private void setTaxTable(String wtax_code, String wtax_desc) {
		lblWTaxTblDesc.setText(wtax_code+": "+wtax_desc);
		WTaxTbl_Code = wtax_code;
		DEFAULT_QUERY = 
			DBConnect.Select("wtax ",
							 "wtax_status, "+					
							 "FORMAT(wtax_exemptamt,2) AS exemptamt, "+
							 "FORMAT(wtax_basesalary1,2) AS basesalary1, "+
							 "FORMAT(wtax_basesalary2,2) AS basesalary2, "+
							 "FORMAT(wtax_basesalary3,2) AS basesalary3, "+
							 "FORMAT(wtax_basesalary4,2) AS basesalary4, "+
							 "FORMAT(wtax_basesalary5,2) AS basesalary5, "+
							 "FORMAT(wtax_basesalary6,2) AS basesalary6, "+
							 "FORMAT(wtax_basesalary7,2) AS basesalary7, "+
							 "FORMAT(wtax_basesalary8,2) AS basesalary8, "+
							 "wtax_code, "+
							 "wtax_seqno ",
							 "wtax_code = '"+wtax_code+"'",
							 "wtax_seqno ASC");
	}//ENDof private void setTaxTable(String wtax_code, String wtax_desc) {

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
