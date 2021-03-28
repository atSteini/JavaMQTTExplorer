package pojo;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/*
 * @author Florian Steinkellner
 * @date March 22, 2021
 */
public class Message {
	String message;
	private int id;
	private int qos;
	private byte[] payload;
	private MessageValue parsedMessage;

	public Message(String message, int qos) {
		this.message = message;
		this.qos = qos;
		this.payload = message.getBytes();
	}
	
	public Message(MqttMessage message) {
		setMessage(message);
	}
	
	@Override
	public String toString() {
		String parsedString = parsedMessage == null ? "" : getParsedMessage().toString();
		String ret = String.format("Message[%d] %s [QOS: %d]", getId(), getMessage(), getQos());
		
		if (!parsedString.isBlank()) {
			ret += " - Parsed to " + parsedString;
		}
		
		return ret;
	}

	public MqttMessage toMqttMessage() {
		MqttMessage msg = new MqttMessage(getPayload());
		msg.setQos(getQos());
		
		return msg;
	}
	
	public String getMessage() {
		return message;
	}

	public MessageValue parseJSONToMessageValue(String message) {
		Gson g = new Gson();
		try {
			return g.fromJson(message, MessageValue.class);
		} catch (IllegalStateException | JsonSyntaxException ex) {
			Logger.errorLog(ex);
		}
		
		return null;
	}
	
	public void setMessage(MqttMessage message) {
		this.id = message.getId();
		this.message = message.toString();
		this.qos = message.getQos();
		this.payload = message.getPayload();
		
		this.parsedMessage = parseJSONToMessageValue(getMessage());
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public MessageValue getParsedMessage() {
		return parsedMessage;
	}

	public void setParsedMessage(MessageValue parsedMessage) {
		this.parsedMessage = parsedMessage;
	}
}
