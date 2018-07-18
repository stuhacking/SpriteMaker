/**
 * Created: 30-Nov-2017
 */
package spritepack.ui;

import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import spritepack.library.ImageFile;
import spritepack.library.ImageLibrary;

/**
 * JTable data model based on ImageFile Document.
 */
public class LibraryTableModel extends AbstractTableModel {

  private static final ResourceBundle RESOURCES =
      ResourceBundle.getBundle("spritepack.ui.labels");

  private ImageLibrary doc;

  public LibraryTableModel (ImageLibrary doc) {
    this.doc = doc;
  }

  @Override
  public int getRowCount () {
    if (null == doc)
      return 0;

    return doc.getSprites().size();
  }

  @Override
  public int getColumnCount () {
    return 3; // id, icon, filename
  }

  @Override
  public Object getValueAt (int row, int col) {
    ImageFile s = doc.getSprite(row);

    switch (col) {
      case 1:
        return s.icon;
      case 2:
        return s.filename.getFileName();
      default:
        return row; // Position in list is ID.
    }
  }

  @Override
  public String getColumnName (int col) {

    switch (col) {
      case 1:
        return RESOURCES.getString("tbl.header.icon");
      case 2:
        return RESOURCES.getString("tbl.header.name");
      default:
        return RESOURCES.getString("tbl.header.id");
    }
  }

  public Class getColumnClass(int col) {
    switch (col) {
      case 1:
        return ImageIcon.class;
      default:
        return int.class;
    }
  }

  public boolean isCellEditable(int row, int col) { return false; }

  public void setValueAt(Object value, int row, int col) { }
}
