/**
 * Created: 18-Jul-2018
 */
package spritepack.document;

import java.awt.Image;

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
}
