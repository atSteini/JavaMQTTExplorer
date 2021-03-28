package draw;

import pojo.*;
import var.*;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

/*
 * @author Florian Steinkellner
 * @date March 22, 2021
 */
public class DrawRawData extends JPanel {
	private static final long serialVersionUID = 1L;
	
	//UI Components
	private JLabel lblId;
	private JLabel lblQOS;
	private JLabel lblPayload;
	private static JTextArea tarPayload;
	private static JTextField txtID;
	private static JTextField txtQOS;
	public static JTextField txtIndex;
	private static JTextField txtSelectedTopic;
	private static JButton btnPause;
	
	public DrawRawData() {
		lblId = new JLabel("ID");
		
		txtID = new JTextField();
		txtID.setToolTipText("ID");
		txtID.setHorizontalAlignment(SwingConstants.CENTER);
		lblId.setLabelFor(txtID);
		txtID.setEditable(false);
		txtID.setColumns(10);
		
		lblQOS = new JLabel("QOS");
		
		txtQOS = new JTextField();
		txtQOS.setToolTipText("QOS (0 - at most once, 1 - At least once, 2 - Exactly once)");
		txtQOS.setHorizontalAlignment(SwingConstants.CENTER);
		lblQOS.setLabelFor(txtQOS);
		txtQOS.setEditable(false);
		txtQOS.setColumns(10);
		
		lblPayload = new JLabel("Payload");
		
		JScrollPane scrlPayload = new JScrollPane();
		
		JButton btnPrevious = new JButton("Previous");
		btnPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnPreviousActionPerformed(e);
			}
		});
		
		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNextActionPerformed(e);
			}
		});
		
		txtIndex = new JTextField();
		txtIndex.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				txtIndexKeyTyped(e);
			}
		});
		txtIndex.setHorizontalAlignment(SwingConstants.CENTER);
		txtIndex.setToolTipText("Current message counter");
		txtIndex.setColumns(10);
		txtIndex.setEnabled(false);
		
		btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnPauseActionPerformed(e);
			}
		});
		
		JLabel lblSelectedTopic = new JLabel("Selected Topic");
		
		txtSelectedTopic = new JTextField();
		txtSelectedTopic.setEditable(false);
		txtSelectedTopic.setColumns(10);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrlPayload)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnPrevious, GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
							.addGap(18)
							.addComponent(txtIndex, GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
							.addGap(18)
							.addComponent(btnNext, GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblSelectedTopic)
							.addGap(18)
							.addComponent(txtSelectedTopic, GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblId, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtID, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
							.addGap(46)
							.addComponent(lblQOS, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(txtQOS, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
							.addGap(45)
							.addComponent(btnPause, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblPayload, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSelectedTopic)
						.addComponent(txtSelectedTopic, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblId)
						.addComponent(txtID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnPause)
						.addComponent(lblQOS)
						.addComponent(txtQOS, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(lblPayload)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrlPayload, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnNext)
							.addComponent(txtIndex, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnPrevious))
					.addGap(7))
		);
		
		tarPayload = new JTextArea();
		tarPayload.setWrapStyleWord(true);
		tarPayload.setToolTipText("The payload (message)");
		tarPayload.setLineWrap(true);
		tarPayload.setEditable(false);
		lblPayload.setLabelFor(tarPayload);
		scrlPayload.setViewportView(tarPayload);
		setLayout(groupLayout);
		
		Logger.noStatusLog("Done loading panel DrawRawData!");
	}
	
	protected void txtIndexKeyTyped(KeyEvent e) {
		Logger.keyLog(e);
		
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			if (txtIndex.hasFocus()) {
				int index = StringOp.toInt(txtIndex.getText());
				
				if (index >= 0) {
					if (index > 0) {
						index--;
					}
					
					Topic selectedTopic = getSelectedTopic();
					if (selectedTopic == null) { return; }
					int maxIndex = selectedTopic.getMessageCounter() - 1;
					
					if (index > maxIndex) {
						index = maxIndex;
					}
					
					selectedTopic.setSelectedMessageIndex(index);

					setTxtIndex(index + 1 + "");
					updateData();
					
					Logger.noStatusLog("Set MessageIndex to " + index);
					
					return;
				}
				
				setTxtIndex("0");
			}
		}
	}

	protected void btnPauseActionPerformed(ActionEvent e) {
		Logger.infoLog("Froze data input");
		
		togglePause();
		updateData();
	}
	
	private void togglePause() {
		GlobalVar.dataPaused = !GlobalVar.dataPaused;
		togglePauseButton();
	}

	private static void togglePauseButton() {
		if (GlobalVar.dataPaused) {
			btnPause.setText("Resume");
			return;
		}

		btnPause.setText("Pause");
	}

	protected void btnPreviousActionPerformed(ActionEvent e) {
		getSelectedTopic().selectPrevious();
		updateData();
	}

	protected void btnNextActionPerformed(ActionEvent e) {
		getSelectedTopic().selectNext();
		updateData();
	}

	public static void updateData() {
		togglePauseButton();
		if (GlobalVar.selectedPanel != GlobalVar.PNL_RAW || GlobalVar.dataPaused) { return; }
		
		Logger.noStatusLog("Update data in DrawRawPanel");
		
		Topic selectedTopic = getSelectedTopic();
		if (selectedTopic == null) { 
			setTxtTopic("");
			return; 
		}
		
		setTxtTopic(selectedTopic.getTopic());
		
		Message selectedMessage = getSelectedMessage();
		
		if (selectedMessage == null) { 
			setValues("", "", "", "0");
			return;
		}

		if (getSelectedTopic().getMessageCounter() > 0) {
			txtIndex.setEnabled(true);
		}
		
		Logger.noStatusLog("Got selected message: " + selectedMessage.toString());
		setValues(	Integer.toString(selectedMessage.getId()),
					Integer.toString(selectedMessage.getQos()),
					selectedMessage.getMessage(),
					Integer.toString(getSelectedTopic().getSelectedMessageIndex() + 1));
	}

	public static void selectionTopicChanged() {
		updateData();
	}
	
	private static void setValues(String id, String qos, String payload, String index) {
		setTxtID(id);
		setTxtQOS(qos);
		setTarPayload(payload);
		setTxtIndex(index);
	}
	
	private static void setTxtTopic(String s) {
		txtSelectedTopic.setText(s);
	}
	
	private static void setTxtID(String s) {
		txtID.setText(s);
	}
	
	private static void setTxtQOS(String s) {
		txtQOS.setText(s);
	}
	
	private static void setTarPayload(String s) {
		tarPayload.setText(s);
	}
	
	private static void setTxtIndex(String s) {
		txtIndex.setText(s + "/" + getMessageCounter());
	}
	
	private static int getMessageCounter() {
		Topic selected = getSelectedTopic();
		
		if (selected == null) { return 0; }
		
		return getSelectedTopic().getMessageCounter();
	}
	
	private static Topic getSelectedTopic() {
		if (GlobalVar.selectedTopic == -1) { return null; }
		
		return GlobalVar.addedTopics.get(GlobalVar.selectedTopic);
	}
	
	private static Message getSelectedMessage() {
		Topic selectedTopic = getSelectedTopic();
		if (selectedTopic == null) { return null; }
		Message latest = selectedTopic.getSelectedMessage();
		
		return latest;
	}
}