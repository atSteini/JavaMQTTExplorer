package pojo;

import java.util.Random;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import app.App;
import var.GlobalVar;

/*
 * @author Florian Steinkellner
 * @date March 22, 2021
 */
public class MQTTHandler implements MqttCallback {
	private IMqttClient client;
	private String mqttServer, mqttUser, mqttPwd, mqttClientName;	
	private int mqttPort = 1883;	//Default Port
	private byte mqttQos = 2;		//Default QOS
	private int generatedClientNameLength = 10;
	
	/**
	 * Constructor with Default Port.
	 * @throws MqttException 
	 */
	public MQTTHandler(String mqttServer, String mqttUser, String mqttPwd, String mqttClientName) throws MqttException {
		setMqttServer(mqttServer);
		setMqttUser(mqttUser);
		setMqttPwd(mqttPwd);
		setMqttClientName(mqttClientName);
		
		getConnection();
	}
	
	/**
	 * Constructor.
	 * @throws MqttException 
	 */
	public MQTTHandler(String mqttServer, String mqttUser, String mqttPwd, String mqttClientName, int mqttPort) throws MqttException {
		setMqttServer(mqttServer);
		setMqttUser(mqttUser);
		setMqttPwd(mqttPwd);
		setMqttClientName(mqttClientName);
		setMqttPort(mqttPort);
		
		getConnection();
	}

	@Override
	public String toString() {
		return String.format("Server: %s, User: %s, Password: %s, ClientName: %s, Port: %d, Alive: %b", mqttServer, mqttUser, mqttPwd, mqttClientName, mqttPort, connectionAlive());
	}

	/**
	 * Publishes a message to a specific topic.
	 * @param topic
	 * @param message
	 * @throws MqttPersistenceException
	 * @throws MqttException
	 */
	public void publish(String topic, String message) throws MqttPersistenceException, MqttException {
		if (!connectionAlive()) { return; }
		
		MqttMessage mqttMsg = new MqttMessage(message.getBytes());
		mqttMsg.setQos(mqttQos);
		client.publish(topic, mqttMsg);
		
		Logger.consoleLog("Published message: " + message);
	}
	
	/**
	 * Sets up the connection to the MQTT Server.
	 * @return
	 * @throws MqttException
	 */
	public boolean getConnection() throws MqttException {
		String broker = getBroker();
		
		client = new MqttClient(broker, mqttClientName);
		client.setCallback(this);
		
		MqttConnectOptions options = setUpConnectionOptions();
		Logger.consoleLog("Connecting to broker: " + broker);

		client.connect(options);
		Logger.consoleLog("Connected to broker: " + broker);
		
		return connectionAlive();
	}
	
	/**
	 * Disconnection and closes the MQTT Connection.
	 * @throws MqttException 
	 */
	public void closeConnection() throws MqttException {
		if (client == null) { return; }
		
		client.disconnect();
		client.close();
	}

	/**
	 * Sets up the basic connection-options.
	 * @return
	 */
	protected MqttConnectOptions setUpConnectionOptions() {
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setAutomaticReconnect(true);
		connOpts.setCleanSession(true);
		connOpts.setConnectionTimeout(10);
		connOpts.setUserName(this.mqttUser);
		connOpts.setPassword(this.mqttPwd.toCharArray());
		
		return connOpts;
	}

	public String getBroker() {
		return "tcp://" + this.mqttServer + ":" + this.mqttPort;
	}
	
	public void subscribeTo(Topic topic) throws MqttSecurityException, MqttException {
		if (!connectionAlive()) { return; }
		
		Logger.consoleLog("Subscribing to " + topic + " [QOS:  " + this.mqttQos + "]");
		
		client.subscribe(topic.getTopic(), this.mqttQos);
	}
	
	/**
     * @see MqttCallback#connectionLost(Throwable)
     */
	public void connectionLost(Throwable t) {
		Logger.errorLog("Connection lost:");
		Logger.errorLog(t);
	}

	/**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
	public void deliveryComplete(IMqttDeliveryToken token) {
		Logger.consoleLog("Message delivered successfully " + token.toString());
	}

	/**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		Message arrivedMessage = new Message(message);
		Logger.consoleLog(String.format("Message arrived for topic %s: %s", topic, arrivedMessage.toString()));
		
		findTopic(topic).addMessage(arrivedMessage);
		
		App.messageArrived(topic, message);
	}
	
	public static Topic findTopic(String topic) {
		Topic retTopic = null;
		
		for (Topic searchTopic : GlobalVar.addedTopics) {
			if (searchTopic.getTopic().equals(topic)) {
				retTopic = searchTopic;
			}
		}
		
		return retTopic;
	}
	
	public boolean connectionAlive() {
		return client.isConnected();
	}
	
	protected String generateRandomClientName() {
		int leftLimit = 48; // numeral '0'
	    int rightLimit = 122; // letter 'z'
	    Random random = new Random();

	    return random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(generatedClientNameLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	}

	public IMqttClient getClient() {
		return client;
	}

	public void setClient(IMqttClient client) {
		this.client = client;
	}

	public String getMqttServer() {
		return mqttServer;
	}

	public void setMqttServer(String mqttServer) {
		this.mqttServer = mqttServer;
	}

	public String getMqttUser() {
		return mqttUser;
	}

	public void setMqttUser(String mqttUser) {
		this.mqttUser = mqttUser;
	}

	public String getMqttPwd() {
		return mqttPwd;
	}

	public void setMqttPwd(String mqttPwd) {
		this.mqttPwd = mqttPwd;
	}

	public String getMqttClientName() {
		return mqttClientName;
	}

	public void setMqttClientName(String mqttClientName) {
		if (mqttClientName.equals("")) {
			this.mqttClientName = generateRandomClientName();
			return;
		}
		
		this.mqttClientName = mqttClientName;
	}

	public int getMqttPort() {
		return mqttPort;
	}

	public void setMqttPort(int mqttPort) {
		this.mqttPort = mqttPort;
	}

	public byte getMqttQos() {
		return mqttQos;
	}

	public void setMqttQos(byte mqttQos) {
		this.mqttQos = mqttQos;
	}

	public int getGeneratedClientNameLength() {
		return generatedClientNameLength;
	}

	public void setGeneratedClientNameLength(int generatedClientNameLength) {
		this.generatedClientNameLength = generatedClientNameLength;
	}
}
