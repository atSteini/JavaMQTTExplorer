package app;


import pojo.*;
import var.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

/**
 * @author Florian Steinkellner
 * @date March 31st 2021
 */
public class FrmCreateJSON extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtValue;
	private JTextField txtUnit;
	private JTextField txtAddition;
	private JLabel lblName;
	private JLabel lblValue;
	private JLabel lblUnit;
	private JLabel lblAddition;
	private JButton btnSet;
	
	private JFrame parent = null;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FrmCreateJSON frame = new FrmCreateJSON(null);
					frame.setVisible(true);
				} catch (Exception e) {
					Logger.errorLog(e);
				}
			}
		});
	}
	
	public FrmCreateJSON(JFrame parent) {
		setTitle("JSON Creator");
		if (parent == null) {
			Logger.errorLog("Parent of FrmCreateJSON is null.");
			Logger.exitSystem(-1);
			
			return;
		}
		
		this.parent = parent;
		
		initUI(parent);
	}

	protected void btnSetActionPerformed(ActionEvent e) {
		Logger.buttonLog(e);
		
		try {
			String JSON = String.format(
					"{\"name\":\"%s\", \"value\": \"%s\", \"unit\": \"%s\", \"addition\": \"%s\"}", 
					this.txtName.getText(),
					this.txtValue.getText(),
					this.txtUnit.getText(),
					this.txtAddition.getText());
			
			((App) this.parent).insertValuesJSON(JSON);
			
			Logger.noStatusLog("Dispose FrmCreateJSON");
			((App) parent).closeJSONCreator();
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		} catch (Exception ex) {
			Logger.errorLog(ex);
		}
	}
	
	private void initUI(JFrame parent) {
		Logger.noStatusLog("Initializing FrmCreateJSON with parent " + parent.getTitle());
		
		try {
			UIManager.setLookAndFeel(App.getLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			Logger.errorLog(e);
		}
		
		setIconImage(parent.getIconImage());
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(	100, 100,
					(int) GlobalVar.JSON_MINIMUM_BOUNDS.getWidth(),
					(int) GlobalVar.JSON_MINIMUM_BOUNDS.getHeight());
		setMinimumSize(GlobalVar.JSON_MINIMUM_BOUNDS);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		lblName = new JLabel("Name");
		
		txtName = new JTextField();
		lblName.setLabelFor(txtName);
		txtName.setColumns(10);
		
		lblValue = new JLabel("Value");
		
		txtValue = new JTextField();
		lblValue.setLabelFor(txtValue);
		txtValue.setColumns(10);
		
		lblUnit = new JLabel("Unit");
		
		txtUnit = new JTextField();
		lblUnit.setLabelFor(txtUnit);
		txtUnit.setColumns(10);
		
		lblAddition = new JLabel("Addition");
		
		txtAddition = new JTextField();
		txtAddition.setText("Sent by My Java MQTT Explorer");
		lblAddition.setLabelFor(txtAddition);
		txtAddition.setColumns(10);
		
		btnSet = new JButton("Insert Values");
		btnSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSetActionPerformed(e);
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnSet, GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblName, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblValue, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblUnit, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblAddition, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE))
							.addGap(18)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(txtAddition, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
								.addComponent(txtUnit, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
								.addComponent(txtValue, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
								.addComponent(txtName, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE))))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblName))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblValue)
						.addComponent(txtValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUnit)
						.addComponent(txtUnit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAddition)
						.addComponent(txtAddition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
					.addComponent(btnSet)
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
		
		Logger.noStatusLog("Done loading FrmCreateJSON.");
	}
}
