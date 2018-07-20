/**
 * Created: 18-Jul-2018
 */
package spritepack.document;

import java.awt.Image;
import java.util.Objects;

public class Sprite {
  public final String id;
  public final Image image;
  public final int x;
  public final int y;

  public Sprite (String pId, Image pImage, int xx, int yy) {
    id = pId;
    image = pImage;
    x = xx;
    y = yy;
  }

  @Override
  public boolean equals (Object pO) {
    if (this == pO) return true;
    if (pO == null || getClass() != pO.getClass()) return false;
    Sprite sprite = (Sprite) pO;
    return Objects.equals(id, sprite.id);
  }

  @Override
  public int hashCode () {

    return Objects.hash(id);
  }

}
