package maintenance.bin.src.Calendar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;

import com.birosoft.liquid.LiquidComboBoxUI;
import com.sun.java.swing.plaf.motif.MotifComboBoxUI;
import com.sun.java.swing.plaf.windows.WindowsComboBoxUI;


public class DateComboBox extends JComboBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
	
	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	public void setSelectedItem(Object item) {
		// Could put extra logic here or in renderer when item is instanceof Date, Calendar, or String
		// Dont keep a list ... just the currently selected item
		removeAllItems(); // hides the popup if visible
		addItem(item);
		super.setSelectedItem(item);
	}

	public void updateUI() {
		ComboBoxUI cui = (ComboBoxUI) UIManager.getUI(this);
		if (cui instanceof MetalComboBoxUI) {
			cui = new MetalDateComboBoxUI();
		} else if (cui instanceof MotifComboBoxUI) {
			cui = new MotifDateComboBoxUI();
		} else if (cui instanceof WindowsComboBoxUI) {
			cui = new WindowsDateComboBoxUI();
		} else if (cui instanceof LiquidComboBoxUI) {
			cui = new LiquidDateComboBoxUI();
		} 
		setUI(cui);
	}

	// Inner classes are used purely to keep DateComboBox component in one file
	//////////////////////////////////////////////////////////////
	// UI Inner classes -- one for each supported Look and Feel
	//////////////////////////////////////////////////////////////

	class MetalDateComboBoxUI extends MetalComboBoxUI {
		protected ComboPopup createPopup() {
			return new DatePopup( comboBox );
		}
	}

	class WindowsDateComboBoxUI extends WindowsComboBoxUI {
		protected ComboPopup createPopup() {
			return new DatePopup( comboBox );
		}
	}

	class LiquidDateComboBoxUI extends LiquidComboBoxUI {
		protected ComboPopup createPopup() {
			return new DatePopup( comboBox );
		}
	}

	class MotifDateComboBoxUI extends MotifComboBoxUI {
		/**
		 * 
		 */
		 private static final long serialVersionUID = 1L;

		 protected ComboPopup createPopup() {
			 return new DatePopup( comboBox );
		 }
	}

	//////////////////////////////////////////////////////////////
	// DatePopup inner class
	//////////////////////////////////////////////////////////////

	class DatePopup implements ComboPopup, MouseMotionListener,
	MouseListener, KeyListener, PopupMenuListener {

		protected JComboBox comboBox;
		protected Calendar calendar;
		protected JPopupMenu popup;
		protected JLabel monthLabel;
		protected JPanel days = null;
		protected SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM dd, yyyy");

		protected Color selectedBackground;
		protected Color selectedForeground;
		protected Color background;
		protected Color foreground;

		public DatePopup(JComboBox comboBox) {
			this.comboBox = comboBox;
			calendar = Calendar.getInstance();
			Date date = calendar.getTime();
			DateFormat ddate = new SimpleDateFormat("MMMM dd, yyyy");
			comboBox.addItem(ddate.format(date) ) ;

			// check Look and Feel
			/*background = UIManager.getColor("ComboBox.background");
	    foreground = UIManager.getColor("ComboBox.foreground");
	    selectedBackground = UIManager.getColor("ComboBox.selectionBackground");
	    selectedForeground = UIManager.getColor("ComboBox.selectionForeground");*/

			background = UIManager.getColor("ComboBox.background");
			foreground = UIManager.getColor("ComboBox.foreground");
			selectedBackground = UIManager.getColor("ComboBox.selectionBackground");
			selectedForeground = UIManager.getColor("ComboBox.selectionForeground");

			initializePopup();
		}

		//========================================
		// begin ComboPopup method implementations
		//
		public void show() {
			try {
				// if setSelectedItem() was called with a valid date, adjust the calendar
				calendar.setTime( dateFormat.parse( comboBox.getSelectedItem().toString() ) );
			} catch (Exception e) {}
			updatePopup();
			popup.show(comboBox, 0, comboBox.getHeight());
		}

		public void hide() {
			popup.setVisible(false);
		}

		protected JList list = new JList();
		public JList getList() {
			return list;
		}

		public MouseListener getMouseListener() {
			return this;
		}

		public MouseMotionListener getMouseMotionListener() {
			return this;
		}

		public KeyListener getKeyListener() {
			return this;
		}

		public boolean isVisible() {
			return popup.isVisible();
		}

		public void uninstallingUI() {
			popup.removePopupMenuListener(this);
		}


		public void mousePressed( MouseEvent e ) {}
		public void mouseReleased( MouseEvent e ) {}
		// something else registered for MousePressed
		public void mouseClicked(MouseEvent e) {
			if ( !SwingUtilities.isLeftMouseButton(e) )
				return;
			if ( !comboBox.isEnabled() )
				return;
			if ( comboBox.isEditable() ) {
				comboBox.getEditor().getEditorComponent().requestFocus();
			} else {
				comboBox.requestFocus();
			}
			togglePopup();
		}

		protected boolean mouseInside = false;
		public void mouseEntered(MouseEvent e) {
			mouseInside = true;
		}
		public void mouseExited(MouseEvent e) {
			mouseInside = false;
		}

		// MouseMotionListener
		public void mouseDragged(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {}

		// KeyListener
		public void keyPressed(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
		public void keyReleased( KeyEvent e ) {
			if ( e.getKeyCode() == KeyEvent.VK_SPACE ||
					e.getKeyCode() == KeyEvent.VK_ENTER ) {
				togglePopup();
			}
		}

		/**
		 * Variables hideNext and mouseInside are used to
		 * hide the popupMenu by clicking the mouse in the JComboBox
		 */
		public void popupMenuCanceled(PopupMenuEvent e) {}
		protected boolean hideNext = false;
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			hideNext = mouseInside;
		}
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}


		protected void togglePopup() {
			if ( isVisible() || hideNext ) {
				hide();
			} else {
				show();
			}
			hideNext = false;
		}


		// Note *** did not use JButton because Popup closes when pressed
		protected JLabel createUpdateButton(final int field, final int amount) {
			final JLabel label = new JLabel();
			final Border selectedBorder = new EtchedBorder(Color.white,Color.white);
			final Border unselectedBorder = new EmptyBorder(selectedBorder.getBorderInsets(new JLabel()));
			label.setBorder(unselectedBorder);
			label.setForeground(foreground);
			label.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					calendar.add(field, amount);
					updatePopup();
				}
				public void mouseEntered(MouseEvent e) {
					label.setBorder(selectedBorder);
				}
				public void mouseExited(MouseEvent e) {
					label.setBorder(unselectedBorder);
				}
			});
			return label;
		}


		protected void initializePopup() {
			JPanel header = new JPanel(); // used Box, but it wasn't Opaque
			header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
			//header.setBackground(background);
			header.setBackground(new Color(26,33,172));
			header.setOpaque(true);

			JLabel label;
			label = createUpdateButton(Calendar.YEAR, -1);
			label.setText("<<");
			label.setForeground(Color.white);
			label.setToolTipText("Previous Year");

			header.add(Box.createHorizontalStrut(12));
			header.add(label);
			header.add(Box.createHorizontalStrut(12));

			label = createUpdateButton(Calendar.MONTH, -1);
			label.setText("<");
			label.setForeground(Color.white);
			label.setToolTipText("Previous Month");
			header.add(label);

			monthLabel = new JLabel("", JLabel.CENTER);
			//monthLabel.setForeground(foreground);
			monthLabel.setForeground(Color.white);
			header.add(Box.createHorizontalGlue());
			header.add(monthLabel);
			header.add(Box.createHorizontalGlue());

			label = createUpdateButton(Calendar.MONTH, 1);
			label.setText(">");
			label.setForeground(Color.white);
			label.setToolTipText("Next Month");
			header.add(label);

			label = createUpdateButton(Calendar.YEAR, 1);
			label.setText(">>");
			label.setForeground(Color.white);
			label.setToolTipText("Next Year");

			header.add(Box.createHorizontalStrut(12));
			header.add(label);
			header.add(Box.createHorizontalStrut(12));

			popup = new JPopupMenu();
			popup.setBorder(BorderFactory.createLineBorder(Color.black));
			popup.setLayout(new BorderLayout());
			popup.setBackground(background);
			popup.addPopupMenuListener(this);
			popup.add(BorderLayout.NORTH, header);
		}

		// update the Popup when either the month or the year of the calendar has been changed
		protected void updatePopup() {
			monthLabel.setText( monthFormat.format(calendar.getTime()) );
			if (days != null) {
				popup.remove(days);
			}
			days = new JPanel(new GridLayout(0, 7));
			days.setBackground(Color.white);
			days.setOpaque(true);

			Calendar setupCalendar = (Calendar) calendar.clone();
			setupCalendar.set(Calendar.DAY_OF_WEEK, setupCalendar.getFirstDayOfWeek());
			for (int i = 0; i < 7; i++) {
				int dayInt = setupCalendar.get(Calendar.DAY_OF_WEEK);
				JLabel label = new JLabel();
				label.setHorizontalAlignment(JLabel.CENTER);
				label.setForeground(Color.black);
				if (dayInt == Calendar.SUNDAY) {
					label.setText("Sun");
					//label.setForeground(Color.red);
				} else if (dayInt == Calendar.MONDAY) {
					label.setText("Mon");
				} else if (dayInt == Calendar.TUESDAY) {
					label.setText("Tue");
				} else if (dayInt == Calendar.WEDNESDAY) {
					label.setText("Wed");
				} else if (dayInt == Calendar.THURSDAY) {
					label.setText("Thu");
				} else if (dayInt == Calendar.FRIDAY) {
					label.setText("Fri");
				} else if (dayInt == Calendar.SATURDAY){
					label.setText("Sat");
				}
				days.add(label);
				setupCalendar.roll(Calendar.DAY_OF_WEEK, true);
			}

			setupCalendar = (Calendar) calendar.clone();
			setupCalendar.set(Calendar.DAY_OF_MONTH, 1);
			int first = setupCalendar.get(Calendar.DAY_OF_WEEK);

			for (int i = 0; i < (first - 1); i++) {
				days.add(new JLabel(""));
			}
			for (int i = 1; i <= setupCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
				final int day = i;
				final JLabel label = new JLabel(String.valueOf(day));
				label.setHorizontalAlignment(JLabel.CENTER);

				label.setForeground(Color.black);
				label.addMouseListener(new MouseListener() {
					public void mousePressed(MouseEvent e) {}
					public void mouseClicked(MouseEvent e) {}
					public void mouseReleased(MouseEvent e) {
						label.setOpaque(false);
						label.setBackground(background);//background
						label.setForeground(foreground);
						calendar.set(Calendar.DAY_OF_MONTH, day);
						comboBox.setSelectedItem(dateFormat.format(calendar.getTime()));
						hide();
						comboBox.requestFocus();
					}
					public void mouseEntered(MouseEvent e) {
						label.setOpaque(true);
						label.setBackground(new Color(26,33,172));//selectedBackground
						label.setForeground(Color.white);//set days color when mouse over(selectedForeground)
					}
					public void mouseExited(MouseEvent e) {
						label.setOpaque(false);
						label.setBackground(Color.white);//background
						label.setForeground(Color.black);//(foreground)
					}
				});

				days.add(label);
			}

			popup.add(BorderLayout.CENTER, days);
			popup.pack();
		}
	}

}





