package app;


import pojo.*;
import var.*;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubIJTheme;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;

import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Position.Bias;
import javax.swing.JMenuBar;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JTabbedPane;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.JSeparator;

/*
 * @author Florian Steinkellner
 * @date March 21, 2021
 */
public class App extends JFrame {
	private static final long serialVersionUID = 1L;
	
	// UI Components
	private JPanel background;
	private JTextField txtTopic;
	private JTextField txtPubTopic;
	private JTextField txtPubMessage;
	private JTextField txtServer;
	private JTextField txtUser;
	private JTextField txtClientName;
	private JTextField txtPassword;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmExit;
	private JTabbedPane tbpData;
	private JScrollPane scrlTopicTree;
	private JPanel pnlGraph;
	private JPanel pnlRawData;
	private JPanel pnlParsed;
	private JPanel pnlTopic;
	private JPanel pnlPublish;
	private JPanel pnlServerSettings;
	private JLabel lblServer;
	private JLabel lblUser;
	private JLabel lblPassword;
	private JLabel lblClientName;
	private JButton btnSetServer;
	private JSpinner spPort;
	private JLabel lblPort;
	private JLabel lblPubTopic;
	private JLabel lblMessage;
	private JButton btnPublish;
	private JButton btnAddTopic;
	private JLabel lblTopic;
	private JTree treeTopics;
	private JMenuItem mntmSaveSession;
	
	ArrayList<JTextField> inputsServer;
	ArrayList<JTextField> inputsTopic;
	ArrayList<JTextField> inputsPublish;
	ImageIcon iconImage;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App frame = new App();
					frame.setVisible(true);
				} catch (Exception e) {
					Logger.errorLog(e);
				}
			}
		});
	}

	public App() {
		initUI();
	}
	
	public static void messageArrived(String topic, MqttMessage message) {
	}
	
	protected void btnPublishActionPerformed(ActionEvent e) throws MqttPersistenceException, MqttException {
		Logger.buttonLog(e);
		
		GlobalVar.mqttHandler.publish(txtPubTopic.getText(), this.txtPubMessage.getText());
	}

	protected void btnAddTopicActionPerformed(ActionEvent e) {
		Logger.buttonLog(e);
		
		String topic = txtTopic.getText();
		
		GlobalVar.addedTopics.add(topic);
		
		Logger.consoleLog("Added Topic: " + topic);
	}

	protected void btnSetServerActionPerformed(ActionEvent e) throws MqttException {
		Logger.buttonLog(e);
		
		GlobalVar.mqttHandler = new MQTTHandler(
						txtServer.getText(),
						txtUser.getText(),
						txtPassword.getText(),
						txtClientName.getText(),
						(int) spPort.getValue());
		
		if (GlobalVar.mqttHandler.connectionAlive()) {
			btnAddTopic.setEnabled(true);
			btnPublish.setEnabled(true);
			
			txtClientName.setText(GlobalVar.mqttHandler.getMqttClientName());
			
			treeTopics.setModel(parseTree("Connected to " + GlobalVar.mqttHandler.getBroker(), '\t'));
		}
		
		Logger.consoleLog(GlobalVar.mqttHandler);
	}
	
	protected void txtPubMessageKeyReleased(KeyEvent e) {
		Logger.keyLog(e);
	}

	protected void txtPubTopicKeyReleased(KeyEvent e) {
		Logger.keyLog(e);
	}
	
	protected void txtTopicKeyReleased(KeyEvent e) {
		Logger.keyLog(e);
	}
	
	protected void txtClientNameKeyReleased(KeyEvent e) {
		Logger.keyLog(e);
		
		checkServerInputs();
	}

	protected void txtPasswordKeyReleased(KeyEvent e) {
		Logger.keyLog(e);
		
		checkServerInputs();
	}

	protected void txtUserKeyReleased(KeyEvent e) {
		Logger.keyLog(e);
		
		checkServerInputs();
	}

	protected void txtServerKeyReleased(KeyEvent e) {
		Logger.keyLog(e);
		
		checkServerInputs();
	}

	protected void mntmSaveSessionActionPerformed(ActionEvent e) throws IOException {
		Logger.buttonLog(e);
		
		File file = showSessionFileChooser("Save Session", true);
		
		if (file == null) { return; }
		
		saveSessionTo(file);
	}

	protected void mntmExitActionPerformed(ActionEvent e) {
		Logger.buttonLog(e);
		
		System.exit(0);
	}
	

	private void saveSessionTo(File file) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		
		String ls = System.getProperty("line.separator");
		
		out.write("server:" + txtServer.getText() + ls);
		out.append("user:" + txtUser.getText() + ls);
		out.append("pwd:" + txtPassword.getText() + ls);
		out.append("clientname:" + txtClientName.getText() + ls);
		out.append("Port:" + spPort.getValue() + ls);
		
		for (int i = 0; i < GlobalVar.addedTopics.size(); i++) {
			out.append("topic" + i + ":" + GlobalVar.addedTopics.get(i) + ls);
		}
		
		out.flush();
		out.close();
	}
	
	public File showSessionFileChooser(String title, boolean isSave) {
		String extension = "session";
		String description = "Session Files (*.session)";
		
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setDialogTitle(title);   
		
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileNameExtensionFilter(description, extension));

		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		
		int userSelection = -1;
		if (isSave) {
			userSelection = fileChooser.showSaveDialog(this);
		} else {
			userSelection = fileChooser.showOpenDialog(this);
		}

		File file = fileChooser.getSelectedFile();
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			if (!file.getAbsolutePath().toLowerCase().endsWith("." + extension)) {
				file = new File(file.getAbsolutePath() + "." + extension);
			}
			
			return file;
		}
		
		return null;
	}
	
	public void setIconImage(String path) {
		iconImage = new ImageIcon(path);
		
		this.setIconImage(iconImage.getImage());
	}
	
	public void checkPublishInputs() {
		if (checkInputs(inputsPublish)) {
			btnPublish.setEnabled(true);
		}
	}
	
	public void checkTopicInputs() {
		if (checkInputs(inputsTopic)) {
			btnAddTopic.setEnabled(true);
		}
	}
	
	public void checkServerInputs() {
		if (!checkInputs(inputsServer)) {
			if (txtClientName.getText().isBlank()) {
				btnSetServer.setEnabled(true);
				return;
			}
			
			btnSetServer.setEnabled(false);
		}
	}
	
	public boolean checkInputs(ArrayList<JTextField> inputs) {
		for (JTextField jTextField : inputs) {
			if (jTextField == null ||
				jTextField.getText().isBlank()) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Parses a String to a TreeModel. 
	 * Every line is a new Node, the level is determined by the amount of indent characters before the object in the line.
	 * Example (indentChar = '-')
	 * root
	 * -level1node
	 * --level2node
	 * ---level3node
	 * -level1node
	 * @param source
	 * @param indentChar
	 * @return
	 */
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
		
		return model;
	}
	
	/**
	 * Converts a char array to a String.
	 * @param a
	 * @return
	 */
	public String charArrayToString(char[] a) {
		String s = "";
		
		for (int i = 0; i < a.length; i++) {
			s += a[i];
		}
		
		return s;
	}
	
	/**
	 * Counts how many times a character is in a String.
	 * @param someString
	 * @param searchedChar
	 * @param index
	 * @return
	 */
	public static int countOccurences(String someString, char searchedChar, int index) {
		if (index >= someString.length()) {
			return 0;
		}
			    
		int count = someString.charAt(index) == searchedChar ? 1 : 0;
		return count + countOccurences(someString, searchedChar, index + 1);
	}

	/**
	 * Initializes all the UI components.
	 */
	private void initUI() {
		Logger.consoleLog("Installing LookAndFeel...");
		FlatGitHubIJTheme.install();
		Logger.consoleLog("Installed " + UIManager.getLookAndFeel());
		
		Logger.consoleLog("Setting Icon Image: " + GlobalVar.iconImagePath);
		setIconImage(GlobalVar.iconImagePath);
		
		Logger.consoleLog("Loading UI Components...");
		setTitle("MQTT Explorer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 700);
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mntmExitActionPerformed(e);
			}
		});
		
		mntmSaveSession = new JMenuItem("Save Session");
		mntmSaveSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mntmSaveSessionActionPerformed(e);
				} catch (IOException ex) {
					Logger.errorLog(ex);
				}
			}
		});
		mnFile.add(mntmSaveSession);
		
		JSeparator sprtrFile = new JSeparator();
		mnFile.add(sprtrFile);
		mnFile.add(mntmExit);
		background = new JPanel();
		background.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(background);
		
		tbpData = new JTabbedPane(JTabbedPane.TOP);
		
		scrlTopicTree = new JScrollPane();
		
		pnlGraph = new JPanel();
		tbpData.addTab("Graph", null, pnlGraph, null);
		GroupLayout grLoGraph = new GroupLayout(pnlGraph);
		grLoGraph.setHorizontalGroup(
			grLoGraph.createParallelGroup(Alignment.LEADING)
				.addGap(0, 496, Short.MAX_VALUE)
		);
		grLoGraph.setVerticalGroup(
			grLoGraph.createParallelGroup(Alignment.LEADING)
				.addGap(0, 342, Short.MAX_VALUE)
		);
		pnlGraph.setLayout(grLoGraph);
		
		pnlParsed = new JPanel();
		tbpData.addTab("Parsed Data", null, pnlParsed, null);
		GroupLayout grpLoParsed = new GroupLayout(pnlParsed);
		grpLoParsed.setHorizontalGroup(
			grpLoParsed.createParallelGroup(Alignment.LEADING)
				.addGap(0, 496, Short.MAX_VALUE)
		);
		grpLoParsed.setVerticalGroup(
			grpLoParsed.createParallelGroup(Alignment.LEADING)
				.addGap(0, 342, Short.MAX_VALUE)
		);
		pnlParsed.setLayout(grpLoParsed);
		
		pnlRawData = new JPanel();
		tbpData.addTab("Raw Data", null, pnlRawData, null);
		GroupLayout grpLoRawData = new GroupLayout(pnlRawData);
		grpLoRawData.setHorizontalGroup(
			grpLoRawData.createParallelGroup(Alignment.LEADING)
				.addGap(0, 496, Short.MAX_VALUE)
		);
		grpLoRawData.setVerticalGroup(
			grpLoRawData.createParallelGroup(Alignment.LEADING)
				.addGap(0, 342, Short.MAX_VALUE)
		);
		pnlRawData.setLayout(grpLoRawData);
		
		pnlTopic = new JPanel();
		pnlTopic.setBorder(new TitledBorder(null, "Topic", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		pnlPublish = new JPanel();
		pnlPublish.setBorder(new TitledBorder(null, "Publish", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		pnlServerSettings = new JPanel();
		pnlServerSettings.setBorder(new TitledBorder(null, "Server Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout grpLoBackground = new GroupLayout(background);
		grpLoBackground.setHorizontalGroup(
			grpLoBackground.createParallelGroup(Alignment.LEADING)
				.addGroup(grpLoBackground.createSequentialGroup()
					.addGroup(grpLoBackground.createParallelGroup(Alignment.LEADING)
						.addComponent(tbpData, GroupLayout.PREFERRED_SIZE, 588, GroupLayout.PREFERRED_SIZE)
						.addComponent(scrlTopicTree, GroupLayout.PREFERRED_SIZE, 586, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(grpLoBackground.createParallelGroup(Alignment.LEADING)
						.addComponent(pnlTopic, 0, 0, Short.MAX_VALUE)
						.addComponent(pnlServerSettings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(pnlPublish, 0, 0, Short.MAX_VALUE))
					.addContainerGap())
		);
		grpLoBackground.setVerticalGroup(
			grpLoBackground.createParallelGroup(Alignment.LEADING)
				.addGroup(grpLoBackground.createSequentialGroup()
					.addGroup(grpLoBackground.createParallelGroup(Alignment.LEADING)
						.addGroup(grpLoBackground.createSequentialGroup()
							.addComponent(scrlTopicTree, GroupLayout.PREFERRED_SIZE, 256, GroupLayout.PREFERRED_SIZE)
							.addGap(20)
							.addGroup(grpLoBackground.createParallelGroup(Alignment.BASELINE)
								.addComponent(tbpData, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGroup(grpLoBackground.createSequentialGroup()
									.addComponent(pnlTopic, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(14)
									.addComponent(pnlPublish, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE))))
						.addComponent(pnlServerSettings, GroupLayout.PREFERRED_SIZE, 267, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		lblServer = new JLabel("Server");
		
		txtServer = new JTextField();
		txtServer.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				txtServerKeyReleased(e);
			}
		});
		txtServer.setFont(GlobalVar.txtFieldFont);
		txtServer.setToolTipText("Server Adress");
		lblServer.setLabelFor(txtServer);
		txtServer.setColumns(10);
		
		txtUser = new JTextField();
		txtUser.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				txtUserKeyReleased(e);
			}
		});
		txtUser.setFont(GlobalVar.txtFieldFont);
		txtUser.setToolTipText("Username for Server Connection");
		txtUser.setColumns(10);
		
		txtClientName = new JTextField();
		txtClientName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				txtClientNameKeyReleased(e);
			}
		});
		txtClientName.setFont(GlobalVar.txtFieldFont);
		txtClientName.setToolTipText("<html>Client Name<br>Leave blank for automatically generated key</html>");
		txtClientName.setColumns(10);
		
		txtPassword = new JTextField();
		txtPassword.setFont(GlobalVar.txtFieldFont);
		txtPassword.setToolTipText("Password for Server Connection");
		
		lblUser = new JLabel("User");
		lblUser.setLabelFor(txtUser);
		
		lblPassword = new JLabel("Password");
		lblPassword.setLabelFor(txtPassword);
		
		lblClientName = new JLabel("Client Name");
		
		btnSetServer = new JButton("Connect");
		btnSetServer.setEnabled(false);
		btnSetServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					btnSetServerActionPerformed(e);
				} catch (MqttException ex) {
					Logger.errorLog(ex);
				}
			}
		});
		
		spPort = new JSpinner();
		spPort.setModel(new SpinnerNumberModel(1883, 0, null, 1));
		
		lblPort = new JLabel("Port");
		GroupLayout grpLoServerSettings = new GroupLayout(pnlServerSettings);
		grpLoServerSettings.setHorizontalGroup(
			grpLoServerSettings.createParallelGroup(Alignment.LEADING)
				.addGroup(grpLoServerSettings.createSequentialGroup()
					.addContainerGap()
					.addGroup(grpLoServerSettings.createParallelGroup(Alignment.LEADING)
						.addGroup(grpLoServerSettings.createSequentialGroup()
							.addGroup(grpLoServerSettings.createParallelGroup(Alignment.LEADING)
								.addComponent(lblServer, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
								.addComponent(lblPassword, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
								.addComponent(lblClientName, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
								.addComponent(lblUser, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
								.addComponent(lblPort, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(grpLoServerSettings.createParallelGroup(Alignment.LEADING)
								.addComponent(txtServer, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
								.addComponent(txtUser, Alignment.TRAILING)
								.addComponent(txtPassword, Alignment.TRAILING)
								.addComponent(txtClientName, Alignment.TRAILING)
								.addComponent(spPort)))
						.addComponent(btnSetServer, GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))
					.addContainerGap())
		);
		grpLoServerSettings.setVerticalGroup(
			grpLoServerSettings.createParallelGroup(Alignment.LEADING)
				.addGroup(grpLoServerSettings.createSequentialGroup()
					.addContainerGap()
					.addGroup(grpLoServerSettings.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblServer)
						.addComponent(txtServer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(grpLoServerSettings.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtUser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblUser))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(grpLoServerSettings.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPassword))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(grpLoServerSettings.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtClientName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblClientName))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(grpLoServerSettings.createParallelGroup(Alignment.BASELINE)
						.addComponent(spPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPort))
					.addPreferredGap(ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
					.addComponent(btnSetServer)
					.addContainerGap())
		);
		pnlServerSettings.setLayout(grpLoServerSettings);
		
		lblPubTopic = new JLabel("Topic");
		
		txtPubTopic = new JTextField();
		txtPubTopic.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				txtPubTopicKeyReleased(e);
			}
		});
		txtPubTopic.setToolTipText("Topic to publish to");
		lblPubTopic.setLabelFor(txtPubTopic);
		txtPubTopic.setFont(GlobalVar.txtFieldFont);
		txtPubTopic.setColumns(10);
		
		lblMessage = new JLabel("Message");
		
		txtPubMessage = new JTextField();
		txtPubMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				txtPubMessageKeyReleased(e);
			}
		});
		txtPubMessage.setToolTipText("Message to publish to");
		lblMessage.setLabelFor(txtPubMessage);
		txtPubMessage.setFont(GlobalVar.txtFieldFont);
		txtPubMessage.setColumns(10);
		
		btnPublish = new JButton("Publish");
		btnPublish.setEnabled(false);
		btnPublish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					btnPublishActionPerformed(e);
				} catch (MqttException ex) {
					Logger.errorLog(ex);
				}
			}
		});
		GroupLayout grpLoPublish = new GroupLayout(pnlPublish);
		grpLoPublish.setHorizontalGroup(
			grpLoPublish.createParallelGroup(Alignment.LEADING)
				.addGroup(grpLoPublish.createSequentialGroup()
					.addContainerGap()
					.addGroup(grpLoPublish.createParallelGroup(Alignment.LEADING)
						.addComponent(btnPublish, GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, grpLoPublish.createSequentialGroup()
							.addGroup(grpLoPublish.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblMessage, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
								.addComponent(lblPubTopic, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addGap(16)
							.addGroup(grpLoPublish.createParallelGroup(Alignment.TRAILING)
								.addComponent(txtPubMessage)
								.addComponent(txtPubTopic, GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE))))
					.addGap(4))
		);
		grpLoPublish.setVerticalGroup(
			grpLoPublish.createParallelGroup(Alignment.LEADING)
				.addGroup(grpLoPublish.createSequentialGroup()
					.addContainerGap()
					.addGroup(grpLoPublish.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPubTopic)
						.addComponent(txtPubTopic, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(10)
					.addGroup(grpLoPublish.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtPubMessage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMessage))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnPublish)
					.addContainerGap(18, Short.MAX_VALUE))
		);
		pnlPublish.setLayout(grpLoPublish);
		
		txtTopic = new JTextField();
		txtTopic.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				txtTopicKeyReleased(e);
			}
		});
		txtTopic.setToolTipText("Topic to subscribe to");
		txtTopic.setFont(GlobalVar.txtFieldFont);
		txtTopic.setColumns(10);
		
		btnAddTopic = new JButton("Subscribe");
		btnAddTopic.setEnabled(false);
		btnAddTopic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAddTopicActionPerformed(e);
			}
		});
		
		lblTopic = new JLabel("Topic");
		GroupLayout grpLoTopic = new GroupLayout(pnlTopic);
		grpLoTopic.setHorizontalGroup(
			grpLoTopic.createParallelGroup(Alignment.TRAILING)
				.addGroup(grpLoTopic.createSequentialGroup()
					.addContainerGap()
					.addGroup(grpLoTopic.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, grpLoTopic.createSequentialGroup()
							.addComponent(lblTopic, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
							.addGap(45)
							.addComponent(txtTopic, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE))
						.addComponent(btnAddTopic, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))
					.addContainerGap())
		);
		grpLoTopic.setVerticalGroup(
			grpLoTopic.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, grpLoTopic.createSequentialGroup()
					.addContainerGap()
					.addGroup(grpLoTopic.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtTopic, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTopic))
					.addPreferredGap(ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
					.addComponent(btnAddTopic)
					.addContainerGap())
		);
		pnlTopic.setLayout(grpLoTopic);
		
		treeTopics = new JTree();
		treeTopics.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("no connection") {
				private static final long serialVersionUID = 1L;
				{}
			}));
		treeTopics.setShowsRootHandles(true);
		
		scrlTopicTree.setViewportView(treeTopics);
		background.setLayout(grpLoBackground);
		
		inputsServer = new ArrayList<JTextField>(
			      Arrays.asList(txtServer, txtUser, txtPassword, txtClientName));

		inputsTopic = new ArrayList<JTextField>(
			      Arrays.asList(txtTopic));
		
		inputsPublish = new ArrayList<JTextField>(
			      Arrays.asList(txtPubTopic, txtPubMessage));
		
		Logger.consoleLog("Done loading UI Components");
	}
}