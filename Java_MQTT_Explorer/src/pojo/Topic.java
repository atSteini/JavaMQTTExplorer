package pojo;

import java.util.ArrayList;

/*
 * @author Florian Steinkellner
 * @date March 22, 2021
 */
public class Topic {
	private String topic;
	private ArrayList<Message> messages;
	private int selectedMessageIndex;
	
	public Topic(String topic) {
		this.topic = removeLast(topic, "/");
		this.messages = new ArrayList<Message>();
		this.selectedMessageIndex = -1;
	}

	@Override
	public String toString() {
		String ret = String.format("Topic %s [%d]", getTopic(), getMessageCounter());
		
		if (getLatestMessage() != null) {
			ret += " - Last Message: " + getLatestMessage();
		}
		
		return ret;
	}

	public String getListView() {
		return String.format("%s [%d]", getTopic(), getMessageCounter());
	}

	private String removeLast(String s, String last) {
		String newTopic = s;
		int lastBeginIndex = newTopic.length()-last.length();
		int lastIndex = newTopic.length(); 
		
		String topicLast = newTopic.substring(lastBeginIndex, lastIndex);
		
		if (topicLast.equals(last)) {
			newTopic = newTopic.substring(0, lastBeginIndex);
		}
		
		return newTopic;
	}
	
	public void selectNext() {
		this.selectedMessageIndex++;
		overFlowMessageIndex();
	}
	
	public void selectPrevious() {
		this.selectedMessageIndex--;
		overFlowMessageIndex();
	}
	
	public void selectFirst() {
		this.selectedMessageIndex = 0;
	}
	
	public void selectLast() {
		this.selectedMessageIndex = getMessageCounter() - 1;
		overFlowMessageIndex();
	}
	
	private void overFlowMessageIndex() {
		this.selectedMessageIndex %= getMessageCounter();
		if (this.selectedMessageIndex < 0) { this.selectedMessageIndex = getMessageCounter() - 1; }
	}
	
	public Message getSelectedMessage() {
		int index = getSelectedMessageIndex();
		return getMessageAt(index >= 0 ? index : 0);
	}
	
	public Message getLatestMessage() {
		return getMessageAt(getMessageCounter() - 1);
	}
	
	public Message getMessageAt(int index) {
		if (index >= getMessageCounter() || index < 0) { return null; }
		
		return messages.get(index);
	}
	
	public void addMessage(Message message) {
		messages.add(message);
		selectLast();
	}
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getMessageCounter() {
		return this.messages.size();
	}

	public ArrayList<Message> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<Message> messages) {
		this.messages = messages;
	}

	public int getSelectedMessageIndex() {
		return selectedMessageIndex;
	}

	public void setSelectedMessageIndex(int selectedMessageIndex) {
		this.selectedMessageIndex = selectedMessageIndex;
	}
}
