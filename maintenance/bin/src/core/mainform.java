/*
 * Author: Angelo Mercado - Raymond Tiongco
 * Module: MainForm (Core)
 * Date:   October 12, 2006
 */
package maintenance.bin.src.core;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.birosoft.liquid.LiquidLookAndFeel;

import maintenance.bin.src.Component.WriteLog;
import maintenance.bin.src.DBBASE.docDB;
import maintenance.bin.src.DBConn.DBConnect;
import maintenance.bin.src.DBConn.DbaseConnectorFrame;
import maintenance.bin.src.DBConn.capture_ini;
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.createINI;
import maintenance.bin.src.Functions.filePath;
import maintenance.bin.src.GetGUI.GetGUI;
import maintenance.bin.src.RasterPanel.RasterPanel;
import maintenance.bin.src.Splash.Splash;



public final class mainform extends JFrame {

	private static final long serialVersionUID = 1L;

	//public static double maxGTZ = 999999999.99;
	public static Timer timerTree = null;
	public static int i = 0;
	public static JTree 		tree;
	public static JScrollPane 	scroller;
	public static RasterPanel 	desktop;
	public static BorderLayout 	layout;
	public static JLabel 	    lblMessage;
	public static Statement 	stat;
	public static mainform 		frame ;
	public static LoginDialog 	dialog;
	public static DBConnect dbConn = new DBConnect();
	private static ImageIcon imageIcon;
	final static FileManager fmgr = new FileManager();

	//inherit from login
	private static String 		gcode;
	private static String 		ucode;//
	private static String       lname;
	private static String       fname;

	private JSplitPane 			splitter;
	private MainFrameEvent 		listener = new MainFrameEvent(); 
	private LoggedLabel 		loggedPanel;



	public static boolean connected = false;
	public static capture_ini ini = new capture_ini();
	private Timer timer;
	public static Timer timer2;
	public static Thread check;


	public mainform() {
		File file = new File("maintenance/mycon.dat");
		if (!file.exists()) {
			new DbaseConnectorFrame();
			dispose();
			return;
		}
		new Splash();
		timer = new Timer(1000,new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				try {
					//while (mainform.connected == false){
					if (mainform.connected == false){
						//System.out.println("Connecting To Database..."+Splash.jpbConn.getValue()+"%");
						final Thread x1 = new Thread(new Runnable(){
							public void run() {
								try	{
									stat = DBConnect.getConnection().createStatement();
								}
								catch(SQLException e){}
								catch(Exception e){}
							}
						});
						x1.start();
						if	(DBConnect.isConnected == true){
							connected = DBConnect.isConnected();
							Splash.timer.setInitialDelay(100);
							Splash.timer.setDelay(100);
							Splash.show = true;
							Splash.timeValue = 10;
							Splash.val = 10;
							timer.stop();
							if	(x1.isAlive() == true)x1.interrupt();
							check = new Thread(new Runnable(){
								public void run() {
									checkConn();
									timer2.start();
								}
							});
							check.start();
						}
					}//if
				}catch (Exception e) {
					StringWriter traceWriter = new StringWriter();
					PrintWriter printWriter = new PrintWriter(traceWriter, false);
					e.printStackTrace(printWriter);
					createINI.create("Mainform", "new Timer()", traceWriter.toString());
					Splash.show = false;
				}
			}	
		});
		timer.start();

	} // end constructor

	public mainform(String gcode, String ucode, String lname, String fname) {
		if (fname == null) fname = "";
		if (lname == null) lname = "";
		mainform.gcode = gcode;
		mainform.ucode = ucode;
		mainform.lname = lname;
		mainform.fname = fname;
	}

	private static Container createDesktop() {
		//initialize the layout components
		layout = new BorderLayout();
		desktop = new RasterPanel();
		desktop.setLayout(layout);
		desktop.setOpaque(false);
		//Make dragging a little faster but perhaps uglier.
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		return desktop;
	}

	//Quit the application.
	protected void quit() {
		System.exit(0);
	}

	void setMenu(boolean isDisplayed) {
		if (isDisplayed) {
			createTree(); 
			createStatusBar();
			setTree();
		} else {
			splitter.removeAll();
			JInternalFrame[] iframe = desktop.getAllFrames();
			for (int i = 0; i < iframe.length; i++){
				iframe[i].removeAll();
			}
		}
		validate();
	}



	private void setLogin() {
		desktop.removeAll();
		dialog = new LoginDialog(frame);
		dialog.setLocationRelativeTo(frame);
	}

	private void createStatusBar() {
		StatusBar statusBar = new StatusBar();
		lblMessage = new JLabel(" ");
		statusBar.add(lblMessage);
		desktop.add(statusBar,BorderLayout.SOUTH);
	}
	private void createTree() {

		//Create the nodes.
		DefaultMutableTreeNode left =
			new DefaultMutableTreeNode("Application");
		createNodes(left);

		//Creates a tree that allows one selection at a time.
		tree = new JTree(left);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		//listen for when the selection change
		tree.addTreeSelectionListener(listener);
		//remove default key listener
		InputMap IMP = tree.getInputMap(JTree.WHEN_FOCUSED);
		KeyStroke ent = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		IMP.put(ent, "none");

		InputMap IMPU = tree.getInputMap(JTree.WHEN_FOCUSED);
		KeyStroke up = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
		IMPU.put(up, "none");

		InputMap IMPd = tree.getInputMap(JTree.WHEN_FOCUSED);
		KeyStroke down = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
		IMPd.put(down, "none");

		InputMap IMPl = tree.getInputMap(JTree.WHEN_FOCUSED);
		KeyStroke leftl = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
		IMPl.put(leftl, "none");

		InputMap IMPr = tree.getInputMap(JTree.WHEN_FOCUSED);
		KeyStroke right = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
		IMPr.put(right, "none");


		tree.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e) {
				if	(e.getKeyCode() == 10){
					//System.out.println("enter");

					DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
					tree.getLastSelectedPathComponent();
					Statement stat;
					ResultSet rs;
					if (node == null) return;
					Object nodeInfo = node.getUserObject();
					if (node.isLeaf()) {
						String sqlTree = DBConnect.Select("mmenu",
								"jclass",
								"mmenu.desc = '"+tree.getLastSelectedPathComponent().toString()+"' and " +
								"mmenu.jclass != ' ' ",
								null);
						try {
							setCursor(new Cursor(Cursor.WAIT_CURSOR));
							stat = DBConnect.getConnection().createStatement();
							rs = stat.executeQuery(sqlTree);
							while (rs.next()) {
								desktop.setLayout(null);
								try	{
									//									Inventory.describe = tree.getLastSelectedPathComponent().toString();
									//									Inventory.mode = rs.getString("jclass").substring(rs.getString("jclass").indexOf(' '),rs.getString("jclass").length());
									docDB.dcode = rs.getString("jclass").substring(rs.getString("jclass").indexOf(' ')+1,rs.getString("jclass").lastIndexOf(' '));
									docDB.screen = rs.getString("jclass").substring(rs.getString("jclass").length()-1, rs.getString("jclass").length());
									docDB.dDesc = rs.getString("jclass").substring(rs.getString("jclass").lastIndexOf(' '),rs.getString("jclass").length());
									new GetGUI(desktop,rs.getString("jclass").substring(0, rs.getString("jclass").indexOf(' ')));
								}catch(Exception e1){
									new GetGUI(desktop,rs.getString("jclass"));
								}
							}	
						} catch (SQLException e1) {
							StringWriter traceWriter = new StringWriter();
							PrintWriter printWriter = new PrintWriter(traceWriter, false);
							e1.printStackTrace(printWriter);
							createINI.create("Mainform", "MainFrameEvent-valueChanged", traceWriter.toString());
						} finally {	
							setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						}
					} //end if (node.isLeaf())	

				}
				else if	(e.getKeyCode() == 40){
					//System.out.println("down");
					i += 1;
					tree.setSelectionRow(i);
				}
				else if	(e.getKeyCode() == 38){
					//System.out.println("up");
					if	(i > 0)i -= 1;
					tree.setSelectionRow(i);
				}
				else if	(e.getKeyCode() == 37){
					//System.out.println("left");
					/*setMenu(false);
					setMenu(true);
					tree.grabFocus();
					tree.setSelectionRow(i);*/
					//TreePath temp = tree.get
					//tree.collapsePath(temp);
					int row = tree.getRowCount() - 1;
					//int row = tree.getRowCount() + 1;
					while (row >= 1) {
						tree.collapseRow(row);
						row--;
					}
				}
				else if	(e.getKeyCode() == 39){
					//System.out.println("right");
					tree.setExpandsSelectedPaths(true);
					tree.expandPath(tree.getSelectionPath());
				}
			}
		});

		tree.addMouseListener(new MouseAdapter(){

			public void mouseReleased(MouseEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
				tree.getLastSelectedPathComponent();
				Statement stat;
				ResultSet rs;
				if (node == null) return;
				Object nodeInfo = node.getUserObject();
				if (node.isLeaf()) {
					String sqlTree = DBConnect.Select("mmenu",
							"jclass",
							"mmenu.desc = '"+tree.getLastSelectedPathComponent().toString()+"' and " +
							"mmenu.jclass != ' ' ",
							null);
					try {
						setCursor(new Cursor(Cursor.WAIT_CURSOR));
						stat = DBConnect.getConnection().createStatement();
						rs = stat.executeQuery(sqlTree);
						while (rs.next()) {
							desktop.setLayout(null);
							try	{
								//								Inventory.describe = tree.getLastSelectedPathComponent().toString();
								//								Inventory.mode = rs.getString("jclass").substring(rs.getString("jclass").indexOf(' '),rs.getString("jclass").length());
								docDB.dcode = rs.getString("jclass").substring(rs.getString("jclass").indexOf(' ')+1,rs.getString("jclass").lastIndexOf(' '));
								docDB.screen = rs.getString("jclass").substring(rs.getString("jclass").length()-1, rs.getString("jclass").length());
								docDB.dDesc = rs.getString("jclass").substring(rs.getString("jclass").lastIndexOf(' '),rs.getString("jclass").length());
								new GetGUI(desktop,rs.getString("jclass").substring(0, rs.getString("jclass").indexOf(' ')));
							}catch(Exception e1){
								new GetGUI(desktop,rs.getString("jclass"));
							}
						}	
					} catch (SQLException e1) {
						StringWriter traceWriter = new StringWriter();
						PrintWriter printWriter = new PrintWriter(traceWriter, false);
						e1.printStackTrace(printWriter);
						createINI.create("Mainform", "MainFrameEvent-valueChanged", traceWriter.toString());
					} finally {	
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					}
				} //end if (node.isLeaf())	
			}

		});

		JPanel panelLogged = new JPanel();
		//panelLogged.setLayout();

		scroller = new JScrollPane(tree);
		// scroller.setPreferredSize(new Dimension(250,300));
		scroller.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		Thread th = new Thread(loggedPanel = new LoggedLabel());
		th.start();
		//loggedPanel.setPreferredSize(new Dimension(160,loggedPanel.getHeight()+10));

		loggedPanel.setPreferredSize(new Dimension(240,80));
		panelLogged.setLayout(new BorderLayout());
		panelLogged.add(loggedPanel,BorderLayout.NORTH);

		panelLogged.add(scroller,BorderLayout.CENTER);

		splitter = new JSplitPane();
		splitter.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitter.setLeftComponent(panelLogged);
		splitter.setRightComponent(null);

		desktop.add(splitter,BorderLayout.WEST);



	}

	static void setTree(){
		//		System.out.println("hello1");
		timerTree = new Timer(1000,new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				JInternalFrame[] in = desktop.getAllFrames();
				if	(in.length == 0 && desktop.isVisible() && !tree.isFocusOwner()){
					//					System.out.println("hello");
					tree.grabFocus();
					//i = 0;
					tree.setSelectionRow(i);
					i = i;
				}
			}	
		});
		timerTree.start();
	}

	private void createNodes(DefaultMutableTreeNode left) {

		DefaultMutableTreeNode category = null;
		DefaultMutableTreeNode frame = null;
		DefaultMutableTreeNode subframe = null;

		Statement stat;
		Statement stat2;
		Statement stat3;
		ResultSet rs;
		ResultSet rs2;
		ResultSet rs3;

		String sql = DBConnect.Select("mmenu,groupmenu",
				"*",
				"mmenu.mcode = '' and groupmenu.mcode = ''  and groupmenu.position = '"+gcode+"' and groupmenu.code = mmenu.code",
		"mmenu.level asc");



		try {

			stat = DBConnect.getConnection().createStatement();
			stat2 = DBConnect.getConnection().createStatement();
			stat3 = DBConnect.getConnection().createStatement();

			rs = stat.executeQuery(sql);

			while (rs.next()) {
				category = new DefaultMutableTreeNode(rs.getString(3));
				left.add(category);

				String sql2 = DBConnect.Select("mmenu, groupmenu",
						"*",
						"mmenu.mcode = '"+rs.getString(2)+"' and " +
						"groupmenu.mcode = '"+rs.getString(2)+"' and  " +
						"mmenu.code = groupmenu.code and " +
						"groupmenu.position = '"+gcode+"'",
				"mmenu.level asc");

				rs2 = stat2.executeQuery(sql2);

				while (rs2.next()) {
					frame = new DefaultMutableTreeNode(rs2.getString(3));
					category.add(frame);
					String sqlSubFrame = DBConnect.Select("mmenu, groupmenu",
							"*",
							"mmenu.mcode = '"+rs2.getString(2)+"' and " +
							"mmenu.code = groupmenu.code and " +
							"groupmenu.mcode = '"+rs2.getString(2)+"' and " +
							"groupmenu.position = '"+gcode+"'",
					"mmenu.level asc");
					rs3 = stat3.executeQuery(sqlSubFrame);
					while (rs3.next()) {
						subframe = new DefaultMutableTreeNode(rs3.getString(3).toString());
						frame.add(subframe);
					}
				}
			}
		} catch (SQLException e) {
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("Mainform", "CreateNodes", traceWriter.toString());
		}
	}

	private class MainFrameEvent 
	implements 
	ActionListener,WindowListener,TreeSelectionListener {

		//React to menu selections.
		public void actionPerformed(ActionEvent e) {}

		public void windowClosing(WindowEvent arg0) {
			int save = Message.messageYesNo("Are You Sure?");
			if (save == JOptionPane.YES_OPTION) {
				try {
					DBConnect.getConnection().setAutoCommit(true);
					String update = DBConnect.Update("user","loggedin = 'N'","userid = '"+ucode+"'");

					stat.execute(update);
					WriteLog.WriteLog("Logged off to back-end");

					//call login screen
					setMenu(false);
					JInternalFrame[] iframe = desktop.getAllFrames();
					for (int i = 0; i < iframe.length; i++){
						iframe[i].setVisible(false);
						iframe[i].removeAll();
					}
					lblMessage.setVisible(false);
					setLogin();
					dialog.setVisible(true);
					timerTree.stop();
					timerTree = null;
				} catch(Exception exc) {
					Splash.show = false;
					StringWriter traceWriter = new StringWriter();
					PrintWriter printWriter = new PrintWriter(traceWriter, false);
					exc.printStackTrace(printWriter);
					createINI.create("Mainform", "MainFrameEvent-windowClosing", traceWriter.toString());
				}
			}

		}

		public void windowOpened(WindowEvent arg0) {}
		public void windowClosed(WindowEvent arg0) {}
		public void windowIconified(WindowEvent arg0) {}
		public void windowDeiconified(WindowEvent arg0) {	}
		public void windowActivated(WindowEvent arg0) {}
		public void windowDeactivated(WindowEvent arg0) {}

		/* Required by TreeSelection Interface*/
		public void valueChanged(TreeSelectionEvent e) {
			/*DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
					tree.getLastSelectedPathComponent();
		Statement stat;
		ResultSet rs;

		if (node == null) return;

		Object nodeInfo = node.getUserObject();
		if (node.isLeaf()) {

			String sqlTree = DBConnect.Select("mmenu",
     				"jclass",
     				"mmenu.desc = '"+tree.getLastSelectedPathComponent().toString()+"' and " +
     				"mmenu.jclass != ' ' ",
     				null);
			try {
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				stat = DBConnect.getConnection().createStatement();
				rs = stat.executeQuery(sqlTree);
				while (rs.next()) {
					desktop.setLayout(null);
					try	{
						Inventory.describe = tree.getLastSelectedPathComponent().toString();
						Inventory.mode = rs.getString("jclass").substring(rs.getString("jclass").indexOf(' '),rs.getString("jclass").length());
						docDB.dcode = rs.getString("jclass").substring(rs.getString("jclass").indexOf(' ')+1,rs.getString("jclass").lastIndexOf(' '));
						docDB.screen = rs.getString("jclass").substring(rs.getString("jclass").length()-1, rs.getString("jclass").length());
						docDB.dDesc = rs.getString("jclass").substring(rs.getString("jclass").lastIndexOf(' '),rs.getString("jclass").length());
						new GetGUI(desktop,rs.getString("jclass").substring(0, rs.getString("jclass").indexOf(' ')));
					}catch(Exception e1){
						new GetGUI(desktop,rs.getString("jclass"));
					}
			        //desktop.setLayout(layout);
					//createFrame(rs.getString("jclass"));
				}	
			} catch (SQLException e1) {
				StringWriter traceWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(traceWriter, false);
				e1.printStackTrace(printWriter);
				createINI.create("Mainform", "MainFrameEvent-valueChanged", traceWriter.toString());
			} finally {	
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		} //end if (node.isLeaf())	*/	   
		} //end public void valueChanged


	} 


	private static void createAndShowGUI() {
		try {
			LiquidLookAndFeel.setToolbarButtonsFocusable(false);
			UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
			UIManager.put("ComboBox.disabledBackground",  new Color(255,255,255));
			UIManager.put("InternalFrame.icon", getImageIcon());
			LiquidLookAndFeel.setLiquidDecorations(true, "mac");
			JDialog.setDefaultLookAndFeelDecorated(true);
			JFrame.setDefaultLookAndFeelDecorated(true);
		} 
		catch (Exception e)  {
			StringWriter traceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(traceWriter, false);
			e.printStackTrace(printWriter);
			createINI.create("Mainform", "createAndShowGUI", traceWriter.toString());
		}

		frame = new mainform();
		//frame.setAlwaysOnTop(true);
		frame.setUndecorated(true);
		frame.setIconImage(getImage());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		//Display the window
		frame.setTitle("Payroll "+filePath.getVersion()+" (Connected to "+ini.host+" @ "+ini.dbase+")");
		frame.setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize(screenSize);
		frame.add(createDesktop());
		frame.addWindowListener(frame.listener);
		frame.getRootPane().addMouseMotionListener(new MouseMotionListener(){
			public void mouseDragged(MouseEvent arg0){
				//Do nothing on drag
			}
			public void mouseMoved(MouseEvent arg0) {
				//Do nothing on drag
				//				--------------------------------------------------------
				try	{
					int n  = 0;
					int m = 0;
					int l = 0;
					ArrayList<String> userlist = new ArrayList<String>();
					String query = "select userid " +
					"from user " +
					"where loggedin = 'Y'";

					Statement stmt = DBConnect.getConnection().createStatement();
					ResultSet rs = stmt.executeQuery(query);


					while(rs.next()){
						userlist.add(rs.getString(1));
						n++;
						m = n;
					}

					for(int i=0; i<n; i++){
						if(userlist.get(i).equalsIgnoreCase(dialog.txtUserName.getText()) == false)
							l++;
						else
							l--;
					}

					if(l == m){
						desktop.removeAll();
						JOptionPane.showMessageDialog(null, "You were force Logout\n the System will exit.");
						System.exit(0);
					}

					rs.close();
					stmt.close();

				}catch(Exception e){
					e.printStackTrace();
					System.out.println("getUser "+e.getMessage());
				}
				//				----------------------------------------------------
			}
		});
		frame.setLogin();

	}

	static public void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				createAndShowGUI();
			}
		});
	}

	static String getFname() {
		return fname;
	}

	public static String getGcode() {
		return gcode;
	}

	public static String getLname() {
		return lname;
	}

	public static String getUcode() {
		return ucode;
	}

	private void checkConn(){
		timer2 = new Timer(800,new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				new checkConnection();
			}
		});
		//timer2.start();
	}

	private static Image getImage(){
		return Toolkit.getDefaultToolkit().getImage(fmgr.getIcon());
	}

	private static ImageIcon getImageIcon(){
		return imageIcon = new ImageIcon(getImage());
	}

}


