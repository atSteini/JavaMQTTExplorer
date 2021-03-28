package pojo;

/*
 * @author Florian Steinkellner
 * @date March 22, 2021
 */
public class StringOp {

	public static Integer toInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			Logger.errorLog(e);
			return -1;
		}
	}
	
}
