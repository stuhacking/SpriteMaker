package spritepack.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import spritepack.library.ImageLibrary;
import spritepack.library.LibraryTableModel;

/**
 * SpriteMaker application frmMainWnd.
 * Created: 29-Nov-2017
 */
public class Main {

  public static final int DEFAULT_GRID_SIZE = 32;

  private static JFrame frmMainWnd;
  private static CanvasBackground pnlCanvasBg;
  private static JTable tblLibrary;
  private static JFormattedTextField fldWidth, fldHeight, fldOX, fldOY;
  private static JTextField fldFilter;

  // Data
  private static ImageLibrary doc = new ImageLibrary("/home/shacking/dev/art/");

  private static void createMenus () {
    final JFileChooser libraryChooser = new JFileChooser();
    libraryChooser.setCurrentDirectory(null);
    libraryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    // FILE MENU
    JMenu mFile = new JMenu("File");
    mFile.setMnemonic('F');

    JMenuItem miOpen = new JMenuItem("Open");
    miOpen.setMnemonic('O');

    miOpen.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed (ActionEvent e) {

        if (libraryChooser.showOpenDialog(frmMainWnd) == JFileChooser.APPROVE_OPTION) {
          doc = new ImageLibrary(libraryChooser.getSelectedFile().toString());
          tblLibrary.setModel(new LibraryTableModel(doc));
        }

        libraryChooser.setCurrentDirectory(libraryChooser.getSelectedFile());
      }
    });

    JMenuItem miSave = new JMenuItem("Save");
    miSave.setMnemonic('S');

    miSave.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed (ActionEvent e) {
      }
    });

    JMenuItem miQuit = new JMenuItem("Quit");
    miQuit.setMnemonic('Q');

    miQuit.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed (ActionEvent e) {
        System.exit(0);
      }
    });

    mFile.add(miOpen);
    mFile.add(miSave);
    mFile.addSeparator();
    mFile.add(miQuit);

    // IMAGE MENU

    JMenu mImage = new JMenu("Image");
    mImage.setMnemonic('I');

    JMenuItem miSelect = new JMenuItem("Select Image");
    miSelect.setMnemonic('S');

    miSelect.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed (ActionEvent e) {
      }
    });

    JMenuItem miReset = new JMenuItem("Reset");
    miReset.setMnemonic('R');

    miReset.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed (ActionEvent e) {
      }
    });

    mImage.add(miSelect);
    mImage.add(miReset);

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(mFile);
    menuBar.add(mImage);

    frmMainWnd.setJMenuBar(menuBar);
  }

  private static void createContent () {
    Container contentPane = frmMainWnd.getContentPane();
    contentPane.setLayout(new BorderLayout());

    // =======================================================================
    // Main View
    // =======================================================================

    pnlCanvasBg = new CanvasBackground();
    new Thread(pnlCanvasBg).start();

    MouseAdapter m = new MouseAdapter() {

      @Override
      public void mouseClicked (MouseEvent e) {
      }

      @Override
      public void mouseReleased (MouseEvent e) {
      }

      @Override
      public void mousePressed (MouseEvent e) {
      }

      @Override
      public void mouseDragged (MouseEvent e) {
      }
    };

    pnlCanvasBg.addMouseListener(m);
    pnlCanvasBg.addMouseMotionListener(m);

    JScrollPane imageContainer = new JScrollPane(pnlCanvasBg);
    imageContainer.getVerticalScrollBar().setUnitIncrement(10);

    // =======================================================================
    // Image Library
    // =======================================================================



    // =======================================================================
    // Controls
    // =======================================================================

    fldOX = new JFormattedTextField(NumberFormat.getInstance());
    fldOX.setColumns(3);
    fldOX.setValue(0);

    fldOY = new JFormattedTextField(NumberFormat.getInstance());
    fldOY.setColumns(3);
    fldOY.setValue(0);

    fldWidth = new JFormattedTextField(NumberFormat.getInstance());
    fldWidth.setColumns(3);
    fldWidth.setValue(DEFAULT_GRID_SIZE);

    fldHeight = new JFormattedTextField(NumberFormat.getInstance());
    fldHeight.setColumns(3);
    fldHeight.setValue(DEFAULT_GRID_SIZE);

    JCheckBox chkShowGrid = new JCheckBox();
    chkShowGrid.setSelected(false);
    chkShowGrid.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged (ItemEvent e) {
        int width = ((Number) fldWidth.getValue()).intValue();
        int height = ((Number) fldHeight.getValue()).intValue();
        int ox = ((Number) fldOX.getValue()).intValue();
        int oy = ((Number) fldOY.getValue()).intValue();

        if (e.getStateChange() == ItemEvent.DESELECTED)
          pnlCanvasBg.resetGrid();
        else
          pnlCanvasBg.setGridSize(ox, oy, width, height);
      }
    });

    JButton colorPicker = new JButton();
    colorPicker.setBackground(pnlCanvasBg.getBgColor());
    colorPicker.setPreferredSize(new Dimension(20, 20));

    colorPicker.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed (ActionEvent e) {
        Color newColor = JColorChooser.showDialog(frmMainWnd,
                                                  "Choose Background",
                                                  pnlCanvasBg.getBgColor());

        colorPicker.setBackground(newColor);
        pnlCanvasBg.setBgColor(newColor);
      }
    });

    JPanel pnlCanvasCtl = new JPanel();
    pnlCanvasCtl.add(new JLabel("Background"));
    pnlCanvasCtl.add(colorPicker);
    pnlCanvasCtl.add(new JLabel("Show Grid"));
    pnlCanvasCtl.add(chkShowGrid);
    pnlCanvasCtl.add(new JLabel("Grid Size"));
    pnlCanvasCtl.add(fldWidth);
    pnlCanvasCtl.add(fldHeight);
    pnlCanvasCtl.add(new JLabel("Offset"));
    pnlCanvasCtl.add(fldOX);
    pnlCanvasCtl.add(fldOY);


    tblLibrary = new JTable(new LibraryTableModel(doc));
    tblLibrary.setRowHeight(48);
    tblLibrary.setFillsViewportHeight(false);

    JScrollPane spLibrary = new JScrollPane(tblLibrary);

    JPanel pnlLibrary = new JPanel();
    pnlLibrary.setLayout(new BorderLayout());

    fldFilter = new JTextField();
    fldFilter.setColumns(30);

    JPanel pnlLibraryCtl = new JPanel();
    pnlLibraryCtl.add(new JLabel("Filter: "));
    pnlLibraryCtl.add(fldFilter);

    pnlLibrary.add(spLibrary, BorderLayout.CENTER);
    pnlLibrary.add(pnlLibraryCtl, BorderLayout.NORTH);

    // Build Main Tab
    JPanel pnlTab1 = new JPanel();
    pnlTab1.setLayout(new BorderLayout());

    pnlTab1.add(imageContainer, BorderLayout.CENTER);
    pnlTab1.add(pnlCanvasCtl, BorderLayout.SOUTH);
    pnlTab1.add(pnlLibrary, BorderLayout.EAST);

    contentPane.add(pnlTab1);
  }


  public static void main (String[] args) {
    frmMainWnd = new JFrame("Sprite Pack Editor");
    frmMainWnd.setSize(new Dimension(1280, 800));
    frmMainWnd.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    createMenus();
    createContent();

    frmMainWnd.setVisible(true);
  }
}
