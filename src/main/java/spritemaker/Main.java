package spritemaker;

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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * SpriteMaker application frame.
 * Created: 29-Nov-2017
 */
public class Main {

  private static JFrame frame;

  private static ImagePanel imagePanel;
  private static JTable tblSprites;

  private static JFormattedTextField fldWidth, fldHeight;

  // Data
  private static SpriteDocument doc = new SpriteDocument();

  private static void createMenus () {
    // FILE MENU
    JMenu mFile = new JMenu("File");
    mFile.setMnemonic('F');

    JMenuItem miOpen = new JMenuItem("Open");
    miOpen.setMnemonic('O');

    miOpen.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed (ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
          File f = chooser.getSelectedFile();

          try (BufferedReader in = new BufferedReader(new FileReader(f))) {
            doc.read(in);

            String imageFile = doc.getFilename();
            if (null != imageFile) {
              File img = new File(imageFile);
              imagePanel.setImage(ImageIO.read(img));
            }

            for (Sprite s : doc.getSprites()) {
              s.icon = imagePanel.getIcon(s.x, s.y, s.width, s.height);
            }

            tblSprites.revalidate();
          } catch (IOException ioe) {
            ioe.printStackTrace();
          }
        }

      }
    });

    JMenuItem miSave = new JMenuItem("Save");
    miSave.setMnemonic('S');

    miSave.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed (ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
          File f = chooser.getSelectedFile();

          try (BufferedWriter out = new BufferedWriter(new FileWriter(f))) {
            doc.save(out);

          } catch (IOException ioe) {
            ioe.printStackTrace();
          }
        }

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
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        chooser.setFileFilter(
            new FileNameExtensionFilter(null, "jpg", "png", "bmp", "gif"));

        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
          File f = chooser.getSelectedFile();

          doc.setFilename(f.getPath());
          doc.reset();

          try {
            imagePanel.setImage(ImageIO.read(f));
          } catch (IOException pE) {
            pE.printStackTrace();
          }
        }
      }
    });

    JMenuItem miReset = new JMenuItem("Reset");
    miReset.setMnemonic('R');

    miReset.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed (ActionEvent e) {
        doc.reset();
      }
    });

    mImage.add(miSelect);
    mImage.add(miReset);

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(mFile);
    menuBar.add(mImage);

    frame.setJMenuBar(menuBar);
  }

  private static void createContent () {
    Container contentPane = frame.getContentPane();
    contentPane.setLayout(new BorderLayout());

    // Main View

    imagePanel = new ImagePanel();
    new Thread(imagePanel).start();

    MouseAdapter m = new MouseAdapter() {

      @Override
      public void mouseClicked (MouseEvent e) {
        int width = ((Number) fldWidth.getValue()).intValue();
        int height = ((Number) fldHeight.getValue()).intValue();

        int x = (e.getX() / width) * width;
        int y = (e.getY() / height) * height;

        ImageIcon icon = imagePanel.getIcon(x, y, width, height);

        Sprite s = new Sprite("untitled", icon, width, height, x, y);

        doc.addSprite(s);
        tblSprites.revalidate();
      }

      @Override
      public void mouseReleased (MouseEvent e) {

        imagePanel.reset();
      }

      @Override
      public void mousePressed (MouseEvent e) {
        int width = ((Number) fldWidth.getValue()).intValue();
        int height = ((Number) fldHeight.getValue()).intValue();

        int x = (e.getX() / width) * width;
        int y = (e.getY() / height) * height;

        imagePanel.setStart(x, y);
        imagePanel.setEnd(x + width, y + height);
      }

      @Override
      public void mouseDragged (MouseEvent e) {
        int width = ((Number) fldWidth.getValue()).intValue();
        int height = ((Number) fldHeight.getValue()).intValue();

        int x = (e.getX() / width) * width;
        int y = (e.getY() / height) * height;

        imagePanel.setEnd(x + width, y + height);
      }

    };
    imagePanel.addMouseListener(m);
    imagePanel.addMouseMotionListener(m);

    JScrollPane imageContainer = new JScrollPane(imagePanel);
    imageContainer.getVerticalScrollBar().setUnitIncrement(10);

    // Controls
    fldWidth = new JFormattedTextField(NumberFormat.getInstance());
    fldWidth.setValue(48);

    fldHeight = new JFormattedTextField(NumberFormat.getInstance());
    fldHeight.setValue(48);

    JCheckBox chkShowGrid = new JCheckBox();
    chkShowGrid.setSelected(false);
    chkShowGrid.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged (ItemEvent e) {
        int width = ((Number) fldWidth.getValue()).intValue();
        int height = ((Number) fldHeight.getValue()).intValue();

        if (e.getStateChange() == ItemEvent.DESELECTED)
          imagePanel.resetGrid();
        else
          imagePanel.setGridSize(width, height);
      }
    });

    JButton colorPicker = new JButton();
    colorPicker.setBackground(imagePanel.getBgColor());
    colorPicker.setPreferredSize(new Dimension(20, 20));

    colorPicker.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed (ActionEvent e) {
        Color newColor = JColorChooser.showDialog(frame,
                                                  "Choose Background",
                                                  imagePanel.getBgColor());

        colorPicker.setBackground(newColor);
        imagePanel.setBgColor(newColor);
      }
    });

    JPanel pnlControls = new JPanel();
    pnlControls.add(new JLabel("Background"));
    pnlControls.add(colorPicker);
    pnlControls.add(new JLabel("Show Grid"));
    pnlControls.add(chkShowGrid);
    pnlControls.add(new JLabel("Grid Size"));
    pnlControls.add(fldWidth);
    pnlControls.add(fldHeight);

    // Main tabs
    JTabbedPane content = new JTabbedPane();

    // Add Image View tab.
    JPanel pnlTab1 = new JPanel();
    pnlTab1.setLayout(new BorderLayout());

    pnlTab1.add(imageContainer, BorderLayout.CENTER);
    pnlTab1.add(pnlControls, BorderLayout.SOUTH);

    content.addTab("Image", pnlTab1);

    // Add Document view tab
    JPanel pnlTab2 = new JPanel();
    pnlTab2.setLayout(new BorderLayout());

    tblSprites = new JTable(new SpriteTableModel(doc));
    tblSprites.setRowHeight(48);
    tblSprites.setPreferredScrollableViewportSize(new Dimension(pnlTab2.getWidth(), pnlTab2.getHeight()));
    tblSprites.setFillsViewportHeight(true);

    JScrollPane tableContainer = new JScrollPane(tblSprites);

    JButton btnSort = new JButton("Sort");
    btnSort.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed (ActionEvent e) {
        doc.sort();
        tblSprites.invalidate();
      }
    });

    pnlControls = new JPanel();
    pnlControls.add(btnSort);

    pnlTab2.add(tableContainer, BorderLayout.CENTER);
    pnlTab2.add(pnlControls, BorderLayout.SOUTH);

    content.addTab("Sprites", pnlTab2);

    contentPane.add(content);
  }


  public static void main (String[] args) {
    frame = new JFrame("Sprite Maker");
    frame.setSize(new Dimension(640, 800));
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    createMenus();
    createContent();

    frame.setVisible(true);
  }
}
