package pojo;

import java.awt.Color;
import java.awt.Rectangle;

import java.util.ArrayList;

public class Graph {
	private ArrayList<DataPoint> points;
	private Rectangle bounds;
	
	//Settings
	private double maxOverShoot = 0.1;
	private int maxPoints = 10;
	private boolean drawDataPoints = true;
	private boolean enableAntialiasing = true;
	
	//Colors
	public static final Color COLOR_CYAN = new Color (10, 176, 209);
	private Color lineColor = COLOR_CYAN;
	
	//Dynamic
	private double scaleXMin, scaleXMax, scaleYMin, scaleYMax;
	
	public Graph (Rectangle bounds) {
		this.bounds = bounds;
		initPoints();
	}
	
	public Graph (int x, int y, int width, int height) {
		this.bounds = new Rectangle(x, y, width, height);
		initPoints();
	}
	
	public void updateGraph() {
		setScaleYMax(getMaxValue().getValue() + getMaxValue().getValue()*getMaxOverShoot());
	}
	
	public void initPoints() {
		points = new ArrayList<>();
	}
	
	public void addPoint(DataPoint point) {
		if (point == null) { return; }
		
		if (getNumPoints() >= getMaxPoints()) {
			removePointAt(0);
		}
		
		points.add(point);
	}
	
	public void removePointAt(int index) {
		if (index >= getNumPoints() || index < 0) { return; }
		
		points.remove(index);
	}
	
	public int getNumPoints() {
		return points.size();
	}

	public DataPoint getMaxValue() {
		DataPoint max = new DataPoint(-Double.MAX_VALUE);
		
		for (DataPoint dataPoint : points) {
			if (dataPoint.getValue() > max.getValue()) {
				max = dataPoint;
			}
		}
		
		return max;
	}
	
	public DataPoint getMinValue() {
		DataPoint min = new DataPoint(Double.MAX_VALUE);
		
		for (DataPoint dataPoint : points) {
			if (dataPoint.getValue() < min.getValue()) {
				min = dataPoint;
			}
		}
		
		return min;
	}
	
	public ArrayList<DataPoint> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<DataPoint> points) {
		this.points = points;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public void setBounds(int x, int y, int width, int height) {
		this.bounds = new Rectangle (x, y, width, height);
	}

	public int getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}

	public boolean isDrawDataPoints() {
		return drawDataPoints;
	}

	public void setDrawDataPoints(boolean drawDataPoints) {
		this.drawDataPoints = drawDataPoints;
	}

	public boolean isEnableAntialiasing() {
		return enableAntialiasing;
	}

	public void setEnableAntialiasing(boolean enableAntialiasing) {
		this.enableAntialiasing = enableAntialiasing;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public double getMaxOverShoot() {
		return maxOverShoot;
	}

	public void setMaxOverShoot(double maxOverShoot) {
		this.maxOverShoot = maxOverShoot;
	}

	public double getScaleXMin() {
		return scaleXMin;
	}

	public void setScaleXMin(double scaleXMin) {
		this.scaleXMin = scaleXMin;
	}

	public double getScaleXMax() {
		return scaleXMax;
	}

	public void setScaleXMax(double scaleXMax) {
		this.scaleXMax = scaleXMax;
	}

	public double getScaleYMin() {
		return scaleYMin;
	}

	public void setScaleYMin(double scaleYMin) {
		this.scaleYMin = scaleYMin;
	}

	public double getScaleYMax() {
		return scaleYMax;
	}

	public void setScaleYMax(double scaleYMax) {
		this.scaleYMax = scaleYMax;
	}
}
