package spritepack.library;

import java.nio.file.Path;
import javax.swing.ImageIcon;

/**
 * ImageFile Data.
 * Created: 29-Nov-2017
 */
public class ImageFile {

  /** Image filename */
  public final Path filename;

  /** Image preview icon */
  public final ImageIcon icon;

  /**
   * Create a ImageFile.
   *
   * @param pFilename Image filename
   */
  ImageFile (Path pFilename) {
    filename = pFilename.toAbsolutePath();
    icon = new ImageIcon(filename.toString());
  }

  /**
   * ImageFile string printer.
   */
  public String toString () {
    return "<ImageFile " + filename + ">";
  }
}
