package spritepack.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

import spritepack.document.SampleScene;
import spritepack.document.Sprite;

/**
 * A Panel displaying an image and various selection details.
 * Created: 29-Nov-2017
 */
public class SampleCanvas extends JPanel implements Runnable {

  private static final Color DEFAULT_BACKGROUND  = new Color(50, 50, 65);
  private static final Color DEFAULT_MAIN_GRID   = Color.LIGHT_GRAY.darker().darker();
  private static final Color DEFAULT_SMALL_GRID  = DEFAULT_MAIN_GRID.darker();

  private Color bgColor = DEFAULT_BACKGROUND;

  private boolean drawGrid = false;
  private SampleScene mScene;

  SampleCanvas (SampleScene pScene) {
    mScene = pScene;

    setDoubleBuffered(true);
  }

  void setDrawGrid (boolean pDrawGrid) {
    drawGrid = pDrawGrid;
  }

  void setBgColor (Color col) {
    if (null != col) {
      bgColor = col;
    }
  }

  Color getBgColor () {
    return bgColor;
  }

  private boolean running = false;

  @Override
  public void run () {
    while (running) {
      repaint();

      try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
    }
  }

  void start () {
    if (!running) {
      running = true;
      new Thread(this).start();
    }
  }

  void stop () {
    if (running) {
      running = false;
    }
  }

  @Override
  public void paintComponent (Graphics g) {
    int width = getWidth();
    int height = getHeight();

    g.setColor(bgColor);
    g.fillRect(0, 0, getWidth(), getHeight());

    // Only draw grid if the increments are reasonable.. 8x8 is probably small enough.
    Dimension size = mScene.grid;
    Dimension sSize = mScene.sGrid;

    if (drawGrid) {

      int incrementX = size.width / sSize.width;
      int incrementY = size.height / sSize.height;

      // Draw Horizontal
      for (int x = 0, c = 0; x < width; x += sSize.width, c = (c + 1) % incrementX) {
        g.setColor((c == 0) ? DEFAULT_MAIN_GRID : DEFAULT_SMALL_GRID);
        g.drawLine(x, 0, x, height);
      }

      // Draw Vertical
      for (int y = 0, c = 0; y < height; y += sSize.height, c = (c + 1) % incrementY) {
        g.setColor((c == 0) ? DEFAULT_MAIN_GRID : DEFAULT_SMALL_GRID);
        g.drawLine(0, y, width, y);
      }

    }

    // Draw Sprites
    for (Sprite s : mScene.getSprites()) {
      g.drawImage(s.image, s.x, s.y, null);
    }
  }
}
