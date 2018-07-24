package spritepack.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

import spritepack.document.SampleScene;
import spritepack.document.Sprite;
import spritepack.library.ImageFile;
import spritepack.library.ImageLibrary;

/**
 * SpriteMaker application frmMainWnd.
 * Created: 29-Nov-2017
 */
class Main {

  // Display strings.
  private static final ResourceBundle RESOURCES =
      ResourceBundle.getBundle("spritepack.ui.labels");

  // UI Controls used by listeners.
  private static JFrame frmMainWnd;
  private static SampleCanvas pnlCanvasBg;
  private static JTable tblLibrary;
  private static TableRowSorter<LibraryTableModel> trsLibrarySorter;
  private static JFormattedTextField fldWidth, fldHeight;
  private static DocumentListener dlGridChange;

  private static JTextField fldFilter;

  // Data models.
  private static ImageLibrary library = new ImageLibrary("/home/shacking/dev/art/");
  private static SampleScene scene = new SampleScene();

  /**
   * Populate Menu actions.
   */
  private static void createMenus () {
    final JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(null);
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    JMenuBar menuBar = new JMenuBar();

    JMenu mnFile = new JMenu(RESOURCES.getString("menu.file"));
    mnFile.setMnemonic(KeyEvent.VK_F);

    JMenu mnEdit = new JMenu(RESOURCES.getString("menu.edit"));
    mnEdit.setMnemonic(KeyEvent.VK_E);

    menuBar.add(mnFile);
    menuBar.add(mnEdit);

    // =======================================================================
    // File Menu
    // =======================================================================

    // File -> Open
    {
      JMenuItem item = new JMenuItem(RESOURCES.getString("menu.item.open"));
      item.setMnemonic(KeyEvent.VK_O);
      item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));

      item.addActionListener(e -> {
        JFileChooser sceneChooser = new JFileChooser();
        sceneChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        sceneChooser.setDialogTitle(RESOURCES.getString("dlg.title.open_scene"));

        if (sceneChooser.showOpenDialog(frmMainWnd) == JFileChooser.APPROVE_OPTION) {
          try (BufferedReader in = new BufferedReader(new FileReader(sceneChooser.getSelectedFile()))) {

            scene.reset();

            String line = in.readLine();
            while (null != line) {
              String[] tokens = line.split(":");

              if ("g".equals(tokens[0]) && tokens.length == 3) { // Grid dimensions
                scene.setGrid(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));

                fldWidth.getDocument().removeDocumentListener(dlGridChange);
                fldHeight.getDocument().removeDocumentListener(dlGridChange);
                fldWidth.setValue(scene.grid.width);
                fldHeight.setValue(scene.grid.height);
                fldWidth.getDocument().addDocumentListener(dlGridChange);
                fldHeight.getDocument().addDocumentListener(dlGridChange);
              }

              if ("s".equals(tokens[0]) && tokens.length == 4) { // Sprite
                scene.addSprite(tokens[1],
                                Integer.parseInt(tokens[2]),
                                Integer.parseInt(tokens[3]));
              }

              line = in.readLine();
            }

          } catch (IOException ioe) {
            ioe.printStackTrace();
          }
        }

      });

      mnFile.add(item);
    }

    // File -> Save
    {
      JMenuItem item = new JMenuItem(RESOURCES.getString("menu.item.save"));
      item.setMnemonic(KeyEvent.VK_S);
      item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));

      item.addActionListener(e -> {
        JFileChooser sceneChooser = new JFileChooser();
        sceneChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        sceneChooser.setDialogTitle(RESOURCES.getString("dlg.title.save"));

        if (sceneChooser.showSaveDialog(frmMainWnd) == JFileChooser.APPROVE_OPTION) {

          try (BufferedWriter out = new BufferedWriter(new FileWriter(sceneChooser.getSelectedFile()))) {

            out.write(String.format("g:%d:%d\n", scene.grid.width, scene.grid.height));
            for (Sprite s : scene.getSprites()) {
              out.write(String.format("s:%s:%d:%d\n", s.id, s.x, s.y));
            }

          } catch (IOException ioe) {
            ioe.printStackTrace();
          }
        }
      });

      mnFile.add(item);
    }

    mnFile.addSeparator();

    // File -> Load Library
    {
      JMenuItem item = new JMenuItem(RESOURCES.getString("menu.item.load_library"));
      item.setMnemonic(KeyEvent.VK_L);

      item.addActionListener(e -> {
        chooser.setDialogTitle(RESOURCES.getString("dlg.title.select_library_path"));

        if (chooser.showOpenDialog(frmMainWnd) == JFileChooser.APPROVE_OPTION) {
          library = new ImageLibrary(chooser.getSelectedFile().toString());
          tblLibrary.setModel(new LibraryTableModel(library));
          trsLibrarySorter = new TableRowSorter<>((LibraryTableModel) tblLibrary.getModel());
          tblLibrary.setRowSorter(trsLibrarySorter);
        }

        chooser.setCurrentDirectory(chooser.getSelectedFile());
      });

      mnFile.add(item);
    }

    // File -> Export Texture
    {
      JMenuItem item = new JMenuItem(RESOURCES.getString("menu.item.export"));
      item.setMnemonic(KeyEvent.VK_E);

      item.addActionListener(e -> {
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

      mnFile.add(item);
    }

    mnFile.addSeparator();

    // File -> Quit
    {
      JMenuItem item = new JMenuItem(RESOURCES.getString("menu.item.quit"));
      item.setMnemonic(KeyEvent.VK_Q);
      item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));

      item.addActionListener(e -> {
        pnlCanvasBg.stop();
        System.exit(0);
      });

      mnFile.add(item);
    }

    // =======================================================================
    // Edit Menu
    // =======================================================================

    // Edit -> Undo
    {
      JMenuItem item = new JMenuItem(RESOURCES.getString("menu.item.undo"));
      item.setMnemonic(KeyEvent.VK_U);

      item.addActionListener(e -> scene.undo());

      mnEdit.add(item);
    }

    // Edit -> reset
    {
      JMenuItem item = new JMenuItem(RESOURCES.getString("menu.item.reset"));
      item.setMnemonic(KeyEvent.VK_R);

      item.addActionListener(e -> scene.reset());

      mnEdit.add(item);
    }


    frmMainWnd.setJMenuBar(menuBar);
  }

  /**
   * Populate Frame content area.
   */
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

      @Override
      public void mouseClicked (MouseEvent e) {
      }

      @Override
      public void mouseReleased (MouseEvent e) {
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

    dlGridChange = new DocumentListener() {
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
    fldWidth.getDocument().addDocumentListener(dlGridChange);

    fldHeight = new JFormattedTextField(NumberFormat.getInstance());
    fldHeight.setColumns(3);
    fldHeight.setValue(scene.grid.height);
    fldHeight.getDocument().addDocumentListener(dlGridChange);

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
