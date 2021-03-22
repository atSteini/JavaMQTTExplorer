package var;

import java.awt.Font;
import java.util.ArrayList;

import pojo.*;

/*
 * @author Florian Steinkellner
 * @date March 22, 2021
 */
public class GlobalVar {
	//Final Variables (Only to be changed here!)
	public static final Font txtFieldFont = new Font("Monospaced", Font.PLAIN, 12);
	
	public static final String iconImagePath = "icon/icon_logo_mqtt_1.png";
	
	//Dynamic Variables
	public static boolean serverSet = false;
	public static MQTTHandler mqttHandler = null;
	public static ArrayList<String> addedTopics = new ArrayList<>();
}
