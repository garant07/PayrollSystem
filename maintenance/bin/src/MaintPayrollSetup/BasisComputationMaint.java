package maintenance.bin.src.MaintPayrollSetup;

/**
 * Author:  Norberto L. Silva
 * Date:    March 14, 2012
 * Company: Applied Ideas, Inc 
 * Program: Basis of Computation Maintenance ==-> Payroll System
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
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

public class BasisComputationMaint extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	
	private JDesktopPane desk = mainform.desktop;
	public static JInternalFrame iFrame = null;

	static String DEFAULT_QUERY = "";
	
	private ResultSetTableModel tableCompensation;

	Container container;
	Message msg = new Message();
	JTable tbl;

	//variable current cell selected
	private int tempR;
	private int tempC;
	String curRecord = "";

	//variable for buttons
	private JButton jbtnadd;
	private JButton jbtndelete;

	//variable for user entry
	JScrollPane scrollPane;
	private JComboBox jcbCompensation = new JComboBox();

	private	JLabel BComType_Desc = new JLabel();
	private static String BComType_Code = "";

	public BasisComputationMaint(String bcomtype_code, String bcomtype_desc, int w, int h) {
		//super ("Item Maintenance", resizable, close, maximize, iconifiable);
		super ("Compensation Maintenance", false, true, false, false);
		iFrame = this;
	    Dimension rootSize = Toolkit.getDefaultToolkit().getScreenSize();
		setCompensation(bcomtype_code,bcomtype_desc);
		this.setName("compensationmaint");
		this.setResizable(false);
		this.setLocation(((rootSize.width + 100) - w) / 2,((rootSize.height + 78) - h) / 3);

		this.addInternalFrameListener(new InternalFrameListener(){
			public void internalFrameActivated(InternalFrameEvent arg0) {
				BasisComputationTypeMaint.subMain = 1;
			}
			public void internalFrameClosed(InternalFrameEvent arg0) {}
			public void internalFrameClosing(InternalFrameEvent arg0) {
				BasisComputationTypeMaint.subMain = 0;
			}
			public void internalFrameDeactivated(InternalFrameEvent arg0) {}
			public void internalFrameDeiconified(InternalFrameEvent arg0) {}
			public void internalFrameIconified(InternalFrameEvent arg0) {}
			public void internalFrameOpened(InternalFrameEvent arg0) {}
		});

		desk.setLayout(null);
		desk.add(this, new Integer(1));
		
		try {
			tableCompensation = new ResultSetTableModel( DEFAULT_QUERY );

			//this.setAlwaysOnTop(true);
			container = this.getContentPane();
			container.setLayout(null);

			//Set frame objects
			JPanel jpan = new JPanel();
			jbtnadd = new JButton("Add");
			jbtnadd.setMnemonic('A');
			jbtndelete = new JButton("Delete");
			jbtndelete.setMnemonic('D');
			
			JLabel lblCompensation = new JLabel("Compensation");

			tbl = new JTable(tableCompensation);

			TableCellRenderer renderer = new TestRenderer();
			tbl.setDefaultRenderer(Object.class, renderer);
			scrollPane = new JScrollPane(tbl);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			tbl.setSelectionMode(0);

			String sql = "SELECT * FROM compensation ORDER By comps_desc";
			tableCompensation.resultSet = tableCompensation.statement.executeQuery(sql);

			jcbCompensation.setFont(new Font("",Font.PLAIN, 12));
			jcbCompensation.setBackground(Color.white);
			jcbCompensation.addItem("Choose");
			while (tableCompensation.resultSet.next()){
				jcbCompensation.addItem(Formatter.formatString(100,' ','-',tableCompensation.resultSet.getString("comps_desc"))+"-"+tableCompensation.resultSet.getString("comps_code"));
			}//while (tableCompensation.resultSet.next())

			tableCompensation.resultSet = tableCompensation.statement.executeQuery(DEFAULT_QUERY);
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
			scrollPane.setPreferredSize(new Dimension(577,170));
			jpan.setBounds(10,20,577,180);

			BComType_Desc.setBounds(10,3,400,20);
			lblCompensation.setBounds(28,230,100,20);

			jcbCompensation.setBounds(120,230,300,20);
			
			jbtnadd.setBounds(120,200,80,22);
			jbtndelete.setBounds(400,200,80,22);

			jpan.add(scrollPane, BorderLayout.CENTER);

			if (tbl.getRowCount() >= 1){
				tbl.setRowSelectionInterval(0,0);
				tbl.setColumnSelectionInterval(0,0);
				setFieldValue();
			}

			//add objects to container
			container.add(jpan);
			container.add(BComType_Desc);
			container.add(lblCompensation);
			container.add(jcbCompensation);
			container.add(jbtnadd);
			container.add(jbtndelete);        

			//Disabled text fields
			disableFields();

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
						jcbCompensation.grabFocus();

						jbtnadd.setText("Save");
						jbtnadd.setMnemonic('S');
						jbtndelete.setText("Cancel");
						jbtndelete.setMnemonic('C');
					}
					else if (caption == "Save") {
						String xcode = "";
						if (jcbCompensation.getSelectedIndex() > 0) {
							xcode = jcbCompensation.getSelectedItem().toString().substring(101);
						}
						int iColumn = 0;
						String save_sql = DBConnect.Insert("basiscomputation",
								"bcomp_code,comps_code",
								"'"+BComType_Code+"', " +
								"'"+xcode+"' "
						);

						tbl.setEnabled(false);
						try {
							if (jcbCompensation.getSelectedIndex() == 0) {
								Message.messageError("Invalid input for Compensation field.");
								jcbCompensation.grabFocus();
							}
							else{
								tableCompensation.setInsert(save_sql);

								tableCompensation.setQuery(DEFAULT_QUERY);
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
								tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(230);
								tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(230);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(90);
								tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(90);
								tbl.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(50);
								tbl.getTableHeader().getColumnModel().getColumn(2).setMinWidth(50);
								tbl.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(80);
								tbl.getTableHeader().getColumnModel().getColumn(3).setMinWidth(80);
								tbl.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(110);
								tbl.getTableHeader().getColumnModel().getColumn(4).setMinWidth(110);
								tbl.getColumnModel().getColumn(0).setHeaderValue("Compensation");
								tbl.getColumnModel().getColumn(1).setHeaderValue("Payroll Type");
								tbl.getColumnModel().getColumn(2).setHeaderValue("Taxable");
								tbl.getColumnModel().getColumn(3).setHeaderValue("Process Type");
								tbl.getColumnModel().getColumn(4).setHeaderValue("Frequency");

								curRecord = jcbCompensation.getSelectedItem().toString().substring(101);
								
								clearFields();

								Message.messageInfo(Message.messageAdd);								
								//tbl.scrollRectToVisible(tbl.getCellRect(tbl.getRowCount()-1, tbl.getColumnCount(), true));
								tbl.scrollRectToVisible(tbl.getCellRect(setCurRecord(), tbl.getColumnCount(), true));
								jcbCompensation.grabFocus();
							}
						}
						catch (SQLException se) {
							try {
								tableCompensation.setQuery(DEFAULT_QUERY);
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
									String xcode = jcbCompensation.getSelectedItem().toString().substring(101);

									//row level locking
									LockHandler.startLock(DBConnect.SelectForUpdate("basiscomputation",
											"*",
											"bcomp_code = '"+BComType_Code+"' "+
											"AND comps_code = '"+tbl.getValueAt(tbl.getSelectedRow(),5).toString()+"'"));

									String del_sql = DBConnect.delete("basiscomputation","bcomp_code = '"+BComType_Code+"' AND comps_code = '"+xcode+"'");
									//System.out.println(del_sql);
									
									mainform.lblMessage.setText("");
									//int setDialog = JOptionPane.showConfirmDialog(container, "Are you sure ?",  "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
									int setDialog = Message.messageYesNo("Are you sure you want to delete\n" + tbl.getValueAt(tbl.getSelectedRow(),0).toString()+ " ?");
									if (JOptionPane.YES_OPTION == setDialog) {
										try {
											// unlock mode and delete 
											LockHandler.removeLockAndDelete(del_sql);
											//tableCompensation.setInsert(del_sql);
											//																												
											//tableCompensation.setQuery(DEFAULT_QUERY);
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
										tableCompensation.setQuery(DEFAULT_QUERY);
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

			setSize(599,300);
			setVisible(true);

		}//try
		catch ( ClassNotFoundException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("BasisComputationMaint", "BasisComputationMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch ( SQLException e ) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("BasisComputationMaint", "BasisComputationMaint", traceWriter.toString());        
			dispose();
		} // end catch
		catch (Exception e) {
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("BasisComputationMaint", "BasisComputationMaint", traceWriter.toString());        
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
		jbtnadd.setText("Add");
		jbtnadd.setMnemonic('A');
		jbtndelete.setText("Delete");
		jbtndelete.setMnemonic('D');
		jbtnadd.setEnabled(true);
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
			tableCompensation.setQuery(DEFAULT_QUERY);
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
			tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(230);
			tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(230);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(90);
			tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(90);
			tbl.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(50);
			tbl.getTableHeader().getColumnModel().getColumn(2).setMinWidth(50);
			tbl.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(80);
			tbl.getTableHeader().getColumnModel().getColumn(3).setMinWidth(80);
			tbl.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(110);
			tbl.getTableHeader().getColumnModel().getColumn(4).setMinWidth(110);

			//set column header width (to avoid the ... sign)
			tbl.getColumnModel().getColumn(0).setHeaderValue("Compensation");
			tbl.getColumnModel().getColumn(1).setHeaderValue("Payroll Type");
			tbl.getColumnModel().getColumn(2).setHeaderValue("Taxable");
			tbl.getColumnModel().getColumn(3).setHeaderValue("Process Type");
			tbl.getColumnModel().getColumn(4).setHeaderValue("Frequency");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//ENDof private void reload() {

	private void enableFields() {
		jcbCompensation.setEnabled(true);
	}//ENDof private void enableFields() {

	private void disableFields() {
		jcbCompensation.setEnabled(false);
	}//ENDof private void disableFields() {

	private void clearFields() {
		jcbCompensation.setSelectedIndex(0);
	}//ENDof private void clearFields() {

	private void setFieldValue() {
		if (tbl.getSelectedRow() >= 0) {
			int getrow = tbl.getSelectedRow();
			jcbCompensation.setSelectedIndex(0);
			for (int ii=0; ii<=jcbCompensation.getItemCount()-1; ii++) {
				if (jcbCompensation.getItemAt(ii).toString().length() > 50) {
					if (tbl.getValueAt(getrow,5).toString().equalsIgnoreCase(jcbCompensation.getItemAt(ii).toString().substring(101)) == true) {
						jcbCompensation.setSelectedIndex(ii);
					}
				}
			}//ENDof for (int ii=0; ii<=jcbFrequency.getItemCount()-1; ii++) {
		}//ENDof if (tbl.getSelectedRow() >= 0) {
	}//ENDof private void getFieldValue() {
	
	private void setCompensation(String bcomtype_code, String bcomtype_desc) {
		BComType_Desc.setText(bcomtype_code+": "+bcomtype_desc);
		BComType_Code = bcomtype_code;
		DEFAULT_QUERY = DBConnect.Select("basiscomputation as b, compensation as c, payrolltype as p, frequency as f ",
					 "c.comps_desc, "+					
					 "p.ptype_desc, "+
					 "IF(c.comps_taxable = 'Y','Yes','No') as taxable, "+
					 "IF(c.comps_proctype = 'C','Computed','Input') as proctype, "+
					 "f.freq_desc as freq, "+
					 "b.comps_code AS compen ",
					 "bcomp_code = '"+BComType_Code+"' AND "+
					 "c.comps_code = b.comps_code AND "+
					 "p.ptype_code = c.ptype_code AND "+
					 "f.freq_code = c.freq_code",
					 "comps_desc");
	}//ENDof private void setCompensation(String ssstype_code, String ssstype_desc) {

	private int setCurRecord() {
		int i = 0;
		if (tbl.getRowCount() > 0) {
			if (curRecord.trim().length() > 0) {
				for (i=0; i<=tbl.getRowCount()-1; i++) {
					if (tbl.getValueAt(i,5).toString().equalsIgnoreCase(curRecord) == true) {
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
