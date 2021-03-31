package draw;

import javax.swing.JPanel;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import pojo.Topic;
import var.GlobalVar;

import javax.swing.JSpinner;
import javax.swing.JCheckBox;
import javax.swing.SpinnerNumberModel;

/*
 * @author Florian Steinkellner
 * @date March 22, 2021
 */
public class DrawGraph extends JPanel {
	private static final long serialVersionUID = 1L;

	//UI Components
	private static JPanel pnlGraph;
	private JLabel lblNumPoints;
	private JSpinner spNumPoints;
	private JCheckBox chbxDrawPoints;
	private JCheckBox chbxShowLatestValue;
	
	public DrawGraph() {
		pnlGraph = new JGraph();
		
		lblNumPoints = new JLabel("Number of Points");
		
		spNumPoints = new JSpinner();
		spNumPoints.setModel(new SpinnerNumberModel(0, 0, null, 1));
		lblNumPoints.setLabelFor(spNumPoints);
		
		chbxDrawPoints = new JCheckBox("Draw Points");
		
		chbxShowLatestValue = new JCheckBox("Show Latest Value");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNumPoints)
							.addGap(18)
							.addComponent(spNumPoints, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 96, Short.MAX_VALUE)
							.addComponent(chbxShowLatestValue)
							.addGap(18)
							.addComponent(chbxDrawPoints))
						.addComponent(pnlGraph, GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNumPoints)
						.addComponent(spNumPoints, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(chbxDrawPoints)
						.addComponent(chbxShowLatestValue))
					.addGap(11)
					.addComponent(pnlGraph, GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
					.addContainerGap())
		);
		setLayout(groupLayout);
	}
	
	public void repaintGraph() {
		pnlGraph.repaint();
	}
	
	private static boolean isSelected () {
		return GlobalVar.selectedPanel == GlobalVar.PNL_GRAPH;
	}
	
	public static void tabSelectionChanged() {
		if (!isSelected() || !JGraph.isInitGraph()) { return; }
		
		JGraph.startTimer();
	}

	public static void selectionTopicChanged() {
		if (!isSelected()) { return; }
		
		((JGraph) pnlGraph).setTopic(getSelectedTopic());
	}
	
	private static Topic getSelectedTopic() {
		if (GlobalVar.selectedTopic == -1) { return null; }
		
		return GlobalVar.addedTopics.get(GlobalVar.selectedTopic);
	}
}
