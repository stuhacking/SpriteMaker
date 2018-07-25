package spritepack.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

import spritepack.document.SampleScene;
import spritepack.document.Sprite;

/**
 * Custom painted canvas showing sample scene.
 */
class SampleCanvas extends JComponent implements Runnable {

  private static final int SLEEP_TIME = 1000 / 15; // ~15fps should be plenty.

  private static final Color DEFAULT_BACKGROUND  = new Color(50, 50, 65);
  private static final Color DEFAULT_MAIN_GRID   = new Color(110, 110, 110);
  private static final Color DEFAULT_SMALL_GRID  = new Color(110, 110, 110, 100);

  private Color bgColor = DEFAULT_BACKGROUND;

  private boolean drawGrid = true;
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

      try {
        Thread.sleep(SLEEP_TIME); // ~15fps (We don't have any animation)
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * Start this component rendering in a background thread.
   */
  void start () {
    if (!running) {
      running = true;
      new Thread(this).start();
    }
  }

  /**
   * Stop render thread.
   */
  void stop () {
    if (running) {
      running = false;
    }
  }

  @Override
  public void paintComponent (Graphics g) {
    Graphics2D g2 = (Graphics2D)g;

    int width = getWidth();
    int height = getHeight();

    g2.setColor(bgColor);
    g2.fillRect(0, 0, getWidth(), getHeight());

    // Only draw grid if the increments are reasonable.. 8x8 is probably small enough.
    Dimension size = mScene.grid;
    Dimension sSize = mScene.sGrid;

    // Draw Sprites
    for (Sprite s : mScene.getSprites()) {
      g2.drawImage(s.image, s.x, s.y, null);
    }

    if (drawGrid) {

      int incrementX = size.width / sSize.width;
      int incrementY = size.height / sSize.height;

      // Draw Horizontal
      for (int x = 0, c = 0; x < width; x += sSize.width, c = (c + 1) % incrementX) {
        g2.setColor((c == 0) ? DEFAULT_MAIN_GRID : DEFAULT_SMALL_GRID);
        g2.drawLine(x, 0, x, height);
      }

      // Draw Vertical
      for (int y = 0, c = 0; y < height; y += sSize.height, c = (c + 1) % incrementY) {
        g2.setColor((c == 0) ? DEFAULT_MAIN_GRID : DEFAULT_SMALL_GRID);
        g2.drawLine(0, y, width, y);
      }
    }
  }
}
