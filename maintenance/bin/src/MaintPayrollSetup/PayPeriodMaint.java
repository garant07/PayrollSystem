package maintenance.bin.src.MaintPayrollSetup;

/**
 * Author:  Norberto L. Silva
 * Date:    March 7, 2012
 * Company: Applied Ideas, Inc 
 * Program: Pay Period Maintenance ==-> Payroll System
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


public class PayPeriodMaint extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	static final String DEFAULT_QUERY = DBConnect.Select("payperiod",
														 "payp_code,payp_desc," +
														 "CONVERT(payp_from,CHAR) as payfrom," +
														 "CONVERT(month_covered,CHAR) as moncovered," +
														 "FORMAT(man_hours,2) AS mhours," +
														 "CONVERT(cutoff_from,CHAR) as coff_from," +
														 "CONVERT(cutoff_to,CHAR) as coff_to," +
														 "payroll_process_type",
														 null,
														 "payp_code ASC");

	private ResultSetTableModel tablePayPeriod;
	Container container;
	JTable tbl;

	//variable current cell selected
	private int tempR;
	private int tempC;
	String curRecord = "";

	//variable for buttons
	private JButton jbtnadd;
	private JButton jbtnedit;
	private JButton jbtndelete;

	String payproctype = "";

	//varible for data entry
	private MyField txtcode = ((MyField)new MyField(true,8));
	private MyField txtdescription = new MyField(true,30);
	private DateComboBox cboPayPeriodFrom = new DateComboBox();
	private MyField txtPayPeriodFrom = new MyField(true,15);	
	private DateComboBox cboMonthCovered = new DateComboBox();
	private MyField txtMonthCovered = new MyField(true,15);	
	private JFormattedTextField txtManHours = new JFormattedTextField(new DecimalFormat("##0.00"));
	private DateComboBox cboCutOffFrom = new DateComboBox();
	private MyField txtCutOffFrom = new MyField(true,15);	
	private DateComboBox cboCutOffTo = new DateComboBox();
	private MyField txtCutOffTo = new MyField(true,15);	
	private JComboBox jcbpayproctype = new JComboBox();
	
	SimpleDateFormat dteFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat strFormat = new SimpleDateFormat("MMMM dd, yyyy");

	public PayPeriodMaint() {
		super ("Payroll Period Maintenance", false, true, false, false);
		this.setName("payperiod");
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
			tablePayPeriod  = new ResultSetTableModel(  DEFAULT_QUERY );

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
			
			JLabel lblcode = new JLabel("Code");
			JLabel lbldescription = new JLabel("Description");
			JLabel lblpayperiodfrom = new JLabel("Pay Period From");
			JLabel lblmonthcovered = new JLabel("Covering Month");
			JLabel lblmanhours = new JLabel("Man Hours");
			JLabel lblcutoffdatefrom = new JLabel("Cut-Off Date From");
			JLabel lblcutoffdateto = new JLabel("Cut-Off Date To");
			JLabel lblprocesstype = new JLabel("Type of Payroll");

			tbl = new JTable(tablePayPeriod);
			final JScrollPane scrollPane = new JScrollPane(tbl);
			final JTextField txtsearch = new JTextField();

			txtcode.setCapital(true);

			tbl.getTableHeader().setResizingAllowed(false);
			TableCellRenderer renderer = new pRenderer();
			tbl.setDefaultRenderer(Object.class, renderer);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			tbl.getTableHeader().setReorderingAllowed(false);
			
			//Payroll Process Type
			String sql = "SELECT * FROM payrollprocesstype ORDER By pptype_desc";
			tablePayPeriod.resultSet = tablePayPeriod.statement.executeQuery(sql);

			jcbpayproctype.setFont(new Font("",Font.PLAIN, 12));
			jcbpayproctype.setBackground(Color.white);
			jcbpayproctype.addItem("Choose");
			while (tablePayPeriod.resultSet.next()){
				jcbpayproctype.addItem(Formatter.formatString(50,' ','-',tablePayPeriod.resultSet.getString("pptype_desc"))+"-"+tablePayPeriod.resultSet.getString("pptype_code"));
			}//while (tablePayPeriod.resultSet.next())

			tablePayPeriod.resultSet = tablePayPeriod.statement.executeQuery(DEFAULT_QUERY);
			int colIndex0 = 0;
			int colIndex1 = 1;
			int width0 = 100;
			int width1 = 100;
			TableColumn col0 = tbl.getColumnModel().getColumn(colIndex0);
			TableColumn col1 = tbl.getColumnModel().getColumn(colIndex1);

			col0.setMaxWidth(width0);
			col1.setMaxWidth(width1);
			col0.setMinWidth(100);
			col1.setMinWidth(100);

			//set column width
			tbl.getColumnModel().getColumn(0).setMaxWidth(0);
			tbl.getColumnModel().getColumn(0).setMinWidth(0);
			tbl.getColumnModel().getColumn(1).setMaxWidth(0);
			tbl.getColumnModel().getColumn(1).setMinWidth(0);
			tbl.getColumnModel().getColumn(0).setHeaderValue("Code");
			tbl.getColumnModel().getColumn(1).setHeaderValue("Description");
			tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(97);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(97);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(290);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(290);

			tbl.getTableHeader().setResizingAllowed(false);
			tbl.getTableHeader().setMaximumSize(new Dimension(23,56));
			tbl.setSelectionMode(0);

			tbl.setPreferredScrollableViewportSize(new Dimension(385,118));

			jpan.setBounds(-39,20,520,150);

			lblcode.setBounds(17,73,100,280);
			lbldescription.setBounds(17,103,100,270);
			lblpayperiodfrom.setBounds(17,133,100,270);
			lblmonthcovered.setBounds(17,163,100,270);
			lblmanhours.setBounds(17,193,100,270);
			lblcutoffdatefrom.setBounds(17,223,100,270);
			lblcutoffdateto.setBounds(17,253,100,270);
			lblprocesstype.setBounds(17,283,100,270);

			txtcode.setBounds(119,200,80,20);
			txtcode.setDisabledTextColor(new Color(139,113,113));
			txtdescription.setBounds(119,230,250,20);
			txtdescription.setDisabledTextColor(new Color(139,113,113));
			txtPayPeriodFrom.setBounds(119,260,150,20);
			cboPayPeriodFrom.setBounds(new Rectangle(264, 259, 23, 23));
			txtMonthCovered.setBounds(119,290,150,20);
			cboMonthCovered.setBounds(new Rectangle(264, 289, 23, 23));
			txtManHours.setBounds(119,320,50,20);
			txtManHours.setHorizontalAlignment(JTextField.TRAILING);
			txtCutOffFrom.setBounds(119,350,150,20);
			cboCutOffFrom.setBounds(new Rectangle(264, 349, 23, 23));
			txtCutOffTo.setBounds(119,380,150,20);
			cboCutOffTo.setBounds(new Rectangle(264, 379, 23, 23));
			jcbpayproctype.setBounds(119,410,150,20);

			jbtnadd.setBounds(80,170,80,22);
			jbtnedit.setBounds(180,170,80,22);
			jbtndelete.setBounds(280,170,80,22);
			txtsearch.setBounds(18,3,250,18);
			txtsearch.setBackground(new Color(255,255,225));
			txtsearch.setVisible(false);
			txtsearch.setBorder(BorderFactory.createEmptyBorder());

			jpan.add(scrollPane, BorderLayout.CENTER);


			if (tbl.getRowCount() >= 1) {
				tbl.setRowSelectionInterval(0,0);
				tbl.setColumnSelectionInterval(0,0);
				setValue();
			}

			//add objects to container
			container.add(jbtndelete);
			container.add(jpan);
			container.add(txtcode);
			container.add(jbtnadd);
			container.add(jbtnedit);
			container.add(lblcode);
			container.add(lbldescription);
			container.add(txtdescription);
			container.add(lblpayperiodfrom);
			container.add(txtPayPeriodFrom);
			container.add(cboPayPeriodFrom);
			container.add(lblmonthcovered);
			container.add(txtMonthCovered);
			container.add(cboMonthCovered);
			container.add(lblmanhours);
			container.add(txtManHours);
			container.add(lblcutoffdatefrom);
			container.add(txtCutOffFrom);
			container.add(cboCutOffFrom);
			container.add(lblcutoffdateto);
			container.add(txtCutOffTo);
			container.add(cboCutOffTo);
			container.add(lblprocesstype);
			container.add(jcbpayproctype);
			container.add(txtsearch);

			//Disabled text fields
			disabledFields();

			cboPayPeriodFrom.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (cboPayPeriodFrom.getSelectedIndex() != -1) {
						txtPayPeriodFrom.setText(cboPayPeriodFrom.getSelectedItem().toString());
					}
				}
			});

			cboMonthCovered.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (cboMonthCovered.getSelectedIndex() != -1) {
						txtMonthCovered.setText(cboMonthCovered.getSelectedItem().toString());
					}
				}
			});

			txtManHours.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) {
							e.consume();
						}
						else {
							if (txtManHours.getText().indexOf(".") == -1) {
								if (txtManHours.getText().length() >= 3) {
									e.consume();
								}
							}
							else {
								if (txtManHours.getText().length() > (txtManHours.getText().indexOf(".")+2)) {
									e.consume();
								}
							}
						}
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			cboCutOffFrom.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (cboCutOffFrom.getSelectedIndex() != -1) {
						txtCutOffFrom.setText(cboCutOffFrom.getSelectedItem().toString());
					}
				}
			});

			cboCutOffTo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (cboCutOffTo.getSelectedIndex() != -1) {
						txtCutOffTo.setText(cboCutOffTo.getSelectedItem().toString());
					}
				}
			});

			jcbpayproctype.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbpayproctype.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						payproctype = "";
					}
					else {
						payproctype = jcbpayproctype.getSelectedItem().toString().substring(51);
					}
				}
			});

			//ADD BUTTON LISTENER
			jbtnadd.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String caption = jbtnadd.getText();
					String xcode = txtcode.getText().trim();
					String xdesc = txtdescription.getText().trim();
					String payperiodfrom = "";
					String monthcovered = "";
					String manhours = "";
					String cutofffrom = "";
					String cutoffto = "";
					if (cboPayPeriodFrom.getSelectedItem().toString().trim().length() > 0) {
						try {
							payperiodfrom = dteFormat.format(strFormat.parse(cboPayPeriodFrom.getSelectedItem().toString()));
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
					if (cboMonthCovered.getSelectedItem().toString().trim().length() > 0) {
						try {
							monthcovered = dteFormat.format(strFormat.parse(cboMonthCovered.getSelectedItem().toString()));
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
					manhours = txtManHours.getText().trim().replaceAll(",","");
					if (cboCutOffFrom.getSelectedItem().toString().trim().length() > 0) {
						try {
							cutofffrom = dteFormat.format(strFormat.parse(cboCutOffFrom.getSelectedItem().toString()));
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
					if (cboCutOffTo.getSelectedItem().toString().trim().length() > 0) {
						try {
							cutoffto = dteFormat.format(strFormat.parse(cboCutOffTo.getSelectedItem().toString()));
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
					if (caption == "Add"){
						ListSelectionModel selectionModel = tbl.getSelectionModel();
						selectionModel.clearSelection();
						tbl.setEnabled(false);
						enableFields();
						txtsearch.setVisible(false);
						clearFields();
						jbtnadd.setText("Save");
						jbtnadd.setMnemonic('S');
						jbtndelete.setText("Cancel");
						jbtndelete.setMnemonic('C');
						jbtnedit.setEnabled(false);
						txtcode.grabFocus();
					}
					else if (caption == "Save") {
						String save_sql = DBConnect.Insert("payperiod",
							    "payp_code,payp_desc,payp_from,month_covered,man_hours,cutoff_from,cutoff_to,payroll_process_type",
								"'"+DBConnect.clean(xcode)+"'," +
								"'"+DBConnect.clean(xdesc)+"'," +
								"'"+payperiodfrom+"'," +
								"'"+monthcovered+"'," +
								"'"+manhours+"'," +
								"'"+cutofffrom+"'," +
								"'"+cutoffto+"'," +
								"'"+payproctype+"'"
								);

						tbl.setEnabled(false);

						try {
							if (txtcode.getText().length() == 0) {   
								Message.messageError("Invalid input for code");
								txtcode.grabFocus();
							}
							else if (txtdescription.getText().length() == 0) {   
								Message.messageError("Invalid input for description");
								txtdescription.grabFocus();
							}
							else if (payperiodfrom.trim().length() == 0) {   
								Message.messageError("Invalid input for pay period from");
								cboPayPeriodFrom.grabFocus();
							}
							else if (monthcovered.trim().length() == 0) {   
								Message.messageError("Invalid input for covering month");
								cboMonthCovered.grabFocus();
							}
							else if (manhours.trim().length() == 0) {   
								Message.messageError("Invalid input for man hours");
								txtManHours.grabFocus();
							}
							else if (cutofffrom.trim().length() == 0) {   
								Message.messageError("Invalid input for cut-off date from");
								cboCutOffFrom.grabFocus();
							}
							else if (cutoffto.trim().length() == 0) {   
								Message.messageError("Invalid input for cut-off date to");
								cboCutOffTo.grabFocus();
							}
							else if (payproctype.trim().length() == 0) {   
								Message.messageError("Invalid input for payroll process type");
								jcbpayproctype.grabFocus();
							}
							else{
								tablePayPeriod.setInsert(save_sql);

								tablePayPeriod.setQuery(DEFAULT_QUERY);
								tbl.getColumnModel().getColumn(0).setMaxWidth(0);
								tbl.getColumnModel().getColumn(0).setMinWidth(0);
								tbl.getColumnModel().getColumn(1).setMaxWidth(0);
								tbl.getColumnModel().getColumn(1).setMinWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(97);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(97);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(290);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(290);
								tbl.getColumnModel().getColumn(0).setHeaderValue("Code");
								tbl.getColumnModel().getColumn(1).setHeaderValue("Description");

								curRecord = txtcode.getText().trim();
								
								clearFields();

								Message.messageInfo(Message.messageAdd);
								//tbl.scrollRectToVisible(tbl.getCellRect(tbl.getRowCount()-1, tbl.getColumnCount(), true));
								tbl.scrollRectToVisible(tbl.getCellRect(setCurRecord(), tbl.getColumnCount(), true));
								txtcode.grabFocus();
							}
						}
						catch(SQLException se){
							try{
								tablePayPeriod.setQuery(DEFAULT_QUERY);
								reload();
								Message.messageError(Message.messageExist+"\n"+se.getMessage());
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
					//  String xcode = txtcode.getText();

					if (dcaption == "Cancel"){
						setCancel();
					}//if (caption == "Add"){
					else if (dcaption == "Delete"){
						// lock indicator 
						LockHandler.initializeLock(container);

						SwingUtilities.invokeLater(new Runnable(){

							public void run() {
								try {
									String xcode = txtcode.getText();
									String code = tbl.getValueAt
									(tbl.getSelectedRow(),0).toString();
									String lockCmd = DBConnect.SelectForUpdate
									("payperiod","*","payp_code = '" + DBConnect.clean(code) +"'");
									LockHandler.startLock(lockCmd);

									String del_sql = DBConnect.delete("payperiod",
											"payp_code = '"+DBConnect.clean(xcode)+"'");
									//String del_sql = "Delete from payperiod where code = '"+xcode+"'";

									txtsearch.setVisible(false);	
									if (txtcode.getText().length() != 0) {
										int setDialog = Message.messageYesNo("Are You Sure you want to delete\n" +txtcode.getText()+" ?");
										if(setDialog == JOptionPane.YES_OPTION){
											try {
												LockHandler.removeLockAndDelete(del_sql);
												//tablePayPeriod.setInsert(del_sql);

												tablePayPeriod.setQuery(DEFAULT_QUERY);

												ListSelectionModel selectionModel = tbl.getSelectionModel();
												selectionModel.setSelectionInterval(0, 0);
												//}//end if
												if (tbl.getRowCount() >= 1) {
													setValue();
												}else{
													clearFields();
												}
												reload();
												tbl.getSelectedRow();
												tbl.grabFocus();

												Message.messageInfo(Message.messageDelete);

											}
											catch(SQLException se){
												System.out.println(se.getMessage());
											}
										}//if(setDialog == JOptionPane.YES_OPTION){
										tbl.grabFocus();
									}//if (txtcode.getText().length() != 0) {

								} catch (SQLException e) {
									try {
										//refresh mode
										tablePayPeriod.setQuery(DEFAULT_QUERY);
										reload();
										setCancel();
										LockHandler.LimitLockExceeded(container);
									} catch (IllegalStateException e1) {
										e1.printStackTrace();
									} catch (SQLException e1) {
										e1.printStackTrace();
									}
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
						});
					}
				}
			});

			//edit button listener
			jbtnedit.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){

					String ecaption = jbtnedit.getText();
					String ucode = txtcode.getText().trim();
					String udesc = txtdescription.getText().trim();
					String payperiodfrom = "";
					String monthcovered = "";
					String manhours = "";
					String cutofffrom = "";
					String cutoffto = "";
					if (cboPayPeriodFrom.getSelectedItem().toString().trim().length() > 0) {
						try {
							payperiodfrom = dteFormat.format(strFormat.parse(cboPayPeriodFrom.getSelectedItem().toString()));
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
					if (cboMonthCovered.getSelectedItem().toString().trim().length() > 0) {
						try {
							monthcovered = dteFormat.format(strFormat.parse(cboMonthCovered.getSelectedItem().toString()));
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
					manhours = txtManHours.getText().trim().replaceAll(",","");
					if (cboCutOffFrom.getSelectedItem().toString().trim().length() > 0) {
						try {
							cutofffrom = dteFormat.format(strFormat.parse(cboCutOffFrom.getSelectedItem().toString()));
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
					if (cboCutOffTo.getSelectedItem().toString().trim().length() > 0) {
						try {
							cutoffto = dteFormat.format(strFormat.parse(cboCutOffTo.getSelectedItem().toString()));
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
					int currentr = tbl.getSelectedRow();
					int currentc = tbl.getSelectedColumn();
					if (ucode.length() != 0){
						if (ecaption == "Edit"){
							currentr = tbl.getSelectedRow();
							currentc = tbl.getSelectedColumn();
							//                        	 lock indicator 
							LockHandler.initializeLock(container);

							SwingUtilities.invokeLater(new Runnable(){

								public void run() {
									try {
										String code = tbl.getValueAt
										(tbl.getSelectedRow(),0).toString();
										String lockCmd = DBConnect.SelectForUpdate
										("payperiod","*","payp_code = '" + DBConnect.clean(code) +"'");
										LockHandler.startLock(lockCmd);

										//refresh
										tablePayPeriod.setQuery(DEFAULT_QUERY);
										reload();

										//populate records
										tbl.setRowSelectionInterval(tempR,tempR);
										tbl.setColumnSelectionInterval(tempC,tempC);
										setValue();

										tbl.grabFocus();	
										tbl.setEnabled(false);
										jbtnedit.setText("Update");
										jbtnedit.setMnemonic('U');
										jbtndelete.setText("Cancel");
										jbtndelete.setMnemonic('C');
										jbtnadd.setEnabled(false);
										enableFields();
										txtcode.setEnabled(false);
										txtsearch.setVisible(false);
										txtdescription.grabFocus();


									} catch (SQLException e) {
										try {
											//refresh mode
											tablePayPeriod.setQuery(DEFAULT_QUERY);
											reload();
											setCancel();
											LockHandler.LimitLockExceeded(container);
										} catch (IllegalStateException e1) {
											e1.printStackTrace();
										} catch (SQLException e1) {
											e1.printStackTrace();
										}
									} finally {
										try {
											//remove lock indicator
											LockHandler.finallyLimitLockExceeded(container);
										} catch (IllegalStateException e) {
											e.printStackTrace();
										} catch (SQLException e) {
											e.printStackTrace();
										}
									}
								}// end public void run
							}); // end SwingUtilities.invokeLater
						}
						else if (ecaption == "Update"){
							try{
								String e_sql = DBConnect.Update("payperiod",
										"payp_desc='"+DBConnect.clean(udesc)+"', "+
										"payp_from='"+payperiodfrom+"', "+
										"month_covered='"+monthcovered+"', "+
										"man_hours='"+manhours+"', "+
										"cutoff_from='"+cutofffrom+"', "+
										"cutoff_to='"+cutoffto+"', "+
										"payroll_process_type='"+payproctype+"'",
										"payp_code= '"+DBConnect.clean(ucode)+"'");

								if (txtdescription.getText().length() == 0) {   
									Message.messageError("Invalid input for description");
									txtdescription.grabFocus();
								}
								else if (payperiodfrom.trim().length() == 0) {   
									Message.messageError("Invalid input for pay period from");
									cboPayPeriodFrom.grabFocus();
								}
								else if (monthcovered.trim().length() == 0) {   
									Message.messageError("Invalid input for covering month");
									cboMonthCovered.grabFocus();
								}
								else if (manhours.trim().length() == 0) {   
									Message.messageError("Invalid input for man hours");
									txtManHours.grabFocus();
								}
								else if (cutofffrom.trim().length() == 0) {   
									Message.messageError("Invalid input for cut-off date from");
									cboCutOffFrom.grabFocus();
								}
								else if (cutoffto.trim().length() == 0) {   
									Message.messageError("Invalid input for cut-off date to");
									cboCutOffTo.grabFocus();
								}
								else if (payproctype.trim().length() == 0) {   
									Message.messageError("Invalid input for payroll process type");
									jcbpayproctype.grabFocus();
								}
								else{
									LockHandler.removeLockAndUpdate(e_sql);

									tablePayPeriod.setQuery(DEFAULT_QUERY);
									tbl.setEnabled(true);
									jbtnadd.setText("Add");
									jbtnadd.setMnemonic('A');
									jbtndelete.setText("Delete");
									jbtndelete.setMnemonic('D');
									jbtnedit.setText("Edit");
									jbtnedit.setMnemonic('E');
									jbtnadd.setEnabled(true);
									ListSelectionModel selectionModel = tbl.getSelectionModel();
									selectionModel.setSelectionInterval(0, 0);
									reload();
									txtcode.setText(tbl.getValueAt(currentr,0).toString());
									txtdescription.setText(tbl.getValueAt(currentr,1).toString());
									tbl.getSelectedRow();
									tbl.grabFocus();
									disabledFields();
									txtsearch.setVisible(false);
									try{
										tbl.setRowSelectionInterval(currentr,currentr);
										tbl.setColumnSelectionInterval(currentc,currentc);
									}catch(Exception te){}
									Message.messageInfo(Message.messageUpdate);
								}
							}
							catch(SQLException se){
								System.out.println(se.getMessage());
							}
						}
					}
				}
			});

			//table mouse click listener
			tbl.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					txtsearch.setVisible(false);
					txtsearch.setText(null);
					if (jbtndelete.getLabel() != "Cancel"){
						//get last selected cell
						tempR = tbl.getSelectedRow();
						tempC = tbl.getSelectedColumn();
						int getrow = tbl.getSelectedRow();
						txtcode.setText(tbl.getValueAt(getrow,0).toString());
						txtdescription.setText(tbl.getValueAt(getrow,1).toString());
					}
				}
			});


			//table key listener
			tbl.addKeyListener(new KeyAdapter(){
				public void keyPressed(KeyEvent e){
					InputMap IMP = tbl.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
					KeyStroke ent = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
					int eventKey = e.getKeyChar();
					int getrow = tbl.getSelectedRow();
					IMP.put(ent, "none");
					if (eventKey == 10){
						//get last selected cell
						tempR = tbl.getSelectedRow();
						tempC = tbl.getSelectedColumn();
						txtcode.setText(tbl.getValueAt(getrow,0).toString());
						txtdescription.setText(tbl.getValueAt(getrow,1).toString());
					}

					if ((e.getKeyCode() >= 65) && (e.getKeyCode() <= 105) && (e.isAltDown() != true)){
						txtsearch.setVisible(true);
						txtsearch.grabFocus();

						//Object text = e.getKeyChar();
						txtsearch.setText("Description:" + " " + e.getKeyChar());
						//}
					}

				}//public void keyPressed(KeyEvent e){
			}//new KeyAdapter()
			);//tbl.addKeyListener(

			//txtsearch key listener
			txtsearch.addKeyListener(new KeyAdapter(){
				public void keyReleased(KeyEvent e){
					int eventKey = e.getKeyChar();
					if(eventKey == 8){
						if( txtsearch.getText().length() == 12){
							txtsearch.setVisible(false);
							Rectangle bounds = tbl.getCellRect(tbl.getSelectedRow(),0,true);
							scrollPane.getViewport().setViewPosition(bounds.getLocation());
							scrollPane.getViewport().setOpaque(false);
							scrollPane.getViewport().setView(tbl);

						}	
					}
					try{
						if (txtsearch.getText().length() == 0) {
							txtsearch.setVisible(false);
						}//if (txtsearch.getText().length() == 0) {
							for(int col = 0; col < tbl.getRowCount(); col++){
								String getcode = tbl.getValueAt(col,1).toString();
								String[] getcode4 = txtsearch.getText().replaceFirst(" ", "-").split("-");
								String getcode2 = getcode4[1];
								//System.out.println(getcode);
								if ((getcode.toUpperCase().equalsIgnoreCase(getcode2.toUpperCase())) && (txtsearch.getText().length() != 0)){
									ListSelectionModel selectionModel = tbl.getSelectionModel();
									selectionModel.removeSelectionInterval(0,tbl.getRowCount());
									selectionModel.addSelectionInterval(col,col);



									col = tbl.getRowCount();
									int getrow = tbl.getSelectedRow();

									txtcode.setText(tbl.getValueAt(getrow,0).toString());
									txtdescription.setText(tbl.getValueAt(getrow,1).toString());

									Rectangle bounds = tbl.getCellRect(tbl.getSelectedRow(),0,true);
									scrollPane.getViewport().setViewPosition(bounds.getLocation());
									scrollPane.getViewport().setOpaque(false);
									scrollPane.getViewport().setView(tbl);


								}//if ((getcode.toUpperCase().equals(getcode2.toUpperCase())) && (txtsearch.getText().length() != 0)){
								}//for(int col = 0; col < tbl.getRowCount(); col++){
								}//try{
									catch(Exception xe){
										//System.out.println(xe.getMessage());
									}//catch(Exception xe){
				}//public void keyReleased(KeyEvent e){
			}//new KeyAdapter(){
			);//txtsearch.addKeyListener(

			//		Add Key Listener
			tbl.addKeyListener(new KeyAdapter(){
				public void keyReleased(KeyEvent e){
					//				int eventKey = e.getKeyCode();
					tempR = tbl.getSelectedRow();
					tempC = tbl.getSelectedColumn();
					setValue();
				}
			}//new KeyAdapter()
			);//tbl.addKeyListener( 



		}//try

		catch ( ClassNotFoundException classNotFound ) 
		{
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			classNotFound.printStackTrace(printWriter);
			createINI.create("PayPeriodMaint", "PayPeriodMaint", traceWriter.toString());      
			dispose();
		}
		catch ( SQLException sqlE )
		{
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			sqlE.printStackTrace(printWriter);
			createINI.create("PayPeriodMaint", "PayPeriodMaint", traceWriter.toString());      
			dispose();
		} 
		catch(Exception e){
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("PayPeriodMaint", "PayPeriodMaint", traceWriter.toString());      
			dispose();
		}

		setSize(445,475);
		setVisible(true);
		//setLocation((800 + 450)/5,((546 - 340)/2));
	}


	//Add JTable Renderer
	private class pRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			/*if (column == 1)
				setHorizontalAlignment( LEFT );
			else*/
			/*if (column == 0){
				setHorizontalAlignment( CENTER );
			}*/
			setHorizontalAlignment( LEFT );
			setBorder(BorderFactory.createLineBorder(getBackground(), 1));
			return this;
		}
	}//class kRenderer extends DefaultTableCellRenderer

	private void setCancel() {
		try {
			//rollback row level locking
			LockHandler.removeLock();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		tbl.setEnabled(true);
		jbtnedit.setText("Edit");
		jbtnedit.setMnemonic('E');
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
			setValue();
		}else{
			txtcode.setText(null);
			txtdescription.setText(null);
		}
		tbl.getSelectedRow();
		tbl.grabFocus();
		txtcode.setEnabled(false);
		txtdescription.setEnabled(false);
		txtPayPeriodFrom.setEnabled(false);
		cboPayPeriodFrom.setEnabled(false);
		txtMonthCovered.setEnabled(false);
		cboMonthCovered.setEnabled(false);
		txtManHours.setEnabled(false);
		txtCutOffFrom.setEnabled(false);
		cboCutOffFrom.setEnabled(false);
		txtCutOffTo.setEnabled(false);
		cboCutOffTo.setEnabled(false);
		jcbpayproctype.setEnabled(false);
	}
	
	private void enableFields() {
		txtcode.setEnabled(true);
		txtdescription.setEnabled(true);
		txtPayPeriodFrom.setEnabled(true);
		txtPayPeriodFrom.setFocusable(false);
		cboPayPeriodFrom.setEnabled(true);
		txtMonthCovered.setEnabled(true);
		txtMonthCovered.setFocusable(false);
		cboMonthCovered.setEnabled(true);
		txtManHours.setEnabled(true);
		txtCutOffFrom.setEnabled(true);
		txtCutOffFrom.setFocusable(false);
		cboCutOffFrom.setEnabled(true);
		txtCutOffTo.setEnabled(true);
		txtCutOffTo.setFocusable(false);
		cboCutOffTo.setEnabled(true);
		jcbpayproctype.setEnabled(true);
	}//ENDof private void enableFields() {

	private void disabledFields() {
		txtcode.setEnabled(false);
		txtdescription.setEnabled(false);
		txtPayPeriodFrom.setEnabled(false);
		cboPayPeriodFrom.setEnabled(false);
		txtMonthCovered.setEnabled(false);
		cboMonthCovered.setEnabled(false);
		txtManHours.setEnabled(false);
		txtCutOffFrom.setEnabled(false);
		cboCutOffFrom.setEnabled(false);
		txtCutOffTo.setEnabled(false);
		cboCutOffTo.setEnabled(false);
		jcbpayproctype.setEnabled(false);
	}//ENDof private void disabledFields() {

	private void clearFields() {
		txtcode.setText("");
		txtdescription.setText("");
		cboPayPeriodFrom.setSelectedItem(strFormat.format(new Date()));
		txtPayPeriodFrom.setText("");
		cboMonthCovered.setSelectedItem(strFormat.format(new Date()));
		txtMonthCovered.setText("");
		txtManHours.setText("");
		cboCutOffFrom.setSelectedItem(strFormat.format(new Date()));
		txtCutOffFrom.setText("");
		cboCutOffTo.setSelectedItem(strFormat.format(new Date()));
		txtCutOffTo.setText("");
		jcbpayproctype.setSelectedItem("Choose");
	}//ENDof private void clearFields() {
	
	private void setValue() {
		int getrow = tbl.getSelectedRow();
		txtcode.setText(tbl.getValueAt(getrow,0).toString());
		txtdescription.setText(tbl.getValueAt(getrow,1).toString());
		if (tbl.getValueAt(getrow,2).toString().equals("0000-00-00") == false) {
			try {
				cboPayPeriodFrom.setSelectedItem(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,2).toString())));
				txtPayPeriodFrom.setText(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,2).toString())));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		else {
			cboPayPeriodFrom.setSelectedItem(strFormat.format(new Date()));
			txtPayPeriodFrom.setText("");
		}
		if (tbl.getValueAt(getrow,3).toString().equals("0000-00-00") == false) {
			try {
				cboMonthCovered.setSelectedItem(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,3).toString())));
				txtMonthCovered.setText(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,3).toString())));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		else {
			cboMonthCovered.setSelectedItem(strFormat.format(new Date()));
			txtMonthCovered.setText("");
		}
		txtManHours.setText(tbl.getValueAt(getrow,4).toString());
		if (tbl.getValueAt(getrow,5).toString().equals("0000-00-00") == false) {
			try {
				cboCutOffFrom.setSelectedItem(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,5).toString())));
				txtCutOffFrom.setText(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,5).toString())));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		else {
			cboCutOffFrom.setSelectedItem(strFormat.format(new Date()));
			txtCutOffFrom.setText("");
		}
		if (tbl.getValueAt(getrow,6).toString().equals("0000-00-00") == false) {
			try {
				cboCutOffTo.setSelectedItem(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,6).toString())));
				txtCutOffTo.setText(strFormat.format(dteFormat.parse(tbl.getValueAt(getrow,6).toString())));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		else {
			cboCutOffTo.setSelectedItem(strFormat.format(new Date()));
			txtCutOffTo.setText("");
		}
		jcbpayproctype.setSelectedIndex(0);
		for (int ii=0; ii<=jcbpayproctype.getItemCount()-1; ii++) {
			if (jcbpayproctype.getItemAt(ii).toString().length() > 50) {
				if (tbl.getValueAt(getrow,7).toString().equalsIgnoreCase(jcbpayproctype.getItemAt(ii).toString().substring(51)) == true) {
					jcbpayproctype.setSelectedIndex(ii);
				}
			}
		}//ENDof for (int ii=0; ii<=jcbpayproctype.getItemCount()-1; ii++) {
	}//ENDof private void setValue() {

	private void reload() {
		tbl.getColumnModel().getColumn(0).setMaxWidth(0);
		tbl.getColumnModel().getColumn(0).setMinWidth(0);
		tbl.getColumnModel().getColumn(1).setMaxWidth(0);
		tbl.getColumnModel().getColumn(1).setMinWidth(0);
		tbl.getColumnModel().getColumn(0).setHeaderValue("Code");
		tbl.getColumnModel().getColumn(1).setHeaderValue("Description");
		tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(97);
		tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(97);
		tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(290);
		tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(290);
	}

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
	
}//public class keymap extends JInternalFrame {
