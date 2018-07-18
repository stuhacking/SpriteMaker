package spritepack.ui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * A Panel displaying an image and various selection details.
 * Created: 29-Nov-2017
 */
public class CanvasBackground extends JPanel implements Runnable {

  private static final Color DEFAULT_BACKGROUND  = new Color(50, 50, 65);
  private static final Color DEFAULT_MAIN_GRID   = Color.LIGHT_GRAY.darker().darker();
  private static final Color DEFAULT_SMALL_GRID  = DEFAULT_MAIN_GRID.darker();

  private Color bgColor = DEFAULT_BACKGROUND;

  private int gridOffsetX = 0, gridOffsetY = 0;
  private int gridSizeX = 0, gridSizeY = 0;

  public CanvasBackground () {
    setDoubleBuffered(true);
  }

  public void setGridSize (int x, int y, int w, int h) {
    gridOffsetX = x;
    gridOffsetY = y;
    gridSizeX = w;
    gridSizeY = h;
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

  @Override
  public void run () {
    while (true) {
      repaint();

      try { Thread.sleep(250); } catch (InterruptedException e) { e.printStackTrace(); }
    }
  }

  @Override
  public void paintComponent (Graphics g) {
    int width = getWidth();
    int height = getHeight();

    g.setColor(bgColor);
    g.fillRect(0, 0, getWidth(), getHeight());

    // Only draw grid if the increments are reasonable.. 8x8 is probably small enough.
    if (gridSizeX >= 8 && gridSizeY >= 8) {

      int increment = (gridSizeX <= 16 || gridSizeY <= 16) ? 2 : 4;
      int incrementX = gridSizeX / increment;
      int incrementY = gridSizeY / increment;

      // Draw Horizontal
      for (int x = 0, c = 0; x < width; x += incrementX, c = (c + 1) % increment) {
        g.setColor((c == 0) ? DEFAULT_MAIN_GRID : DEFAULT_SMALL_GRID);
        g.drawLine(x + gridOffsetX, 0, x + gridOffsetX, height);
      }

      // Draw Vertical
      for (int y = 0, c = 0; y < height; y += incrementY, c = (c + 1) % increment) {
        g.setColor((c == 0) ? DEFAULT_MAIN_GRID : DEFAULT_SMALL_GRID);
        g.drawLine(0, y + gridOffsetY, width, y + gridOffsetY);
      }
    }
  }
}
