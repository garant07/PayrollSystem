package maintenance.bin.src.MaintGovernment;

/**
 * Author:  Norberto L. Silva
 * Date:    February 28, 2012
 * Company: Applied Ideas, Inc 
 * Program: OverTime Rates Maintenance ==-> Payroll System
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
import maintenance.bin.src.Functions.MyField;
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.ResultTableModel.ResultSetTableModel;
import maintenance.bin.src.core.LockHandler;
import maintenance.bin.src.core.mainform;

public class OTRatesMaintOLD extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private JDesktopPane desk = mainform.desktop;

	public static JInternalFrame iFrame = null;

	static String DEFAULT_QUERY = "";

	private ResultSetTableModel tableOTRates;
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
	private MyField txtcode = new MyField(true,6);
	private MyField txtdescription = new MyField(true,60);
	private JFormattedTextField txtrates = new JFormattedTextField(new DecimalFormat("##0.00"));

			JLabel lblOTdesc = new JLabel();

	private static String OT_Code = "";
	
	public OTRatesMaintOLD(String ot_code, String ot_desc, int w, int h) {
		//super ("Item Maintenance", resizable, close, maximize, iconifiable);
		super ("OverTime Rates Maintenance", false, true, false, false);
		iFrame = this;
	    Dimension rootSize = Toolkit.getDefaultToolkit().getScreenSize();
		setCustomer(ot_code,ot_desc);
		this.setName("OTRatesmaint");
		this.setResizable(false);
		//this.setLocation(((rootSize.width + 203) - w) / 2,((rootSize.height + 580) - h) / 3);
		this.setLocation(((rootSize.width + 235) - w) / 2,(rootSize.height - h) / 3);

		this.addInternalFrameListener(new InternalFrameListener(){
			public void internalFrameActivated(InternalFrameEvent arg0) {
				OverTimeMaint.subMain = 1;
			}
			public void internalFrameClosed(InternalFrameEvent arg0) {}
			public void internalFrameClosing(InternalFrameEvent arg0) {
				OverTimeMaint.subMain = 0;
			}
			public void internalFrameDeactivated(InternalFrameEvent arg0) {}
			public void internalFrameDeiconified(InternalFrameEvent arg0) {}
			public void internalFrameIconified(InternalFrameEvent arg0) {}
			public void internalFrameOpened(InternalFrameEvent arg0) {}
		});

		desk.setLayout(null);
		desk.add(this, new Integer(1));
		
		try {
			tableOTRates = new ResultSetTableModel( DEFAULT_QUERY );

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
			
			JLabel lblcode = new JLabel("Code");
			JLabel lbldescription = new JLabel("Description");
			JLabel lblrate = new JLabel("Rate");

			tbl = new JTable(tableOTRates);

			txtcode.setCapital(true);

			TableCellRenderer renderer = new TestRenderer();
			tbl.setDefaultRenderer(Object.class, renderer);
			scrollPane = new JScrollPane(tbl);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			tbl.setSelectionMode(0);

			tableOTRates.resultSet = tableOTRates.statement.executeQuery(DEFAULT_QUERY);
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
			scrollPane.setPreferredSize(new Dimension(457,170));
			jpan.setBounds(10,20,457,180);

			lblOTdesc.setBounds(10,3,400,20);
			lblcode.setBounds(28,100,100,280);
			lbldescription.setBounds(28,130,100,280);
			lblrate.setBounds(28,160,100,280);

			txtcode.setBounds(120,230,60,20);
			txtcode.setDisabledTextColor(new Color(139,113,113));
			txtdescription.setBounds(120,260,340,20);
			txtdescription.setDisabledTextColor(new Color(139,113,113));
			txtrates.setBounds(120,290,50,20);
			txtrates.setDisabledTextColor(new Color(139,113,113));
			txtrates.setHorizontalAlignment(JTextField.TRAILING);
			
			jbtnadd.setBounds(100,200,80,22);
			jbtnedit.setBounds(210,200,80,22);
			jbtndelete.setBounds(320,200,80,22);

			jpan.add(scrollPane, BorderLayout.CENTER);

			if (tbl.getRowCount() >= 1){
				tbl.setRowSelectionInterval(0,0);
				tbl.setColumnSelectionInterval(0,0);
				setFieldValue();
			}

			//add objects to container
			container.add(jpan);
			container.add(lblOTdesc);
			container.add(lblcode);
			container.add(txtcode);
			container.add(lbldescription);
			container.add(txtdescription);
			container.add(lblrate);
			container.add(txtrates);
			container.add(jbtnadd);
			container.add(jbtnedit);
			container.add(jbtndelete);        

			//Disabled text fields
			disableFields();

			txtrates.addKeyListener(new KeyAdapter() {
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
					String xcode = txtcode.getText().trim();
					String desc = "";
					String ramt = "0";
					if (txtdescription.getText().trim().length() > 0){
						desc = txtdescription.getText().trim().replaceAll(",","");
					}
					if (txtrates.getText().trim().length() > 0){
						ramt = txtrates.getText().trim().replaceAll(",","");
					}

					if (caption == "Add"){
						ListSelectionModel selectionModel = tbl.getSelectionModel();
						selectionModel.clearSelection();
						tbl.setEnabled(false);

						enableFields();

						clearFields();
						txtcode.grabFocus();

						jbtnadd.setText("Save");
						jbtnadd.setMnemonic('S');
						jbtndelete.setText("Cancel");
						jbtndelete.setMnemonic('C');
						jbtnedit.setEnabled(false);
					}
					else if (caption == "Save") {
						int iColumn = 0;
						String save_sql = DBConnect.Insert("otrates",
								"ot_code,otrates_code,otrates_desc,otrates_rate",
								"'"+DBConnect.clean(OT_Code)+"', " +
								"'"+DBConnect.clean(xcode)+"', " +
								"'"+DBConnect.clean(desc)+"', " +
								"'"+DBConnect.clean(ramt)+"' "
						);

						tbl.setEnabled(false);
						try {
							if (xcode.length() == 0) {   
								Message.messageError("Invalid input for code.");
								txtcode.grabFocus();
							}
							else if(desc.length() == 0) {
								Message.messageError("Invalid input for description field.");
								txtdescription.grabFocus();
							}
							else if (Double.parseDouble(ramt) < 1) {
								Message.messageError("Invalid input for rate field.");
								txtrates.grabFocus();
							}
							else{
								tableOTRates.setInsert(save_sql);

								tableOTRates.setQuery(DEFAULT_QUERY);
								tbl.getColumnModel().getColumn(iColumn).setMaxWidth(0);
								tbl.getColumnModel().getColumn(iColumn).setMinWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(iColumn).setMaxWidth(0);
								tbl.getTableHeader().getColumnModel().getColumn(iColumn).setMinWidth(0);
								tbl.getColumnModel().getColumn(0).setMaxWidth(60);
								tbl.getColumnModel().getColumn(0).setMinWidth(60);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(60);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(60);
								tbl.getColumnModel().getColumn(1).setMaxWidth(330);
								tbl.getColumnModel().getColumn(1).setMinWidth(330);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(330);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(330);
								tbl.getColumnModel().getColumn(2).setMaxWidth(50);
								tbl.getColumnModel().getColumn(2).setMinWidth(50);
								tbl.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(50);
								tbl.getTableHeader().getColumnModel().getColumn(2).setMinWidth(50);
								tbl.getColumnModel().getColumn(0).setHeaderValue("Code");
								tbl.getColumnModel().getColumn(1).setHeaderValue("Description");
								tbl.getColumnModel().getColumn(2).setHeaderValue("Rate");

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
								tableOTRates.setQuery(DEFAULT_QUERY);
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
									String xcode = txtcode.getText();

									//row level locking
									LockHandler.startLock(DBConnect.SelectForUpdate("otrates",
											"*",
											"ot_code = '"+OT_Code+"' "+
											"AND otrates_code = '"+tbl.getValueAt(tbl.getSelectedRow(),0).toString()+"'"));

									String del_sql = DBConnect.delete("otrates","ot_code = '"+OT_Code+"' AND otrates_code = '"+DBConnect.clean(xcode)+"'");
									//System.out.println(del_sql);
									
									mainform.lblMessage.setText("");
									//int setDialog = JOptionPane.showConfirmDialog(container, "Are you sure ?",  "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
									int setDialog = Message.messageYesNo("Are you sure you want to delete\n" + OT_Code + ": "+ txtcode.getText()+ " ?");
									if (JOptionPane.YES_OPTION == setDialog) {
										try {
											// unlock mode and delete 
											LockHandler.removeLockAndDelete(del_sql);
											//tableOTRates.setInsert(del_sql);
											//																												
											//tableOTRates.setQuery(DEFAULT_QUERY);
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
										tableOTRates.setQuery(DEFAULT_QUERY);
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
					String xcode = txtcode.getText().trim();
					String desc = "";
					String ramt = "0";
					if (txtdescription.getText().trim().length() > 0) {
						desc = txtdescription.getText().trim().replaceAll(",","");
					}
					if (txtrates.getText().trim().length() > 0) {
						ramt = txtrates.getText().trim().replaceAll(",","");
					}
					int currentr = tbl.getSelectedRow();
					int currentc = tbl.getSelectedColumn();	
					if (ecaption == "Edit"){
						if (txtcode.getText().toString().length() >= 1){
							currentr = tbl.getSelectedRow();
							currentc = tbl.getSelectedColumn();	

							// start indicating lock
							LockHandler.initializeLock(container);

							//wait for lock indicator to invoke
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									try {
										//row level locking
										LockHandler.startLock(DBConnect.SelectForUpdate("otrates",
												"*",
												"ot_code = '"+OT_Code+"' "+
												"AND otrates_code = '"+tbl.getValueAt(tbl.getSelectedRow(),0).toString()+"'"));
										//refresh
										tableOTRates.setQuery(DEFAULT_QUERY);
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

										txtcode.setEnabled(false);
										txtdescription.grabFocus();

									}
									catch (SQLException e) {
										try {
											//refresh and remove lock mode
											tableOTRates.setQuery(DEFAULT_QUERY);
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
								DBConnect.Update("otrates",
										"otrates_desc = '"+desc+"', " +
										"otrates_rate = '"+ramt+"' ",
										"ot_code = '" +OT_Code+"' AND otrates_code = '"+xcode+"'");
							if(desc.length() == 0) {
								Message.messageError("Invalid input for description field.");
								txtdescription.grabFocus();
							}
							else if (Double.parseDouble(ramt) < 1) {
								Message.messageError("Invalid input for rate field.");
								txtrates.grabFocus();
							}
							else {
								//unlock mode then update
								LockHandler.removeLockAndUpdate(e_sql);

								tableOTRates.setQuery(DEFAULT_QUERY);
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

			setSize(477,350);
			setVisible(true);

		}//try
		catch ( ClassNotFoundException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("OTRatesMaint", "OTRatesMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch ( SQLException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("OTRatesMaint", "OTRatesMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch (Exception e) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("OTRatesMaint", "OTRatesMaint", traceWriter.toString());        
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

			if (column == 0 || column == 1) {
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
			tableOTRates.setQuery(DEFAULT_QUERY);
			tbl.getColumnModel().getColumn(0).setMaxWidth(0);
			tbl.getColumnModel().getColumn(0).setMinWidth(0);
			tbl.getColumnModel().getColumn(1).setMaxWidth(0);
			tbl.getColumnModel().getColumn(1).setMinWidth(0);
			tbl.getColumnModel().getColumn(2).setMaxWidth(0);
			tbl.getColumnModel().getColumn(2).setMinWidth(0);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(60);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(60);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(330);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(330);
			tbl.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(50);
			tbl.getTableHeader().getColumnModel().getColumn(2).setMinWidth(50);

			//set column header width (to avoid the ... sign)
			tbl.getColumnModel().getColumn(0).setHeaderValue("Code");
			tbl.getColumnModel().getColumn(1).setHeaderValue("Description");
			tbl.getColumnModel().getColumn(2).setHeaderValue("Rate");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void reload() {

	private void enableFields() {
		txtcode.setEnabled(true);
		txtdescription.setEnabled(true);
		txtrates.setEnabled(true);
	}//ENDof private void enableFields() {

	private void disableFields() {
		txtcode.setEnabled(false);
		txtdescription.setEnabled(false);
		txtrates.setEnabled(false);
	}//ENDof private void disableFields() {

	private void clearFields() {
		txtcode.setText("");
		txtdescription.setText("");
		txtrates.setText("");
	}//ENDof private void clearFields() {

	private void setFieldValue() {
		if (tbl.getSelectedRow() >= 0) {
			int getrow = tbl.getSelectedRow();
			txtcode.setText(tbl.getValueAt(getrow,0).toString());
			txtdescription.setText(tbl.getValueAt(getrow,1).toString());
			txtrates.setText(tbl.getValueAt(getrow,2).toString());
		}//ENDof if (tbl.getSelectedRow() >= 0) {
	}//ENDof private void getFieldValue() {
	
	private void setCustomer(String ot_code, String ot_desc) {
		lblOTdesc.setText(ot_code+": "+ot_desc);
		OT_Code = ot_code;
		DEFAULT_QUERY = 
			DBConnect.Select("otrates ",
							 "otrates_code, "+					
							 "otrates_desc, "+					
							 "FORMAT(otrates_rate,2) AS rates, "+
							 "ot_code ",
							 "ot_code = '"+ot_code+"'",
							 "otrates_code ASC");
	}//ENDof private void setCustomer(String custno) {

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
