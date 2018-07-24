package spritepack.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
import javax.swing.RowFilter;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import spritepack.document.SampleScene;
import spritepack.library.ImageFile;
import spritepack.library.ImageLibrary;

/**
 * SpriteMaker application frmMainWnd.
 * Created: 29-Nov-2017
 */
public class Main {

  private static final ResourceBundle RESOURCES =
      ResourceBundle.getBundle("spritepack.ui.labels");

  private static JFrame frmMainWnd;
  private static SampleCanvas pnlCanvasBg;
  private static JTable tblLibrary;
  private static TableRowSorter<LibraryTableModel> trsLibrarySorter;
  private static JFormattedTextField fldWidth, fldHeight;
  private static JTextField fldFilter;

  // Data
  private static ImageLibrary library = new ImageLibrary("/home/shacking/dev/art/");
  private static SampleScene scene = new SampleScene();

  private static void createMenus () {
    final JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(null);
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    // FILE MENU
    JMenu mnFile = new JMenu(RESOURCES.getString("menu.file"));
    mnFile.setMnemonic(KeyEvent.VK_F);

    JMenuItem miOpen = new JMenuItem(RESOURCES.getString("menu.item.open"));
    miOpen.setMnemonic(KeyEvent.VK_O);

    miOpen.addActionListener(e -> {
      chooser.setDialogTitle(RESOURCES.getString("dlg.title.select_library_path"));

      if (chooser.showOpenDialog(frmMainWnd) == JFileChooser.APPROVE_OPTION) {
        library = new ImageLibrary(chooser.getSelectedFile().toString());
        tblLibrary.setModel(new LibraryTableModel(library));
        trsLibrarySorter = new TableRowSorter<>((LibraryTableModel)tblLibrary.getModel());
        tblLibrary.setRowSorter(trsLibrarySorter);
      }

      chooser.setCurrentDirectory(chooser.getSelectedFile());
    });

    JMenuItem miSave = new JMenuItem(RESOURCES.getString("menu.item.save"));
    miSave.setMnemonic(KeyEvent.VK_S);

    miSave.addActionListener(e -> {
      chooser.setDialogTitle(RESOURCES.getString("dlg.title.save"));

      if (chooser.showSaveDialog(frmMainWnd) == JFileChooser.APPROVE_OPTION) {
        // TODO [smh] Save sample scene
        System.out.println("Save scene...");
      }
    });

    JMenuItem miExport = new JMenuItem(RESOURCES.getString("menu.item.export"));
    miExport.setMnemonic(KeyEvent.VK_E);

    miExport.addActionListener(e -> {
      chooser.setDialogTitle(RESOURCES.getString("dlg.title.export"));

      if (chooser.showSaveDialog(frmMainWnd) == JFileChooser.APPROVE_OPTION) {
        RenderedImage texture = scene.export();

        try {
          ImageIO.write(texture, "png", chooser.getSelectedFile());
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }
    });

    JMenuItem miQuit = new JMenuItem(RESOURCES.getString("menu.item.quit"));
    miQuit.setMnemonic(KeyEvent.VK_Q);

    miQuit.addActionListener(e -> {
      pnlCanvasBg.stop();
      System.exit(0);
    });

    mnFile.add(miOpen);
    mnFile.add(miSave);
    mnFile.addSeparator();
    mnFile.add(miExport);
    mnFile.addSeparator();
    mnFile.add(miQuit);

    // EDIT MENU
    JMenu mnEdit = new JMenu(RESOURCES.getString("menu.edit"));
    mnEdit.setMnemonic(KeyEvent.VK_E);

    JMenuItem miUndo = new JMenuItem(RESOURCES.getString("menu.item.undo"));
    miUndo.setMnemonic(KeyEvent.VK_U);

    miUndo.addActionListener(e -> scene.undo());

    JMenuItem miReset = new JMenuItem(RESOURCES.getString("menu.item.reset"));
    miReset.setMnemonic(KeyEvent.VK_R);

    miReset.addActionListener(e -> scene.reset());

    mnEdit.add(miUndo);
    mnEdit.add(miReset);

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(mnFile);
    menuBar.add(mnEdit);

    frmMainWnd.setJMenuBar(menuBar);
  }

  private static void createContent () {
    Container contentPane = frmMainWnd.getContentPane();
    contentPane.setLayout(new BorderLayout());

    // =======================================================================
    // Main View
    // =======================================================================

    pnlCanvasBg = new SampleCanvas(scene);
    pnlCanvasBg.start();

    MouseAdapter m = new MouseAdapter() {

      int startX, startY;
      int endX, endY;

      @Override
      public void mouseClicked (MouseEvent e) {
      }

      @Override
      public void mouseReleased (MouseEvent e) {
        TableModel model = tblLibrary.getModel();

        int row = tblLibrary.convertRowIndexToModel(tblLibrary.getSelectedRow());
        ImageFile sprite = library.getSprite(row);

        if (row >= 0) {
          ImageIcon icon = sprite.icon;
          String id = sprite.filename.toString();

          int x = e.getX() + scene.grid.width;
          x -= x % scene.grid.width;

          int y = e.getY() + scene.grid.height;
          y -= y % scene.grid.height;

          for (int xx = startX; xx < x; xx += scene.grid.width) {
            for (int yy = startY; yy < y; yy += scene.grid.height) {
              scene.addSprite(id, icon.getImage(), xx, yy);
            }
          }
        }
      }

      @Override
      public void mousePressed (MouseEvent e) {
        startX = e.getX();
        startX -= startX % scene.grid.width;

        startY = e.getY();
        startY -= startY % scene.grid.height;
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

    tblLibrary = new JTable(new LibraryTableModel(library));
    tblLibrary.setRowHeight(48);
    tblLibrary.setFillsViewportHeight(false);

    trsLibrarySorter =
        new TableRowSorter<>((LibraryTableModel)tblLibrary.getModel());
    tblLibrary.setRowSorter(trsLibrarySorter);

    JScrollPane spLibrary = new JScrollPane(tblLibrary);

    JPanel pnlLibrary = new JPanel();
    pnlLibrary.setLayout(new BorderLayout());

    fldFilter = new JTextField();
    fldFilter.setColumns(30);

    fldFilter.getDocument().addDocumentListener(new DocumentListener () {
      @Override
      public void insertUpdate (DocumentEvent e) { doChange(); }

      @Override
      public void removeUpdate (DocumentEvent e) { doChange(); }

      @Override
      public void changedUpdate (DocumentEvent e) { doChange(); }

      private void doChange () {
        RowFilter<LibraryTableModel, Object> rf;

        //If current expression doesn't parse, don't update.
        try {
          rf = RowFilter.regexFilter(fldFilter.getText(), 2);
        } catch (java.util.regex.PatternSyntaxException se) {
          return;
        }

        trsLibrarySorter.setRowFilter(rf);
      }
    });

    JPanel pnlLibraryCtl = new JPanel();
    pnlLibraryCtl.add(new JLabel(RESOURCES.getString("fld.filter")));
    pnlLibraryCtl.add(fldFilter);

    pnlLibrary.add(spLibrary, BorderLayout.CENTER);
    pnlLibrary.add(pnlLibraryCtl, BorderLayout.NORTH);

    // =======================================================================
    // Controls
    // =======================================================================

    DocumentListener gridListener = new DocumentListener() {
      @Override
      public void insertUpdate (DocumentEvent e) { doChange(); }

      @Override
      public void removeUpdate (DocumentEvent e) { doChange(); }

      @Override
      public void changedUpdate (DocumentEvent e) { doChange(); }

      private void doChange () {
        int width = ((Number) fldWidth.getValue()).intValue();
        int height = ((Number) fldHeight.getValue()).intValue();

        scene.setGrid(width, height);
      }
    };

    fldWidth = new JFormattedTextField(NumberFormat.getInstance());
    fldWidth.setColumns(3);
    fldWidth.setValue(scene.grid.width);
    fldWidth.getDocument().addDocumentListener(gridListener);

    fldHeight = new JFormattedTextField(NumberFormat.getInstance());
    fldHeight.setColumns(3);
    fldHeight.setValue(scene.grid.height);
    fldHeight.getDocument().addDocumentListener(gridListener);

    JCheckBox chkShowGrid = new JCheckBox();
    chkShowGrid.setSelected(true);
    chkShowGrid.addItemListener(
        e -> pnlCanvasBg.setDrawGrid(e.getStateChange() == ItemEvent.SELECTED));

    JButton colorPicker = new JButton();
    colorPicker.setBackground(pnlCanvasBg.getBgColor());
    colorPicker.setPreferredSize(new Dimension(20, 20));

    colorPicker.addActionListener(e -> {
      Color newColor = JColorChooser.showDialog(
          frmMainWnd,
          RESOURCES.getString("dlg.title.select_background"),
          pnlCanvasBg.getBgColor());

      colorPicker.setBackground(newColor);
      pnlCanvasBg.setBgColor(newColor);
    });

    JPanel pnlCanvasCtl = new JPanel();
    pnlCanvasCtl.add(new JLabel(RESOURCES.getString("fld.background")));
    pnlCanvasCtl.add(colorPicker);
    pnlCanvasCtl.add(new JLabel(RESOURCES.getString("fld.show_grid")));
    pnlCanvasCtl.add(chkShowGrid);
    pnlCanvasCtl.add(new JLabel(RESOURCES.getString("fld.grid_size")));
    pnlCanvasCtl.add(fldWidth);
    pnlCanvasCtl.add(fldHeight);

    // =======================================================================
    // Assemble Main View
    // =======================================================================

    JPanel pnlTab1 = new JPanel();
    pnlTab1.setLayout(new BorderLayout());

    pnlTab1.add(imageContainer, BorderLayout.CENTER);
    pnlTab1.add(pnlCanvasCtl, BorderLayout.SOUTH);
    pnlTab1.add(pnlLibrary, BorderLayout.EAST);

    contentPane.add(pnlTab1);
  }


  public static void main (String[] args) {
    frmMainWnd = new JFrame(RESOURCES.getString("dlg.title.main"));
    frmMainWnd.setSize(new Dimension(1280, 800));
    frmMainWnd.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    createMenus();
    createContent();

    frmMainWnd.setVisible(true);
  }
}
