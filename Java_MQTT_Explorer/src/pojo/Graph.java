package pojo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.Timer;

import var.GlobalVar;

public class Graph {
	private ArrayList<DataPoint> points;
	private Rectangle bounds;
	
	//Settings
	private double maxOverShoot = 0.1;
	private int maxPoints = 10;
	private boolean drawDataPoints = true;
	private boolean drawLatestValue = false;
	private boolean enableAntialiasing = true;
	private int repaintDelay = 200;
	private int borderSize = 10;
	private int yScaleXOffset = 25, xScaleYOffset = 25;
	private boolean drawDebugBoxes = false;
	private boolean drawXSeparators = true, drawYSeparators = true;
	private int separatorsLength = 5;
	private double ySubdivision = 10;
	private boolean drawXNumbers = true, drawYNumbers = true;
	private boolean drawCoordinateSystem = true;
	private boolean drawGraphLine = true;
	private int xNumbersYOffset = 25, yNumbersXOffset = -25;
	private boolean autoPlaceNumbers = true;
	private int decimalPrecision = 2;
	private int autoPlaceNumbersOffset = 10;
	
	//Colors
	public static final Color
				COLOR_CYAN = new Color (10, 176, 209),
				COLOR_DGRAY = new Color (45, 45, 45),
				COLOR_RED = new Color(245, 66, 90);
	
	private Color
				graphColor = COLOR_CYAN,
				csColor = COLOR_DGRAY,
				debugColor = COLOR_RED;
	
	//Strokes
	public static final Stroke
				STROKE_1F = new BasicStroke(1.0f),
				STROKE_2F = new BasicStroke(2.0f);
				
	private Stroke
				graphStroke = STROKE_2F,
				csStroke = STROKE_2F,
				debugStroke = STROKE_1F;
	
	//Fonts
	public static final Font
				FONT_BASIC_SMALL = new Font("Arial", Font.PLAIN, 10);
	
	private Font
				csFont = FONT_BASIC_SMALL;
	
	//Dynamic
	private Timer repaintTimer;
	private double scaleXMin, scaleXMax, scaleYMin, scaleYMax;
	private int width, height;
	private Rectangle innerBounds;
	
	public Graph (Rectangle bounds) {
		setBounds(bounds);
		initPoints();
	}
	
	public Graph (int x, int y, int width, int height) {
		this(new Rectangle(x, y, width, height));
	}
	
	public Graph () {
		this(0, 0, 0, 0);
	}
	
	public void paintComponent(Graphics g) {
		if (GlobalVar.selectedPanel != GlobalVar.PNL_GRAPH) { return; }
		
		Graphics2D g2d = (Graphics2D) g;
		refreshPanel(g2d);
		
		drawDebugBoxes(g2d);
		
		drawCoordinateSystem(g2d);
		drawGraphLine(g2d);
	}

	private void drawDebugBoxes(Graphics2D g2d) {
		if (!drawDebugBoxes) { return; }
		
		g2d.setColor(debugColor);
		g2d.setStroke(csStroke);
		
		g2d.draw(bounds);
		g2d.draw(innerBounds);
	}

	private void drawCoordinateSystem(Graphics2D g2d) {
		if (!drawCoordinateSystem) { return; }
		
		g2d.setColor(csColor);
		g2d.setStroke(csStroke);
		g2d.setFont(csFont);
		
		//Y-Line
		int yX = (int) (innerBounds.getX() + yScaleXOffset);
		int yStartY = borderSize;
		int yEndY = (int) (borderSize + innerBounds.getHeight());
		
		g2d.drawLine(yX, yStartY, yX, yEndY);
		
		//X-Line (At Zero)
		int xX = (int) innerBounds.getX();
		int zeroY = getZeroY();
		int xY = zeroY + (zeroY > getMidY() ? -xScaleYOffset : xScaleYOffset);
		int xEndX = (int) (borderSize + innerBounds.getWidth());
		
		g2d.drawLine(xX, xY, xEndX, xY);
		
		//Zero String
		double zeroStringX = innerBounds.getX() + (double) yScaleXOffset - (double) xScaleYOffset/2.0;
		double zeroStringY = (innerBounds.getY() + innerBounds.getHeight()) - (((double) xScaleYOffset)/2.0);
		if (drawXNumbers || drawYNumbers) {
			drawCenteredString(
							g2d, "0",
							zeroStringX,
							zeroStringY);
		}
		
		//X-Separators
		if (drawXSeparators || drawXNumbers) {
			int xNumPoints = Math.max(getNumPoints(), 1);
			double sepWidthDistance = getWidthSpacing();
			
			int xStart = (int) (innerBounds.getX() + yScaleXOffset);
			
			for (int i = 1; i <= xNumPoints; i++) {
				int xLine = (int) (xStart + ((double) i * sepWidthDistance));
				
				if (i == xNumPoints) {
					xLine = (int) (innerBounds.getX() + innerBounds.getWidth());
				}
				
				if (drawXSeparators) {
					g2d.drawLine(	xLine, (int) (xY - ((double) separatorsLength/2.0)), 
									xLine, (int) (xY + ((double) separatorsLength/2.0)));
				}
				
				if (drawXNumbers) {
					String number = "" + i;
					Rectangle2D numberBounds = g2d.getFontMetrics().getStringBounds(number, g2d);
					double textWidth = numberBounds.getWidth();
					int yOffset = xNumbersYOffset;
					
					if (autoPlaceNumbers) {
						double textHeight = numberBounds.getHeight();
						yOffset = (int) (textHeight + autoPlaceNumbersOffset);
					}
					
					g2d.drawString(number, (int) (xLine - textWidth/2.0) , xY + yOffset);
				}
			}
		}
		
		//Y-Separators
		if (drawYSeparators || drawYNumbers) {
			String numberFormat = "0.";
			for (int j = 0; j < decimalPrecision; j++) {
				numberFormat += "#";
			}
			numberFormat += "E0";
			NumberFormat formatter = new DecimalFormat(numberFormat);
			
			double sepHeightDistance = (innerBounds.getHeight() - (double) xScaleYOffset) / ySubdivision;
			
			int yStart = (int) (innerBounds.getY() + innerBounds.getHeight() - xScaleYOffset);
			
			int xNumberOffsetTemp = 0;
			boolean autoPlaceOneBigger = false;
			for (int i = 1; i <= ySubdivision; i++) {
				int yLine = (int) (yStart - ((double) i * sepHeightDistance));
				
				if (i == ySubdivision) {
					yLine = (int) (innerBounds.getY());
				}
				
				if (drawYSeparators) {
					g2d.drawLine(	(int) (yX - ((double) separatorsLength/2.0)), yLine,
									(int) (yX + ((double) separatorsLength/2.0)), yLine);
				}
				
				if (drawYNumbers) {
					String number = formatter.format((double) i * (getMaxValue().getValue() / (double) ySubdivision));
					Rectangle2D numberBounds = g2d.getFontMetrics().getStringBounds(number, g2d);
					double textHeight = numberBounds.getHeight();
					xNumberOffsetTemp = yNumbersXOffset;
					
					autoPlaceOneBigger = false;
					if (autoPlaceNumbers) {
						double textWidth = numberBounds.getWidth();
						xNumberOffsetTemp = - (int) (textWidth + autoPlaceNumbersOffset);
						if (Math.abs(xNumberOffsetTemp) > yScaleXOffset) {
							autoPlaceOneBigger = true;
						}
					}
					
					g2d.drawString(number,  yX + xNumberOffsetTemp, (int) (yLine + (double) textHeight/3.0));
				}
			}
			
			if (autoPlaceOneBigger) {
				setYScaleXOffset(xNumberOffsetTemp);
			} else {
				setYScaleXOffset(yScaleXOffset);
			}
		}
	}

	private double mapDouble(double x, double in_min, double in_max, double out_min, double out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
	
	private double getWidthSpacing() {
		int xNumPoints = Math.max(getNumPoints(), 1);
		return (innerBounds.getWidth() - (double) yScaleXOffset) / (double) xNumPoints;
	}

	private void drawCenteredString(Graphics2D g2d, String string, double x, double y) {
		Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(string, g2d);
		double stringWidth = bounds.getWidth();
		double stringHeight = bounds.getHeight();
		
		g2d.drawString(string, (int) (x - (stringWidth / 2.0)), (int) (y + stringHeight / 2.0));
	}

	private void drawGraphLine(Graphics2D g2d) {
		if (!drawGraphLine) { return; }
		
		g2d.setColor(graphColor);
		g2d.setStroke(graphStroke);
		
		double lastY = 0;
		for (int i = 1; i < points.size(); i++) {
			//lastY = map(points.get(i).getValue(), getMinValue(), getMaxValue(), )
			//g2d.drawLine(, y1, x2, y2);
		}
	}
	
	private int getMidY() {
		return (int) (bounds.getHeight() / 2);
	}
	
	private int getZeroY() {
		double minValue = getMinValue().getValue();
		double maxValue = getMaxValue().getValue();
		
		minValue = Math.abs(Math.min(0, minValue));
		maxValue = Math.abs(Math.max(0, maxValue));
		
		double lineHeight = innerBounds.getHeight();
		
		double relation = 1;
		if (maxValue > 0 || minValue > 0) {
			relation = (maxValue/(minValue + maxValue));
		}
		
		return (int) (relation * lineHeight + borderSize);
	}

	public void updateVar() {
		if (bounds == null) { return; }
		
		setInnerBounds(new Rectangle(
						getBorderSize(),
						getBorderSize(),
						(int) (bounds.getWidth() - getBorderSize() * 2),
						(int) (bounds.getHeight() - getBorderSize() * 2)));
	}
	
	private void refreshPanel(Graphics2D g2d) {
		g2d.clearRect(0, 0, width, height);
		
		if (enableAntialiasing) {
			g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		}
	}

	public void updateYScale() {
		DataPoint max = getMaxValue();
		setScaleYMax(max.getValue() + max.getValue()*getMaxOverShoot());
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
		updateYScale();
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
		
		updateVar();
	}
	
	public void setBounds(int x, int y, int width, int height) {
		setBounds(new Rectangle (x, y, width, height));
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

	public boolean isDrawLatestValue() {
		return drawLatestValue;
	}

	public void setDrawLatestValue(boolean drawLatestValue) {
		this.drawLatestValue = drawLatestValue;
	}

	public int getRepaintDelay() {
		return repaintDelay;
	}

	public void setRepaintDelay(int repaintDelay) {
		this.repaintDelay = repaintDelay;
	}

	public Timer getRepaintTimer() {
		return repaintTimer;
	}

	public void setRepaintTimer(Timer repaintTimer) {
		this.repaintTimer = repaintTimer;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getBorderSize() {
		return borderSize;
	}

	public void setBorderSize(int borderSize) {
		this.borderSize = borderSize;
	}

	public int getYScaleXOffset() {
		return yScaleXOffset;
	}

	public void setYScaleXOffset(int yScaleXOffset) {
		this.yScaleXOffset = Math.abs(yScaleXOffset);
	}

	public Color getCsColor() {
		return csColor;
	}

	public void setCsColor(Color csColor) {
		this.csColor = csColor;
	}

	public Color getDebugColor() {
		return debugColor;
	}

	public void setDebugColor(Color debugColor) {
		this.debugColor = debugColor;
	}

	public int getXScaleYOffset() {
		return xScaleYOffset;
	}

	public void setXScaleYOffset(int xScaleYOffset) {
		this.xScaleYOffset = Math.abs(xScaleYOffset);
	}

	public boolean isDrawDebugBoxes() {
		return drawDebugBoxes;
	}

	public void setDrawDebugBoxes(boolean drawDebugBoxes) {
		this.drawDebugBoxes = drawDebugBoxes;
	}

	public boolean isDrawXSeparators() {
		return drawXSeparators;
	}

	public void setDrawXSeparators(boolean drawXSeparators) {
		this.drawXSeparators = drawXSeparators;
	}

	public boolean isDrawYSeparators() {
		return drawYSeparators;
	}

	public void setDrawYSeparators(boolean drawYSeparators) {
		this.drawYSeparators = drawYSeparators;
	}

	public int getSeparatorsLength() {
		return separatorsLength;
	}

	public void setSeparatorsLength(int separatorsLength) {
		this.separatorsLength = separatorsLength;
	}

	public Color getGraphColor() {
		return graphColor;
	}

	public void setGraphColor(Color graphColor) {
		this.graphColor = graphColor;
	}

	public Stroke getGraphStroke() {
		return graphStroke;
	}

	public void setGraphStroke(Stroke graphStroke) {
		this.graphStroke = graphStroke;
	}

	public Stroke getCsStroke() {
		return csStroke;
	}

	public void setCsStroke(Stroke csStroke) {
		this.csStroke = csStroke;
	}

	public Stroke getDebugStroke() {
		return debugStroke;
	}

	public void setDebugStroke(Stroke debugStroke) {
		this.debugStroke = debugStroke;
	}

	public Rectangle getInnerBounds() {
		return innerBounds;
	}

	public void setInnerBounds(Rectangle innerBounds) {
		if (innerBounds == null) { return; }
		
		this.innerBounds = innerBounds;
	}

	public double getySubdivision() {
		return ySubdivision;
	}

	public void setySubdivision(double ySubdivision) {
		this.ySubdivision = ySubdivision;
	}

	public boolean isDrawXNumbers() {
		return drawXNumbers;
	}

	public void setDrawXNumbers(boolean drawXNumbers) {
		this.drawXNumbers = drawXNumbers;
	}

	public boolean isDrawYNumbers() {
		return drawYNumbers;
	}

	public void setDrawYNumbers(boolean drawYNumbers) {
		this.drawYNumbers = drawYNumbers;
	}
}
