package spritepack.document;

import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public class SampleScene {

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

  public void addSprite (String id, Image icon, int x, int y) {
    mSprites.add(new Sprite(id, icon, x, y));
  }
}
