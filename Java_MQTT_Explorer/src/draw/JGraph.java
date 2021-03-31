package draw;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import javax.swing.Timer;

import pojo.*;

public class JGraph extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private Topic topic = null;
	private static boolean initGraph = false;
	private int repaintDelay = 200;
	private static Timer timer;
	
	public JGraph() {
		timer = new Timer(repaintDelay, this);
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateGraphOnNextRepaint();
			}
		});
	}
	
	private void updateGraphOnNextRepaint() {
		initGraph = false;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
		updateGraphOnNextRepaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (!initGraph) {
			if (initGraph() == -1) { return; }
		}
		
		//topic.getGraph().paintComponent(g);
	}

	private int initGraph() {
		if (this.topic == null) { return -1; }
		
		Rectangle bounds = getBounds();
		bounds = new Rectangle(0, 0, (int) bounds.getWidth(), (int) bounds.getHeight());
		/*
		if (topic.getGraph().getBounds() == null) {
			Logger.noStatusLog(String.format("Initialized Graph with Bounds[x: %.0f, y: %.0f, width: %.0f, height: %.0f", bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight()));
		
			topic.setGraph(new Graph(bounds));
		} else {
			Logger.noStatusLog(String.format("Updated Graph to Bounds[x: %.0f, y: %.0f, width: %.0f, height: %.0f", bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight()));
		
			topic.getGraph().setBounds(bounds);
		}
		*/
		initGraph = true;
		repaint();
		return 0;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//repaint();
	}
	
	public static void stopTimer() {
		timer.stop();
	}
	
	public static void startTimer() {
		Logger.noStatusLog("Started Timer in DrawGraph");
		timer.start();
	}
	
	public Graph getGraph() {
		return null; //topic.getGraph();
	}

	public void setGraph(Graph graph) {
		//this.topic.setGraph(graph);
	}

	public int getRepaintDelay() {
		return repaintDelay;
	}

	public void setRepaintDelay(int repaintDelay) {
		this.repaintDelay = repaintDelay;
	}

	public static Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		JGraph.timer = timer;
	}

	public static boolean isInitGraph() {
		return initGraph;
	}

	public static void setInitGraph(boolean initGraph) {
		JGraph.initGraph = initGraph;
	}
}
