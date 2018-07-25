package spritepack.ui;

import java.nio.file.Path;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import spritepack.library.ImageFile;
import spritepack.library.ImageLibrary;

/**
 * JTable data model based on ImageLibrary data.
 */
class LibraryTableModel extends AbstractTableModel {

  /** Preview icon column. */
  static final int COLUMN_ICON = 0;

  /** Filename column (is also the ID.) */
  static final int COLUMN_NAME = 1;

  private static final ResourceBundle RESOURCES =
      ResourceBundle.getBundle("spritepack.ui.labels");

  private final ImageLibrary mLibrary;

  LibraryTableModel (ImageLibrary pLibrary) {
    this.mLibrary = pLibrary;
  }

  @Override
  public int getRowCount () {
    if (null == mLibrary)
      return 0;

    return mLibrary.getSprites().size();
  }

  @Override
  public int getColumnCount () {
    return 2;
  }

  @Override
  public Object getValueAt (int row, int col) {
    ImageFile image = mLibrary.getSprite(row);

    switch (col) {
      case COLUMN_ICON:
        return image.icon;
      case COLUMN_NAME:
        return image.filename.getFileName();
      default:
        return null;
    }
  }

  @Override
  public String getColumnName (int col) {

    switch (col) {
      case COLUMN_ICON:
        return RESOURCES.getString("tbl.header.icon");
      case COLUMN_NAME:
        return RESOURCES.getString("tbl.header.name");
      default:
        return String.valueOf(col); // Label extra columns with column index.
    }
  }

  public Class getColumnClass(int col) {
    switch (col) {
      case COLUMN_ICON:
        return ImageIcon.class;
      case COLUMN_NAME:
        return Path.class;
      default:
        return int.class;
    }
  }

  public boolean isCellEditable(int row, int col) { return false; }

  public void setValueAt(Object value, int row, int col) { /* nop */ }
}
