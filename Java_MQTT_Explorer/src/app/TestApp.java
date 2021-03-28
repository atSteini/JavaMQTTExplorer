package app;

import javax.swing.JTree;
import javax.swing.text.Position.Bias;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import pojo.Logger;

public class TestApp {

	public static void main(String[] args) {
		String topic = "htl/4CHEL/Labor/Steinkellner/";
		
		System.out.println(removeLast(topic, "/"));
	}
	
	private static String removeLast(String s, String last) {
		String newTopic = s;
		int lastBeginIndex = newTopic.length()-last.length();
		int lastIndex = newTopic.length(); 
		
		String topicLast = newTopic.substring(lastBeginIndex, lastIndex);
		
		if (topicLast.equals(last)) {
			newTopic = newTopic.substring(0, lastBeginIndex);
		}
		
		return newTopic;
	}
	
	public static void searchChildren(DefaultMutableTreeNode node, String parentToSearch, String aChild) {
		int childCount = node.getChildCount();
		
		if (childCount == 0) {
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
			String userObject = (String) parent.getUserObject();
			if (userObject.contains(parentToSearch)) {
				if (!hasChild(parent, aChild)) {
					parent.add(new DefaultMutableTreeNode(aChild));
				}
			}
		}
		
		for (int i = 0; i < childCount; i++) {
			searchChildren((DefaultMutableTreeNode) node.getChildAt(i), parentToSearch, aChild);
		}
	}
	
	public static boolean hasChild(DefaultMutableTreeNode parent, String userObject) {
		for (int i = 0; i < parent.getChildCount(); i++) {
			DefaultMutableTreeNode childAt = (DefaultMutableTreeNode) parent.getChildAt(i);
			if (((String) childAt.getUserObject()).contains(userObject)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static DefaultTreeModel parseTree(String source, char indentChar) {
		String[] lines = source.split(System.getProperty("line.separator"));
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(lines[0]);
		DefaultTreeModel model = new DefaultTreeModel(root);
		JTree tree = new JTree(model);
		
		for (int i = 1; i < lines.length; i++) {
			String newLine = lines[i];
			int newIndent = countOccurences(newLine, indentChar, 0);
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newLine);
			
			for (int j = i; j >= 0; j--) {
				String previousLine = lines[j];
				int previousIndent = countOccurences(previousLine, indentChar, 0);
				
				if (previousIndent == 0) {
					root.add(newNode);
				} else if (newIndent > previousIndent) {
					TreePath path = tree.getNextMatch(previousLine, 0, Bias.Forward);
					
					if (path != null) {
						DefaultMutableTreeNode previousNode = (DefaultMutableTreeNode) path.getLastPathComponent();
						int previousChildCount = previousNode.getChildCount();
						
						previousNode.insert(newNode, previousChildCount == 0 ? previousChildCount : previousChildCount - 1);
						break;
					}
				}
			}
			
			model = new DefaultTreeModel(root);
			tree = new JTree(model);
		}
		
		Logger.infoLog("Parsed\n" + source + "\ninto " + model);
		
		return model;
	}
	
	public static int countOccurences(String someString, char searchedChar, int index) {
		if (index >= someString.length()) {
			return 0;
		}
			    
		int count = someString.charAt(index) == searchedChar ? 1 : 0;
		return count + countOccurences(someString, searchedChar, index + 1);
	}
}
