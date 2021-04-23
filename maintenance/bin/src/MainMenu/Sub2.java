package maintenance.bin.src.MainMenu;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
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
import javax.swing.JDesktopPane;
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
import maintenance.bin.src.Functions.Formatter;
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.MyField;
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.ResultTableModel.ResultSetTableModel;
import maintenance.bin.src.core.mainform;

public class Sub2 extends JInternalFrame{

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
	private JLabel jLabel3 = null;
	private MyField jTextField3 = null;
	private ResultSetTableModel tableSub2 = null;
	private String code1;
	private int tempR = 0;
	private int tempC = 0;
	private BorderLayout layout = new BorderLayout();
	private Message message = new Message();
	private JDesktopPane desk = mainform.desktop;
	public static String subTitle = null;
	public static JInternalFrame iFrame = null;
	

	public Sub2(String Mcode,int w,int h) {
		super();
		MenuCode(Mcode);
		setConnection(Mcode);
		initialize(w,h);
		
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
	
	private void setConnection(String Mcode){
		String sqlQuery = DBConnect.Select("mmenu",
				"mcode, code, mmenu.desc, level, jclass",
				"mcode = '"+DBConnect.clean(Mcode)+"'",
				"level asc");
		try	{
			tableSub2 = new ResultSetTableModel(sqlQuery);
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

	private void initialize(int w, int h) {
		iFrame = this;
	    Dimension rootSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(432, 336);
		this.setTitle("Sub Menu ("+subTitle+")");
		this.setContentPane(getJContentPane());
		if	(jTable.getRowCount() >= 1){
			jTable.setRowSelectionInterval(0, 0);
			setValue();
		}
		this.setLocation(((rootSize.width + 235) - w) / 2,((rootSize.height + 40) - h) / 3);
		this.addInternalFrameListener(new InternalFrameListener(){
			public void internalFrameOpened(InternalFrameEvent arg0) {}
			public void internalFrameClosing(InternalFrameEvent arg0) {
				Sub.subThis = 0;
			}
			public void internalFrameClosed(InternalFrameEvent arg0) {}
			public void internalFrameIconified(InternalFrameEvent arg0) {}
			public void internalFrameDeiconified(InternalFrameEvent arg0) {}
			public void internalFrameActivated(InternalFrameEvent arg0) {
				Sub.subThis = 1;
			}
			public void internalFrameDeactivated(InternalFrameEvent arg0) {}
		});
		this.setClosable(true);
		desk.setLayout(null);
		desk.add(this, new Integer(1));
		this.setResizable(false);
		this.setVisible(true);
		try {
			this.setSelected(true);
		} catch (PropertyVetoException e) {}
	}

	private JPanel getJContentPane() {
			jLabel3 = new JLabel();
			jLabel3.setText("Class");
			jLabel3.setSize(new java.awt.Dimension(87,20));
			jLabel3.setLocation(new java.awt.Point(18,271));
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
			jContentPane.add(jLabel3, null);
			jContentPane.add(getJTextField3(), null);
		return jContentPane;
	}

	private JScrollPane getJScrollPane() {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new java.awt.Rectangle(3,4,415,128));
			jScrollPane.setViewportView(getJTable());
			jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		return jScrollPane;
	}

	private JTable getJTable() {
			jTable = new JTable();
			jTable = new JTable(tableSub2);
			TableCellRenderer renderer = new Renderer();
			jTable.setDefaultRenderer(Object.class,renderer);
			jTable.setSelectionMode(0);
			jTable.getTableHeader().setResizingAllowed(false);
			jTable.getTableHeader().setReorderingAllowed(false);
			reload();
			jTable.addMouseListener(new MouseListener(){
				public void mouseClicked(MouseEvent arg0) {
					tempR = jTable.getSelectedRow();
					tempC = jTable.getSelectedColumn();
					setValue();
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

	private JButton getJButton() {
			jButton = new JButton();
			jButton.setText("Add");
			jButton.setSize(new java.awt.Dimension(91,22));
			jButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
			jButton.setLocation(new java.awt.Point(31,136));
			jButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae){
					String sqlQuery = DBConnect.Select("mmenu",
							"mcode, code, mmenu.desc, level, jclass",
							"mcode = '"+DBConnect.clean(code1)+"'",
							"level asc");
					if	(jButton.getText().equals("Add") == true){
						jButton.setText("Save");
						jButton.setMnemonic('S');
						jButton1.setEnabled(false);
						jButton2.setText("Cancel");
						jButton2.setMnemonic('C');
						Enable();
						jTable.setEnabled(false);
						Clear();
					}else{
						if	(jTextField.getText().length() == 0){
							Message.messageError("Invalid input for code");
							jTextField.grabFocus();
						}
						else if	(jTextField1.getText().length() == 0){
							Message.messageError("Invalid input for description");
							jTextField1.grabFocus();
						}
						else if	(jTextField2.getText().length() == 0){
							Message.messageError("Invalid input for level");
							jTextField2.grabFocus();
						}
						else	{
							String text;
							if	(jTextField3.getText().length() == 0)text = " ";
							else	text = jTextField3.getText().trim();
							String sqlInsert = DBConnect.Insert("mmenu", 
								"mcode,code,mmenu.desc,level,jclass", 
								"'"+DBConnect.clean(code1)+"', '"+DBConnect.clean(jTextField.getText())+"', " +
								"'"+DBConnect.clean(jTextField1.getText())+"', '"+DBConnect.clean(jTextField2.getText())+"', " +
								"'"+DBConnect.clean(text)+"' ");
							try	{
								tableSub2.setInsert(sqlInsert);
								
//								MsgWriter.msgWriter(16, 0,
//										Formatter.formatString(6, ' ', ' ', code1) +
//										Formatter.formatString(6, ' ', ' ', jTextField.getText().trim()) +
//										Formatter.formatString(30, ' ', ' ', jTextField1.getText().trim()) +
//										Formatter.formatString(3, ' ', ' ', jTextField2.getText().trim()) +
//										Formatter.formatString(100, ' ', ' ',	text));
								
								tableSub2.setQuery(sqlQuery);
								reload();
								Clear();
							}catch(Exception e){
								Message.messageError("Record already exist !");
								System.out.println(e.getMessage()+" ->> method jButton()-insert");
							}
						}
					}
				}
			});
		return jButton;
	}

	private JButton getJButton1() {
			jButton1 = new JButton();
			jButton1.setText("Edit");
			jButton1.setSize(new java.awt.Dimension(91,22));
			jButton1.setMnemonic(java.awt.event.KeyEvent.VK_E);
			jButton1.setLocation(new java.awt.Point(157,135));
			jButton1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					String sqlQuery = DBConnect.Select("mmenu",
							"mcode, code, mmenu.desc, level, jclass",
							"mcode = '"+DBConnect.clean(code1)+"'",
							"level asc");
					if	(jButton1.getText().equals("Edit") == true){
						
						jButton.setEnabled(false);
						jButton1.setText("Update");
						jButton1.setMnemonic('U');
						jButton2.setText("Cancel");
						jButton2.setMnemonic('C');
						Enable();
						jTextField.setEnabled(false);
						jTable.setEnabled(false);
						jTextField1.grabFocus();
						
					}else{
						if	(jTextField1.getText().length() == 0){
							Message.messageError("Invalid input for description");
							jTextField1.grabFocus();
						}
						else if	(jTextField2.getText().length() == 0){
							Message.messageError("Invalid input for level");
							jTextField2.grabFocus();
						}
						else	{
							String text;
							if	(jTextField3.getText().length() == 0)text = " ";
							else text = jTextField3.getText().trim();
							String sqlUpdate = DBConnect.Update("mmenu", 
									"mmenu.desc = '"+DBConnect.clean(jTextField1.getText().trim())+"', " +
									"mmenu.level = '"+DBConnect.clean(jTextField2.getText().trim())+"', " +
									"mmenu.jclass = '"+DBConnect.clean(text)+"'", 
									"code = '"+DBConnect.clean(jTextField.getText())+"' and mcode = '"+DBConnect.clean(code1)+"'");
							try	{
								tableSub2.setInsert(sqlUpdate);
								
//								MsgWriter.msgWriter(16, 0,
//										Formatter.formatString(6, ' ', ' ', code1) +
//										Formatter.formatString(6, ' ', ' ', jTextField.getText().trim()) +
//										Formatter.formatString(30, ' ', ' ', jTextField1.getText().trim()) +
//										Formatter.formatString(3, ' ', ' ', jTextField2.getText().trim()) +
//										Formatter.formatString(100, ' ', ' ',	text));
								
								tableSub2.setQuery(sqlQuery);
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
						}catch(Exception e){
							System.out.println(e.getMessage()+" ->> method jButton1-update");
						}
						}
					}
				}
			});
		return jButton1;
	}

	private JButton getJButton2() {
			jButton2 = new JButton();
			jButton2.setText("Delete");
			jButton2.setSize(new java.awt.Dimension(91,22));
			jButton2.setMnemonic(java.awt.event.KeyEvent.VK_D);
			jButton2.setLocation(new java.awt.Point(285,137));
			jButton2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					String sqlQuery = DBConnect.Select("mmenu",
							"mcode, code, mmenu.desc, level, jclass",
							"mcode = '"+DBConnect.clean(code1)+"'",
							"level asc");
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
						jButton.setMnemonic('A');
						jButton1.setEnabled(true);
						jButton1.setText("Edit");
						jButton1.setMnemonic('E');
						jButton2.setText("Delete");
						jButton2.setMnemonic('D');
						jTable.setEnabled(true);
						Disabled();
					}else{
						String sqldelete = DBConnect.delete("mmenu","mcode = '"+DBConnect.clean(code1)+"' and code = '"+DBConnect.clean(jTextField.getText())+"'");
						String sqldelete2 = DBConnect.delete("groupmenu","mcode = '"+DBConnect.clean(code1)+"' and code = '"+DBConnect.clean(jTextField.getText())+"'");
						int confirmDelete = Message.messageYesNo("Are You Sure you want to delete\n" +jTextField1.getText()+" ?");
						if ( confirmDelete == JOptionPane.YES_OPTION){
							try	{
								
//								MsgWriter.msgWriter(16, 1,
//										Formatter.formatString(6, ' ', ' ', code1) +
//										Formatter.formatString(6, ' ', ' ', jTextField.getText().trim()) +
//										Formatter.formatString(30, ' ', ' ', jTextField1.getText().trim()) +
//										Formatter.formatString(3, ' ', ' ', jTextField2.getText().trim()) +
//										Formatter.formatString(100, ' ', ' ',	jTextField3.getText().trim()));
								
								tableSub2.setInsert(sqldelete2);
								tableSub2.setInsert(sqldelete);
								tableSub2.setQuery(sqlQuery);
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

	private MyField getJTextField() {
			jTextField = new MyField(true,6);
			jTextField.setCapital(true);
			jTextField.setLocation(new java.awt.Point(122,172));
			jTextField.setSize(new java.awt.Dimension(109,20));
			jTextField.setEnabled(false);
		return jTextField;
	}

	private MyField getJTextField1() {
			jTextField1 = new MyField(true,30);
			jTextField1.setLocation(new java.awt.Point(122,204));
			jTextField1.setSize(new java.awt.Dimension(266,20));
			jTextField1.setEnabled(false);
		return jTextField1;
	}

	private MyField getJTextField2() {
			jTextField2 = new MyField(false,3);
			jTextField2.setLocation(new java.awt.Point(121,234));
			jTextField2.setSize(new java.awt.Dimension(64,20));
			jTextField2.setEnabled(false);
		return jTextField2;
	}

	private MyField getJTextField3() {
			jTextField3 = new MyField(true,100);
			jTextField3.setLocation(new java.awt.Point(122,270));
			jTextField3.setSize(new java.awt.Dimension(265,20));
			jTextField3.setEnabled(false);
		return jTextField3;
	}
	
	public void reload(){
		try {
			jTable.getColumnModel().getColumn(0).setMaxWidth(1);
			jTable.getColumnModel().getColumn(0).setMinWidth(1);
			jTable.getColumnModel().getColumn(1).setMaxWidth(2);
			jTable.getColumnModel().getColumn(1).setMinWidth(2);
			jTable.getColumnModel().getColumn(2).setMaxWidth(3);
			jTable.getColumnModel().getColumn(2).setMinWidth(3);
			jTable.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(75);
			jTable.getTableHeader().getColumnModel().getColumn(1).setMinWidth(75);
			jTable.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(282);
			jTable.getTableHeader().getColumnModel().getColumn(2).setMinWidth(282);
			jTable.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(40);
			jTable.getTableHeader().getColumnModel().getColumn(3).setMinWidth(40);
			jTable.getColumnModel().getColumn(1).setHeaderValue("Code");
			jTable.getColumnModel().getColumn(2).setHeaderValue("Description");
			jTable.getColumnModel().getColumn(3).setHeaderValue("Level");
		} catch (Exception e) {
		}
	}
	
	private class Renderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (column == 3)
				setHorizontalAlignment( CENTER );
			else
				setHorizontalAlignment( LEFT );
			setBorder(BorderFactory.createLineBorder(getBackground(), 1));
			
			return this;
		}
	}
	
	private void MenuCode(String mcode){
		code1 = mcode;
	}
	
	private void setValue() {
		int getRow = jTable.getSelectedRow();
		jTextField.setText(jTable.getValueAt(getRow,1).toString());
		jTextField1.setText(jTable.getValueAt(getRow,2).toString());
		jTextField2.setText(jTable.getValueAt(getRow,3).toString());
		jTextField3.setText(jTable.getValueAt(getRow,4).toString());
	}
	
	private void Enable(){
		jTextField.setEnabled(true);
		jTextField1.setEnabled(true);
		jTextField2.setEnabled(true);
		jTextField3.setEnabled(true);
	}
	
	private void Disabled(){
		jTextField.setEnabled(false);
		jTextField1.setEnabled(false);
		jTextField2.setEnabled(false);
		jTextField3.setEnabled(false);
	}
	
	private void Clear(){
		jTextField.setText(null);
		jTextField1.setText(null);
		jTextField2.setText(null);
		jTextField3.setText(null);
		jTextField.grabFocus();
	}
	
}  //  @jve:decl-index=0:visual-constraint="10,10"
