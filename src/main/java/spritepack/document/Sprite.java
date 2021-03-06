package spritepack.document;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Sample Scene sprite.
 */
public class Sprite {
  private static final Logger logger = Logger.getLogger("Sprite");

  public final String id;
  public final Image image;
  public final int x;
  public final int y;


  public Sprite (String pId, int xx, int yy) {
    id = pId;
    x = xx;
    y = yy;

    Image readImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    try  {
      readImage = ImageIO.read(new File(pId));
    } catch (IOException e) {
      logger.log(Level.WARNING, "Can't open image: " + pId, e);
    } finally {
      image = readImage;
    }
  }

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
