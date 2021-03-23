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

	public static final int CONN_WAITING = 0, CONN_CONNECTED = 1, CONN_DISCONNECTED = 2;
	
	//Dynamic Variables
	public static boolean serverSet = false;
	public static MQTTHandler mqttHandler = null;
	public static ArrayList<Topic> addedTopics = new ArrayList<>();
}
