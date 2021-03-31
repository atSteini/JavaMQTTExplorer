package pojo;

/*
 * @author Florian Steinkellner
 * @date March 24, 2021
 */
public class StringOp {

	public static Integer toInt(String s) {
		int i;
		try {
			i = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			Logger.errorLog(e);
			i = Integer.MIN_VALUE;
		}
		
		return i;
	}
	
	public static Double toDouble(String s) {
		double d;
		try {
			if (s.contains(",")) {
				s = s.replace(',', '.');
			}
			
			d = Double.parseDouble(s);
		} catch (NumberFormatException e) {
			Logger.errorLog(e);
			d = Double.MIN_VALUE;
		}
		
		return d;
	}
}
