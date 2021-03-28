package app;

import pojo.*;
import var.*;
import draw.*;

import com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JMenuBar;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JTabbedPane;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.JSeparator;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

/*
 * @author Florian Steinkellner
 * @date March 22, 2021
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
	private JLabel lblTopic;
	private JMenuItem mntmSaveSession;
	private JMenuItem mntmOpenSession;
	private JMenuItem mntmClear;
	private JScrollPane scrlShowTopics;
	private static JPanel pnlParsed;
	private static JPanel pnlRawData;
	private static JPanel pnlGraph;
	private static JButton btnAddTopic;
	private static JButton btnPublish;
	private static JButton btnCreateJSON;
	private static JPanel pnlShowTopics;
	private static JList<String> listShowTopics;
	private static JPanel pnlTopic;
	public static JTextField txtStatus;
	public static JPanel pnlStatus;
	
	private ArrayList<JTextField> inputsServer;
	private ArrayList<JTextField> inputsTopic;
	private ArrayList<JTextField> inputsPublish;
	private ImageIcon iconImage;
	private boolean initUI = false, initDraw = false;
	public static ServerSocket serverSocket;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					try {
				        serverSocket = new ServerSocket(1044);
				    } catch (IOException e) {
				        Logger.errorLog("Application " + this.getClass() + " already running!");
				        Logger.errorLog(e);
				        
				        Logger.exitSystem(-1);
				    }
					
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

	public static void selectionTopicChanged() {
		DrawRawData.selectionTopicChanged();
		DrawParsedData.selectionTopicChanged();
		DrawGraph.selectionTopicChanged();
	}
	
	public static void updateData() {
		DrawRawData.updateData();
		DrawParsedData.updateData();
		DrawGraph.updateData();
	}
	
	public static void messageArrived(String topic, Message message) {
		reloadTopics();
	}

	public static void connectionLost() {
		setInterfaceComponents(false);
	}
	
	protected void btnPublishActionPerformed(ActionEvent e) throws MqttPersistenceException, MqttException {
		Logger.buttonLog(e);
		
		if (txtPubTopic.getText().isBlank()) { return; }
		
		GlobalVar.mqttHandler.publish(txtPubTopic.getText(), new Message(this.txtPubMessage.getText(), GlobalVar.mqttHandler.getMqttQos()));
	}

	protected void btnAddTopicActionPerformed(ActionEvent e) throws MqttSecurityException, MqttException {
		Logger.buttonLog(e);
		
		String topic = txtTopic.getText();
		
		addTopic(topic);
	}

	protected void btnSetServerActionPerformed(ActionEvent e) throws MqttException {
		Logger.buttonLog(e);

		setServerButton(GlobalVar.CONN_WAITING);
		
		GlobalVar.mqttHandler = new MQTTHandler(
						txtServer.getText(),
						txtUser.getText(),
						txtPassword.getText(),
						txtClientName.getText(),
						(int) spPort.getValue());
		
		if (GlobalVar.mqttHandler.connectionAlive()) {
			setInterfaceComponents(true);
			
			txtClientName.setText(GlobalVar.mqttHandler.getMqttClientName());
			
			setServerButton(GlobalVar.CONN_CONNECTED);
		}
		
		Logger.infoLog(GlobalVar.mqttHandler);
	}

	protected void btnCreateJSONActionPerformed(ActionEvent e) {
		Logger.buttonLog(e);
	}

	protected void tbpDataStateChanged(ChangeEvent e) {
		Logger.stateChangeLog(e);
		
		if (!initUI) { return; }
		
		if (!initDraw) { initDraw(); }
		
		GlobalVar.selectedPanel = (int) GlobalVar.drawPanels.get(tbpData.getSelectedComponent().getClass().toString());
		
		updateData();
		
		Logger.noStatusLog("Selected Panel " + GlobalVar.selectedPanel);
	}
	
	private void initDraw() {
		try {
			GlobalVar.drawPanels.put(pnlRawData.getClass().toString(), (int) GlobalVar.PNL_RAW);
			GlobalVar.drawPanels.put(pnlParsed.getClass().toString(), (int) GlobalVar.PNL_PARSED);
			GlobalVar.drawPanels.put(pnlGraph.getClass().toString(), (int) GlobalVar.PNL_GRAPH);
			initDraw = true;
		} catch (NullPointerException ex) {
			initDraw = false;
			Logger.errorLog(ex);
		}
	}

	protected void listShowTopicsValueChanged(ListSelectionEvent e) {
		Logger.selectionChangedLog(e);
		
		int newTopic = listShowTopics.getSelectedIndex();
		Logger.noStatusLog("Selected Index: " + newTopic);
		
		if (newTopic != -1 && GlobalVar.selectedTopic != newTopic) {
			GlobalVar.selectedTopic = newTopic;
		}
		
		selectionTopicChanged();
	}
	
	private static void setInterfaceComponents(boolean b) {
		btnAddTopic.setEnabled(b);
		btnCreateJSON.setEnabled(b);
		btnPublish.setEnabled(b);
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

	protected void mntmOpenSessionActionPerformed(ActionEvent e) throws IOException, MqttException {
		Logger.buttonLog(e);
		
		File file = showSessionFileChooser("Load Session", false);
		
		if (file == null) { return; }
		
		loadSession(file);
	}

	protected void mntmClearActionPerformed(ActionEvent e) {
		clearAll();
	}
	
	protected void mntmExitActionPerformed(ActionEvent e) {
		Logger.buttonLog(e);
		
		Logger.exitSystem(0);
	}
	
	private void addTopic(Topic topic) throws MqttSecurityException, MqttException {
		if (topicExists(topic)) { 
			Logger.infoLog(topic.getTopic() + " already exists!");
			return; 
		}
		GlobalVar.addedTopics.add(topic);
		
		/*
		if (GlobalVar.addedTopics.size() > 0) {
			GlobalVar.selectedTopic = 0;
		}
		*/
		
		reloadTopics();
		subscribeToTopic(topic);
		
		Logger.infoLog("Added Topic: " + topic);
	}
	
	private boolean topicExists(Topic topic) {
		for (Topic searchTopic : GlobalVar.addedTopics) {
			if (searchTopic.getTopic().equals(topic.getTopic())) {
				return true;
			}
		}
		
		return false;
	}

	private void addTopic(String topic) throws MqttSecurityException, MqttException {
		addTopic(new Topic(topic));
	}
	
	private void subscribeToTopic(Topic topic) throws MqttSecurityException, MqttException {
		if (GlobalVar.mqttHandler == null) { return; }
		GlobalVar.mqttHandler.subscribeTo(topic);
	}
	
	private void subscribeToAllTopics() throws MqttSecurityException, MqttException {
		for(Topic topic : GlobalVar.addedTopics) {
			subscribeToTopic(topic);
		}
	} 
	
	private static void reloadTopics(String ... addBefore) {
		DefaultListModel<String> model = new DefaultListModel<>();

		for (String before : addBefore) {
			if (before != null) {
				model.addElement(before);
			}
		}

		for (Topic topic : GlobalVar.addedTopics) {
			model.addElement(topic.getListView());
		}
		
		listShowTopics.setModel(model);
		pnlTopic.repaint();
	}
	
	private void setServerSettings(String line, int index, String splitter) {
		switch(index) {
		case 0:
			setServer(line.split(splitter)[1]);
			break;
		case 1:
			setUser(line.split(splitter)[1]);
			break;
		case 2:
			setPassword(line.split(splitter)[1]);
			break;
		case 3:
			setClientName(line.split(splitter)[1]);
			break;
		case 4:
			setPort(StringOp.toInt(line.split(splitter)[1]));
			break;
		}
		
		checkServerInputs();
	}

	private void setServerButton(int state) {
		switch (state) {
		case GlobalVar.CONN_WAITING:
			btnSetServer.setText("Connecting...");
			break;
		case GlobalVar.CONN_CONNECTED:
			btnSetServer.setText("Reconnect");
			break;
		case GlobalVar.CONN_DISCONNECTED:
			btnSetServer.setText("Connect");
			break;
		default:
				
		}
	}
	
	private void setPort(int port) {
		spPort.setValue(port);
		
		Logger.infoLog("Set Port: " + port);
	}

	private void setClientName(String clientName) {
		txtClientName.setText(clientName);
		
		Logger.infoLog("Set ClientName: " + clientName);
	}

	private void setPassword(String password) {
		txtPassword.setText(password);
		
		Logger.infoLog("Set Password: " + password);
	}

	private void setUser(String user) {
		txtUser.setText(user);
		
		Logger.infoLog("Set User: " + user);
	}

	private void setServer(String server) {
		txtServer.setText(server);
		
		Logger.infoLog("Set Server: " + server);
	}
	
	private void setTopics(ArrayList<Topic> topics) {
		GlobalVar.addedTopics = topics;
		
		Logger.infoLog("Set topics: " + topics);
	}

	private void loadSession(File file) throws IOException, MqttException {
		Logger.infoLog("Loading session: " + file);
		BufferedReader in = new BufferedReader(new FileReader(file));
		
		String line = null;
		int counter = 0;
		String splitter = ":";
		
		while ((line = in.readLine()) != null) {
			if(counter < 5) {
				setServerSettings(line, counter, splitter);
				
				counter++;
				continue;
			}
			
			if(line.contains("topic")) {
				addTopic(line.split(splitter)[1]);
			}
		}
		
		in.close();

		Logger.infoLog("Done loading session: " + file);
		
		btnSetServerActionPerformed(null);
		subscribeToAllTopics();
	}
	
	private void saveSessionTo(File file) throws IOException {
		Logger.infoLog("Saving session: " + file);
		
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		
		String ls = System.getProperty("line.separator");
		
		out.write("server:" + txtServer.getText() + ls);
		out.append("user:" + txtUser.getText() + ls);
		out.append("pwd:" + txtPassword.getText() + ls);
		out.append("clientname:" + txtClientName.getText() + ls);
		out.append("Port:" + spPort.getValue() + ls);
		
		for (int i = 0; i < GlobalVar.addedTopics.size(); i++) {
			out.append("topic" + i + ":" + GlobalVar.addedTopics.get(i));
			
			if (i < GlobalVar.addedTopics.size() - 1) {
				out.append(ls);
			}
		}
		
		out.flush();
		out.close();
		
		Logger.infoLog("Done saving session: " + file);
	}
	
	public File showSessionFileChooser(String title, boolean isSave) {
		String extension = "session";
		String description = "Session Files (*.session)";
		
		JFileChooser fileChooser = new JFileChooser();

		Logger.noStatusLog("Showing FileChooser [" + title + "] - isSave: " + isSave);
		
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
				Logger.noStatusLog(file.getAbsoluteFile() + " has no valid extension. Adding " + extension);
				
				file = new File(file.getAbsolutePath() + "." + extension);
			}
			
			Logger.infoLog("FileChooser [" + title + "] returned " + file);
			return file;
		}
		
		return null;
	}
	
	private void clearAll() {
		setServer("");
		setUser("");
		setPassword("");
		setClientName("");
		setPort(1883);
		setTopics(new ArrayList<Topic>());
		reloadTopics();
		
		Logger.infoLog("Cleared session!");
	}
	
	public void setIconImage(String path) {
		Logger.noStatusLog("Setting Icon Image: " + path);
		
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

	public static void setStatusBar(Object o) {
		if (o == null || txtStatus == null || pnlStatus == null) { return; }
		
		txtStatus.setText(o.toString());
		pnlStatus.repaint();
	}
	
	public static void centerWindow(Window frame) {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
	    frame.setLocation(x, y);
	}
	
	/**
	 * Initializes all the UI components.
	 */
	private void initUI() {
		Logger.noStatusLog("Installing LookAndFeel...");
		FlatCyanLightIJTheme.install();
		Logger.noStatusLog("Installed " + UIManager.getLookAndFeel());
		
		setIconImage(GlobalVar.iconImagePath);
		
		Logger.noStatusLog("Loading UI Components...");
		setTitle("MQTT Explorer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(GlobalVar.WINDOW_BOUNDS);
		centerWindow(this);
		
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
		
		mntmOpenSession = new JMenuItem("Load Session");
		mntmOpenSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mntmOpenSessionActionPerformed(e);
				} catch (IOException | MqttException ex) {
					Logger.errorLog(ex);
				}
			}
		});
		mnFile.add(mntmOpenSession);
		
		mntmClear = new JMenuItem("Clear Session");
		mntmClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mntmClearActionPerformed(e);
			}
		});
		mnFile.add(mntmClear);
		
		JSeparator sprtrFile = new JSeparator();
		mnFile.add(sprtrFile);
		mnFile.add(mntmExit);
		background = new JPanel();
		background.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(background);
		
		tbpData = new JTabbedPane(JTabbedPane.TOP);
		tbpData.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				tbpDataStateChanged(e);
			}
		});
		
		pnlRawData = new DrawRawData();
		tbpData.addTab("Raw Data", null, pnlRawData, null);
		
		pnlParsed = new DrawParsedData();
		tbpData.addTab("Parsed Data", null, pnlParsed, null);
		
		pnlGraph = new DrawGraph();
		tbpData.addTab("Graph", null, pnlGraph, null);

		pnlTopic = new JPanel();
		pnlTopic.setBorder(new TitledBorder(null, "Topic", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		pnlPublish = new JPanel();
		pnlPublish.setBorder(new TitledBorder(null, "Publish", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		pnlServerSettings = new JPanel();
		pnlServerSettings.setBorder(new TitledBorder(null, "Server Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		pnlStatus = new JPanel();
		
		pnlShowTopics = new JPanel();
		pnlShowTopics.setBorder(new TitledBorder(null, "Topics", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout grpLoBackground = new GroupLayout(background);
		grpLoBackground.setHorizontalGroup(
			grpLoBackground.createParallelGroup(Alignment.TRAILING)
				.addGroup(grpLoBackground.createSequentialGroup()
					.addContainerGap()
					.addGroup(grpLoBackground.createParallelGroup(Alignment.TRAILING)
						.addGroup(grpLoBackground.createSequentialGroup()
							.addGroup(grpLoBackground.createParallelGroup(Alignment.TRAILING)
								.addComponent(tbpData, GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
								.addComponent(pnlShowTopics, GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE))
							.addGap(18)
							.addGroup(grpLoBackground.createParallelGroup(Alignment.LEADING)
								.addGroup(grpLoBackground.createSequentialGroup()
									.addComponent(pnlPublish, 0, 0, Short.MAX_VALUE)
									.addContainerGap())
								.addGroup(grpLoBackground.createSequentialGroup()
									.addComponent(pnlServerSettings, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addGap(8))
								.addGroup(grpLoBackground.createSequentialGroup()
									.addComponent(pnlTopic, 0, 0, Short.MAX_VALUE)
									.addContainerGap())))
						.addGroup(grpLoBackground.createSequentialGroup()
							.addComponent(pnlStatus, GroupLayout.DEFAULT_SIZE, 956, Short.MAX_VALUE)
							.addContainerGap())))
		);
		grpLoBackground.setVerticalGroup(
			grpLoBackground.createParallelGroup(Alignment.LEADING)
				.addGroup(grpLoBackground.createSequentialGroup()
					.addGroup(grpLoBackground.createParallelGroup(Alignment.LEADING)
						.addGroup(grpLoBackground.createSequentialGroup()
							.addComponent(pnlShowTopics, GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE)
							.addGroup(grpLoBackground.createParallelGroup(Alignment.LEADING)
								.addGroup(grpLoBackground.createSequentialGroup()
									.addGap(61)
									.addComponent(pnlTopic, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(pnlPublish, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE))
								.addGroup(grpLoBackground.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(tbpData, GroupLayout.PREFERRED_SIZE, 364, GroupLayout.PREFERRED_SIZE))))
						.addComponent(pnlServerSettings, GroupLayout.PREFERRED_SIZE, 267, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(pnlStatus, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		pnlShowTopics.setLayout(new BorderLayout(0, 0));
		
		scrlShowTopics = new JScrollPane();
		pnlShowTopics.add(scrlShowTopics, BorderLayout.CENTER);
		
		listShowTopics = new JList<>();
		listShowTopics.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				listShowTopicsValueChanged(e);
			}
		});
		listShowTopics.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrlShowTopics.setViewportView(listShowTopics);
		
		txtStatus = new JTextField("no connection");
		txtStatus.setEditable(false);
		txtStatus.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_pnlStatus = new GroupLayout(pnlStatus);
		gl_pnlStatus.setHorizontalGroup(
			gl_pnlStatus.createParallelGroup(Alignment.TRAILING)
				.addComponent(txtStatus, GroupLayout.DEFAULT_SIZE, 956, Short.MAX_VALUE)
		);
		gl_pnlStatus.setVerticalGroup(
			gl_pnlStatus.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_pnlStatus.createSequentialGroup()
					.addComponent(txtStatus, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
					.addContainerGap())
		);
		pnlStatus.setLayout(gl_pnlStatus);
		
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
								.addComponent(txtUser, 240, 240, Short.MAX_VALUE)
								.addComponent(txtPassword, 240, 240, Short.MAX_VALUE)
								.addComponent(txtClientName, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
								.addComponent(spPort, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)))
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
		
		btnCreateJSON = new JButton("Create JSON Object");
		btnCreateJSON.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnCreateJSONActionPerformed(e);
			}
		});
		btnCreateJSON.setEnabled(false);
		GroupLayout grpLoPublish = new GroupLayout(pnlPublish);
		grpLoPublish.setHorizontalGroup(
			grpLoPublish.createParallelGroup(Alignment.LEADING)
				.addGroup(grpLoPublish.createSequentialGroup()
					.addContainerGap()
					.addGroup(grpLoPublish.createParallelGroup(Alignment.LEADING)
						.addGroup(grpLoPublish.createSequentialGroup()
							.addGroup(grpLoPublish.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblMessage, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
								.addComponent(lblPubTopic, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addGap(16)
							.addGroup(grpLoPublish.createParallelGroup(Alignment.LEADING)
								.addComponent(txtPubTopic, GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
								.addComponent(txtPubMessage, GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE))
							.addContainerGap())
						.addGroup(Alignment.TRAILING, grpLoPublish.createSequentialGroup()
							.addGroup(grpLoPublish.createParallelGroup(Alignment.TRAILING)
								.addComponent(btnCreateJSON, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
								.addComponent(btnPublish, GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))
							.addContainerGap())))
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
					.addPreferredGap(ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
					.addComponent(btnCreateJSON)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnPublish)
					.addContainerGap())
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
				try {
					btnAddTopicActionPerformed(e);
				} catch (MqttException ex) {
					Logger.errorLog(ex);
				}
			}
		});
		
		lblTopic = new JLabel("Topic");
		GroupLayout grpLoTopic = new GroupLayout(pnlTopic);
		grpLoTopic.setHorizontalGroup(
			grpLoTopic.createParallelGroup(Alignment.TRAILING)
				.addGroup(grpLoTopic.createSequentialGroup()
					.addContainerGap()
					.addGroup(grpLoTopic.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnAddTopic, GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
						.addGroup(grpLoTopic.createSequentialGroup()
							.addComponent(lblTopic, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
							.addGap(45)
							.addComponent(txtTopic, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)))
					.addContainerGap())
		);
		grpLoTopic.setVerticalGroup(
			grpLoTopic.createParallelGroup(Alignment.LEADING)
				.addGroup(grpLoTopic.createSequentialGroup()
					.addContainerGap()
					.addGroup(grpLoTopic.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtTopic, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTopic))
					.addPreferredGap(ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
					.addComponent(btnAddTopic)
					.addContainerGap())
		);
		pnlTopic.setLayout(grpLoTopic);
		background.setLayout(grpLoBackground);
		
		inputsServer = new ArrayList<JTextField>(
			      Arrays.asList(txtServer, txtUser, txtPassword, txtClientName));

		inputsTopic = new ArrayList<JTextField>(
			      Arrays.asList(txtTopic));
		
		inputsPublish = new ArrayList<JTextField>(
			      Arrays.asList(txtPubTopic, txtPubMessage));
		
		initDraw();
		setServerButton(GlobalVar.CONN_DISCONNECTED);
		
		repaint();
		
		Logger.noStatusLog("Done loading UI Components\n");
		Logger.statusLog("no connection");
		initUI = true;
	}
}