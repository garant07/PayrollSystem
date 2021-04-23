/**
 * Author       : Raymond A. Tiongco
 * Date Created : July 6, 2006
 * Description  : Extended JOptionPane, to lessen the code
 * 				: for invoking message dialog
 * 
 * "WE CAN DO ALL THINGS THROUGH CHRIST WHO STRENGTHENS US...."
 */

package maintenance.bin.src.Functions;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;

import maintenance.bin.src.core.mainform;


/*
 * Note: Use static import very sparingly, if at all. 
 * It's useful for situations when you need frequent access to a 
 * few static objects from one or two classes. 
 * Overusing static import can result in code that is difficult to read and 
 * maintain, because readers of the code won't know which class defines a 
 * particular static object. Used properly, static import makes code more 
 * readable by removing class name repetition. 
 */

public class Message extends JOptionPane {
	private static final long serialVersionUID = 1L;
	
	public static String messageAdd 	= "Record Successfully Added";
	public static String messageUpdate 	= "Record Successfully Updated";
	public static String messageDelete 	= "Record Successfully Deleted";
	public static String messageExist	= "Record Already Exists";

	public static String catchMessage	= "";
	
	static JDesktopPane desk = mainform.desktop;
	static BorderLayout layout = new BorderLayout();
	
	public static void messageInfo(String strmsg) {
		mainform.desktop.setLayout(null);
		JOptionPane.showInternalMessageDialog(desk,
				strmsg,"Information",
				JOptionPane.INFORMATION_MESSAGE);
		mainform.desktop.setLayout(layout);
	}//public void messageInfo(String strmsg){
	
	public static void messageError(String strmsg) {
		mainform.desktop.setLayout(null);
		JOptionPane.showInternalMessageDialog(desk,strmsg,
				"Error",JOptionPane.ERROR_MESSAGE);
		mainform.desktop.setLayout(layout);
	}//public void messageError(String strmsg){

	public static void messageError1(String strmsg) {
		mainform.desktop.setLayout(null);
		JOptionPane.showInternalMessageDialog(desk,strmsg,
				"Error",JOptionPane.ERROR_MESSAGE);
		mainform.desktop.setLayout(layout);
	}//public void messageError(String strmsg){

	public static void messageWarning(String strmsg) {
		mainform.desktop.setLayout(null);
		JOptionPane.showInternalMessageDialog(desk,strmsg,
				"Warning",JOptionPane.WARNING_MESSAGE);
		mainform.desktop.setLayout(layout);
	}//public void messageWarning(String strmsg){
	
	public static int messageYesNo(String strmsg) {
		mainform.desktop.setLayout(null);
		int ans =  JOptionPane.showInternalConfirmDialog(desk,strmsg,
				"Confirm", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		mainform.desktop.setLayout(layout);
		return ans;
	}//end public void messageYesNo(){
	
	public static int messageYesNo1(String strmsg) {
		mainform.desktop.setLayout(null);
		int ans =  JOptionPane.showInternalConfirmDialog(desk,strmsg,
				"Confirm", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		//mainform.desktop.setLayout(layout);
		return ans;
	}//end public void messageYesNo(){
	
	
	public static void messageInfo(Component component,String strmsg) {
		JOptionPane.showMessageDialog(component,
				strmsg,"Information",
				JOptionPane.INFORMATION_MESSAGE);
	}//public void messageInfo(String strmsg){

	public static void messageError(Component component,String strmsg) {
		JOptionPane.showMessageDialog(component,strmsg,
				"Error",JOptionPane.ERROR_MESSAGE);
	}//public void messageError(String strmsg){

	public static void messageWarning(Component component,String strmsg) {
		JOptionPane.showMessageDialog(component,strmsg,
				"Warning",JOptionPane.WARNING_MESSAGE);
	}//public void messageWarning(String strmsg){

	public static int messageYesNo(Component component,String strmsg) {
		int ans =  JOptionPane.showConfirmDialog(component,strmsg,
				"Confirm", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		return ans;
	}//end public void messageYesNo(){
	
  
}//end class
