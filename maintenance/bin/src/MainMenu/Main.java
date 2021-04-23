package maintenance.bin.src.MainMenu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.KeyStroke;
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

public class Main extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	private JButton jButton2 = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private MyField jTextField = null;
	private MyField jTextField1 = null;
	private MyField jTextField2 = null;
	private JButton jButton3 = null;
	private ResultSetTableModel tableMain = null;
	private String qryMainMenu = DBConnect.Select("mmenu",
					"code,mmenu.desc,level",
					"mcode = ' '",
					"level ASC");
	private int tempR = 0;
	private int tempC = 0;
	public static int subMain = 0;
	/**
	 * This is the default constructor
	 */
	public Main() {
		super();
		setConnection();
		initialize();
		
//		Add Key Listener
		jTable.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e){
//				int eventKey = e.getKeyCode();
//				System.out.println("hello2 "+eventKey);
				tempR = jTable.getSelectedRow();
				tempC = jTable.getSelectedColumn();
				setValue();
			}
		}//new KeyAdapter()
		);//tbl.addKeyListener( 
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(432, 296);
		this.setTitle("Main Menu");
		this.setClosable(true);
		this.setContentPane(getJContentPane());
		if	(jTable.getRowCount() >= 1){
			jTable.setRowSelectionInterval(0, 0);
			setValue();
		}
		this.addInternalFrameListener(new InternalFrameListener(){
			public void internalFrameActivated(InternalFrameEvent arg0) {
				if	(subMain == 1){
					try {
						Sub.iFrame.setSelected(true);
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
		this.setResizable(false);
		this.setVisible(true);
	}
	
	private void setConnection(){
		try	{
			tableMain = new ResultSetTableModel(qryMainMenu);
		}
		catch(SQLException sqlE){
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			sqlE.printStackTrace(printWriter);
			createINI.create("MainMenu", "setConnection", traceWriter.toString());      
	        dispose();
		}
		catch(Exception e){
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("MainMenu", "setConnection", traceWriter.toString());      
	        dispose();
		}
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
			jLabel2 = new JLabel();
			jLabel2.setText("Level");
			jLabel2.setSize(new java.awt.Dimension(54,20));
			jLabel2.setLocation(new java.awt.Point(18,235));
			jLabel1 = new JLabel();
			jLabel1.setText("Description");
			jLabel1.setSize(new java.awt.Dimension(97,20));
			jLabel1.setLocation(new java.awt.Point(18,204));
			jLabel = new JLabel();
			jLabel.setText("Code");
			jLabel.setSize(new java.awt.Dimension(47,20));
			jLabel.setLocation(new java.awt.Point(18,173));
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
			jContentPane.add(getJTextField2(), null);
			jContentPane.add(getJButton3(), null);
		return jContentPane;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new java.awt.Rectangle(3,4,415,128));
			jScrollPane.setViewportView(getJTable());
			jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		return jScrollPane;
	}

	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable() {
			jTable = new JTable(tableMain);
			TableCellRenderer renderer = new Renderer();
			jTable.setDefaultRenderer(Object.class,renderer);
			jTable.setSelectionMode(0);
			jTable.getTableHeader().setResizingAllowed(false);
			jTable.getTableHeader().setReorderingAllowed(false);
			reload();
			jTable.addMouseListener(new MouseListener(){
				public void mouseClicked(MouseEvent me){
					if	(jButton2.getText().equals("Delete") == true){
						setValue();
						tempR = jTable.getSelectedRow();
						tempC = jTable.getSelectedColumn();
					}
				}
				public void mousePressed(MouseEvent arg0) {}
				public void mouseReleased(MouseEvent arg0) {}
				public void mouseEntered(MouseEvent arg0) {}
				public void mouseExited(MouseEvent arg0) {}
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
	        	}
			});
		return jTable;
	}
	
	

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
			jButton = new JButton();
			jButton.setText("Add");
			jButton.setSize(new java.awt.Dimension(91,22));
			jButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
			jButton.setLocation(new java.awt.Point(15,136));
			jButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae){
					if	(jButton.getText().equals("Add") == true){
						Enabled();
						jButton.setText("Save");
						jButton.setMnemonic('S');
						jButton1.setEnabled(false);
						jButton2.setText("Cancel");
						jButton2.setMnemonic('C');
						Clear();
						jTable.setEnabled(false);
						jTextField.grabFocus();
						jButton3.setEnabled(false);
						jTable.setEnabled(false);
					}else	{
						try	{
							if (jTextField.getText().length() == 0){
								Message.messageError("Invalid input for code");
								jTextField.grabFocus();
							} else if (jTextField1.getText().length() == 0){
								Message.messageError("Invalid input for description");
								jTextField1.grabFocus();
							} else if (jTextField2.getText().length() == 0){
								Message.messageError("Invalid input for level");
								jTextField2.grabFocus();
							} else {
								String sqlsave = DBConnect.Insert("mmenu", 
										"mcode,code,mmenu.desc,level,jclass,userid", 
										"' ', " +
										"'"+DBConnect.clean(jTextField.getText().trim())+"', " +
										"'"+DBConnect.clean(jTextField1.getText().trim())+"', " +
										"'"+DBConnect.clean(jTextField2.getText().trim())+"', " +
										"' ',"+
										"'"+mainform.getUcode()+"'");
								try	{
									tableMain.setInsert(sqlsave);
									
//									MsgWriter.msgWriter(16, 0,
//											Formatter.formatString(6, ' ', ' ', " ") +
//											Formatter.formatString(6, ' ', ' ', jTextField.getText().trim()) +
//											Formatter.formatString(30, ' ', ' ', jTextField1.getText().trim()) +
//											Formatter.formatString(3, ' ', ' ', jTextField2.getText().trim()) +
//											Formatter.formatString(100, ' ', ' ',	" "));
									
									tableMain.setQuery(qryMainMenu);
									reload();
									Clear();
								}catch(Exception sqle){
									Message.messageYesNo("Record already exist!");
									System.out.println(sqle.getMessage()+"->> method jButton()-Insert");
								}
							}
						}catch(Exception e){
							System.out.println(e.getMessage()+" ->> method jButton()");
						}
					}
				}
			});
		return jButton;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
			jButton1 = new JButton();
			jButton1.setText("Edit");
			jButton1.setSize(new java.awt.Dimension(91,22));
			jButton1.setMnemonic(java.awt.event.KeyEvent.VK_E);
			jButton1.setLocation(new java.awt.Point(117,137));
			jButton1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if	(jButton1.getText().equals("Edit") == true){
						jButton.setEnabled(false);
						jButton1.setText("Update");
						jButton1.setMnemonic('U');
						jButton2.setText("Cancel");
						jButton2.setMnemonic('C');
						Enabled();
						jTextField.setEnabled(false);
						jTable.setEnabled(false);
						jButton3.setEnabled(false);
						jTable.setEnabled(false);
					}else	{
						if	(jTextField1.getText().length() == 0){
							Message.messageError("Invalid input for description");
							jTextField1.grabFocus();
						}else if	(jTextField2.getText().length() == 0){
							Message.messageError("Invalid input for level");
							jTextField2.grabFocus();
						}else	{
							String sqlupdate = DBConnect.Update("mmenu", 
									"mmenu.desc = '"+DBConnect.clean(jTextField1.getText().trim())+"', " +
									"mmenu.level = '"+DBConnect.clean(jTextField2.getText().trim())+"' ", 
									"code = '"+DBConnect.clean(jTextField.getText())+"' and " +
									"mcode = ' '");
							try	{
								tableMain.setInsert(sqlupdate);
								
//								MsgWriter.msgWriter(16, 0,
//										Formatter.formatString(6, ' ', ' ', " ") +
//										Formatter.formatString(6, ' ', ' ', jTextField.getText().trim()) +
//										Formatter.formatString(30, ' ', ' ', jTextField1.getText().trim()) +
//										Formatter.formatString(3, ' ', ' ', jTextField2.getText().trim()) +
//										Formatter.formatString(100, ' ', ' ',	" "));
								
								tableMain.setQuery(qryMainMenu);
								reload();
								Disabled();
								jTable.setEnabled(true);
								jTable.setColumnSelectionInterval(tempC,tempC);
								jTable.setRowSelectionInterval(tempR,tempR);
								setValue();
								jButton.setEnabled(true);
								jButton1.setText("Edit");
								jButton1.setMnemonic('E');
								jButton2.setText("Delete");
								jButton.setMnemonic('D');
								jButton3.setEnabled(true);
							}catch(Exception sqle){
								System.out.println(sqle.getMessage()+"->> method jButton1()-update");
							}
						}
					}
				}
			});
		return jButton1;
	}

	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton2() {
			jButton2 = new JButton();
			jButton2.setText("Delete");
			jButton2.setSize(new java.awt.Dimension(91,22));
			jButton2.setMnemonic(java.awt.event.KeyEvent.VK_D);
			jButton2.setLocation(new java.awt.Point(222,137));
			jButton2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if	(jButton2.getText().equals("Cancel") == true){
						if	(jTable.getRowCount() >= 1){
							if	(jTable.getRowCount() >= tempR){
								jTable.setRowSelectionInterval(tempR,tempR);
								jTable.setColumnSelectionInterval(tempC,tempC);
								setValue();
							}else{
								jTable.setRowSelectionInterval(0,0);
								jTable.setColumnSelectionInterval(0,0);
								setValue();
							}
						}
						jButton.setEnabled(true);
						jButton1.setEnabled(true);
						jButton.setText("Add");
						jButton1.setText("Edit");
						jButton.setMnemonic('A');
						jButton1.setMnemonic('E');
						jButton2.setText("Delete");
						jButton2.setMnemonic('D');
						jTable.setEnabled(true);
						Disabled();
						jButton3.setEnabled(true);
						jTable.setEnabled(true);
						jTable.requestFocus();
					}else	{
						String sqldelete = DBConnect.delete("mmenu","code = '"+DBConnect.clean(jTextField.getText().trim())+"' and mcode = ' '");
						int confirmDelete = Message.messageYesNo("Are You Sure you want to delete\n" +jTextField1.getText() +  " ?");
						if ( confirmDelete == JOptionPane.YES_OPTION){
							try	{
								tableMain.setInsert(sqldelete);
								
//								MsgWriter.msgWriter(16, 1,
//										Formatter.formatString(6, ' ', ' ', " ") +
//										Formatter.formatString(6, ' ', ' ', jTextField.getText().trim()) +
//										Formatter.formatString(30, ' ', ' ', jTextField1.getText().trim()) +
//										Formatter.formatString(3, ' ', ' ', jTextField2.getText().trim()) +
//										Formatter.formatString(100, ' ', ' ',	" "));
								
								tableMain.setQuery(qryMainMenu);
								reload();
								if	(jTable.getRowCount() >= 1){
									jTable.setRowSelectionInterval(0,0);
									jTable.setColumnSelectionInterval(0,0);
									setValue();
								}
							}catch	(Exception sqle){
									System.out.println(sqle.getMessage()+" ->> method jButton2-Delete");
							}
						}
					}
				}
			});
		return jButton2;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private MyField getJTextField() {
			jTextField = new MyField(true,6);
			jTextField.setCapital(true);
			jTextField.setLocation(new java.awt.Point(122,172));
			jTextField.setSize(new java.awt.Dimension(109,20));
			jTextField.setEnabled(false);
		return jTextField;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private MyField getJTextField1() {
			jTextField1 = new MyField(true,30);
			jTextField1.setLocation(new java.awt.Point(122,204));
			jTextField1.setSize(new java.awt.Dimension(266,20));
			jTextField1.setEnabled(false);
		return jTextField1;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private MyField getJTextField2() {
			jTextField2 = new MyField(false,3);
			jTextField2.setLocation(new java.awt.Point(121,234));
			jTextField2.setSize(new java.awt.Dimension(64,20));
			jTextField2.setEnabled(false);
		return jTextField2;
	}

	/**
	 * This method initializes jButton3	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton3() {
			jButton3 = new JButton();
			jButton3.setText("Sub");
			jButton3.setMnemonic('S');
			jButton3.setSize(new java.awt.Dimension(87,22));
			jButton3.setLocation(new java.awt.Point(324,138));
			jButton3.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Sub.titleMain = jTextField1.getText();
					new Sub(jTextField.getText(),Width(),Height());
				}
			});
		return jButton3;
	}
	
	private int Width(){
		return this.getBounds().width;
	}
	private int Height(){
		return this.getBounds().height;
	}
	public void reload(){
		try {
			jTable.getColumnModel().getColumn(0).setMaxWidth(0);
			jTable.getColumnModel().getColumn(0).setMinWidth(0);
			jTable.getColumnModel().getColumn(1).setMaxWidth(0);
			jTable.getColumnModel().getColumn(1).setMinWidth(0);
			jTable.getColumnModel().getColumn(2).setMaxWidth(0);
			jTable.getColumnModel().getColumn(2).setMinWidth(0);
			jTable.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(75);
			jTable.getTableHeader().getColumnModel().getColumn(0).setMinWidth(75);
			jTable.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(282);
			jTable.getTableHeader().getColumnModel().getColumn(1).setMinWidth(282);
			jTable.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(40);
			jTable.getTableHeader().getColumnModel().getColumn(2).setMinWidth(40);
			jTable.getColumnModel().getColumn(0).setHeaderValue("Code");
			jTable.getColumnModel().getColumn(1).setHeaderValue("Description");
			jTable.getColumnModel().getColumn(2).setHeaderValue("Level");
		} catch (Exception e) {
		}
	}
	private class Renderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (column == 2)
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
		jTextField2.setText(jTable.getValueAt(getRow,2).toString()); 
	}
	private void Enabled(){
		jTextField.setEnabled(true);
		jTextField1.setEnabled(true);
		jTextField2.setEnabled(true);
	}
	private void Disabled(){
		jTextField.setEnabled(false);
		jTextField1.setEnabled(false);
		jTextField2.setEnabled(false);
	}
	private void Clear(){
		jTextField.setText(null);
		jTextField1.setText(null);
		jTextField2.setText(null);
	}

	
}  //  @jve:decl-index=0:visual-constraint="10,10"
