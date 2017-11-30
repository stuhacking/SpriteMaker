package spritemaker;

import javax.swing.ImageIcon;

/**
 * Sprite Data.
 * Created: 29-Nov-2017
 */
public class Sprite {

  public String name;      /**< Sprite readable name. */
  public ImageIcon icon;   /**< Sprite icon (for ease of naming). */

  public final int width;  /**< Sprite width. */
  public final int height; /**< Sprite height. */
  public final int x;      /**< X offset from left of texture. */
  public final int y;      /**< Y offset from top of texture. */

  /**
   * Create a Sprite.
   *
   * @param pName
   * @param pIcon
   * @param pWidth
   * @param pHeight
   * @param pX
   * @param pY
   */
  public Sprite (String pName, ImageIcon pIcon, int pWidth, int pHeight, int pX, int pY) {
    name = pName;
    icon = pIcon;
    width = pWidth;
    height = pHeight;
    x = pX;
    y = pY;
  }

  /**
   * Sprite string printer.
   */
  public String toString () {
    return String.format("<%s [%dx%d] %d %d>", name, width, height, x, y);
  }
}
