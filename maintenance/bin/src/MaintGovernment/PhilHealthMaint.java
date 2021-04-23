package maintenance.bin.src.MaintGovernment;

/**
 * Author:  Norberto L. Silva
 * Date:    March 1, 2012
 * Company: Applied Ideas, Inc 
 * Program: PhilHealth Contribution Table Maintenance ==-> Payroll System
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
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.ResultTableModel.ResultSetTableModel;
import maintenance.bin.src.core.LockHandler;
import maintenance.bin.src.core.mainform;

public class PhilHealthMaint extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	private JDesktopPane desk = mainform.desktop;
	public static JInternalFrame iFrame = null;

	static String DEFAULT_QUERY = "";

	private ResultSetTableModel tablePhilHealth;

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
	private JFormattedTextField txtfrom_salary = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtto_salary = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtsal_base = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txtee_share = new JFormattedTextField(new DecimalFormat("#,##0.00"));
	private JFormattedTextField txter_share = new JFormattedTextField(new DecimalFormat("#,##0.00"));

	private	JLabel lblPHHTypeDesc = new JLabel();
	private static String PHHType_code = "";

	public PhilHealthMaint(String phhtype_code, String phhtype_desc, int w, int h) {
		//super ("Item Maintenance", resizable, close, maximize, iconifiable);
		super ("PhilHealth Contribution Table Maintenance", false, true, false, false);
		iFrame = this;
	    Dimension rootSize = Toolkit.getDefaultToolkit().getScreenSize();
		setPHHTable(phhtype_code,phhtype_desc);
		this.setName("PHHmaint");
		this.setResizable(false);
		this.setLocation(((rootSize.width + 170) - w) / 2,((rootSize.height - 20) - h) / 3);

		this.addInternalFrameListener(new InternalFrameListener(){
			public void internalFrameActivated(InternalFrameEvent arg0) {
				PhilHealthTypeMaint.subMain = 1;
				//mainform.tree.setEnabled(false);
			}
			public void internalFrameClosed(InternalFrameEvent arg0) {}
			public void internalFrameClosing(InternalFrameEvent arg0) {
				PhilHealthTypeMaint.subMain = 0;
				//mainform.tree.setEnabled(true);
			}
			public void internalFrameDeactivated(InternalFrameEvent arg0) {}
			public void internalFrameDeiconified(InternalFrameEvent arg0) {}
			public void internalFrameIconified(InternalFrameEvent arg0) {}
			public void internalFrameOpened(InternalFrameEvent arg0) {}
		});

		desk.setLayout(null);
		desk.add(this, new Integer(1));
		
		try {
			tablePhilHealth = new ResultSetTableModel( DEFAULT_QUERY );

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
			
			JLabel lbfrm_salary = new JLabel("From Salary");
			JLabel lblto_salary = new JLabel("To Salary");
			JLabel lblsal_base = new JLabel("Salary Base");
			JLabel lblshare = new JLabel("Share");
			JLabel lbleeshare = new JLabel("Employee");
			JLabel lblershare = new JLabel("Employer");

			tbl = new JTable(tablePhilHealth);

			TableCellRenderer renderer = new TestRenderer();
			tbl.setDefaultRenderer(Object.class, renderer);
			scrollPane = new JScrollPane(tbl);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			tbl.setSelectionMode(0);

			tablePhilHealth.resultSet = tablePhilHealth.statement.executeQuery(DEFAULT_QUERY);
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
			scrollPane.setPreferredSize(new Dimension(497,170));
			jpan.setBounds(10,20,497,180);

			lblPHHTypeDesc.setBounds(10,3,400,20);
			lbfrm_salary.setBounds(28,100,100,280);
			lblto_salary.setBounds(240,100,100,280);
			lblsal_base.setBounds(28,130,100,280);
			lblshare.setBounds(28,190,100,280);
			lbleeshare.setBounds(133,160,100,280);
			lblershare.setBounds(239,160,100,280);

			txtfrom_salary.setBounds(120,230,85,20);
			txtfrom_salary.setDisabledTextColor(new Color(139,113,113));
			txtfrom_salary.setHorizontalAlignment(JTextField.TRAILING);
			txtto_salary.setBounds(315,230,85,20);
			txtto_salary.setDisabledTextColor(new Color(139,113,113));
			txtto_salary.setHorizontalAlignment(JTextField.TRAILING);
			txtsal_base.setBounds(120,260,80,20);
			txtsal_base.setDisabledTextColor(new Color(139,113,113));
			txtsal_base.setHorizontalAlignment(JTextField.TRAILING);
			txtee_share.setBounds(120,320,80,20);
			txtee_share.setDisabledTextColor(new Color(139,113,113));
			txtee_share.setHorizontalAlignment(JTextField.TRAILING);
			txter_share.setBounds(225,320,80,20);
			txter_share.setDisabledTextColor(new Color(139,113,113));
			txter_share.setHorizontalAlignment(JTextField.TRAILING);
			
			jbtnadd.setBounds(80,200,80,22);
			jbtnedit.setBounds(220,200,80,22);
			jbtndelete.setBounds(360,200,80,22);

			jpan.add(scrollPane, BorderLayout.CENTER);

			if (tbl.getRowCount() >= 1){
				tbl.setRowSelectionInterval(0,0);
				tbl.setColumnSelectionInterval(0,0);
				setFieldValue();
			}

			//add objects to container
			container.add(jpan);
			container.add(lblPHHTypeDesc);
			container.add(lbfrm_salary);
			container.add(txtfrom_salary);
			container.add(lblto_salary);
			container.add(txtto_salary);
			container.add(lblsal_base);
			container.add(txtsal_base);
			container.add(lblshare);
			container.add(lbleeshare);
			container.add(txter_share);
			container.add(lblershare);
			container.add(txtee_share);
			container.add(jbtnadd);
			container.add(jbtnedit);
			container.add(jbtndelete);        

			//Disabled text fields
			disableFields();

			txtfrom_salary.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) e.consume();
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtto_salary.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) e.consume();
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtsal_base.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) e.consume();
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txtee_share.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int eventKey = e.getKeyChar();
					char c = e.getKeyChar();
					if (eventKey != 8 && eventKey != 46) {
						if (!Character.isDigit(c)) e.consume();
					}
				}//ENDof public void keyTyped(KeyEvent e) {
			});
			
			txter_share.addKeyListener(new KeyAdapter() {
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
					String frm_salary = "0";
					String too_salary = "0";
					String sal_base = "0";
					String ee_share = "0";
					String er_share = "0";
					if (txtfrom_salary.getText().trim().length() > 0){
						frm_salary = txtfrom_salary.getText().trim().replaceAll(",","");
					}
					if (txtto_salary.getText().trim().length() > 0){
						too_salary = txtto_salary.getText().trim().replaceAll(",","");
					}
					if (txtsal_base.getText().trim().length() > 0){
						sal_base = txtsal_base.getText().trim().replaceAll(",","");
					}
					if (txtee_share.getText().trim().length() > 0){
						ee_share = txtee_share.getText().trim().replaceAll(",","");
					}
					if (txter_share.getText().trim().length() > 0){
						er_share = txter_share.getText().trim().replaceAll(",","");
					}

					if (caption == "Add") {
						ListSelectionModel selectionModel = tbl.getSelectionModel();
						selectionModel.clearSelection();
						tbl.setEnabled(false);

						enableFields();

						clearFields();
						txtfrom_salary.grabFocus();

						jbtnadd.setText("Save");
						jbtnadd.setMnemonic('S');
						jbtndelete.setText("Cancel");
						jbtndelete.setMnemonic('C');
						jbtnedit.setEnabled(false);
					}
					else if (caption == "Save") {
						int iColumn = 0;
						String save_sql = DBConnect.Insert("philhealth",
								"phhtype_code,from_salary,to_salary,salary_base,"+
								"employee_share,employer_share",
								"'"+PHHType_code+"', " +
								"'"+frm_salary+"', " +
								"'"+too_salary+"', " +
								"'"+sal_base+"', " +
								"'"+ee_share+"', " +
								"'"+er_share+"' "
						);

						tbl.setEnabled(false);
						try {
							if (Double.parseDouble(too_salary) < Double.parseDouble(frm_salary)) {
								Message.messageError("Invalid input for To Salary field.");
								txtto_salary.grabFocus();
							}
							else{
								tablePhilHealth.setInsert(save_sql);

								tablePhilHealth.setQuery(DEFAULT_QUERY);
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
								tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(150);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(150);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(90);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(90);
								tbl.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(80);
								tbl.getTableHeader().getColumnModel().getColumn(2).setMinWidth(80);
								tbl.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(80);
								tbl.getTableHeader().getColumnModel().getColumn(3).setMinWidth(80);
								tbl.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(80);
								tbl.getTableHeader().getColumnModel().getColumn(4).setMinWidth(80);
								tbl.getColumnModel().getColumn(0).setHeaderValue("Salary Range");
								tbl.getColumnModel().getColumn(1).setHeaderValue("Salary Base");
								tbl.getColumnModel().getColumn(2).setHeaderValue("EE Share");
								tbl.getColumnModel().getColumn(3).setHeaderValue("ER Share");
								tbl.getColumnModel().getColumn(4).setHeaderValue("Total");

								curRecord = txtfrom_salary.getText().trim().replaceAll(",","");
								
								clearFields();

								Message.messageInfo(Message.messageAdd);								
								//tbl.scrollRectToVisible(tbl.getCellRect(tbl.getRowCount()-1, tbl.getColumnCount(), true));
								tbl.scrollRectToVisible(tbl.getCellRect(setCurRecord(), tbl.getColumnCount(), true));
								txtfrom_salary.grabFocus();
							}
						}
						catch (SQLException se) {
							try {
								tablePhilHealth.setQuery(DEFAULT_QUERY);
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
									String xcode = txtfrom_salary.getText().trim().replaceAll(",","");

									//row level locking
									LockHandler.startLock(DBConnect.SelectForUpdate("philhealth ",
											"*",
											"phhtype_code = '"+PHHType_code+"' "+
											"AND from_salary = '"+tbl.getValueAt(tbl.getSelectedRow(),7).toString()+"'"));

									String del_sql = DBConnect.delete("philhealth","phhtype_code = '"+PHHType_code+"' AND from_salary = '"+xcode+"'");
									//System.out.println(del_sql);
									
									mainform.lblMessage.setText("");
									//int setDialog = JOptionPane.showConfirmDialog(container, "Are you sure ?",  "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
									int setDialog = Message.messageYesNo("Are you sure you want to delete\n" + tbl.getValueAt(tbl.getSelectedRow(),0).toString()+ " ?");
									if (JOptionPane.YES_OPTION == setDialog) {
										try {
											// unlock mode and delete 
											LockHandler.removeLockAndDelete(del_sql);
											//tablePhilHealth.setInsert(del_sql);
											//																												
											//tablePhilHealth.setQuery(DEFAULT_QUERY);
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
										tablePhilHealth.setQuery(DEFAULT_QUERY);
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
					String xcode = txtfrom_salary.getText().trim().replaceAll(",","");
					String too_salary = "0";
					String sal_base = "0";
					String ee_share = "0";
					String er_share = "0";
					if (txtto_salary.getText().trim().length() > 0){
						too_salary = txtto_salary.getText().trim().replaceAll(",","");
					}
					if (txtsal_base.getText().trim().length() > 0){
						sal_base = txtsal_base.getText().trim().replaceAll(",","");
					}
					if (txtee_share.getText().trim().length() > 0){
						ee_share = txtee_share.getText().trim().replaceAll(",","");
					}
					if (txter_share.getText().trim().length() > 0){
						er_share = txter_share.getText().trim().replaceAll(",","");
					}
					int currentr = tbl.getSelectedRow();
					int currentc = tbl.getSelectedColumn();	
					if (ecaption == "Edit"){
						currentr = tbl.getSelectedRow();
						currentc = tbl.getSelectedColumn();	

						// start indicating lock
						LockHandler.initializeLock(container);

						//wait for lock indicator to invoke
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								try {
									//row level locking
									LockHandler.startLock(DBConnect.SelectForUpdate("philhealth ",
											"*",
											"phhtype_code = '"+PHHType_code+"' "+
											"AND from_salary = '"+tbl.getValueAt(tbl.getSelectedRow(),7).toString()+"'"));
									//refresh
									tablePhilHealth.setQuery(DEFAULT_QUERY);
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

									txtfrom_salary.setEnabled(false);
									txtto_salary.grabFocus();
								}
								catch (SQLException e) {
									try {
										//refresh and remove lock mode
										tablePhilHealth.setQuery(DEFAULT_QUERY);
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
					else if (ecaption == "Update") {
						try {
							String e_sql = 
								DBConnect.Update("philhealth ",
										"to_salary = '"+too_salary+"', " +
										"salary_base = '"+sal_base+"', " +
										"employee_share = '"+ee_share+"', " +
										"employer_share = '"+er_share+"' ",
										"phhtype_code = '"+PHHType_code+"' AND from_salary = '"+xcode+"'");
							//unlock mode then update
							LockHandler.removeLockAndUpdate(e_sql);

							tablePhilHealth.setQuery(DEFAULT_QUERY);
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

			setSize(519,385);
			setVisible(true);

		}//try
		catch ( ClassNotFoundException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("PhilHealthMaint", "PhilHealthMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch ( SQLException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("PhilHealthMaint", "PhilHealthMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch (Exception e) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("PhilHealthMaint", "PhilHealthMaint", traceWriter.toString());        
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
			tablePhilHealth.setQuery(DEFAULT_QUERY);
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
			tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(150);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(150);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(90);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(90);
			tbl.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(80);
			tbl.getTableHeader().getColumnModel().getColumn(2).setMinWidth(80);
			tbl.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(80);
			tbl.getTableHeader().getColumnModel().getColumn(3).setMinWidth(80);
			tbl.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(80);
			tbl.getTableHeader().getColumnModel().getColumn(4).setMinWidth(80);

			//set column header width (to avoid the ... sign)
			tbl.getColumnModel().getColumn(0).setHeaderValue("Salary Range");
			tbl.getColumnModel().getColumn(1).setHeaderValue("Salary Base");
			tbl.getColumnModel().getColumn(2).setHeaderValue("EE Share");
			tbl.getColumnModel().getColumn(3).setHeaderValue("ER Share");
			tbl.getColumnModel().getColumn(4).setHeaderValue("Total");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void reload() {

	private void enableFields() {
		txtfrom_salary.setEnabled(true);
		txtto_salary.setEnabled(true);
		txtsal_base.setEnabled(true);
		txtee_share.setEnabled(true);
		txter_share.setEnabled(true);
	}//ENDof private void enableFields() {

	private void disableFields() {
		txtfrom_salary.setEnabled(false);
		txtto_salary.setEnabled(false);
		txtsal_base.setEnabled(false);
		txtee_share.setEnabled(false);
		txter_share.setEnabled(false);
	}//ENDof private void disableFields() {

	private void clearFields() {
		txtfrom_salary.setText("");
		txtto_salary.setText("");
		txtsal_base.setText("");
		txtee_share.setText("");
		txter_share.setText("");
	}//ENDof private void clearFields() {

	private void setFieldValue() {
		if (tbl.getSelectedRow() >= 0) {
			int getrow = tbl.getSelectedRow();
			txtfrom_salary.setText(tbl.getValueAt(getrow,5).toString());
			txtto_salary.setText(tbl.getValueAt(getrow,6).toString());
			txtsal_base.setText(tbl.getValueAt(getrow,1).toString());
			txtee_share.setText(tbl.getValueAt(getrow,2).toString());
			txter_share.setText(tbl.getValueAt(getrow,3).toString());
		}//ENDof if (tbl.getSelectedRow() >= 0) {
	}//ENDof private void getFieldValue() {
	
	private void setPHHTable(String phhtype_code, String phhtype_desc) {
		lblPHHTypeDesc.setText(phhtype_code+": "+phhtype_desc);
		PHHType_code = phhtype_code;
		DEFAULT_QUERY = DBConnect.Select("philhealth ",
				 "CONCAT(FORMAT(from_salary,2), ' - ', FORMAT(to_salary,2)) AS rangecomp, "+					
				 "FORMAT(salary_base,2) AS sal_base, "+
				 "FORMAT(employee_share,2) AS ee_share, "+
				 "FORMAT(employer_share,2) AS er_share, "+
				 "FORMAT((employer_share + employee_share),2) AS philtotal, "+
				 "FORMAT(from_salary,2) AS frm_salary, "+
				 "FORMAT(to_salary,2) AS too_salary, "+
				 "from_salary",
				 "phhtype_code = '"+PHHType_code+"'",
				 "from_salary ASC");
	}//ENDof private void setPHHTable(String phhtype_code, String phhtype_desc) {

	private int setCurRecord() {
		int i = 0;
		if (tbl.getRowCount() > 0) {
			if (curRecord.trim().length() > 0) {
				for (i=0; i<=tbl.getRowCount()-1; i++) {
					if (tbl.getValueAt(i,7).toString().equalsIgnoreCase(curRecord) == true) {
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
