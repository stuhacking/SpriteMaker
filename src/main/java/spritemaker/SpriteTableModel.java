/**
 * Created: 30-Nov-2017
 */
package spritemaker;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

/**
 * JTable data model based on Sprite Document.
 */
public class SpriteTableModel extends AbstractTableModel {

  private SpriteDocument doc;

  public SpriteTableModel (SpriteDocument doc) {
    this.doc = doc;
  }

  @Override
  public int getRowCount () {
    return doc.getSprites().size();
  }

  @Override
  public int getColumnCount () {
    return 7; // id, icon, name, width, height, x, y
  }

  @Override
  public Object getValueAt (int row, int col) {
    Sprite s = doc.getSprite(row);

    switch (col) {
      case 1:
        return s.icon;
      case 2:
        return s.name;
      case 3:
        return s.width;
      case 4:
        return s.height;
      case 5:
        return s.x;
      case 6:
        return s.y;
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
      case 3:
        return "Width";
      case 4:
        return "Height";
      case 5:
        return "x";
      case 6:
        return "y";
      default:
        return "id";
    }
  }

  public Class getColumnClass(int col) {
    switch (col) {
      case 1:
        return ImageIcon.class;
      case 2:
        return String.class;
      default:
        return int.class;
    }
  }

  /* Only name is editable by hand. */
  public boolean isCellEditable(int row, int col) { return col == 2; }

  public void setValueAt(Object value, int row, int col) {
    if (col == 2) {
      Sprite s = doc.getSprite(row);
      s.name = (String)value;
      fireTableCellUpdated(row, col);
    }
  }
}
