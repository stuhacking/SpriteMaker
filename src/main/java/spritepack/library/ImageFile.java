package spritepack.library;

import java.nio.file.Path;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * ImageFile Data.
 * Created: 29-Nov-2017
 */
public class ImageFile {

  public Path filename;      /**< Image file name */
  public ImageIcon icon;     /**< Image icon preview. */
  public List<String> tags;  /**< Tags for filtering. */

  /**
   * Create a ImageFile.
   *
   * @param pFilename Image filename
   * @param pTags List of filterable terms
   */
  public ImageFile (Path pFilename, List<String> pTags) {
    filename = pFilename.toAbsolutePath();
    icon = new ImageIcon(filename.toString());
    tags = pTags;
  }

  /**
   * ImageFile string printer.
   */
  public String toString () {
    StringBuilder sb = new StringBuilder();

    sb.append("<ImageFile ").append(filename).append(" [");
    String sep = "";
    for (String s : tags) {
      sb.append(sep).append(s);
      sep = ", ";
    }
    sb.append("]>");

    return sb.toString();
  }
}
