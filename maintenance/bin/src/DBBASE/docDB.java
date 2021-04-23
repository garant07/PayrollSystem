package maintenance.bin.src.DBBASE;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import maintenance.bin.src.DBConn.DBConnect;
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.MyField;
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.ResultTableModel.ResultSetTableModel;
import maintenance.bin.src.core.mainform;

public class docDB extends JInternalFrame {

	private JPanel jContentPane = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	private JButton jButton2 = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private static MyField jTextField = null;
	private static MyField jTextField1 = null;
	private static JComboBox jComboBox = null;
	public static String dcode = "";
	public static String dDesc = "";
	public static String screen = "";
	
	static String xRecord;
	public static ResultSet rs1 = null;
	//variables
	private String status;
	
	//variables for dbase connection
	Statement stmt = null;
	ResultSet rs = null;
	
	//Use for query
	private String query = "";
	private ResultSetTableModel rsDocDB = null;
	private JLabel jLabel3 = null;
	private static MyField jTextField2 = null;
	private JLabel jLabel4 = null;
	private static MyField jTextField3 = null;
	
	Date now = new Date();
	SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat tformat = new SimpleDateFormat("HH:mm:ss");
	
	public docDB() {
		super();
		
		try{
			initialize();
		}catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
		}
		
	}

	private void initialize() throws ArrayIndexOutOfBoundsException{
		this.setSize(391, 353);
		this.setClosable(true);
		this.setTitle(dDesc.substring(0, dDesc.length()-1));
		this.setContentPane(getJContentPane());
		
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
		
		if	(screen.trim().equalsIgnoreCase("N") == true){
			setScreen();
		}
		this.setVisible(true);
	}

	private JPanel getJContentPane() throws ArrayIndexOutOfBoundsException{
		if (jContentPane == null) {
			try {
				jLabel4 = new JLabel();
				jLabel4.setText("Balance");
				jLabel4.setSize(new Dimension(59, 20));
				jLabel4.setLocation(new Point(11, 267));
				jLabel3 = new JLabel();
				jLabel3.setText("Amount");
				jLabel3.setSize(new Dimension(58, 20));
				jLabel3.setLocation(new Point(11, 241));
				stmt = DBConnect.getConnection().createStatement();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
			query = DBConnect.Select("docdb","code, name, status, amt, balance","dcode = '"+dcode+"'","dcode asc");
			jLabel2 = new JLabel();
			jLabel2.setText("Status");
			jLabel2.setSize(new java.awt.Dimension(52,20));
			jLabel2.setLocation(new Point(12, 295));
			jLabel1 = new JLabel();
			jLabel1.setText("Name");
			jLabel1.setSize(new java.awt.Dimension(45,20));
			jLabel1.setLocation(new java.awt.Point(11,215));
			jLabel = new JLabel();
			jLabel.setText("Code");
			jLabel.setSize(new java.awt.Dimension(44,20));
			jLabel.setLocation(new java.awt.Point(11,187));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJScrollPane(), null);
			jContentPane.add(getJButton(), null);
			jContentPane.add(getJButton1(), null);
			jContentPane.add(getJButton2(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(jLabel2, null);
			jContentPane.add(getJTextField(), null);
			jContentPane.add(getJTextField1(), null);
			jContentPane.add(getJComboBox(), null);
			jContentPane.add(jLabel3, null);
			jContentPane.add(getJTextField2(), null);
			jContentPane.add(jLabel4, null);
			jContentPane.add(getJTextField3(), null);
			if	(jTable.getRowCount() > 0){
				try{
					jTable.setRowSelectionInterval(0,0);
					setValue();
				}catch(NullPointerException e){
					
				}
				
			}
		}
		return jContentPane;
	}

	private JScrollPane getJScrollPane() throws ArrayIndexOutOfBoundsException{
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new java.awt.Rectangle(6,5,371,132));
			jScrollPane.setViewportView(getJTable());
			jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return jScrollPane;
	}

	private JTable getJTable() throws ArrayIndexOutOfBoundsException {
		//query = DBConnect.Select("docdb","code, name, status","dcode = '"+dcode+"'","dcode asc");
		
		try	{
			rsDocDB = new ResultSetTableModel(query);
			jTable = new JTable(rsDocDB);
			//jTable = new JTable();
			
			TableCellRenderer renderer = new DOCRenderer();
			jTable.setDefaultRenderer(Object.class, renderer);
			jTable.setSelectionMode(0);
			jTable.getTableHeader().setResizingAllowed(false);
			jTable.getTableHeader().setReorderingAllowed(false);
			jTable.getColumnModel().getColumn(0).setMinWidth(80);
			jTable.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(80);
			jTable.getColumnModel().getColumn(1).setMinWidth(210);
			jTable.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(210);
			jTable.getColumnModel().getColumn(2).setMinWidth(65);
			jTable.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(65);
			jTable.getColumnModel().getColumn(0).setHeaderValue("Code");
			jTable.getColumnModel().getColumn(1).setHeaderValue("Name");
			jTable.getColumnModel().getColumn(2).setHeaderValue("Status");
			jTable.grabFocus();
			jTable.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					setValue();
				}
			});
			jTable.addKeyListener(new KeyAdapter(){
				public void keyPressed(KeyEvent e){
					int eventKey = e.getKeyChar();
					InputMap IMP = jTable.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
					KeyStroke ent = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
					IMP.put(ent, "none");
					if (eventKey == 10){
						setValue();
					}

					if	(e.getKeyCode() == 112){
						try{
							mainform.desktop.add(new docDBSearch());
						}catch(IllegalArgumentException ill){

						}
					}
				}
			});
			
	        jTable.addKeyListener(new KeyAdapter(){
	        	public void keyReleased(KeyEvent e){
	        		setValue();
	        	}
	        });  

			
		}catch(Exception e){
			System.out.println(e.getMessage()+" DOCDB");
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("DiscountMaint", "DiscountMaint", traceWriter.toString());
			dispose();
		}
		return jTable;
	}

	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(new java.awt.Rectangle(46,145,89,28));
			jButton.setText("ADD");
			jButton.setMnemonic('A');
			jButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if	(jButton.getText().equalsIgnoreCase("ADD")){
						//Enable Fields
						ListSelectionModel selectionModel = jTable.getSelectionModel();
						selectionModel.clearSelection();
						jTable.setEnabled(false);
						jTextField.setEnabled(true);
						jTextField.grabFocus();
						jTextField1.setEnabled(true);
						jTextField2.setEnabled(true);
						jComboBox.setEnabled(true);
						jButton.setMnemonic('S');
						
						//Set button Text
						jButton.setText("SAVE");
						jButton1.setEnabled(false);
						jButton2.setMnemonic('C');
						jButton2.setText("CANCEL");
						
						jTextField.setText("");
						jTextField1.setText("");
						if	(screen.trim().equalsIgnoreCase("Y") == true){
							jTextField2.setText("");
							jTextField3.setText("");
						}
						jComboBox.setSelectedIndex(0);
						
					}
					else{
						if	(jTextField.getText().trim().length() == 0){
							Message.messageError("Invalid input for code");
							jTextField.grabFocus();
						}
						else if	(jTextField1.getText().trim().length() == 0){
							Message.messageError("Invalid input for name");
							jTextField1.grabFocus();
						}
						else if	(jTextField2.getText().trim().length() == 0){
							Message.messageError("Invalid input for amount");
							jTextField2.grabFocus();
						}
						else if	(jComboBox.getSelectedIndex() == 0){
							Message.messageError("Invalid input for status");
							jComboBox.grabFocus();
						}else{
							try	{
								String executeSave = DBConnect.Insert("docdb",
										"dcode, code, name, addr, phoneno, mobileno, email, initiator, commport, txndate, amt, balance, status",
										"'"+DBConnect.clean(dcode)+"', " +
										"'"+DBConnect.clean(jTextField.getText().trim())+"', " +
										"'"+DBConnect.clean(jTextField1.getText().trim())+"', " +
										"' ', " +
										"' ', " +
										"' ', " +
										"' ', " +
										"' ', " +
										"' ', " +
										"'"+DBConnect.clean(dformat.format(now))+"', " +
										"'"+DBConnect.clean(jTextField2.getText().trim())+"', " +
										"'"+DBConnect.clean(jTextField2.getText().trim())+"', " +
										"'"+DBConnect.clean(status)+"'");
								stmt.execute(executeSave);
														
								rsDocDB.setQuery(query);
								jTable.grabFocus();
								defaultValue();
								
								Message.messageInfo(Message.messageAdd);
							}catch(Exception e){
								Message.messageError(Message.messageExist+"\n"+e.getMessage());
//								System.out.println("docDB "+e.getMessage());
//								e.printStackTrace();
							}
						}
					}
				}
			});
		}
		return jButton;
	}

	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setLocation(new java.awt.Point(142,145));
			jButton1.setText("EDIT");
			jButton1.setMnemonic('E');
			jButton1.setSize(new java.awt.Dimension(89,28));
			jButton1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if	(jButton1.getText().equalsIgnoreCase("EDIT")){
						//Disable Fields
						jTable.setEnabled(false);
						jTextField1.setEnabled(true);
						jComboBox.setEnabled(true);
						
						//Set button Text
						jButton1.setMnemonic('U');
						jButton.setEnabled(false);
						jButton1.grabFocus();
						jButton1.setText("UPDATE");
						jButton2.setMnemonic('C');
						jButton2.setText("CANCEL");
					}
					else{
						if	(jTextField1.getText().trim().length() == 0){
							Message.messageError("Invalid input for name");
							jTextField1.grabFocus();
						}
						else if	(jComboBox.getSelectedIndex() == 0){
							Message.messageError("Invalid input for status");
							jComboBox.grabFocus();
						}else{
							try	{
								String code = jTextField.getText().trim();
								
								String executeUpdate = DBConnect.Update("docdb",
										"name = '"+DBConnect.clean(jTextField1.getText().trim())+"', " +
										"addr = ' ', " +
										"phoneno = ' ', " +
										"mobileno = ' ', " +
										"email = ' ', " +
										"initiator = ' ', " +
										"commport = ' ', " +
										//"txndate = '"+DBConnect.clean(dformat.format(now))+"', " +
										"amt = '"+DBConnect.clean(jTextField2.getText().trim())+"', " +
										"balance = '"+DBConnect.clean(jTextField3.getText().trim())+"', " +
										"status  = '"+status+"'",
										"dcode = '"+dcode+"' and " +
										"code = '"+jTextField.getText().trim()+"' ");
								stmt.execute(executeUpdate);
								
								rsDocDB.setQuery(query);
								if	(jTable.getRowCount() > 0){
									jTable.setRowSelectionInterval(0,0);
								}
								setValue();
								disableALL();
								jTable.grabFocus();
								
								Message.messageInfo(Message.messageUpdate);
							}catch(Exception e){
								System.out.println("docDB "+e.getMessage());
								e.printStackTrace();
							}
						}
					}
				}
			});
		}
		return jButton1;
	}

	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setLocation(new java.awt.Point(239,145));
			jButton2.setText("DELETE");
			jButton2.setSize(new java.awt.Dimension(89,28));
			jButton2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if	(jButton2.getText().equalsIgnoreCase("DELETE")){
						int confirmDelete = Message.messageYesNo("Are You Sure you want to delete\n" + dcode+ " : "+ jTextField.getText()+ " : "+ jTextField1.getText()+ " ?");
						if ( confirmDelete == JOptionPane.YES_OPTION){
							try	{
								String executeDEL = DBConnect.delete("docdb",
									"dcode = '"+dcode+"' and code = '"+jTextField.getText().trim()+"'");
								
								stmt.execute(executeDEL);
																
								rsDocDB.setQuery(query);
								if	(jTable.getRowCount() > 0){
									jTable.setRowSelectionInterval(0,0);
									setValue();
									disableALL();
								}else{
									defaultValue();
								}
								
								Message.messageInfo(Message.messageDelete);
							}catch(Exception e){
								System.out.println("docDB "+e.getMessage());
							}
						}
					}else{
						//Disable Fields
						jTextField.setEnabled(false);
						jTextField1.setEnabled(false);
						jTextField2.setEnabled(false);
						jComboBox.setEnabled(false);
						
						//Set button Text
						jButton.setText("ADD");
						jButton.setEnabled(true);
						jButton1.setText("EDIT");
						jButton1.setEnabled(true);
						jButton2.setText("DELETE");
						jButton.setMnemonic('A');
						jButton1.setMnemonic('E');
						jButton2.setMnemonic('D');
						jTable.setEnabled(true);
						if	(jTable.getRowCount() > 0 ){
							jTable.setRowSelectionInterval(0,0);
							setValue();
						}
						jTable.grabFocus();
					}
				}
			});
		}
		return jButton2;
	}

	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new MyField(true,20);
			jTextField.setCapital(true);
			jTextField.setSize(new java.awt.Dimension(88,20));
			jTextField.setLocation(new java.awt.Point(69,187));
			jTextField.setEnabled(false);
			
		}
		return jTextField;
	}

	private JTextField getJTextField1() {
		if (jTextField1 == null) {
			jTextField1 = new MyField(true,50);
			jTextField1.setLocation(new java.awt.Point(70,215));
			jTextField1.setSize(new java.awt.Dimension(231,20));
			jTextField1.setEnabled(false);
		}
		return jTextField1;
	}

	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox();
			jComboBox.setBounds(new Rectangle(71, 295, 159, 19));
			jComboBox.addItem("Choose");
			jComboBox.addItem("Active");
			jComboBox.addItem("Cancelled");
			jComboBox.addItem("Suspended");
			jComboBox.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if	(jComboBox.getSelectedIndex() == 1){
						status = "A";
					}
					else if	(jComboBox.getSelectedIndex() == 2){
						status = "C";
					}
					else if	(jComboBox.getSelectedIndex() == 3){
						status = "S";
					}
				}
			});
			jComboBox.setEnabled(false);
		}
		return jComboBox;
	}
	
	private class DOCRenderer extends DefaultTableCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
 
			if (column == 2)
				setHorizontalAlignment( CENTER );
			else if (column == 0)
				setHorizontalAlignment( CENTER );
			else
				setHorizontalAlignment( LEFT );
			
			setBorder(BorderFactory.createLineBorder(getBackground(), 1));
			
			return this;
		}
	}
	
	private void setValue() {
		int getRow = jTable.getSelectedRow();
		jTextField.setText(jTable.getValueAt(getRow,0).toString());
		jTextField1.setText(jTable.getValueAt(getRow,1).toString());
		if	(jTable.getValueAt(getRow,2).toString().equalsIgnoreCase("A") == true){
			jComboBox.setSelectedIndex(1);
		}
		else if	(jTable.getValueAt(getRow,2).toString().equalsIgnoreCase("C") == true){
			jComboBox.setSelectedIndex(2);
		}
		else if	(jTable.getValueAt(getRow,2).toString().equalsIgnoreCase("S") == true){
			jComboBox.setSelectedIndex(3);
		}
		jTextField2.setText(jTable.getValueAt(getRow,3).toString());
		jTextField3.setText(jTable.getValueAt(getRow,4).toString());
	}
	
	private void disableALL(){
		//Fields
		jTextField.setEnabled(false);
		jTextField1.setEnabled(false);
		jComboBox.setEnabled(false);
		jTextField2.setEnabled(false);
		jTable.setEnabled(true);
		//jTextField3.setEnabled(false);
		
		//Buttons
		jButton.setEnabled(true);
		jButton1.setEnabled(true);
		jButton2.setEnabled(true);
		jButton.setText("ADD");
		jButton1.setText("EDIT");
		jButton2.setText("DELETE");
		
	}
	
	private void defaultValue(){
		jTextField.setText("");
		jTextField1.setText("");
		jTextField2.setText("");
		jTextField3.setText("");
		jComboBox.setSelectedIndex(0);
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField2() {
		if (jTextField2 == null) {
			jTextField2 = new MyField(false,9,2);
			jTextField2.setLocation(new Point(70, 241));
			jTextField2.setSize(new Dimension(154, 20));
			jTextField2.setEnabled(false);
			jTextField2.addKeyListener(new KeyAdapter(){
				public void keyPressed(KeyEvent ke){
					jTextField3.setText(jTextField2.getText());
				}
				public void keyReleased(KeyEvent ke){
					jTextField3.setText(jTextField2.getText());
				}
			});
		}
		return jTextField2;
	}

	/**
	 * This method initializes jTextField3	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField3() {
		if (jTextField3 == null) {
			jTextField3 = new MyField(false,9,2);
			jTextField3.setBounds(new Rectangle(71, 267, 153, 20));
			jTextField3.setEnabled(false);
		}
		return jTextField3;
	}
	
	private void setScreen(){
		this.setSize(391, 300);
		jLabel2.setLocation(12, 244);
		jComboBox.setBounds(71, 244, 159, 20);
		jLabel3.setVisible(false);
		jLabel4.setVisible(false);
		jTextField2.setText("0");
		jTextField3.setText("0");
		jTextField2.setVisible(false);
		jTextField3.setVisible(false);
	}

	static void getRecord(){
		try	{
			String query = "";

			query = "select " +
			"code, name, amt, balance, status " +
			"from docdb where dcode = '"+dcode+"' and code = '"+xRecord+"' order by code asc";
//			"from docdb where dcode = '"+dcode+"' order by code asc";
//			"from docdb order by code asc";
		
			Statement stmt = DBConnect.getConnection().createStatement();
			rs1 = mainform.stat.executeQuery(query);
			rs1.next();
//			rs1.absolute(xRecord);
			setFields(rs1.getString(1),rs1.getString(2),rs1.getString(3),rs1.getString(4),rs1.getString(5));
			rs1.close();
			stmt.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void setFields(String code,String name,String amt,String balance, String status){
		
		
		jTextField.setText(code);
		jTextField1.setText(name);
		jTextField2.setText(amt);
		jTextField3.setText(balance);
		
		if(status.equalsIgnoreCase("A")){
			jComboBox.setSelectedIndex(1);
			jComboBox.setEnabled(false);
		}else if(status.equalsIgnoreCase("C")){
			jComboBox.setSelectedIndex(2);
			jComboBox.setEnabled(false);
		}else{
			jComboBox.setSelectedIndex(3);
			jComboBox.setEnabled(false);
		}
		
		
	}

}  //  @jve:decl-index=0:visual-constraint="9,24"
