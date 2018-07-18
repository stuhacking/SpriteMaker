/**
 * Created: 30-Nov-2017
 */
package spritepack.library;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

/**
 * JTable data model based on ImageFile Document.
 */
public class LibraryTableModel extends AbstractTableModel {

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
        return "Icon";
      case 2:
        return "Name";
      default:
        return "id";
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
