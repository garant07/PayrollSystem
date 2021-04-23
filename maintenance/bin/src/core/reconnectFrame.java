package maintenance.bin.src.core;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import java.awt.GridBagLayout;
import javax.swing.JProgressBar;

import maintenance.bin.src.DBConn.DBConnect;
import maintenance.bin.src.Functions.Message;
import maintenance.bin.src.Functions.removeButton;

import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;

public class reconnectFrame extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JProgressBar jProgressBar = null;

	/**
	 * @param owner
	 */
	public reconnectFrame(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Connecting");
		removeButton.remove(this,5);
		this.setContentPane(getJContentPane());
		this.addWindowListener(new WindowListener(){
			public void windowActivated(WindowEvent arg0) {
				jProgressBar.setIndeterminate(true);
			}
			public void windowClosed(WindowEvent arg0) {}
			public void windowClosing(WindowEvent arg0) {}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
		});
		pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.weightx = 40.0D;
			gridBagConstraints.weighty = 60.0D;
			gridBagConstraints.gridheight = 2;
			gridBagConstraints.gridwidth = 1;
			gridBagConstraints.gridy = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getJProgressBar(), gridBagConstraints);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setPreferredSize(new Dimension(260, 20));
		}
		return jProgressBar;
	}
	

}  //  @jve:decl-index=0:visual-constraint="10,10"
