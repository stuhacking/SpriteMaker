package spritepack.document;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class SampleScene {

  /** Major grid size. */
  public Dimension grid;

  /** Small grid size. */
  public Dimension sGrid;

  private final List<Sprite> mSprites = new ArrayList<>(256);

  public SampleScene () {
    setGrid(32, 32);
  }

  public void undo () {
    mSprites.remove(mSprites.size() - 1);
  }

  public void reset () {
    mSprites.clear();
  }

  public void setGrid (int x, int y) {
    grid = new Dimension(x, y);

    int incX = (x <= 16) ? 2 : 4;
    int incY = (y <= 16) ? 2 : 4;

    sGrid = new Dimension(x / incX, y / incY);
  }

  public List<Sprite> getSprites () {
    return mSprites;
  }

  /**
   * Add a new Sprite to the sample scene, must provide a unique id (usually
   * absolute path), an icon and the [x,y] coordinates.
   *
   * @param id Unique ID, usually file path.
   * @param icon Sprite icon
   * @param x X coordinate
   * @param y Y coordinate
   */
  public void addSprite (String id, Image icon, int x, int y) {
    mSprites.add(new Sprite(id, icon, x, y));
  }

  /**
   * Write a description of this sample scene to the output writer provided.
   */
  public void save (BufferedWriter out) throws IOException {

    out.write(String.format("g:%d:%d\n", grid.width, grid.height));

    for (Sprite s : mSprites) {
      out.write(String.format("s:%s:%d:%d\n", s.id, s.x, s.y));
    }
  }

  /**
   * Read a description of a scene from the input reader provided and replace
   * the current scene data.
   */
  public void read (BufferedReader in) throws IOException {

    reset();

    String line = in.readLine();
    while (null != line) {
      String[] tokens = line.split(":");

      if ("g".equals(tokens[0]) && tokens.length == 3) { // Grid dimensions
        setGrid(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));

      }

      if ("s".equals(tokens[0]) && tokens.length == 4) { // Sprite
        mSprites.add(new Sprite(tokens[1],
                                Integer.parseInt(tokens[2]),
                                Integer.parseInt(tokens[3])));
      }

      line = in.readLine();
    }
  }

  /**
   * Create a texture pack image containing all unique sprites comprising the
   * sample scene.
   */
  public RenderedImage export () {
    List<Sprite> uniqueSprites = new ArrayList<>(new HashSet<>(mSprites));
    uniqueSprites.sort(Comparator.comparing(pO -> pO.id));

    int sq = (int)Math.ceil(Math.sqrt(uniqueSprites.size()));

    // TODO [smh] Improve texture size calculation, e.g. Non-square output
    int width = nearest2Pow(sq * grid.width);
    int height = nearest2Pow(sq * grid.height);

    BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D)output.getGraphics();

    // TODO [smh] Stabilize sprite placement so that a size change maintains indexing?
    int x = 0, y = 0;
    for (Sprite s : uniqueSprites) {
      g.drawImage(s.image, x, y, null);

      x += grid.width;

      if ((x + grid.width) > width) { // Out of room
        x = 0;
        y += grid.height;
      }
    }

    return output;
  }

  private static int nearest2Pow (int n) {
    int result = 1;

    while (result < n) {
      result *= 2;
    }

    return result;
  }
}
