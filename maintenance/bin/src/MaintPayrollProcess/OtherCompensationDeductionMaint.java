package maintenance.bin.src.MaintPayrollProcess;

/**
 * Author : Norberto L. Silva
 * Date   : March 14, 2012
 * Company: Applied Ideas, Inc 
 * Program: Other Compensation/Deduction Maintenance ==-> Payroll System
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
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
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.ResultTableModel.ResultSetTableModel;
import maintenance.bin.src.core.LockHandler;
import maintenance.bin.src.core.mainform;

public class OtherCompensationDeductionMaint extends JInternalFrame {
	private static final long serialVersionUID = 1L;
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	static final String DEFAULT_QUERY = DBConnect.Select("emppif as e, othercompsdeduct as o, compensation as c, deduction as d ",
										"DISTINCT CONCAT(e.lastname,', ',e.firstname,' ',e.middlename) as empname, "+
										"IF(o.other_type = 'C',c.comps_desc,d.deduct_desc) as othtype, "+
										"FORMAT(o.amount,2) AS amount, "+
										"o.empid, "+
										"o.comps_deduct, "+
										"o.other_type ",
										"e.empid = o.empid AND "+
										"IF(o.other_type = 'C',c.comps_code = o.comps_deduct,d.deduct_code = o.comps_deduct) ",
										"empname, othtype");
	protected static final String Interest = null;

	private ResultSetTableModel tableCompsDeduct;
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
	private JComboBox jcbCompsDeduct = new JComboBox();
	private JFormattedTextField txtAmount = new JFormattedTextField(new DecimalFormat("#,##0.00"));

	private JTextField txtsearch = new JTextField();

	String Employee		= "";
	String CompsDeduct	= "";
	String OtherType	= "";
	String FreqCode		= "";
	String Amount		= "0";

	public OtherCompensationDeductionMaint() {
		super ("Other Compensation and Deduction Maintenance", false, true, false, false);
		this.setName("othcompsdeductmaint");
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
			tableCompsDeduct = new ResultSetTableModel( DEFAULT_QUERY );

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
			JLabel lblCompsDeduct = new JLabel("Comps/Deduct");
			JLabel lblAmount = new JLabel("Amount");

			tbl = new JTable(tableCompsDeduct);

			TableCellRenderer renderer = new TestRenderer();
			tbl.setDefaultRenderer(Object.class, renderer);
			scrollPane = new JScrollPane(tbl);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			tbl.setSelectionMode(0);

			//Employee
			String sql = "SELECT CONCAT(lastname,', ',firstname,' ',middlename) as empname, empid" +
						 "  FROM emppif ORDER By empname";
			tableCompsDeduct.resultSet = tableCompsDeduct.statement.executeQuery(sql);

			jcbEmployee.setFont(new Font("",Font.PLAIN, 12));
			jcbEmployee.setBackground(Color.white);
			jcbEmployee.addItem("Choose");
			while (tableCompsDeduct.resultSet.next()){
				jcbEmployee.addItem(Formatter.formatString(100,' ','-',tableCompsDeduct.resultSet.getString("empname"))+"-"+tableCompsDeduct.resultSet.getString("empid"));
			}//while (tableCompsDeduct.resultSet.next())

			//Compensation
			sql = "SELECT * FROM compensation ORDER By comps_desc";
			tableCompsDeduct.resultSet = tableCompsDeduct.statement.executeQuery(sql);

			jcbCompsDeduct.setFont(new Font("",Font.PLAIN, 12));
			jcbCompsDeduct.setBackground(Color.white);
			jcbCompsDeduct.addItem("Choose");
			while (tableCompsDeduct.resultSet.next()){
				jcbCompsDeduct.addItem(Formatter.formatString(100,' ','-',tableCompsDeduct.resultSet.getString("comps_desc"))+"-"+tableCompsDeduct.resultSet.getString("comps_code")+"=C"+tableCompsDeduct.resultSet.getString("freq_code"));
			}//while (tableCompsDeduct.resultSet.next())

			//Deduction
			sql = "SELECT * FROM deduction ORDER By deduct_desc";
			tableCompsDeduct.resultSet = tableCompsDeduct.statement.executeQuery(sql);

			while (tableCompsDeduct.resultSet.next()){
				jcbCompsDeduct.addItem(Formatter.formatString(100,' ','-',tableCompsDeduct.resultSet.getString("deduct_desc"))+"-"+tableCompsDeduct.resultSet.getString("deduct_code")+"=D"+tableCompsDeduct.resultSet.getString("freq_code"));
			}//while (tableCompsDeduct.resultSet.next())

			tableCompsDeduct.resultSet = tableCompsDeduct.statement.executeQuery(DEFAULT_QUERY);
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
			lblCompsDeduct.setBounds(28,270,100,20);
			lblAmount.setBounds(28,300,100,20);

			jcbEmployee.setBounds(120,240,300,20);
			jcbCompsDeduct.setBounds(120,270,270,20);
			txtAmount.setBounds(120,300,80,20);
			txtAmount.setDisabledTextColor(new Color(139,113,113));
			txtAmount.setHorizontalAlignment(JTextField.TRAILING);

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
			container.add(lblCompsDeduct);
			container.add(jcbCompsDeduct);
			container.add(lblAmount);
			container.add(txtAmount);

			container.add(jbtnadd);
			container.add(jbtnedit);
			container.add(jbtndelete);        
			container.add(txtsearch);

			//Disabled text fields
			disableFields();

			txtAmount.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtAmount.getText().indexOf(".") == -1) {
								if (txtAmount.getText().length() >= 6) {
									e.consume();
								}
							}
							else {
								if (txtAmount.getText().length() > (txtAmount.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
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
						if (jcbCompsDeduct.getSelectedIndex() != 0) {
							int idx = jcbCompsDeduct.getSelectedItem().toString().indexOf("=");
							CompsDeduct = jcbCompsDeduct.getSelectedItem().toString().substring(101,jcbCompsDeduct.getSelectedItem().toString().indexOf("="));
							OtherType   = jcbCompsDeduct.getSelectedItem().toString().substring(idx+1,idx+2);
							FreqCode	= jcbCompsDeduct.getSelectedItem().toString().substring(idx+2);
						}
						else {
							CompsDeduct = "";
							OtherType   = "";
							FreqCode	= "";
						}
						Amount = "0";
						if (txtAmount.getText().trim().length() > 0) {
							Amount = txtAmount.getText().trim().replaceAll(",","");
						}
						String save_sql = DBConnect.Insert("othercompsdeduct",
								"empid,comps_deduct,other_type,amount,freq_code",
								"'"+Employee+"', " +
								"'"+CompsDeduct+"', " +
								"'"+OtherType+"', " +
								"'"+Amount+"'," +
								"'"+FreqCode+"'"
								);

						tbl.setEnabled(false);
						try {
							if (jcbEmployee.getSelectedIndex() == 0) {   
								Message.messageError("Invalid input for Employee field.");
								jcbEmployee.grabFocus();
							}
							else if(jcbCompsDeduct.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Other Comps/Deduct field.");
								jcbCompsDeduct.grabFocus();
							}
							else if(Double.parseDouble(Amount) == 0) {
								Message.messageError("Invalid input for Amount field.");
								txtAmount.grabFocus();
							}
							else{
								tableCompsDeduct.setInsert(save_sql);

								tableCompsDeduct.setQuery(DEFAULT_QUERY);
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

								curRecord = Employee.trim()+CompsDeduct.trim();
								
								clearFields();

								Message.messageInfo(Message.messageAdd);
								//tbl.scrollRectToVisible(tbl.getCellRect(tbl.getRowCount()-1, tbl.getColumnCount(), true));
								tbl.scrollRectToVisible(tbl.getCellRect(setCurRecord(), tbl.getColumnCount(), true));
								jcbEmployee.grabFocus();
							}
						}
						catch(SQLException se){
							try{
								tableCompsDeduct.setQuery(DEFAULT_QUERY);
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
									LockHandler.startLock(DBConnect.SelectForUpdate("othercompsdeduct",
											"*",
											"empid='"+tbl.getValueAt(tbl.getSelectedRow(),3).toString()+"' "+
											"AND comps_deduct = '"+tbl.getValueAt(tbl.getSelectedRow(),4).toString()+"'"));

									String del_sql = DBConnect.delete("othercompsdeduct","empid = '"+tbl.getValueAt(tbl.getSelectedRow(),3).toString()+"' "+
																	  "AND comps_deduct = '"+tbl.getValueAt(tbl.getSelectedRow(),4).toString()+"'");
									//System.out.println(del_sql);
									
									txtsearch.setVisible(false);
									mainform.lblMessage.setText("");
									//int setDialog = JOptionPane.showConfirmDialog(container, "Are you sure ?",  "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
									int setDialog = Message.messageYesNo("Are you sure you want to delete\n" + tbl.getValueAt(tbl.getSelectedRow(),0).toString().trim()+ ": "+ tbl.getValueAt(tbl.getSelectedRow(),1).toString().trim()+ " ?");
									if (JOptionPane.YES_OPTION == setDialog) {
										try {
											// unlock mode and delete 
											LockHandler.removeLockAndDelete(del_sql);
											//tableCompsDeduct.setInsert(del_sql);
											//																												
											//tableCompsDeduct.setQuery(DEFAULT_QUERY);
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
										tableCompsDeduct.setQuery(DEFAULT_QUERY);
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
						if (jcbEmployee.getSelectedItem().toString().length() > 50){
							currentr = tbl.getSelectedRow();
							currentc = tbl.getSelectedColumn();	

							// start indicating lock
							LockHandler.initializeLock(container);

							//wait for lock indicator to invoke
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									try {
										//row level locking
										LockHandler.startLock(DBConnect.SelectForUpdate("othercompsdeduct",
												"*",
												"empid='"+tbl.getValueAt(tbl.getSelectedRow(),3).toString()+"' "+
												"AND comps_deduct = '"+tbl.getValueAt(tbl.getSelectedRow(),4).toString()+"'"));
										//refresh
										tableCompsDeduct.setQuery(DEFAULT_QUERY);
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
										jcbCompsDeduct.setEnabled(false);
										txtAmount.grabFocus();

									}
									catch (SQLException e) {
										try {
											//refresh and remove lock mode
											tableCompsDeduct.setQuery(DEFAULT_QUERY);
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
							Amount = "0";
							if (txtAmount.getText().trim().length() > 0) {
								Amount = txtAmount.getText().trim().replaceAll(",","");
							}
							
							String e_sql = 
								DBConnect.Update("othercompsdeduct",
										"amount = '"+Amount+"' ",
										"empid='"+tbl.getValueAt(tbl.getSelectedRow(),3).toString()+"' "+
										"AND comps_deduct = '"+tbl.getValueAt(tbl.getSelectedRow(),4).toString()+"'"
										);
							
							if (Double.parseDouble(Amount) == 0) {
								Message.messageError("Invalid input for Amount field.");
								txtAmount.grabFocus();
							}
							else {
								//unlock mode then update
								LockHandler.removeLockAndUpdate(e_sql);

								tableCompsDeduct.setQuery(DEFAULT_QUERY);
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

			setSize(620,370);
			setVisible(true);

		}//try
		catch ( ClassNotFoundException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("OtherCompensationDeductionMaint", "OtherCompensationDeductionMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch ( SQLException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("OtherCompensationDeductionMaint", "OtherCompensationDeductionMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch (Exception e) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("OtherCompensationDeductionMaint", "OtherCompensationDeductionMaint", traceWriter.toString());        
			dispose();
		}
	}//ENDof public OtherCompensationDeductionMaint() {

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
			tableCompsDeduct.setQuery(DEFAULT_QUERY);
			tbl.getColumnModel().getColumn(0).setMaxWidth(0);
			tbl.getColumnModel().getColumn(0).setMinWidth(0);
			tbl.getColumnModel().getColumn(1).setMaxWidth(0);
			tbl.getColumnModel().getColumn(1).setMinWidth(0);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(300);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(300);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(284);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(284);

			//set column header width (to avoid the ... sign)
			tbl.getColumnModel().getColumn(0).setHeaderValue("Employee Name");
			tbl.getColumnModel().getColumn(1).setHeaderValue("Compensation / Deduction");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void reload() {

	private void enableFields() {
		jcbEmployee.setEnabled(true);
		jcbCompsDeduct.setEnabled(true);
		txtAmount.setEnabled(true);
	}//ENDof private void enableFields() {

	private void disableFields() {
		jcbEmployee.setEnabled(false);
		jcbCompsDeduct.setEnabled(false);
		txtAmount.setEnabled(false);
	}//ENDof private void disableFields() {

	private void clearFields() {
		jcbEmployee.setSelectedItem("Choose");
		jcbCompsDeduct.setSelectedItem("Choose");
		txtAmount.setText("");
	}//ENDof private void clearFields() {

	private void setFieldValue() {
		if (tbl.getSelectedRow() >= 0) {
			int getrow = tbl.getSelectedRow();
			jcbEmployee.setSelectedIndex(0);
			for (int ii=0; ii<=jcbEmployee.getItemCount()-1; ii++) {
				if (jcbEmployee.getItemAt(ii).toString().length() > 50) {
					if (tbl.getValueAt(getrow,3).toString().equalsIgnoreCase(jcbEmployee.getItemAt(ii).toString().substring(101)) == true) {
						jcbEmployee.setSelectedIndex(ii);
					}
				}
			}//ENDof for (int ii=0; ii<=jcbEmployee.getItemCount()-1; ii++) {
			jcbCompsDeduct.setSelectedIndex(0);
			for (int ii=0; ii<=jcbCompsDeduct.getItemCount()-1; ii++) {
				if (jcbCompsDeduct.getItemAt(ii).toString().length() > 50) {
					if (tbl.getValueAt(getrow,4).toString().equalsIgnoreCase(jcbCompsDeduct.getItemAt(ii).toString().substring(101,jcbCompsDeduct.getItemAt(ii).toString().indexOf("="))) == true) {
						jcbCompsDeduct.setSelectedIndex(ii);
					}
				}
			}//ENDof for (int ii=0; ii<=jcbCompsDeduct.getItemCount()-1; ii++) {
			txtAmount.setText(tbl.getValueAt(getrow,2).toString());
		}//ENDof if (tbl.getSelectedRow() >= 0) {
	}//ENDof private void getFieldValue() {
	
	private int setCurRecord() {
		int i = 0;
		if (tbl.getRowCount() > 0) {
			if (curRecord.trim().length() > 0) {
				for (i=0; i<=tbl.getRowCount()-1; i++) {
					if ((tbl.getValueAt(i,3).toString().trim()+tbl.getValueAt(i,4).toString().trim()).equalsIgnoreCase(curRecord) == true) {
						tbl.setRowSelectionInterval(i,i);
						tempR = i;
						break;
					}
				}//ENDof for (int i=0; i<=tbl.getRowCount()-1; i++) {
			}//ENDof if (curRecord.trim().length() > 0) {
		}//ENDof if (tbl.getRowCount() > 0) {
		return i;
	}//ENDof private int setCurRecord() {
	
}//ENDof public class OtherCompensationDeductionMaint extends JInternalFrame {
