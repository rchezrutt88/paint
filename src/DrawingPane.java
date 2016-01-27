import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.*;
import java.awt.Toolkit;
import java.util.*;

/**
 *
 * This class represents the painting canvas and implements all relevant functionality.
 *
 */
public class DrawingPane extends JPanel implements MouseListener, MouseMotionListener  {
	/** This is useful for creating custom cursors. */
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	/** Default foreground color. */
	private final Color defColor = Color.BLACK;

	/** The width of the image. */
	private int width;
	/** The height of the image. */
	private int height;
	/** The image. */
	private BufferedImage img;
	/** The main window of the program. */
	private Paint window;

	/** The active tool. */
	private Tool activeTool;
	/** The size of the tool. */
	private int toolSize;
	/** half = toolSize/2.0 */
	private double half;
	/** Previous position of the mouse. */
	private Point2D.Double prevMousePos; // previous mouse position (used to interpolate)

	/** State for line drawing. */
	private boolean pointGiven;
	/** State for line drawing. */
	private Point2D.Double linePoint;

	/** State for circle drawing. */
	private boolean centerGiven;
	/** State for circle drawing. */
	private Point2D.Double center;

	/** Position of mouse. */
	private Point2D.Double pMouse;

	/** Foreground color (used for drawing). */
	private Color color;
	/** Background color (used for erasing). */
	private Color backColor;

	/** Random generator for airbrush. */
	private Random gen = new Random(System.currentTimeMillis());

	/**
	 * Constructs a new drawing pane.
	 * 
	 * @param window Main window of application.
	 * @param width Width of image.
	 * @param height Height of image.
	 * @param bckColor Background color.
	 * @param toolSize Tool size.
	 */
	public DrawingPane(Paint window, int width, int height, Color bckColor, int toolSize) {
		this.window = window;
		this.width = width;
		this.height = height;
		this.activeTool = null;
		setToolSize(toolSize);

		// Create image with background color bckColor
		img = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		g2d.setColor(bckColor);
		g2d.fillRect(0,0,width,height);

		color = defColor;
		backColor = bckColor;

		addMouseListener(this);
		addMouseMotionListener(this);		
	}

	/**
	 * 
	 * Sets the foreground color.
	 * 
	 * @param c New foreground color.
	 */
	public void setColor(Color c) {
		if (c == null)
			throw new IllegalArgumentException();

		color = c;

		if (activeTool == Tool.LINE && pointGiven) {
			repaint();
		}
		if (activeTool == Tool.CIRCLE && centerGiven) {
			repaint();
		}
	}

	/**
	 * Sets the background color.
	 * 
	 * @param c New background color.
	 */
	public void setBackColor(Color c) {
		if (c == null)
			throw new IllegalArgumentException();

		backColor = c;
	}

	/**
	 * @return Foreground color.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @return Background color.
	 */
	public Color getBackColor() {
		return backColor;
	}

	/**
	 * @return The image.
	 */
	public BufferedImage getImg() {
		return img;
	}

	/**
	 * @return The tool size.
	 */
	public int getToolSize() {
		return toolSize;
	}

	/**
	 * Sets the tool size.
	 * 
	 * @param v New tool size - 1.
	 */
	public void setToolSize(int v) {
		if (v < 0)
			throw new IllegalArgumentException("setToolSize: v < 0");
		toolSize = v+1;
		half = toolSize / 2.0;
	}

	/**
	 * Creates new blank image (colored with the background color).
	 * 
	 * @param width Width of image.
	 * @param height Height of image.
	 * @param bckColor Background color.
	 */
	public void newBlankImage(int width, int height, Color bckColor) {
		this.width = width;
		this.height = height;

		// reset line/circle state
		pointGiven = false;
		centerGiven = false;

		img = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		g2d.setColor(bckColor);
		g2d.fillRect(0,0,width,height);

		repaint();
		revalidate();
	}

	/**
	 * Changes the image to a new one.
	 * 
	 * @param img An image.
	 */
	public void newImage(BufferedImage img) {
		System.out.println("newImage");

		// reset line/circle state
		pointGiven = false;
		centerGiven = false;

		this.width = img.getWidth();
		this.height = img.getHeight();
		this.img = img;

		repaint();
		revalidate();
	}


	public Dimension getPreferredSize() {
		return new Dimension(width,height);
	}

	public void paintComponent(Graphics g) {
		System.out.println("Paint drawing pane.");

		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// Draw a border around the image.

		int z = 0;
		for (int i=0; i<5; i++) {
			Color c = new Color(z,z,z);
			g2d.setColor(c);
			g2d.drawLine(0,height+i,width+i,height+i);
			g2d.drawLine(width+i,0,width+i,height+i);
			z += 63;
		}

		// TODO: Implement me!

		// HINTS:
		// There are potentially several things to do here.
		// You definitely need to paint the image.
		g2d.setClip(0, 0, width, height);
		g2d.drawImage(img, new AffineTransform(), this);
		System.out.println("Paint component called");
		
		if(pointGiven){
			g2d.setStroke(new BasicStroke((float) half, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2d.setColor(color);
			g2d.drawLine((int)linePoint.x, (int)linePoint.y, (int)pMouse.x, (int)pMouse.y);
		}
		
		if(centerGiven){
			double r = Point2D.distance(center.x, center.y, pMouse.x, pMouse.y);
			g2d.setStroke(new BasicStroke((float) half, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2d.setColor(color);
			g2d.drawOval((int)(center.x-r), (int)(center.y-r), (int)r*2, (int)r*2);
		}
		
		
		// It is also possible you want to draw things that are in the process of drawing:
		// e.g. lines & circles that are not actually drawn on the image until the process
		// of specifying the line/circle is complete.

	}

	/**
	 * 
	 * @return The active tool.
	 */
	public Tool getActiveTool() {
		return activeTool;
	}

	/**
	 * Sets the active tool.
	 * 
	 * @param t New active tool.
	 */
	public void setActiveTool(Tool t) {
		// reset line/circle state
		pointGiven = false;
		centerGiven = false;

		// Change cursor.
		if (t == Tool.PENCIL) {
			Point hotspot = new Point(2,30);
			Image cursorImage = tk.getImage("pencil-cursor.png");
			Cursor cursor = tk.createCustomCursor(cursorImage,hotspot,"Custom Cursor");
			this.setCursor(cursor);
		}
		else if (t == Tool.ERASER) {
			Point hotspot = new Point(5,27);
			Image cursorImage = tk.getImage("eraser-cursor.png");
			Cursor cursor = tk.createCustomCursor(cursorImage,hotspot,"Custom Cursor");
			this.setCursor(cursor);
		}
		else if (t == Tool.COLOR_PICKER) {
			Point hotspot = new Point(9,23);
			Image cursorImage = tk.getImage("picker-cursor.png");
			Cursor cursor = tk.createCustomCursor(cursorImage,hotspot,"Custom Cursor");
			this.setCursor(cursor);
		}
		else if (t == Tool.AIRBRUSH) {
			Point hotspot = new Point(1,25);
			Image cursorImage = tk.getImage("airbrush-cursor.png");
			Cursor cursor = tk.createCustomCursor(cursorImage,hotspot,"Custom Cursor");
			this.setCursor(cursor);
		}
		else if (t == Tool.LINE) {
			Point hotspot = new Point(0,0);
			Image cursorImage = tk.getImage("line-cursor.png");
			Cursor cursor = tk.createCustomCursor(cursorImage,hotspot,"Custom Cursor");
			this.setCursor(cursor);
		}
		else if (t == Tool.CIRCLE) {
			Point hotspot = new Point(16,16);
			Image cursorImage = tk.getImage("circle-cursor.png");
			Cursor cursor = tk.createCustomCursor(cursorImage,hotspot,"Custom Cursor");
			this.setCursor(cursor);
		}
		else {
			System.err.println("setActiveTool " + t);
		}

		activeTool = t;		
	}

	public void mouseClicked(MouseEvent e) {
		// Nothing to do here.
	}

	public void mouseEntered(MouseEvent e) {
		// Nothing to do here.
	}

	/**
	 * Updates the position of the mouse.
	 * 
	 * @param e A mouse event.
	 */
	private void updateMousePosition(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		// center of pixel
		pMouse = new Point2D.Double(x+0.5,y+0.5);
	}

	public void mousePressed(MouseEvent e) {
		updateMousePosition(e);
		System.out.println("mousePressed: " + pMouse + ", active tool: " + getActiveTool());
		if (this.window != null){
			window.setImageUnsaved();
		}
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON); // anti-aliasing

		if (activeTool == Tool.PENCIL) {
			System.out.println("mousePressed: pencil");

			// TODO:

			// Draw a square of size (toolSize x toolSize) and filled with the current foreground color.
			// Its center should be at the position of the mouse.
			// Draw from center of square!
			Shape square = new Rectangle2D.Double(pMouse.x-half, pMouse.y-half, toolSize, toolSize);
			g2d.setColor(color);
			g2d.fill(square);


		}
		else if (activeTool == Tool.ERASER) {
			System.out.println("mousePressed: eraser");
			// Paint with the active background color.

			// TODO:
			// Draw a square of size (toolSize x toolSize) and filled with the current background color.
			// Its center should be at the position of the mouse.
			// Draw from center of the square!

			Shape square = new Rectangle2D.Double(pMouse.x - half, pMouse.y-half, toolSize, toolSize);
			g2d.setColor(backColor);
			g2d.fill(square);
		}
		else if (activeTool == Tool.COLOR_PICKER) {
			System.out.println("mousePressed: pick color");
			int rgb = img.getRGB((int)pMouse.x, (int)pMouse.y);

			//changes color/background color based on button pressed
			Color picked = new Color(rgb);
			if(e.getButton()==MouseEvent.BUTTON1){
				color = picked;
				window.updateColor();
			}else if (e.getButton()==MouseEvent.BUTTON3){
				backColor = picked; 
				window.updateBackColor();
			}
			
			
			
			// TODO: Pick the color of the pixel the mouse is currently over.			
			// Left mouse button pressed: pick new foreground color
			// Right mouse button pressed: pick new background color
		}
		else if (activeTool == Tool.AIRBRUSH) {
			System.out.println("mousePressed: airbrush");
			//clumsy solution
			//Spray circle area rather than square?
			for(int i = 0; i < half; i++){
			
				double theta = gen.nextDouble()*2*Math.PI;
				double r = gen.nextDouble()*half;
				double x = pMouse.x + (r*Math.cos(theta))-half;
				double y = pMouse.y + (r*Math.sin(theta))-half;
				
				Shape point = new Rectangle2D.Double(x+half, y+half, 1, 1);
				g2d.setColor(color);
				g2d.fill(point);
			}
			
			
			// TODO:
			// Airbrush with the current foreground color in an area of size (toolSize x toolSize)
			// centered at the current position of the mouse.
			// HINT: Draw a few random pixels within the specified area.
			// You are free to choose any distribution you like. 
		}
		else if (activeTool == Tool.LINE){
			System.out.println("mousePressed: line");

			if(!pointGiven){
				pointGiven = true;
				linePoint = pMouse;
			}else{
				g2d.setStroke(new BasicStroke((float) half, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g2d.setColor(color);
				g2d.drawLine((int)linePoint.x, (int)linePoint.y, (int)pMouse.x, (int)pMouse.y);
				pointGiven = false;
			}
			
			// TODO:
			// May need to draw a line.
			// 
			// HINT:
			// One way to specify a line is by pressing the mouse twice to give the two endpoints
			// of the line. So, in the first press you just save the location where this
			// occurred and in the second one you do the actual drawing on the image.
			// 
			// After the first press, the user should be able to see a "tentative" line,
			// while they are moving the mouse around to find the spot the want for the
			// second endpoint.
		}
		else if (activeTool == Tool.CIRCLE){
			System.out.println("mousePressed: circle");
			
			if(!centerGiven){
				centerGiven = true;
				center = pMouse;
			}else{
				double r = Point2D.distance(center.x, center.y, pMouse.x, pMouse.y);
				g2d.setColor(color);
				g2d.setStroke(new BasicStroke((float) half, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g2d.drawOval((int)(center.x-r), (int)(center.y-r), (int)r*2, (int)r*2);
				centerGiven = false;
			}
			// TODO:
			// May need to draw a circle.
			// 
			// HINT:
			// One way to specify a circle is by pressing the mouse twice to give the center
			// and one point that belongs to the circle.
			// 
			// After the first press, the user should be able to see a "tentative" circle,
			// while they are moving the mouse around to find the spot the want for the
			// second endpoint.
		}
		else {
			System.err.println("Unknown tool: " + activeTool);
		}
		repaint();
		// set prevMousePos
		prevMousePos = pMouse;
	}

	public void mouseExited(MouseEvent e) {
		// Nothing to do here.
	}

	public void mouseReleased(MouseEvent e) {
		// End of drawing, reset prevMousePos.
		prevMousePos = null;
	}

	/**
	 * YOU DO NOT HAVE TO USE THIS METHOD IF YOU DON'T WANT TO.
	 * 
	 * If you choose to use it, you need to read it and understand it.
	 * This might be useful for drawing with the pencil and for erasing.
	 *
	 * @param from Start point.
	 * @param to End point.
	 * @param half Half the width/height of the interpolated square.
	 * @return
	 */
	private static Path2D.Double createPolygon(Point2D.Double from, Point2D.Double to, double half) {
		Path2D.Double polygon = new Path2D.Double();

		// W.l.o.g. from.y <= to.y
		if (from.y > to.y) {
			Point2D.Double tmp = from;
			from = to;
			to = tmp;
		}

		// So, there are two cases to examine:
		// 1) from.x <= to.x
		// 2) from.x > to.x
		boolean fromXLess = from.x <= to.x;

		// Start point
		double startX = from.x-half;
		double startY = from.y-half;

		// 6 points: A,B,C,D,E,F
		Point2D.Double pA = new Point2D.Double(startX,startY);
		Point2D.Double pB = new Point2D.Double(from.x+half,startY);

		Point2D.Double pC;
		if (fromXLess)
			pC = new Point2D.Double(to.x+half,to.y-half);
		else
			pC = new Point2D.Double(from.x+half,from.y+half);

		Point2D.Double pD = new Point2D.Double(to.x+half,to.y+half);
		Point2D.Double pE = new Point2D.Double(to.x-half,to.y+half);

		Point2D.Double pF;
		if (fromXLess)
			pF = new Point2D.Double(startX,from.y+half);
		else
			pF = new Point2D.Double(to.x-half,to.y-half);

		// Draw the polygon
		polygon.moveTo(pA.x,pA.y);
		polygon.lineTo(pB.x,pB.y);
		polygon.lineTo(pC.x,pC.y);
		polygon.lineTo(pD.x,pD.y);
		polygon.lineTo(pE.x,pE.y);
		polygon.lineTo(pF.x,pF.y);
		polygon.lineTo(pA.x,pA.y);

		return polygon;
	}

	public void mouseDragged(MouseEvent e) {
		updateMousePosition(e);
		if (window != null){
			window.setImageUnsaved();
		}
		System.out.println("mouseDragged: " + pMouse + ", active tool: " + activeTool);		

		Graphics2D g2d = (Graphics2D) img.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

		if (activeTool == Tool.PENCIL) {
			// TODO:
			// When the active tool is the pencil and the mouse is pressed and
			// dragged around, the trace of the mouse movement should be drawn.
			// 
			// HINT:
			// Notice that the mouse dragged events are not necessarily close to
			// each other (e.g. if you move the mouse fast). You have to interpolate
			// somehow. It is your work to find out exactly how.
			Path2D path = DrawingPane.createPolygon(prevMousePos, pMouse, half);
			g2d.setColor(color);
			g2d.fill(path);
			
			
			
		}
		else if (activeTool == Tool.ERASER) {
			// TODO:
			// When the active tool is the eraser and the mouse is pressed and
			// dragged around, the trace of the mouse movement should be drawn
			// (with the background color).
			Path2D path = DrawingPane.createPolygon(prevMousePos, pMouse, half);
			g2d.setColor(backColor);
			g2d.fill(path);
		}
		else if (activeTool == Tool.COLOR_PICKER) {
			// Nothing to do here.
		}
		else if (activeTool == Tool.AIRBRUSH) {
			// TODO:
			// You have to draw, but you do not need to interpolate.
			for(int i = 0; i < half; i++){
				
				double theta = gen.nextDouble()*2*Math.PI;
				double r = gen.nextDouble()*half;
				double x = pMouse.x + (r*Math.cos(theta))-half;
				double y = pMouse.y + (r*Math.sin(theta))-half;
				
				Shape point = new Rectangle2D.Double(x+half, y+half, 1, 1);
				g2d.setColor(color);
				g2d.fill(point);
			}
		}
		else {
			System.err.println("active tool: " + activeTool);
		}

		// update prevMousePos
		repaint();
		prevMousePos = pMouse;
	}

	public void mouseMoved(MouseEvent e) {
		prevMousePos = pMouse;
		updateMousePosition(e);
		window.setMousePosition((int)pMouse.x, (int)pMouse.y);
		repaint();

		// TODO: Implement me!
	}

}
