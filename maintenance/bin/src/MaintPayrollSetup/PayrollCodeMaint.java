package maintenance.bin.src.MaintPayrollSetup;

/**
 * Author:  Norberto L. Silva
 * Date:    March 6, 2012
 * Company: Applied Ideas, Inc 
 * Program: Payroll Code Maintenance ==-> Payroll System
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

import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
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


public class PayrollCodeMaint extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	static final String DEFAULT_QUERY = DBConnect.Select("payrollcode",
														 "pay_code,pay_desc,freq_code,ot_code," +
														 "wtxtype_code,ssstype_code,phhtype_code,pbgtype_code",
														 null,
														 "pay_code ASC");

	private ResultSetTableModel tablePayrollCode;
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

	String frequency = "";
	String ot = "";
	String wtax = "";
	String sss = "";
	String philhealth = "";
	String pagibig = "";

	//varible for data entry
	private MyField txtcode = ((MyField)new MyField(true,4));
	private MyField txtdescription = new MyField(true,15);
	private JComboBox jcbfrequency = new JComboBox();
	private JComboBox jcbot = new JComboBox();
	private JComboBox jcbwtax = new JComboBox();
	private JComboBox jcbsss = new JComboBox();
	private JComboBox jcbphilhealth = new JComboBox();
	private JComboBox jcbpagibig = new JComboBox();

	public PayrollCodeMaint(){
		super ("Payroll Code Maintenance", false, true, false, false);
		this.setName("payrollcode");
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
			tablePayrollCode  = new ResultSetTableModel(  DEFAULT_QUERY );

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
			JLabel lblfrequency = new JLabel("Frequency");
			JLabel lblot = new JLabel("OverTime");
			JLabel lblwtax = new JLabel("WithHolding Tax");
			JLabel lblsss = new JLabel("SSS Premiums");
			JLabel lblphilhealth = new JLabel("PhilHealth");
			JLabel lblpagibig = new JLabel("Pag-Ibig");

			tbl = new JTable(tablePayrollCode);
			final JScrollPane scrollPane = new JScrollPane(tbl);
			final JTextField txtsearch = new JTextField();

			txtcode.setCapital(true);

			tbl.getTableHeader().setResizingAllowed(false);
			TableCellRenderer renderer = new pRenderer();
			tbl.setDefaultRenderer(Object.class, renderer);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			tbl.getTableHeader().setReorderingAllowed(false);
			
			//FREQUENCY
			String sql = "SELECT * FROM frequency ORDER By freq_desc";
			tablePayrollCode.resultSet = tablePayrollCode.statement.executeQuery(sql);

			jcbfrequency.setFont(new Font("",Font.PLAIN, 12));
			jcbfrequency.setBackground(Color.white);
			jcbfrequency.addItem("Choose");
			while (tablePayrollCode.resultSet.next()){
				jcbfrequency.addItem(Formatter.formatString(50,' ','-',tablePayrollCode.resultSet.getString("freq_desc"))+"-"+tablePayrollCode.resultSet.getString("freq_code"));
			}//while (tablePayrollCode.resultSet.next())

			//OverTime
			sql = "SELECT * FROM overtime ORDER By ot_desc";
			tablePayrollCode.resultSet = tablePayrollCode.statement.executeQuery(sql);

			jcbot.setFont(new Font("",Font.PLAIN, 12));
			jcbot.setBackground(Color.white);
			jcbot.addItem("Choose");
			while (tablePayrollCode.resultSet.next()){
				jcbot.addItem(Formatter.formatString(50,' ','-',tablePayrollCode.resultSet.getString("ot_desc"))+"-"+tablePayrollCode.resultSet.getString("ot_code"));
			}//while (tablePayrollCode.resultSet.next())

			//WithHolding Tax
			sql = "SELECT * FROM wtaxtype ORDER By wtxtype_desc";
			tablePayrollCode.resultSet = tablePayrollCode.statement.executeQuery(sql);

			jcbwtax.setFont(new Font("",Font.PLAIN, 12));
			jcbwtax.setBackground(Color.white);
			jcbwtax.addItem("Choose");
			while (tablePayrollCode.resultSet.next()){
				jcbwtax.addItem(Formatter.formatString(50,' ','-',tablePayrollCode.resultSet.getString("wtxtype_desc"))+"-"+tablePayrollCode.resultSet.getString("wtxtype_code"));
			}//while (tablePayrollCode.resultSet.next())

			//SSS
			sql = "SELECT * FROM ssstype ORDER By ssstype_desc";
			tablePayrollCode.resultSet = tablePayrollCode.statement.executeQuery(sql);

			jcbsss.setFont(new Font("",Font.PLAIN, 12));
			jcbsss.setBackground(Color.white);
			jcbsss.addItem("Choose");
			while (tablePayrollCode.resultSet.next()){
				jcbsss.addItem(Formatter.formatString(50,' ','-',tablePayrollCode.resultSet.getString("ssstype_desc"))+"-"+tablePayrollCode.resultSet.getString("ssstype_code"));
			}//while (tablePayrollCode.resultSet.next())

			//PhilHealth
			sql = "SELECT * FROM philhealthtype ORDER By phhtype_desc";
			tablePayrollCode.resultSet = tablePayrollCode.statement.executeQuery(sql);

			jcbphilhealth.setFont(new Font("",Font.PLAIN, 12));
			jcbphilhealth.setBackground(Color.white);
			jcbphilhealth.addItem("Choose");
			while (tablePayrollCode.resultSet.next()){
				jcbphilhealth.addItem(Formatter.formatString(50,' ','-',tablePayrollCode.resultSet.getString("phhtype_desc"))+"-"+tablePayrollCode.resultSet.getString("phhtype_code"));
			}//while (tablePayrollCode.resultSet.next())

			//Pag-Ibig
			sql = "SELECT * FROM pagibigtype ORDER By pbgtype_desc";
			tablePayrollCode.resultSet = tablePayrollCode.statement.executeQuery(sql);

			jcbpagibig.setFont(new Font("",Font.PLAIN, 12));
			jcbpagibig.setBackground(Color.white);
			jcbpagibig.addItem("Choose");
			while (tablePayrollCode.resultSet.next()){
				jcbpagibig.addItem(Formatter.formatString(50,' ','-',tablePayrollCode.resultSet.getString("pbgtype_desc"))+"-"+tablePayrollCode.resultSet.getString("pbgtype_code"));
			}//while (tablePayrollCode.resultSet.next())

			tablePayrollCode.resultSet = tablePayrollCode.statement.executeQuery(DEFAULT_QUERY);
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
			lblfrequency.setBounds(17,133,100,270);
			lblot.setBounds(17,163,100,270);
			lblwtax.setBounds(17,193,100,270);
			lblsss.setBounds(17,223,100,270);
			lblphilhealth.setBounds(17,253,100,270);
			lblpagibig.setBounds(17,283,100,270);

			txtcode.setBounds(119,200,80,20);
			txtcode.setDisabledTextColor(new Color(139,113,113));
			txtdescription.setBounds(119,230,250,20);
			txtdescription.setDisabledTextColor(new Color(139,113,113));
			jcbfrequency.setBounds(119,260,120,20);
			jcbot.setBounds(119,290,150,20);
			jcbwtax.setBounds(119,320,220,20);
			jcbsss.setBounds(119,350,220,20);
			jcbphilhealth.setBounds(119,380,220,20);
			jcbpagibig.setBounds(119,410,220,20);

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
			container.add(lblfrequency);
			container.add(jcbfrequency);
			container.add(lblot);
			container.add(jcbot);
			container.add(lblwtax);
			container.add(jcbwtax);
			container.add(lblsss);
			container.add(jcbsss);
			container.add(lblphilhealth);
			container.add(jcbphilhealth);
			container.add(lblpagibig);
			container.add(jcbpagibig);
			container.add(txtsearch);

			//Disabled text fields
			disabledFields();

			jcbfrequency.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbfrequency.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						frequency = "";
					}
					else {
						frequency = jcbfrequency.getSelectedItem().toString().substring(51);
					}
				}
			});

			jcbot.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbot.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						ot = "";
					}
					else {
						ot = jcbot.getSelectedItem().toString().substring(51);
					}
				}
			});

			jcbwtax.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbwtax.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						wtax = "";
					}
					else {
						wtax = jcbwtax.getSelectedItem().toString().substring(51);
					}
				}
			});

			jcbsss.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbsss.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						sss = "";
					}
					else {
						sss = jcbsss.getSelectedItem().toString().substring(51);
					}
				}
			});

			jcbphilhealth.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbphilhealth.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						philhealth = "";
					}
					else {
						philhealth = jcbphilhealth.getSelectedItem().toString().substring(51);
					}
				}
			});

			jcbpagibig.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcbpagibig.getSelectedItem().toString().equalsIgnoreCase("Choose") == true) {
						pagibig = "";
					}
					else {
						pagibig = jcbpagibig.getSelectedItem().toString().substring(51);
					}
				}
			});

			//ADD BUTTON LISTENER
			jbtnadd.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String caption = jbtnadd.getText();
					String xcode = txtcode.getText().trim();
					String xdesc = txtdescription.getText().trim();
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
						String save_sql = DBConnect.Insert("payrollcode",
								"pay_code,pay_desc,freq_code,ot_code,wtxtype_code,ssstype_code,phhtype_code,pbgtype_code",
								"'"+DBConnect.clean(xcode)+"'," +
								"'"+DBConnect.clean(xdesc)+"'," +
								"'"+frequency+"'," +
								"'"+ot+"'," +
								"'"+wtax+"'," +
								"'"+sss+"'," +
								"'"+philhealth+"'," +
								"'"+pagibig+"'"
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
							else if (frequency.trim().length() == 0) {   
								Message.messageError("Invalid input for frequency");
								jcbfrequency.grabFocus();
							}
							else if (ot.trim().length() == 0) {   
								Message.messageError("Invalid input for overtime");
								jcbot.grabFocus();
							}
							else if (wtax.trim().length() == 0) {   
								Message.messageError("Invalid input for withholding tax");
								jcbwtax.grabFocus();
							}
							else if (sss.trim().length() == 0) {   
								Message.messageError("Invalid input for SSS");
								jcbsss.grabFocus();
							}
							else if (philhealth.trim().length() == 0) {   
								Message.messageError("Invalid input for philhealth");
								jcbphilhealth.grabFocus();
							}
							else if (pagibig.trim().length() == 0) {   
								Message.messageError("Invalid input for pag-ibig");
								jcbpagibig.grabFocus();
							}
							else{
								tablePayrollCode.setInsert(save_sql);

								tablePayrollCode.setQuery(DEFAULT_QUERY);
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
								tablePayrollCode.setQuery(DEFAULT_QUERY);
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
									("payrollcode","*","pay_code = '" + DBConnect.clean(code) +"'");
									LockHandler.startLock(lockCmd);

									String del_sql = DBConnect.delete("payrollcode",
											"pay_code = '"+DBConnect.clean(xcode)+"'");
									//String del_sql = "Delete from payrollcode where code = '"+xcode+"'";

									txtsearch.setVisible(false);	
									if (txtcode.getText().length() != 0) {
										int setDialog = Message.messageYesNo("Are You Sure you want to delete\n" +txtcode.getText()+" ?");
										if(setDialog == JOptionPane.YES_OPTION){
											try {
												LockHandler.removeLockAndDelete(del_sql);
												//tablePayrollCode.setInsert(del_sql);

												tablePayrollCode.setQuery(DEFAULT_QUERY);

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
										tablePayrollCode.setQuery(DEFAULT_QUERY);
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
										("payrollcode","*","pay_code = '" + DBConnect.clean(code) +"'");
										LockHandler.startLock(lockCmd);

										//refresh
										tablePayrollCode.setQuery(DEFAULT_QUERY);
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
										txtsearch.setVisible(false);
										txtdescription.grabFocus();


									} catch (SQLException e) {
										try {
											//refresh mode
											tablePayrollCode.setQuery(DEFAULT_QUERY);
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
								String e_sql = DBConnect.Update("payrollcode",
										"pay_desc='"+DBConnect.clean(udesc)+"', "+
										"freq_code='"+frequency+"', "+
										"ot_code='"+ot+"', "+
										"wtxtype_code='"+wtax+"', "+
										"ssstype_code='"+sss+"', "+
										"phhtype_code='"+philhealth+"', "+
										"pbgtype_code='"+pagibig+"'",
										"pay_code= '"+DBConnect.clean(ucode)+"'");

								if (txtdescription.getText().length() == 0) {   
									Message.messageError("Invalid input for description");
									txtdescription.grabFocus();
								}
								else if (frequency.trim().length() == 0) {   
									Message.messageError("Invalid input for frequency");
									jcbfrequency.grabFocus();
								}
								else if (ot.trim().length() == 0) {   
									Message.messageError("Invalid input for overtime");
									jcbot.grabFocus();
								}
								else if (wtax.trim().length() == 0) {   
									Message.messageError("Invalid input for withholding tax");
									jcbwtax.grabFocus();
								}
								else if (sss.trim().length() == 0) {   
									Message.messageError("Invalid input for SSS");
									jcbsss.grabFocus();
								}
								else if (philhealth.trim().length() == 0) {   
									Message.messageError("Invalid input for philhealth");
									jcbphilhealth.grabFocus();
								}
								else if (pagibig.trim().length() == 0) {   
									Message.messageError("Invalid input for pag-ibig");
									jcbpagibig.grabFocus();
								}
								else{
									LockHandler.removeLockAndUpdate(e_sql);

									tablePayrollCode.setQuery(DEFAULT_QUERY);
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
			createINI.create("PayrollCodeMaint", "PayrollCodeMaint", traceWriter.toString());      
			dispose();
		}
		catch ( SQLException sqlE )
		{
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			sqlE.printStackTrace(printWriter);
			createINI.create("PayrollCodeMaint", "PayrollCodeMaint", traceWriter.toString());      
			dispose();
		} 
		catch(Exception e){
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("PayrollCodeMaint", "PayrollCodeMaint", traceWriter.toString());      
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
		jcbfrequency.setEnabled(false);
		jcbot.setEnabled(false);
		jcbwtax.setEnabled(false);
		jcbsss.setEnabled(false);
		jcbphilhealth.setEnabled(false);
		jcbpagibig.setEnabled(false);
	}
	
	private void enableFields() {
		txtcode.setEnabled(true);
		txtdescription.setEnabled(true);
		jcbfrequency.setEnabled(true);
		jcbot.setEnabled(true);
		jcbwtax.setEnabled(true);
		jcbsss.setEnabled(true);
		jcbphilhealth.setEnabled(true);
		jcbpagibig.setEnabled(true);
	}//ENDof private void enableFields() {

	private void disabledFields() {
		txtcode.setEnabled(false);
		txtdescription.setEnabled(false);
		jcbfrequency.setEnabled(false);
		jcbot.setEnabled(false);
		jcbwtax.setEnabled(false);
		jcbsss.setEnabled(false);
		jcbphilhealth.setEnabled(false);
		jcbpagibig.setEnabled(false);
	}//ENDof private void disabledFields() {

	private void clearFields() {
		txtcode.setText("");
		txtdescription.setText("");
		jcbfrequency.setSelectedItem("Choose");
		jcbot.setSelectedItem("Choose");
		jcbwtax.setSelectedItem("Choose");
		jcbsss.setSelectedItem("Choose");
		jcbphilhealth.setSelectedItem("Choose");
		jcbpagibig.setSelectedItem("Choose");
	}//ENDof private void clearFields() {
	
	private void setValue() {
		int getrow = tbl.getSelectedRow();
		txtcode.setText(tbl.getValueAt(getrow,0).toString());
		txtdescription.setText(tbl.getValueAt(getrow,1).toString());
		jcbfrequency.setSelectedIndex(0);
		for (int ii=0; ii<=jcbfrequency.getItemCount()-1; ii++) {
			if (jcbfrequency.getItemAt(ii).toString().length() > 50) {
				if (tbl.getValueAt(getrow,2).toString().equalsIgnoreCase(jcbfrequency.getItemAt(ii).toString().substring(51)) == true) {
					jcbfrequency.setSelectedIndex(ii);
				}
			}
		}//ENDof for (int ii=0; ii<=jcbfrequency.getItemCount()-1; ii++) {
		jcbot.setSelectedIndex(0);
		for (int ii=0; ii<=jcbot.getItemCount()-1; ii++) {
			if (jcbot.getItemAt(ii).toString().length() > 50) {
				if (tbl.getValueAt(getrow,3).toString().equalsIgnoreCase(jcbot.getItemAt(ii).toString().substring(51)) == true) {
					jcbot.setSelectedIndex(ii);
				}
			}
		}//ENDof for (int ii=0; ii<=jcbot.getItemCount()-1; ii++) {
		jcbwtax.setSelectedIndex(0);
		for (int ii=0; ii<=jcbwtax.getItemCount()-1; ii++) {
			if (jcbwtax.getItemAt(ii).toString().length() > 50) {
				if (tbl.getValueAt(getrow,4).toString().equalsIgnoreCase(jcbwtax.getItemAt(ii).toString().substring(51)) == true) {
					jcbwtax.setSelectedIndex(ii);
				}
			}
		}//ENDof for (int ii=0; ii<=jcbwtax.getItemCount()-1; ii++) {
		jcbsss.setSelectedIndex(0);
		for (int ii=0; ii<=jcbsss.getItemCount()-1; ii++) {
			if (jcbsss.getItemAt(ii).toString().length() > 50) {
				if (tbl.getValueAt(getrow,5).toString().equalsIgnoreCase(jcbsss.getItemAt(ii).toString().substring(51)) == true) {
					jcbsss.setSelectedIndex(ii);
				}
			}
		}//ENDof for (int ii=0; ii<=jcbsss.getItemCount()-1; ii++) {
		jcbphilhealth.setSelectedIndex(0);
		for (int ii=0; ii<=jcbphilhealth.getItemCount()-1; ii++) {
			if (jcbphilhealth.getItemAt(ii).toString().length() > 50) {
				if (tbl.getValueAt(getrow,6).toString().equalsIgnoreCase(jcbphilhealth.getItemAt(ii).toString().substring(51)) == true) {
					jcbphilhealth.setSelectedIndex(ii);
				}
			}
		}//ENDof for (int ii=0; ii<=jcbphilhealth.getItemCount()-1; ii++) {
		jcbpagibig.setSelectedIndex(0);
		for (int ii=0; ii<=jcbpagibig.getItemCount()-1; ii++) {
			if (jcbpagibig.getItemAt(ii).toString().length() > 50) {
				if (tbl.getValueAt(getrow,7).toString().equalsIgnoreCase(jcbpagibig.getItemAt(ii).toString().substring(51)) == true) {
					jcbpagibig.setSelectedIndex(ii);
				}
			}
		}//ENDof for (int ii=0; ii<=jcbpagibig.getItemCount()-1; ii++) {
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
