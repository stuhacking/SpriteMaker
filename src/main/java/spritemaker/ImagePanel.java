package spritemaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * A Panel displaying an image and various selection details.
 * Created: 29-Nov-2017
 */
public class ImagePanel extends JPanel implements Runnable {

  private Image image;
  private Color bgColor = Color.WHITE;

  private int gridSizeX = 0, gridSizeY = 0;
  private int startX = -1, startY = -1;
  private int endX = -1, endY = -1;

  public ImagePanel () {
    setDoubleBuffered(true);
  }

  public void setImage (Image pImage) {
    image = pImage;
    setPreferredSize(new Dimension(image.getWidth(null), image.getHeight(null)));
    revalidate();
  }

  public void setStart (int x, int y) {
    startX = x;
    startY = y;
  }

  public void setEnd (int x, int y) {
    endX = x;
    endY = y;
  }

  public void reset () {
    startX = startY = endY = endX = -1;
  }

  public void setGridSize (int x, int y) {
    gridSizeX = x;
    gridSizeY = y;
  }

  public void resetGrid () {
    gridSizeX = gridSizeY = 0;
  }

  public void setBgColor (Color col) {
    if (null != col) {
      bgColor = col;
    }
  }

  public Color getBgColor () {
    return bgColor;
  }

  public ImageIcon getIcon (int x, int y, int w, int h) {
    BufferedImage i = null;

    if (null != image) {
      i = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
      Graphics g = i.createGraphics();

      g.drawImage(image, -x, -y,
                  image.getWidth(null), image.getHeight(null),
                  null);
    }

    return new ImageIcon(i);
  }


  @Override
  public void run () {
    while (true) {
      repaint();

      try { Thread.sleep(33); } catch (InterruptedException e) { e.printStackTrace(); }
    }
  }

  @Override
  public void paintComponent (Graphics g) {
    int width = getWidth();
    int height = getHeight();

    g.setColor(bgColor);
    g.fillRect(0, 0, getWidth(), getHeight());

    if (null != image) {
      g.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
    }

    // Only draw grid if the increments are reasonable.. 8x8 is probably small enough.
    if (gridSizeX >= 8 && gridSizeY >= 8) {

      g.setColor(Color.LIGHT_GRAY);
      for (int x = 0; x < width; x += gridSizeX) {
        g.drawLine(x, 0, x, height);
      }
      for (int y = 0; y < height; y += gridSizeY) {
        g.drawLine(0, y, width, y);
      }
    }

    g.setColor(Color.BLUE);
    g.drawRect(startX, startY, endX - startX, endY - startY);

  }
}
