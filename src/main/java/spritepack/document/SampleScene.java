package spritepack.document;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class SampleScene implements Serializable {
  private static final long serialVersionUid = 57L;

  /** Major grid size. */
  public Dimension grid;

  /** Small grid size. */
  public Dimension sGrid;

  private List<Sprite> mSprites = new ArrayList<>(256);

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

  public void addSprite (String id, int x, int y) {
    mSprites.add(new Sprite(id, x, y));
  }

  public void addSprite (String id, Image icon, int x, int y) {
    mSprites.add(new Sprite(id, icon, x, y));
  }

  public RenderedImage export () {
    List<Sprite> uniqueSprites = new ArrayList<>(new HashSet<>(mSprites));
    uniqueSprites.sort(new Comparator<Sprite>() {
      @Override
      public int compare (Sprite o1, Sprite o2) {
        return o1.id.compareTo(o2.id);
      }
    });

    int sq = (int)Math.ceil(Math.sqrt(uniqueSprites.size()));

    int width = nearest2Pow(sq * grid.width);
    int height = nearest2Pow(sq * grid.height);

    BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D)output.getGraphics();

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
