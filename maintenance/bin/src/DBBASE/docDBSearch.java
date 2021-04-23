package maintenance.bin.src.DBBASE;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import maintenance.bin.src.DBConn.DBConnect;
import maintenance.bin.src.Functions.ActionCursor;
import maintenance.bin.src.core.mainform;

public class docDBSearch extends JDialog {

	private JPanel jContentPane = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private DefaultTableModel tableSearch = new DefaultTableModel(new String[][]{},new String[]{"Code","Name","Status"});
	private DefaultTableModel tempTable = new DefaultTableModel(new String[][]{},new String[]{"Code","Name","Status"});
	private String defaultQuery = "select code, name, status from docdb where dcode = '"+docDB.dcode+"'order by code asc";
	private String xString = ""; 
	private Timer sTimer = null;
	private int timer = 0;
	private JLabel jLabel = null;
	private JComboBox jcboSearchType = null;
	private JLabel jlSearch = null;
	private JTextField jtSearch = null;
	
	public docDBSearch() {
		super(mainform.frame);
		initialize();
	}

	
	private void initialize() {
		this.setModal(true);
		this.setSize(496, 391);
		this.setTitle(docDB.dDesc.replace('Y', ' ')+" Search");
		//this.setClosable(true);
		this.setLocationRelativeTo(null);
		this.setContentPane(getJContentPane());
		this.setVisible(true);
		
	}

	
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jlSearch = new JLabel();
			jlSearch.setText("Search:");
			jlSearch.setSize(new Dimension(110, 20));
			jlSearch.setLocation(new Point(36, 330));
			jLabel = new JLabel();
			jLabel.setText("Search By:");
			jLabel.setSize(new Dimension(110, 20));
			jLabel.setLocation(new Point(36, 306));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJScrollPane(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(getJcboSearchType(), null);
			jContentPane.add(jlSearch, null);
			jContentPane.add(getJtSearch(), null);
		}
		return jContentPane;
	}

	
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new Rectangle(15, 12, 454, 290));
			jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			jScrollPane.setViewportView(getJTable());
			
		}
		return jScrollPane;
	}
	
	private class Renderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			if (column == 0)setHorizontalAlignment(LEFT);
			if (column == 1)setHorizontalAlignment(LEFT);
			if (column == 2)setHorizontalAlignment(CENTER);
			setBorder(BorderFactory.createLineBorder(getBackground(), 1));
			
			return this;
		}
	}

	
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable(tableSearch){
				public boolean isCellEditable(int irows, int icols){
					return false;
				}
			};
			
			TableCellRenderer renderer = new Renderer();
			jTable.setDefaultRenderer(Object.class,renderer);
			
			jTable.setSelectionMode(0);
			jTable.getTableHeader().setResizingAllowed(false);
			jTable.getTableHeader().setReorderingAllowed(false);
			
			jTable.getColumnModel().getColumn(0).setMinWidth(150);
	        jTable.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(150);
	        jTable.getColumnModel().getColumn(1).setMinWidth(185);
	        jTable.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(185);
	        jTable.getColumnModel().getColumn(2).setMinWidth(100);
	        jTable.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(100);
			
			
			jTable.getColumnModel().getColumn(0).setHeaderValue("Code");
	        jTable.getColumnModel().getColumn(1).setHeaderValue("Name");
	        jTable.getColumnModel().getColumn(2).setHeaderValue("Status");
	        jTable.addKeyListener(new KeyAdapter(){
				public void keyReleased(KeyEvent e){
					InputMap IMP = jTable.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                    KeyStroke ent = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
         		    IMP.put(ent, "none");
         		    
         		    if	(e.getKeyCode() == 10){
         		    	if	(xString.toString().trim().length() == 0){
         		    		for	(int i = 0; i < tempTable.getRowCount(); i++){
         		    			if	(jcboSearchType.getSelectedIndex() == 0){
         		    				if (tempTable.getValueAt(i, 0).toString().equalsIgnoreCase(jTable.getValueAt(jTable.getSelectedRow(), 0).toString())){
             		    				docDB.xRecord = tempTable.getValueAt(i, 0).toString();
             		    				break;
             		    			}
         		    			}else if	(jcboSearchType.getSelectedIndex() == 1){
         		    				if (tempTable.getValueAt(i, 1).toString().equalsIgnoreCase(jTable.getValueAt(jTable.getSelectedRow(), 1).toString()) && tempTable.getValueAt(i, 0).toString().equalsIgnoreCase(jTable.getValueAt(jTable.getSelectedRow(), 0).toString())){
         		    					docDB.xRecord = tempTable.getValueAt(i, 0).toString();
         		    					break;
             		    			}
         		    			}else if	(jcboSearchType.getSelectedIndex() == 2){    
         		    				if (tempTable.getValueAt(i, 2).toString().equalsIgnoreCase(jTable.getValueAt(jTable.getSelectedRow(), 2).toString()) && tempTable.getValueAt(i, 1).toString().equalsIgnoreCase(jTable.getValueAt(jTable.getSelectedRow(), 1).toString())){
             		    				docDB.xRecord = tempTable.getValueAt(i, 0).toString();
             		    				break;
             		    			}
         		    			}
         		    		}
         		    		docDB.getRecord();
             		    	dispose();	
         		    	}
         		    }else{
         		    	
         		    	if	((e.getKeyCode() > 65 && e.getKeyCode() < 90) || (e.getKeyCode() > 96 && e.getKeyCode() < 105)){
         		    		char[] keyChar ={ e.getKeyChar()};
         		    		String x = new String(keyChar);
         		    		xString = (xString+x);
         		    		setTable(xString);
         		    		timer = 0;
         		    	}
         		    }
				}
			});	
			setTable();
			setTimer();
		}
		return jTable;
	}
	
	private void setTimer(){
		sTimer = new Timer (500,new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				timer++;
				//System.out.println(xString);
				if	(timer == 3){
					xString = "";
				}
			}
		});
		sTimer.start();
	}
	
	private void setTable(String like){
		ActionCursor actionCursor = new ActionCursor();
		try	{
			boolean ok = false;
			String likeQuery = "";
			
			//System.out.println(jcboSearchType.getSelectedIndex()+" ***");
			if	(jcboSearchType.getSelectedIndex() == 0){
				likeQuery = "select " +
		    	 "code, name, status " +
		    	 "from docdb  " +
		    	 "where " +
		    	 "code like '"+like.trim()+"%' and dcode = '"+docDB.dcode+"' " +
		    	 "order by code asc";
			}else if	(jcboSearchType.getSelectedIndex() == 1){
				likeQuery = "select " +
		    	 "code, name, status " +
		    	 "from docdb  " +
		    	 "where " +
		    	 "name like '"+like.trim()+"%' and dcode = '"+docDB.dcode+"' " +
		    	 "order by name asc";
			}else if	(jcboSearchType.getSelectedIndex() == 2){
				likeQuery = "select " +
		    	 "code, name, status " +
		    	 "from docdb  " +
		    	 "where " +
		    	 "status like '"+like.trim()+"%' and dcode = '"+docDB.dcode+"' " +
		    	 "order by status asc";
			}
			
			int n = 0;
			Statement stmt = DBConnect.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(likeQuery);
			actionCursor.startWaitCursor(jContentPane);
			if	(ok == false){
				while(jTable.getRowCount() != 0){
						tableSearch.removeRow(0);
				}
				ok = true;
			}
			while (rs.next()){
				tableSearch.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3)});
				n++;
			}
			if	(n > 0){
				jTable.setRowSelectionInterval(0, 0);
				jTable.grabFocus();
			}
			rs.close();
			stmt.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
			actionCursor.stopWaitCursor(jContentPane);
	}
	
	private void setTable(){
		try	{
			Statement stmt = DBConnect.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(defaultQuery);
			int n = 0;
			while (rs.next()){
				tempTable.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3)});
				tableSearch.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3)});
				n++;
			}
			rs.close();
			stmt.close();
			if	(n > 0){
				jTable.setRowSelectionInterval(0, 0);
				jTable.grabFocus();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	private JComboBox getJcboSearchType() {
		if (jcboSearchType == null) {
			jcboSearchType = new JComboBox();
			jcboSearchType.setLocation(new Point(160, 306));
			jcboSearchType.setSize(new Dimension(264, 20));
			jcboSearchType.addItem("Code");
			jcboSearchType.addItem("Name");
			jcboSearchType.addItem("Status");
			//jcboSearchType.setSelectedIndex(0);
			jcboSearchType.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					jTable.setRowSelectionInterval(0, 0);
					jTable.grabFocus();
					
					if	(jcboSearchType.getSelectedIndex() == 0){
						jlSearch.setText("Code:");
					}
					else if	(jcboSearchType.getSelectedIndex() == 1){
						jlSearch.setText("Name:");
					}
					else if	(jcboSearchType.getSelectedIndex() == 2){
						jlSearch.setText("Status:");
					}
					
				}
			});
		}
		return jcboSearchType;
	}


	/**
	 * This method initializes jtSearch	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJtSearch() {
		if (jtSearch == null) {
			jtSearch = new JTextField();
			jtSearch.setLocation(new Point(160, 330));
			jtSearch.setSize(new Dimension(264, 20));
			jtSearch.addKeyListener(new KeyAdapter(){
				public void keyReleased(KeyEvent e){
					InputMap IMP = jtSearch.getInputMap(jtSearch.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                    KeyStroke ent = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
         		    IMP.put(ent, "none");
         		    if(jtSearch.getText().length()==0){
         		    	tableSearch.setRowCount(0);
         		    	tempTable.setRowCount(0);
         		    	setTable();
         		    }
         		    if	(e.getKeyCode() == 10){
   		    		  setTable(jtSearch.getText().trim());
   		    		  timer = 0;
         		    }
         		   
				}    
			});
		}
		return jtSearch;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
