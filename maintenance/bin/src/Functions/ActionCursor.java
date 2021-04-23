package maintenance.bin.src.Functions;


import java.awt.Cursor;

import javax.swing.JComponent;
import javax.swing.RootPaneContainer;

public class ActionCursor implements iActionCursor {

	public void startWaitCursor(JComponent component){
		
		RootPaneContainer root = (RootPaneContainer)component.getTopLevelAncestor();
		root.getGlassPane().setCursor(WAIT);
		root.getGlassPane().setVisible(true);
		
	}//public static void startWaitCursor(JComponent component){
	
	public void stopWaitCursor(JComponent component){
		
		RootPaneContainer root = (RootPaneContainer)component.getTopLevelAncestor();
		root.getGlassPane().setCursor(DEFAULT);
		root.getGlassPane().setVisible(true);
		
	}//public static void stopWaitCursor(JComponent component){

}

interface iActionCursor {
	Cursor WAIT = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	Cursor DEFAULT = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
}
