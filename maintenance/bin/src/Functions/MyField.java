package maintenance.bin.src.Functions;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

public class MyField extends JTextField implements KeyListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int count = 0;
	public int maxp = 0;
	boolean decimal;
	int dcount = 0;
	boolean textf;
	boolean period = false;
	boolean negative = false;
	boolean nsign = false;
	boolean capital = false;
	char ret;
	//String chars = ",./<>?~`!@#$%^&*()_+|/*-+";
	String chars = "";
	
	
	
	 /**
	 * 
	 * @param TextField    - True if alphanumeric
	 * @param Length       - Max length
	 * @param DecimalPlace - 0 if no decimal places
	 * @param Nsign     - true if allowed
	 * 
	 */
	public MyField(boolean TextField, int Length, int DecimalPlace, boolean Nsign ){
		super(null,null,Length);
		nsign = Nsign;
		textf = TextField;
		dcount = DecimalPlace+1;
		if (DecimalPlace >= 1)decimal = true; else decimal = false;
		maxp = Length;
		if(textf == false)this.setHighlighter(null);
		addKeyListener(this);
		this.setDisabledTextColor(new Color(139,113,113));
	}
	
	 /**
	 * 
	 * @param TextField    - True if alphanumeric
	 * @param Length       - Max length
	 * @param DecimalPlace - 0 if no decimal places
	 * 
	 */
	 public MyField(boolean TextField, int Length, int DecimalPlace){
		super(null,null,Length);
		textf = TextField;
		dcount = DecimalPlace+1;
		if (DecimalPlace >= 1)decimal = true; else decimal = false;
		maxp = Length;
		if(textf == false)this.setHighlighter(null);
		addKeyListener(this);
		this.setHorizontalAlignment(MyField.RIGHT);
		this.setDisabledTextColor(new Color(139,113,113));
	}
	
	
	 /**
	 * 
	 * @param TextField    - True if alphanumeric
	 * @param Length       - Max length
	 * @param Nsign     - true if allowed
	 */
	public MyField(boolean TextField, int Length, boolean Nsign){
		super(null,null,Length);
		nsign = Nsign;
		textf = TextField;
		dcount = dcount+1;
		if (dcount == 0)decimal = false;
		maxp = Length;
		if(textf == false)this.setHighlighter(null);
		addKeyListener(this);
		this.setDisabledTextColor(new Color(139,113,113));
	}
	
	 /**
	 * 
	 * @param TextField    - True if alphanumeric
	 * @param Length       - Max length
	 */ 
	public MyField(boolean TextField, int Length){
		super(null,null,Length);
		textf = TextField;
		dcount = dcount+1;
		if (dcount == 0)decimal = false;
		maxp = Length;
		if(textf == false)this.setHighlighter(null);
		addKeyListener(this);
		this.setDisabledTextColor(new Color(139,113,113));
	} 
	

	public void keyTyped(KeyEvent e) {
		int textp = this.getText().length();
        int length = textp;
        int no = e.getKeyChar();
        int dtr;
		
        if (textf == true){
            int abc = e.getKeyChar();
            
            if (abc == 44){
        		try	{
        			String ghi = this.getText().substring(0,this.getCaretPosition())+",";
        			String ghi2 = this.getText().substring(0,this.getCaretPosition()+1)+",";
        			if(ghi.contains("',") == true)e.setKeyChar((char)KeyEvent.VK_CLEAR);
        			if(ghi2.contains("',") == true)e.setKeyChar((char)KeyEvent.VK_CLEAR);
        		}catch(Exception e3){
        			String def = this.getText() + ",";
                	if (def.contains("',") == true)e.setKeyChar((char)KeyEvent.VK_CLEAR);
        		}
            }else if (abc == 39){
        		try	{
        			String ghi4 = this.getText().substring(0,this.getCaretPosition())+"'";
        			String ghi5 = this.getText().substring(0,this.getCaretPosition()+1)+"'";
        			if(ghi4.contains(",'") == true)e.setKeyChar((char)KeyEvent.VK_CLEAR);
        			if(ghi5.contains(",'") == true)e.setKeyChar((char)KeyEvent.VK_CLEAR);
        		}catch(Exception e4){
        			String def = this.getText() + "'";
                	if (def.contains(",'") == true)e.setKeyChar((char)KeyEvent.VK_CLEAR);
        		}
            }
    		/*if ((abc >= 97 && abc <= 122) && (capital == true))
    			this.setText(this.getText().toUpperCase());*/
            
            if (chars.length() >= 1){
            	char[] Lchar = chars.toCharArray();
            	for (int i=0; i<Lchar.length;i++){
            		if (e.getKeyChar() == Lchar[i])e.setKeyChar((char)KeyEvent.VK_CLEAR);
            	}
            }
            
            if (length == maxp && abc != 8){
            	e.consume();
            }
            if (capital == true)consumeSmallLetters(abc, e);
        }else{
        	if ((this.getCaretPosition() > this.getText().indexOf(".") && this.getText().indexOf(".")!=-1) && (this.getText().length() - this.getText().indexOf(".") == dcount) && (decimal==true)){
        		if (no != 8){
        			//System.out.println("hi");
        			e.consume();
        		}
    		}
    		try{
            	dtr = this.getText().indexOf(".");
            	if (dtr == -1){
            		count = 0;
            	}
            }
            catch(Exception xe){}
            if ((no == 127) && (decimal == true)){
            	if (this.getText().indexOf("-")==-1 && this.getCaretPosition() == 0)negative = false;
            	if(this.getText().length() == 0){
               	 count = 0;
               	 period = false;
                }
            	if (this.getCaretPosition() > this.getText().indexOf("."))count = count - 1;
            }else{
            	if (this.getText().indexOf("-")==-1 && this.getCaretPosition() == 0)negative = false;
            }
            if( (no==KeyEvent.VK_BACK_SPACE)  && (decimal == true)){
            	 if (this.getText().indexOf("-")==-1 && this.getCaretPosition() == 0)negative = false;
                 if(this.getText().length() == 0){
                	 count = 0;
                	 period = false;
                 }
                 if (this.getCaretPosition() > this.getText().indexOf("."))count = count - 1;
            }else{
            	if (this.getText().indexOf("-")==-1 && this.getCaretPosition() == 0)negative = false;
            }
            if ((no == KeyEvent.VK_PERIOD) && 
               (decimal == true) && (period == false) ){
            	period = true;
            }
            if (no == 45 && negative == false && this.getCaretPosition() == 0){
            	negative = true;
            }else if (no == 45 && negative == true && nsign == true){
            	e.consume();
            }
            
            if (count >= 1){
            	if((no > 47 && no < 58)  || (no==45 && nsign == true && this.getCaretPosition() == 0) && (decimal == true)){
            		if (this.getCaretPosition() > this.getText().indexOf(".")){count++; }
                	if ((count > dcount) && (this.getCaretPosition() > this.getText().indexOf(".")) ){
                		if (no != 8){
                			e.setKeyChar((char)KeyEvent.VK_CLEAR);
                		}
                  		count = count - 1;
                  	}
                }else{
                	if (no != 8){
                		e.setKeyChar((char)KeyEvent.VK_CLEAR);
                	}
                }
            }
            else{
                if((no > 47 && no < 58) || 
                  (no == 45 && nsign == true && this.getCaretPosition() == 0) || 
                  (no == KeyEvent.VK_PERIOD && this.getText().length() == 0 || this.getCaretPosition() > 0) && (decimal == true)){
                	count++;
                	if	((no > 47 && no < 58) || (no == 46) || (no == 45 && this.getCaretPosition() == 0)){
                		if	(this.getText().contains("-") == true){
                			if(no == 45)e.consume();
                		}
                	}else{
                		if(no != 8)e.consume();
                	}
                		
                	if ((count > dcount) && (this.getCaretPosition() > this.getText().indexOf("."))){
                		if (no != 8){
                			e.setKeyChar((char)KeyEvent.VK_CLEAR);
                		}
                  		count = count - 1;
                  	}
                	
                }
                else{
                	if (no != 8){
                		e.setKeyChar((char)KeyEvent.VK_CLEAR);
                	}
                }
            }
            
            
            if (length == maxp && no != 8)e.consume();
        }
	}
	
	
	public void setCapital(boolean caps){
		capital = caps;
	}
	
	public void setLimitChars(String character){
		chars = character;
	}

	public void keyPressed(KeyEvent e) {
		int abc = e.getKeyChar();
		if ((textf == true && this.getText().length() != maxp) && (abc >= 65 && abc <= 122) && (capital == true)){
			if	(e.isShiftDown() == false){
				e.setKeyChar(capital(e.getKeyCode()));
				this.setText(this.getText()+KeyEvent.getKeyText(e.getKeyCode()));
			}
		}
	}
	
	public void keyReleased(KeyEvent e) {}
	
	private char capital(int CHAR){
		switch (CHAR){
		case 97:
			ret = 'A';
			break;
		case 98:
			ret = 'B';
			break;
		case 99:
			ret = 'C';
			break;
		case 100:
			ret = 'D';
			break;
		case 101:
			ret = 'E';
			break;
		case 102:
			ret = 'F';
			break;
		case 103:
			ret = 'G';
			break;
		case 104:
			ret = 'H';
			break;
		case 105:
			ret = 'I';
			break;
		case 106:
			ret = 'J';
			break;
		case 107:
			ret = 'K';
			break;
		case 108:
			ret = 'L';
			break;
		case 109:
			ret = 'M';
			break;
		case 110:
			ret = 'N';
			break;
		case 111:
			ret = 'O';
			break;
		case 112:
			ret = 'P';
			break;
		case 113:
			ret = 'Q';
			break;
		case 114:
			ret = 'R';
			break;
		case 115:
			ret = 'S';
			break;
		case 116:
			ret = 'T';
			break;
		case 117:
			ret = 'U';
			break;
		case 118:
			ret = 'V';
			break;
		case 119:
			ret = 'W';
			break;
		case 120:
			ret = 'X';
			break;
		case 121:
			ret = 'Y';
			break;
		case 122:
			ret = 'Z';
			break;
		}
		return ret;
	}
	
	private void consumeSmallLetters(int xchar, KeyEvent e){
		if (xchar == 97)e.consume();
		else if (xchar == 98)e.consume();
		else if (xchar == 99)e.consume();
		else if (xchar == 100)e.consume();
		else if (xchar == 101)e.consume();
		else if (xchar == 102)e.consume();
		else if (xchar == 103)e.consume();
		else if (xchar == 104)e.consume();
		else if (xchar == 105)e.consume();
		else if (xchar == 106)e.consume();
		else if (xchar == 107)e.consume();
		else if (xchar == 108)e.consume();
		else if (xchar == 109)e.consume();
		else if (xchar == 110)e.consume();
		else if (xchar == 111)e.consume();
		else if (xchar == 112)e.consume();
		else if (xchar == 113)e.consume();
		else if (xchar == 114)e.consume();
		else if (xchar == 115)e.consume();
		else if (xchar == 116)e.consume();
		else if (xchar == 117)e.consume();
		else if (xchar == 118)e.consume();
		else if (xchar == 119)e.consume();
		else if (xchar == 120)e.consume();
		else if (xchar == 121)e.consume();
		else if (xchar == 122)e.consume();
	}
	
	
}

