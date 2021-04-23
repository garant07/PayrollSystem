package maintenance.bin.src.Functions;


import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/*
 * Created on Jul 5, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Limited extends PlainDocument {
	public static final String LOWERCASE  =
	        "abcdefghijklmnopqrstuvwxyz";
	public static final String UPPERCASE  =
	        "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String ALPHA   = 
	        LOWERCASE + UPPERCASE;
	public static final String NUMERIC = 
	        "0123456789";
	public static final String FLOAT = 
	        NUMERIC + ".";
	public static final String ALPHA_NUMERIC = 
	        ALPHA + NUMERIC;
	
	protected String acceptedChars = null;
	protected boolean negativeAccepted = false;
	
	
	private int decimalPlacesAccepted = 2;
	private int size;

	public Limited(int size) {
		this.size = size;
	}
	
	
	public Limited(int size, String acceptedchars) {
		this.size = size;
		this.acceptedChars = acceptedchars;
	}
	
	public void setNegativeAccepted(boolean negativeaccepted) {
	     if (acceptedChars.equals(NUMERIC) ||
	         acceptedChars.equals(FLOAT) ||
	         acceptedChars.equals(ALPHA_NUMERIC)){
	         negativeAccepted = negativeaccepted;
	        acceptedChars += "-";
	      }
	}

	public void setDecimalPlacesAccepted(int i) 
	{
		 //if (acceptedChars.equals(FLOAT)) 
			this.decimalPlacesAccepted = i;
	}

	 //this is where we'll control all input to our document.
//	If the text that is being entered passes our criteria, then we'll just call
//	super.insertString(...)
	public void insertString(int offset, String str, AttributeSet a) 
					throws BadLocationException {
		
		if (acceptedChars != null)
		{
		if (acceptedChars.equals(UPPERCASE))
	        str = str.toUpperCase();
	     else if (acceptedChars.equals(LOWERCASE))
	        str = str.toLowerCase();

	     for (int i=0; i < str.length(); i++) {
	       if (acceptedChars.indexOf(str.valueOf(str.charAt(i))) == -1)
	         return;
	       }

	     if (acceptedChars.equals(FLOAT) || 
	        (acceptedChars.equals(FLOAT + "-") && negativeAccepted)) {
	    	 
	    	 int indexOfDecPoint = getText(0, getLength()).indexOf("."); 
	        
	    	 if (str.indexOf(".") != -1) {
	           if (indexOfDecPoint != -1) {
	              return;
	              }
	           }
	        
	    	 
	        if (indexOfDecPoint != -1) {

	        	//if (getText(indexOfDecPoint,getLength()).length() == decimalPlacesAccepted)
	        	if ((indexOfDecPoint + decimalPlacesAccepted+1 ) == this.getLength())	
	        		
	        		if(offset > getText(0, getLength()).indexOf("."))
	        			
	        		return;
	        }
	        }

	     if (negativeAccepted && str.indexOf("-") != -1) {
	        if (str.indexOf("-") != 0 || offset != 0 ) {
	           return;
	           }
	        }
		}
	     
		
		if (str == null || this.getLength() >= size) {
			return;
		}
		
		// workaround if pre database value is more than 
		int spaceLeft = size - (this.getLength());
		if (spaceLeft > str.length()) {
			spaceLeft = str.length();
		}
		
		super.insertString(offset, str.substring(0, spaceLeft), a);
		
		//super.insertString(offset, str, a);
	}



}

