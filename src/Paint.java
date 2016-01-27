import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.image.*;

/**
 * 
 * The class for the main window of the program. Most of the GUI components are set up here.
 *
 */
class Paint extends JFrame implements ActionListener, ChangeListener {
	/** Size of icons for buttons. */
	private final int iconSize = 48;
	/** Width of drawing region. */
	private final int drawRegionWidth = 700;
	/** Height of drawing region. */
	private final int drawRegionHeight = 520;

	/** Default image width. */
	private final int defImgWidth = 640;
	/** Default image height. */
	private final int defImgHeight = 480;
	/** Default background color. */
	private final Color defImgBckColor = Color.WHITE;

	/** Width of last blank image created. */
	private int lastImgWidth = defImgWidth;
	/** Height of last blank image created. */
	private int lastImgHeight = defImgHeight;

	/** The drawing canvas. */
	private DrawingPane canvas;
	/** Label for dimensions of image. */
	private JLabel sizeLabel;
	/** Label for position of mouse. */
	private JLabel mousePositionLabel;
	/** Label for size of tool. */
	private JLabel toolSizeLabel;
	/** Label that informs the user if there are unsaved changes. */
	private JLabel unsavedLabel;
	/** Default message when there are unsaved changes. */
	private final String unsavedMsg = "SAVE";
	
	/** Default tool size. */
	private final int defToolSize = 1;
	
	/** Pencil button. */
	private JToggleButton pencil;
	/** Eraser button. */
	private JToggleButton eraser;
	/** Color picker button. */
	private JToggleButton colorPicker;
	/** Airbrush button. */
	private JToggleButton airbrush;
	/** Line button. */
	private JToggleButton line;
	/** Circle button. */
	private JToggleButton circle;
	/** Foreground color button. */
	private JButton colorButton;
	/** Background color button. */
	private JButton backColorButton;
	/** Color chooser. */
	
	/** Slider for choosing tool size. */
	private JSlider toolSizeSlider;
	/** Minimum value for slider. */
	private final int sliderMin = 0;
	/** Maximum value for slider. */
	private final int sliderMax = 50;
	/** Initial value for slider. */
	private final int sliderInit = defToolSize-1;

	/** Default window title. */
	final String defTitle = "CS 2110 Paint";
	/** Last used file. */
	File lastUsedFile;
	/** Default file name to save to. */
	final String defFileName = "untitled.png";
	/** Whether the image has unsaved changes or not. */
	boolean imageUnsaved = false;

	/**
	 * Constructs the main window of the program.
	 */
	public Paint() {
		super("CS 2110 Paint");
		setLayout(new BorderLayout());

		JMenuBar menuBar = setUpMenuBar();

		// Canvas & scoller
		canvas = new DrawingPane(this,defImgWidth,defImgHeight,defImgBckColor,defToolSize-1);
		JScrollPane scroller = new JScrollPane(canvas);
		scroller.setPreferredSize(new Dimension(drawRegionWidth,drawRegionHeight));

		// Set up tool bar.
		JToolBar toolBar = setUpToolBar();
		
		// Tool Size slider
		toolSizeSlider = new JSlider(JSlider.VERTICAL,sliderMin,sliderMax,sliderInit);
		toolSizeSlider.addChangeListener(this);
		toolSizeSlider.setMinorTickSpacing(1);
		toolSizeSlider.setPaintTicks(true);
		toolSizeSlider.setSnapToTicks(true);

		// Status bar panel
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		statusPanel.setPreferredSize(new Dimension(getWidth(),18));
		statusPanel.setLayout(new GridLayout(1,4));
		
		mousePositionLabel = new JLabel("Position:");
		mousePositionLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(mousePositionLabel);
		
		toolSizeLabel = new JLabel("Tool Size: " + canvas.getToolSize());
		statusPanel.add(toolSizeLabel);
		
		sizeLabel = new JLabel();
		updateSizeLabel();
		statusPanel.add(sizeLabel);
		
		unsavedLabel = new JLabel("");
		unsavedLabel.setForeground(Color.RED);
		statusPanel.add(unsavedLabel);

		// Add to window
		add(menuBar,BorderLayout.NORTH);
		add(toolSizeSlider,BorderLayout.WEST);
		add(statusPanel,BorderLayout.SOUTH);
		add(toolBar,BorderLayout.EAST);
		add(scroller,BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}


	/**
	 * Update the color of the foreground color button.
	 */
	public void updateColor() {
		ImageIcon icon = getIcon(canvas.getColor(),iconSize);
		colorButton.setIcon(icon);		
	}
	
	/**
	 * Update the color of the background color button.
	 */
	public void updateBackColor() {
		ImageIcon icon = getIcon(canvas.getBackColor(),iconSize);
		backColorButton.setIcon(icon);		
	}
	
	/**
	 * Call it to indicate the the image has been saved.
	 */
	private void setImageSaved() {
		imageUnsaved = false;
		unsavedLabel.setText("Saved");
		
	}
	
	/**
	 * Call it to indicate the the image has unsaved changes.
	 */
	public void setImageUnsaved() {
		imageUnsaved = true;
		unsavedLabel.setText(unsavedMsg);
		
	}
	
	/**
	 * Updates the label that displays the position of the mouse.
	 * 
	 * @param x x-coordinate of mouse
	 * @param y y-coordinate of mouse
	 */
	public void setMousePosition(int x, int y) {
		mousePositionLabel.setText("Location: ("+x+","+y+")");
	}

	public void stateChanged(ChangeEvent e) {
		Object s = e.getSource();

		if (s == toolSizeSlider) {
			// TODO: Implement me!
			//TODO: Change other things? Seems to work now?
				canvas.setToolSize(toolSizeSlider.getValue());
				this.toolSizeLabel.setText("Tool Size: " + Integer.toString(toolSizeSlider.getValue() + 1));
		}
		else {
			System.err.println("stateChanged: "+s);
		}
	}

	/**
	 * Updates the image size (dimensions) label.
	 */
	private void updateSizeLabel() {
		sizeLabel.setText(canvas.getPreferredSize().width+"x"+canvas.getPreferredSize().height);
		
	}
	
	private void newAction(ActionEvent e) {
		System.out.println("Action: New");
		
		NewImageDialog dialog = new NewImageDialog(this,true,lastImgWidth,lastImgHeight);
		Dimension d = dialog.getDimension();
		System.out.println("Dimension given in dialog: "+d);
		
		if (d != null) {
			canvas.newBlankImage(d.width,d.height,defImgBckColor);
			updateSizeLabel();
			
			lastUsedFile = null;
			setTitle(defTitle);
			setImageSaved();
			
			// update lastImgWidth, lastImgHeight
			lastImgWidth = d.width;
			lastImgHeight = d.height;
		}
	}

	private void openAction(ActionEvent e) {
		System.out.println("Action: Open");

		JFileChooser chooser = new JFileChooser(".");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files","jpeg","jpg","gif","png","bmp");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(this);
		File selectedFile = chooser.getSelectedFile();
		if (returnVal != JFileChooser.APPROVE_OPTION) return;

		System.out.println("You chose to open this file: " + selectedFile.getName());
		BufferedImage img = null;
		try {
			img = ImageIO.read(selectedFile);
		} catch (IOException exc) {
			System.out.println(exc.getMessage());
			return;
		}

		lastUsedFile = selectedFile;
		setTitle(defTitle + " - " + lastUsedFile.getName());
		setImageSaved();
		
		canvas.newImage(img);
		updateSizeLabel();
	}

	/**
	 * 
	 * Saves the image to the given file.
	 * 
	 * @param file File to save to.
	 * @throws IOException
	 */
	private void saveImg(File file) throws IOException {
		String fileName = file.getName();
		int dotPosition = fileName.lastIndexOf(".");
		String format = fileName.substring(dotPosition+1);
		System.out.println("Saving in: " + fileName);
		System.out.println("Format: " + format);

		ImageIO.write(canvas.getImg(),format,file);
	}

	private void saveAction(ActionEvent e) {
		System.out.println("Action: Save");

		if (lastUsedFile == null) {
			saveAsAction(e);
		}
		else {
			try {
				saveImg(lastUsedFile);
			}
			catch(IOException exc) {
				System.err.println(exc.getMessage());
			}
			setImageSaved();
		}
	}

	private void saveAsAction(ActionEvent e) {
		System.out.println("Action: Save As");	

		JFileChooser chooser = new JFileChooser();
		if (lastUsedFile != null)
			chooser.setSelectedFile(lastUsedFile);
		else {
			File currentDir = new File("");
			String currentDirPath = currentDir.getAbsolutePath();
			File defaultFile = new File(currentDirPath + "/" + defFileName);
			chooser.setSelectedFile(defaultFile);
		}

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files","jpeg","jpg","gif","png","bmp");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showSaveDialog(this);
		File selectedFile = chooser.getSelectedFile(); 
		if (returnVal != JFileChooser.APPROVE_OPTION) return;

		System.out.println("You chose to save to the file: " + selectedFile.getName());
		try {
			saveImg(selectedFile);

			lastUsedFile = selectedFile;
			setTitle(defTitle + " - " + lastUsedFile.getName());
			setImageSaved();
		}
		catch(IOException exc) {
			System.err.println(exc.getMessage());
		}
	}

	private void quitAction(ActionEvent e) {
		System.out.println("Action: Quit");
		// TODO: Implement me!
		System.exit(0);
		System.err.println("Implement me!");
	}

	private void helpAction(ActionEvent e) {
		System.out.println("Action: Help");
		
		JOptionPane.showMessageDialog(this,"help...","Help",JOptionPane.PLAIN_MESSAGE);
	}

	private void aboutAction(ActionEvent e) {
		System.out.println("Action: About");	

		JOptionPane.showMessageDialog(this,"about...","About",JOptionPane.PLAIN_MESSAGE);
	}

	// Handle events from toolbar.
	public void actionPerformed(ActionEvent e) {
		System.out.println("actionPerformed");
		
		Object s = e.getSource();

		if (s == pencil) {
			canvas.setActiveTool(Tool.PENCIL);
		}
		else if (s == eraser) {
			canvas.setActiveTool(Tool.ERASER);
		}
		else if (s == colorPicker) {
			canvas.setActiveTool(Tool.COLOR_PICKER);
		}
		else if (s == airbrush) {
			canvas.setActiveTool(Tool.AIRBRUSH);
		}
		else if (s == line) {
			canvas.setActiveTool(Tool.LINE);
		}
		else if (s == circle) {
			canvas.setActiveTool(Tool.CIRCLE);
		}
		else if (s == colorButton) {
			Color newColor = JColorChooser.showDialog(this,"Foreground Color",canvas.getColor());
			
			// TODO: Implement me!
			if(newColor != null){
				canvas.setColor(newColor);
				colorButton.setIcon(getIcon(canvas.getColor(), iconSize));
				
			}
			
		}
		else if (s == backColorButton) {
			Color newBackColor = JColorChooser.showDialog(this,"Background Color",canvas.getBackColor());
			
			// TODO: Implement me!
			if (newBackColor != null){
				canvas.setBackColor(newBackColor);
				backColorButton.setIcon(getIcon(canvas.getBackColor(), iconSize));
			
			}
			
		}
		else {
			System.err.println(s);
		}
	}
	
	
	/**
	 * Sets up the menu bar.
	 * 
	 * @return The menu bar.
	 */
	private JMenuBar setUpMenuBar() {
		// Menu bar
		JMenuBar menuBar = new JMenuBar();

		// File menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		JMenuItem newItem = new JMenuItem("New");
		newItem.setMnemonic(KeyEvent.VK_N);
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));
		newItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newAction(e);
			}
		});
		JMenuItem openItem = new JMenuItem("Open");
		openItem.setMnemonic(KeyEvent.VK_O);
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openAction(e);
			}
		});
		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.setMnemonic(KeyEvent.VK_S);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAction(e);
			}
		});
		JMenuItem saveAsItem = new JMenuItem("Save As");
		saveAsItem.setMnemonic(KeyEvent.VK_A);
		saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
		saveAsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAsAction(e);
			}
		});
		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.setMnemonic(KeyEvent.VK_Q);
		quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,ActionEvent.CTRL_MASK));
		quitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quitAction(e);
			}
		});
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(quitItem);
		
		// Help menu
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		JMenuItem helpItem = new JMenuItem("Help");
		helpItem.setMnemonic(KeyEvent.VK_H);
		helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,ActionEvent.CTRL_MASK));
		helpItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				helpAction(e);
			}
		});
		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.setMnemonic(KeyEvent.VK_A);
		aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aboutAction(e);
			}
		});
		helpMenu.add(helpItem);
		helpMenu.add(new JSeparator());
		helpMenu.add(aboutItem);
		
		// Add to menu bar
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);

		return menuBar;
	}

	/**
	 * Create a single-color icon, that is meant to be used as an icon for the foreground/background color buttons.
	 * 
	 * @param c Color.
	 * @param size Width/height.
	 * @return Icon of dimensions (size x size) and colored with color c.
	 */
	private static ImageIcon getIcon(Color c, int size) {
		// TODO: Implement me!
		BufferedImage imgIcon = new BufferedImage( size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) imgIcon.getGraphics();
		g2d.setColor(c);
		g2d.fillRect( 0, 0, size, size);
		return new ImageIcon(imgIcon);
	}
	
	/**
	 * Sets up the tool bar.
	 * 
	 * @return The tool bar.
	 */
	private JToolBar setUpToolBar() {
		// Toolbar

		ButtonGroup tools = new ButtonGroup();

		pencil = new JToggleButton(new ImageIcon("pencil.png"));
		pencil.setToolTipText("pencil");
		pencil.addActionListener(this);

		colorPicker = new JToggleButton(new ImageIcon("picker.png"));
		colorPicker.setToolTipText("color picker");
		colorPicker.addActionListener(this);

		eraser = new JToggleButton(new ImageIcon("eraser.png"));
		eraser.setToolTipText("eraser");
		eraser.addActionListener(this);

		airbrush = new JToggleButton(new ImageIcon("airbrush.png"));
		airbrush.setToolTipText("airbrush");
		airbrush.addActionListener(this);

		line = new JToggleButton(new ImageIcon("line.png"));
		line.setToolTipText("line");
		line.addActionListener(this);

		circle = new JToggleButton(new ImageIcon("circle.png"));
		circle.setToolTipText("circle");
		circle.addActionListener(this);
		
		tools.add(pencil);
		tools.add(colorPicker);
		tools.add(eraser);
		tools.add(airbrush);
		tools.add(line);
		tools.add(circle);
		
		// Foreground color chooser
		// TODO: After you implement the method getIcon, you should icons instead of text.
		//ImageIcon icon = getIcon(canvas.getColor(),iconSize);
		//colorButton = new JButton(icon);
		colorButton = new JButton("F. Color");		
		colorButton.setIcon(getIcon(canvas.getColor(), iconSize));
		
		colorButton.addActionListener(this);
		
		// Background color chooser
		// TODO: After you implement the method getIcon, you should icons instead of text.
		//ImageIcon backIcon = getIcon(canvas.getBackColor(),iconSize);
		//backColorButton = new JButton(backIcon);
		backColorButton = new JButton("B. Color");
		backColorButton.setIcon(getIcon(canvas.getBackColor(), iconSize));
		backColorButton.addActionListener(this);
		
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		toolBar.add(pencil);
		toolBar.add(colorPicker);
		toolBar.add(eraser);
		toolBar.add(airbrush);
		toolBar.add(line);
		toolBar.add(circle);
		toolBar.add(colorButton);
		toolBar.add(backColorButton);
		
		return toolBar;
	}
	
	public static void main(String[] args) {
		Paint mainWindow = new Paint();
		mainWindow.canvas.revalidate();

		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}
