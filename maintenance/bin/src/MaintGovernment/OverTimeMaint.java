package maintenance.bin.src.MaintGovernment;

/**
 * Author:  Norberto L. Silva
 * Date:    February 28, 2012
 * Company: Applied Ideas, Inc 
 * Program: Over Time Maintenance ==-> Payroll System
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
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

import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
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


public class OverTimeMaint extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	static final String DEFAULT_QUERY = DBConnect.Select("overtime",
														 "ot_code, ot_desc",
														 null,
														 "ot_code ASC");
	public static int subMain = 0;

	public static JInternalFrame iFrame = null;
	private ResultSetTableModel tableOverTime;
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
	private JButton jbtnrates;

	//varible for data entry
	private MyField txtcode = ((MyField)new MyField(true,4));
	private MyField txtdescription = new MyField(true,25);

	public OverTimeMaint(){
		super ("Over Time Maintenance", false, true, false, false);
		this.setName("overtime");
		this.setResizable(false);

		this.addInternalFrameListener(new InternalFrameListener(){
			public void internalFrameActivated(InternalFrameEvent arg0) {
				if (subMain == 1) {
					try {
						OTRatesMaintOLD.iFrame.setSelected(true);
					}
					catch (PropertyVetoException e) {}
				}
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

		try{

			tableOverTime  = new ResultSetTableModel(  DEFAULT_QUERY );

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
			jbtnrates = new JButton("Rates");
			jbtnrates.setMnemonic('R');
			
			JLabel lblcode = new JLabel("Code");
			final JLabel lbldescription = new JLabel("Description");
			tbl = new JTable(tableOverTime);
			final JScrollPane scrollPane = new JScrollPane(tbl);
			txtcode.setDisabledTextColor(new Color(139,113,113));
			txtdescription.setDisabledTextColor(new Color(139,113,113));
			final JTextField txtsearch = new JTextField();

			txtcode.setCapital(true);

			tbl.getTableHeader().setResizingAllowed(false);
			TableCellRenderer renderer = new pRenderer();
			tbl.setDefaultRenderer(Object.class, renderer);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			tbl.getTableHeader().setReorderingAllowed(false);
			tableOverTime.resultSet = tableOverTime.statement.executeQuery(DEFAULT_QUERY);
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

			lblcode.setBounds(17,70,100,280);
			lbldescription.setBounds(17,105,100,270);

			txtcode.setBounds(119,200,80,20);
			txtdescription.setBounds(119,230,250,20);

			jbtnadd.setBounds(30,170,80,22);
			jbtnedit.setBounds(130,170,80,22);
			jbtndelete.setBounds(230,170,80,22);
			jbtnrates.setBounds(330,170,80,22);
			
			txtsearch.setBounds(18,3,250,18);
			txtsearch.setBackground(new Color(255,255,225));
			txtsearch.setVisible(false);
			txtsearch.setBorder(BorderFactory.createEmptyBorder());

			jpan.add(scrollPane, BorderLayout.CENTER);


			if	(tbl.getRowCount() >= 1){
				tbl.setRowSelectionInterval(0,0);
				tbl.setColumnSelectionInterval(0,0);
				int getrow = tbl.getSelectedRow();
				txtcode.setText(tbl.getValueAt(getrow,0).toString());
				txtdescription.setText(tbl.getValueAt(getrow,1).toString());
			}

			//add objects to container
			container.add(jbtndelete);
			container.add(jpan);
			container.add(txtcode);
			container.add(jbtnadd);
			container.add(jbtnedit);
			container.add(jbtnrates);
			container.add(lblcode);
			container.add(lbldescription);
			container.add(txtdescription);
			container.add(txtsearch);

			//Disabled text fields
			txtcode.setEnabled(false);
			txtdescription.setEnabled(false);


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
						txtcode.setEnabled(true);
						txtdescription.setEnabled(true);
						txtsearch.setVisible(false);
						txtcode.setText("");
						txtdescription.setText("");
						jbtnadd.setText("Save");
						jbtnadd.setMnemonic('S');
						jbtndelete.setText("Cancel");
						jbtndelete.setMnemonic('C');
						jbtnedit.setEnabled(false);
						jbtnrates.setEnabled(false);
						txtcode.grabFocus();
					}
					else if (caption == "Save"){
						String save_sql = DBConnect.Insert("overtime",
								"ot_code,ot_desc",
								"'"+DBConnect.clean(xcode)+"','"+DBConnect.clean(xdesc)+"'");
						//String save_sql = "Insert into overtime (overtime.code,overtime.desc,overtime.authority) values ('"+xcode+"','"+xdesc+"','"+xauth+"')";
						tbl.setEnabled(false);

						try{
							if (txtcode.getText().length() == 0){   
								Message.messageError("Invalid input for code");
								txtcode.grabFocus();
							}
							else if (txtdescription.getText().length() == 0){   
								Message.messageError("Invalid input for description");
								txtdescription.grabFocus();
							}
							else{
								tableOverTime.setInsert(save_sql);

								tableOverTime.setQuery(DEFAULT_QUERY);
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
								
								txtcode.setText(null);
								txtdescription.setText(null);

								Message.messageInfo(Message.messageAdd);
								//tbl.scrollRectToVisible(tbl.getCellRect(tbl.getRowCount()-1, tbl.getColumnCount(), true));
								tbl.scrollRectToVisible(tbl.getCellRect(setCurRecord(), tbl.getColumnCount(), true));
								txtcode.grabFocus();
							}
						}
						catch(SQLException se){
							try{
								tableOverTime.setQuery(DEFAULT_QUERY);
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
									("overtime","*","ot_code = '" + DBConnect.clean(code) +"'");
									LockHandler.startLock(lockCmd);

									String del_sql = DBConnect.delete("overtime",
											"ot_code = '"+DBConnect.clean(xcode)+"'");
									String del_sql1 = "DELETE FROM otrates WHERE ot_code = '"+xcode+"'";

									txtsearch.setVisible(false);	
									if (txtcode.getText().length() != 0) {
										int setDialog = Message.messageYesNo("Are You Sure you want to delete\n" +txtdescription.getText()+" ?");
										if(setDialog == JOptionPane.YES_OPTION){
											try {
												LockHandler.removeLockAndDelete(del_sql);
												LockHandler.removeLockAndDelete(del_sql1);

												tableOverTime.setQuery(DEFAULT_QUERY);
												int iColumn = 0;

												//if (xcode != "Code - Rank"){
												ListSelectionModel selectionModel = tbl.getSelectionModel();
												selectionModel.setSelectionInterval(0, 0);
												//}//end if
												if (tbl.getRowCount() >= 1) {
													int getrow = tbl.getSelectedRow();
													txtcode.setText(tbl.getValueAt(getrow,0).toString());
													txtdescription.setText(tbl.getValueAt(getrow,1).toString());
												}else{
													txtcode.setText(null);
													txtdescription.setText(null);
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
										tableOverTime.setQuery(DEFAULT_QUERY);
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
										("overtime","*","ot_code = '" + DBConnect.clean(code) +"'");
										LockHandler.startLock(lockCmd);

										//refresh
										tableOverTime.setQuery(DEFAULT_QUERY);
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
										jbtnrates.setEnabled(false);
										txtdescription.setEnabled(true);
										txtsearch.setVisible(false);
										txtdescription.grabFocus();


									} catch (SQLException e) {
										try {
											//refresh mode
											tableOverTime.setQuery(DEFAULT_QUERY);
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
								String e_sql = DBConnect.Update("overtime",
										"ot_desc='"+DBConnect.clean(udesc)+"'",
										"ot_code= '"+DBConnect.clean(ucode)+"'");

								if (txtdescription.getText().length() == 0){   
									Message.messageError("Invalid input for description");
									txtdescription.grabFocus();
								}
								else{
									LockHandler.removeLockAndUpdate(e_sql);

									tableOverTime.setQuery(DEFAULT_QUERY);
									tbl.setEnabled(true);
									jbtnadd.setText("Add");
									jbtnadd.setMnemonic('A');
									jbtndelete.setText("Delete");
									jbtndelete.setMnemonic('D');
									jbtnedit.setText("Edit");
									jbtnedit.setMnemonic('E');
									jbtnadd.setEnabled(true);
									jbtnrates.setEnabled(true);
									ListSelectionModel selectionModel = tbl.getSelectionModel();
									selectionModel.setSelectionInterval(0, 0);
									reload();
									txtcode.setText(tbl.getValueAt(currentr,0).toString());
									txtdescription.setText(tbl.getValueAt(currentr,1).toString());
									tbl.getSelectedRow();
									tbl.grabFocus();
									txtcode.setEnabled(false);
									txtdescription.setEnabled(false);
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

			//rates button listener
			jbtnrates.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (tbl.getRowCount() >= 1) {
						if (tbl.getSelectedRow() >= 0) {
							int getrow = tbl.getSelectedRow();
							new OTRatesMaintOLD(tbl.getValueAt(getrow,0).toString(),tbl.getValueAt(getrow,1).toString(),Width(),Height());
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
			createINI.create("OverTimeMaint", "OverTimeMaint", traceWriter.toString());      
			dispose();
		}
		catch ( SQLException sqlE )
		{
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			sqlE.printStackTrace(printWriter);
			createINI.create("OverTimeMaint", "OverTimeMaint", traceWriter.toString());      
			dispose();
		} 
		catch(Exception e){
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("OverTimeMaint", "OverTimeMaint", traceWriter.toString());      
			dispose();
		}


		//this.setBounds(240,200,0,0);
		setSize(445,305);
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
		jbtnrates.setEnabled(true);
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
	}

	private void setValue() {
		int getrow = tbl.getSelectedRow();
		txtcode.setText(tbl.getValueAt(getrow,0).toString());
		txtdescription.setText(tbl.getValueAt(getrow,1).toString());
	}

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
