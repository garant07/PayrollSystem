package maintenance.bin.src.Functions;

public class format {
	public static String Stringformat(int x, String value){
		String temp = value;
		int y = x - temp.length();
		for	(int xf = 0; xf < y; xf++)value = "0" + value;
		return value;
	}
	
	public static String Stringformat(int x, String value, String Char){
		String temp = value;
		int y = x - temp.length();
		for	(int xf = 0; xf < y; xf++)value = Char + value;
		return value;
	}
	
}
