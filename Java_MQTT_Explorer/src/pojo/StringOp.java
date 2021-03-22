package pojo;

/*
 * @author Florian Steinkellner
 * @date March 22, 2021
 */
public class StringOp {

	public static Integer toInt(String s) {
		int i = -1;
		try {
			i = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			Logger.errorLog(e);
			return -1;
		}
		
		return i;
	}
	
}
