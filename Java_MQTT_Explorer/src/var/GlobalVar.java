package var;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import pojo.*;

/*
 * @author Florian Steinkellner
 * @date March 22, 2021
 */
public class GlobalVar {
	//Final Variables (Only to be changed here!)
	public static final Rectangle WINDOW_BOUNDS = new Rectangle(100, 100, 1000, 700);
	public static final Dimension 
							MINIMUM_BOUNDS = new Dimension(830, 660),
							JSON_MINIMUM_BOUNDS = new Dimension(350, 250);
	
	public static final Font txtFieldFont = new Font("Monospaced", Font.PLAIN, 12);
	
	public static final String iconImagePath = "icon/icon_logo_mqtt_2.png";

	public static final int CONN_WAITING = 0, CONN_CONNECTED = 1, CONN_DISCONNECTED = 2;
	public static final int PNL_RAW = 0, PNL_PARSED = 1, PNL_GRAPH = 2;
	public static final String NOT_A_JSON = "nAJ", NOT_A_MESSAGE = "nAM";
	
	//Dynamic Variables
	public static boolean serverSet = false;
	public static MQTTHandler mqttHandler = null;
	public static Message latestMessage = null;
	public static int selectedTopic = -1;
	public static ArrayList<Topic> addedTopics = new ArrayList<>();
	public static int selectedPanel = PNL_RAW;
	public static boolean dataPaused = false;
	public static HashMap<String, Integer> drawPanels = new HashMap<>();
}
