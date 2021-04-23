package maintenance.bin.src.GroupMenu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import maintenance.bin.src.DBConn.DBConnect;
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.ResultTableModel.ResultSetTableModel;
import maintenance.bin.src.core.mainform;

public class GroupMenu extends JInternalFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Container container;
	private ResultSetTableModel cboModel;
	private ResultSetTableModel tableModelgroup;
	private ResultSetTableModel tableModelm;
	JTable tbl;
	JTable tblm;
	final JPanel jpan = new JPanel();
	final JPanel jpanm = new JPanel();
	JScrollPane scrollPane;
	JScrollPane sPane;
	JComboBox  jcbopost = new JComboBox();
	JLabel jlblpost = new JLabel("Group");
	JButton jbtnadd = new JButton("<<");
	JButton jbtnrem = new JButton(">>");
	String Vjcbopost;
	String group = "";
	String Querygroup;
	String Querym;
	private ResultSet rs;
	private Statement st = mainform.stat;
	
	String QueryP;
	
	String grp;
	
	public GroupMenu(){
		
		super ("Group Menu", false, true, false, false);
		Querygroup = DBConnect.Select("groupmenu, mmenu",
				"groupmenu.position, mmenu.desc, groupmenu.code, groupmenu.mcode",
				"groupmenu.position = '"+DBConnect.clean(group)+"' and groupmenu.code = mmenu.code and groupmenu.mcode = mmenu.mcode",
				"mmenu.level");
		Querym = DBConnect.Join("mmenu",
				"groupmenu",
				"mmenu.code,  mmenu.desc, mmenu.mcode",
				DBConnect.LEFT_JOIN,
				"mmenu.code = groupmenu.code and groupmenu.position = '"+DBConnect.clean(group)+"' and groupmenu.mcode = mmenu.mcode",
				"groupmenu.mcode is null",
				"mmenu.mcode, groupmenu.mcode asc");
		
		QueryP = DBConnect.Select("userposition",
				"userposition.code,userposition.desc,userposition.authority",
				null,
				"userposition.code asc");
		this.setName("groupmenu");
		container = this.getContentPane();
		container.setLayout(null);
		
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
		
		try{
			cboModel  = new ResultSetTableModel(QueryP);
			tableModelgroup  = new ResultSetTableModel(Querygroup);
			tableModelm  = new ResultSetTableModel(Querym);
			tbl = new JTable(tableModelgroup);
			tblm = new JTable(tableModelm);
			scrollPane = new JScrollPane(tbl);
			sPane = new JScrollPane(tblm);
			reload();
			reloadm();
			
			tblm.getTableHeader().setResizingAllowed(false);
	        tblm.setPreferredScrollableViewportSize(new Dimension(220,210));
	        tblm.getTableHeader().setReorderingAllowed(false); //disabled column dragging
	        ListSelectionModel selectionModelm = tblm.getSelectionModel();
			selectionModelm.clearSelection();
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			tblm.setSelectionMode(0);
			
			TableCellRenderer renderer = new Renderer();
			tblm.setDefaultRenderer(Object.class,renderer);
			
			TableCellRenderer renderer1 = new Renderer1();
			tbl.setDefaultRenderer(Object.class,renderer1);
			
			tbl.getTableHeader().setResizingAllowed(false);
	        tbl.getTableHeader().setMaximumSize(new Dimension(23,56));
	        tbl.setPreferredScrollableViewportSize(new Dimension(220,210));
	        tbl.getTableHeader().setReorderingAllowed(false); //disabled column dragging
	        ListSelectionModel selectionModel = tbl.getSelectionModel();
	        sPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			selectionModel.clearSelection();
			tbl.setSelectionMode(0);
			
			 //Set Bounds
	        jpanm.setBounds(330,60,260,255);
			jpan.setBounds(1,60,260,255);
			
			jpan.add(scrollPane);
			jpanm.add(sPane);
			container.add(jpan);
			container.add(jpanm);
			
			//Set jlblpost
			jlblpost.setBounds(10, 30, 120, 20);
			container.add(jlblpost);
			
			//Set JButton - jbtnadd
			jbtnadd.setBounds(266,130, 60, 50);
			container.add(jbtnadd);
			
			//Set JButton - jbtnrem
			jbtnrem.setBounds(266,190, 60, 50);
			container.add(jbtnrem);
			
			
			//Set jcbopost
			jcbopost.setFont(new Font("",Font.PLAIN, 12));
			jcbopost.setBackground(Color.WHITE);
			jcbopost.setBounds(60, 30, 190, 20);
			container.add(jcbopost);
			
			
			
			try{  
				  int count = 1;
				  
				  cboModel.resultSet = cboModel.statement.executeQuery(QueryP);
				  
				  //jcboPosition.setSelectedItem(tbl.getValueAt(1,4));
				  jcbopost.addItem("Code-Position");
				  while (cboModel.resultSet.next()){
					  String item = cboModel.resultSet.getString("Desc");
					  String item2 = cboModel.resultSet.getString("Code");
					  String fin;
					  fin =  item2 +"-"+ item;
					  jcbopost.addItem(fin);
					 ++count;
				  }//while (tableModel.resultSet.next())
				  }//try
				catch(Exception e){
//					System.out.println("73");
					System.out.println(e.getMessage());
				}//catch
				//cboModel.resultSet = tableModel.statement.executeQuery(QueryP);
		
            //jcboPosition listener
			jcbopost.addActionListener( new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		
	                try {
	                	String getcbo = jcbopost.getSelectedItem().toString();
	                	
		                int nn = getcbo.indexOf("-");
		                //System.out.println(getcbo.substring(0,nn));
		                Vjcbopost = getcbo.substring(0,nn);
		                grp = Vjcbopost;
		                if (jcbopost.getSelectedItem().toString().equals("Code-Position") == true){
		    				jbtnadd.setEnabled(false);
		    				jbtnrem.setEnabled(false);
		    				tbl.setEnabled(false);
		    				tblm.setEnabled(false);
		    			}else{
		    				jbtnadd.setEnabled(true);
		    				jbtnrem.setEnabled(true);
		    				tbl.setEnabled(true);
		    				tblm.setEnabled(true);
		    			}
		                String Querygrp = DBConnect.Select("groupmenu, mmenu",
		                		"groupmenu.position, mmenu.desc, groupmenu.code, groupmenu.mcode",
		                		"groupmenu.position = '"+DBConnect.clean(grp)+"' and groupmenu.code = mmenu.code and " +
		                		"groupmenu.mcode = mmenu.mcode",
		                		"mmenu.mcode");
		                
		                String Querymenu = DBConnect.Join("mmenu",
		                		"groupmenu",
		                		"mmenu.code, mmenu.desc, mmenu.mcode",
		                		DBConnect.LEFT_JOIN,
		                		"mmenu.code = groupmenu.code and groupmenu.position = '"+DBConnect.clean(grp)+"' and groupmenu.mcode = mmenu.mcode",
		                		"groupmenu.mcode is null",
		                		
		                		"mmenu.mcode"
		                		);
		                
//		                System.out.println(Querygrp);
		                
		                tableModelgroup.setQuery(Querygrp);
		                tableModelm.setQuery(Querymenu);
		                reload();
		            	reloadm();
		            	jbtnadd.setEnabled(false);
		            	jbtnrem.setEnabled(false);
		            	
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
	        	}			
	        });
			
			//Add Listener to jbtnadd
			jbtnadd.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae){
					String mcode;
					
					try{
						
						
						String code = tblm.getValueAt(tblm.getSelectedRow(),0).toString();
						
						if (tblm.getValueAt(tblm.getSelectedRow(),2).toString().equals("") == true){
							mcode = " ";
						}else{
							mcode = tblm.getValueAt(tblm.getSelectedRow(),2).toString();
						}
						
						
						
						String insert = DBConnect.Insert("groupmenu",
								"groupmenu.position, groupmenu.mcode, groupmenu.code",
								"'"+DBConnect.clean(grp)+"','"+DBConnect.clean(mcode)+"', '"+DBConnect.clean(code)+"'");
						
//						MsgWriter.msgWriter(30, 0, 
//								Formatter.formatString(4, ' ', ' ', grp) +
//								Formatter.formatString(6, ' ', ' ', mcode) +
//								Formatter.formatString(6, ' ', ' ', code));
						
						tableModelgroup.setInsert(insert);
						String Querygrp = DBConnect.Select("groupmenu, mmenu",
								"groupmenu.position, mmenu.desc, groupmenu.code, groupmenu.mcode",
								"groupmenu.position = '"+DBConnect.clean(grp)+"' and groupmenu.code = mmenu.code and " +
								"groupmenu.mcode = mmenu.mcode",
								"mmenu.level");
	                	
						String Querymenu = DBConnect.Join("mmenu",
	                			"groupmenu",
	                			"mmenu.code,  mmenu.desc, mmenu.mcode",
	                			DBConnect.LEFT_JOIN,
	                			"mmenu.code = groupmenu.code and groupmenu.position = '"+DBConnect.clean(grp)+"' and " +
	                			"groupmenu.mcode = mmenu.mcode",
	                			"groupmenu.mcode is null",
	                			"mmenu.mcode");
						
						tableModelgroup.setQuery(Querygrp);
		                tableModelm.setQuery(Querymenu);
		                
		                reload();
		            	reloadm();
					}catch(Exception e){
						System.out.println("Error sa jbtnadd.addActionListener");
					}
					
					}
			});
			
			//Add Listener to jbtnrem
			jbtnrem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae){
					String code = tbl.getValueAt(tbl.getSelectedRow(),2).toString();
					String mcode = tbl.getValueAt(tbl.getSelectedRow(),3).toString();
					String grpcode = tbl.getValueAt(tbl.getSelectedRow(),0).toString();
					String delete = DBConnect.delete("groupmenu",
							"groupmenu.position = '"+DBConnect.clean(grpcode)+"' and groupmenu.code = '"+DBConnect.clean(code)+"' and " +
							"groupmenu.mcode = '"+DBConnect.clean(mcode)+"'");
					
					try{
						
//						MsgWriter.msgWriter(30, 1, 
//								Formatter.formatString(4, ' ', ' ', grp) +
//								Formatter.formatString(6, ' ', ' ', mcode) +
//								Formatter.formatString(6, ' ', ' ', code));
						
						tableModelgroup.setInsert(delete);
						String Querygrp = DBConnect.Select("groupmenu, mmenu",
								"groupmenu.position, mmenu.desc, groupmenu.code, groupmenu.mcode",
								"groupmenu.position = '"+DBConnect.clean(grp)+"' and groupmenu.code = mmenu.code and " +
								"groupmenu.mcode = mmenu.mcode",
								"mmenu.level");
	                	
	                	String Querymenu = DBConnect.Join("mmenu",
	                			"groupmenu",
	                			"mmenu.code,  mmenu.desc, mmenu.mcode",
	                			DBConnect.LEFT_JOIN,
	                			"mmenu.code = groupmenu.code and groupmenu.position = '"+DBConnect.clean(grp)+"' and " +
	                			"groupmenu.mcode = mmenu.mcode",
	                			"groupmenu.mcode is null",
	                			"mmenu.mcode");
						
	                	tableModelgroup.setQuery(Querygrp);
		                tableModelm.setQuery(Querymenu);
		                reload();
		            	reloadm();
					}catch(Exception e){
						System.out.println("Error sa jbtnadd.addActionListener");
					}
				}
			});
			
			if (jcbopost.getSelectedItem().toString().equals("Code-Position") == true){
				jbtnadd.setEnabled(false);
				jbtnrem.setEnabled(false);
				tbl.setEnabled(false);
				tblm.setEnabled(false);
			}
			
			
			tbl.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent me){
					if(tbl.isEnabled() == true){
					  ListSelectionModel selectionModelm = tblm.getSelectionModel();
					  selectionModelm.clearSelection();
					  jbtnrem.setEnabled(true);
					  jbtnadd.setEnabled(false);
					}
				}
			});
			
			tblm.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent me){
					if(tblm.isEnabled() == true){
					  ListSelectionModel selectionModel = tbl.getSelectionModel();
					  selectionModel.clearSelection();
					  jbtnadd.setEnabled(true);
					  jbtnrem.setEnabled(false);
					}  
				}
			});
			
		}
		catch(ClassNotFoundException classE){
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			classE.printStackTrace(printWriter);
			createINI.create("GroupMenu", "GroupMenu", traceWriter.toString());     
	        dispose();
		}
		catch(SQLException sqlE){
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			sqlE.printStackTrace(printWriter);
			createINI.create("GroupMenu", "GroupMenu", traceWriter.toString());      
	        dispose();
		}
		catch(Exception E){
			Message.messageInfo("Error log has been created");
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			E.printStackTrace(printWriter);
			createINI.create("GroupMenu", "GroupMenu", traceWriter.toString());      
	        dispose();
		}
		
		setSize(600,348);
        setResizable(false);
        setVisible(true);
	}//public GroupMenu()
	
	
	//Function to reload
	public void reload(){
		
		tbl.getColumnModel().getColumn(0).setMaxWidth(0);
        tbl.getColumnModel().getColumn(0).setMinWidth(0);
        tbl.getColumnModel().getColumn(1).setMaxWidth(0);
        tbl.getColumnModel().getColumn(1).setMinWidth(0);
        tbl.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(0);
        tbl.getTableHeader().getColumnModel().getColumn(0).setMinWidth(0);
        tbl.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(250);
        tbl.getTableHeader().getColumnModel().getColumn(1).setMinWidth(250);
        tbl.getColumnModel().getColumn(0).setHeaderValue("Group");
        tbl.getColumnModel().getColumn(1).setHeaderValue("Group Menu");
		
	}//public void reload(){
	
	//Function to reload
	public void reloadm(){
		
		tblm.getColumnModel().getColumn(0).setMaxWidth(0);
        tblm.getColumnModel().getColumn(0).setMinWidth(0);
        tblm.getColumnModel().getColumn(1).setMaxWidth(0);
        tblm.getColumnModel().getColumn(1).setMinWidth(0);
        tblm.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(0);
        tblm.getTableHeader().getColumnModel().getColumn(0).setMinWidth(0);
        tblm.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(250);
        tblm.getTableHeader().getColumnModel().getColumn(1).setMinWidth(250);
        tblm.getColumnModel().getColumn(0).setHeaderValue("Group");
        tblm.getColumnModel().getColumn(1).setHeaderValue("Available Menu");
		
	}//public void reload(){
	
	private class Renderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			for	(int i = 0; i < table.getRowCount(); i++){
				if	((column == 1 && table.getValueAt(i,2).toString().trim().equalsIgnoreCase("") == true) && (row == i)){	
					setText("- "+getText());
				}
				if	((column == 1 && table.getValueAt(i,2).toString().trim().equalsIgnoreCase("") == false) && (row == i)){	
					setText("("+getDescription(table.getValueAt(i,2).toString())+") "+getText());
				}
			}
			
			setBorder(BorderFactory.createLineBorder(getBackground(), 1));
			
			return this;
		}
	}
	
	private String getDescription(String menu){
		String mmenu = "";
		String queryDesc = DBConnect.Select("mmenu", "mmenu.`desc`", "code = '"+DBConnect.clean(menu)+"'", null);
		try	{
			rs = st.executeQuery(queryDesc);
			while(rs.next()){
				mmenu = rs.getString(1);
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return mmenu;
	}
	
	private class Renderer1 extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			for	(int i = 0; i < table.getRowCount(); i++){
				if	((column == 1 && table.getValueAt(i,3).toString().trim().equalsIgnoreCase("") == true) && (row == i)){	
					setText("- "+getText());
				}
			}
			setBorder(BorderFactory.createLineBorder(getBackground(), 1));
			return this;
		}
	}
	
}//public class GroupMenu extends JInternalFrame {
