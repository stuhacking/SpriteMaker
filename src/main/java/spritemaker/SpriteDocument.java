/**
 * Created: 30-Nov-2017
 */
package spritemaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpriteDocument {

  private String filename;
  private ArrayList<Sprite> sprites = new ArrayList<>();


  public void read (BufferedReader in) throws IOException {
    String line;

    while ((line = in.readLine()) != null) {
      if (line.isEmpty() || line.startsWith("#")) continue;

      String[] tokens = line.split(":");
      if (tokens[0].startsWith("file")) {
        filename = tokens[1];
      } else if (tokens.length >= 6){
        int id = Integer.parseInt(tokens[0], 10);
        String name = tokens[1];
        int w = Integer.parseInt(tokens[2], 10);
        int h = Integer.parseInt(tokens[3], 10);
        int x = Integer.parseInt(tokens[4], 10);
        int y = Integer.parseInt(tokens[5], 10);

        if (id < sprites.size()) {
          // Evidence of tampering...
          System.err.println("Warning: Out of order ids in source file.");
        }

        sprites.add(new Sprite(name, null, w, h, x, y));
      }
    }
  }

  public void save (Writer out) throws IOException {
    out.write("# Sprite Maker v0.1\n\n");
    out.write("file:" + filename + "\n\n");
    out.write("# Format: id:desc:width:height:x:y:[x2:y2...]\n");
    out.write("# All values in pixels, origin at top left.\n");
    out.write("# Additional x,y pairs represent frames.\n\n");

    int k = 0;
    for (Sprite s : sprites) {
      out.write(String.format("%d:%s:%d:%d:%d:%d\n",
                              k, s.name,
                              s.width, s.height,
                              s.x, s.y));

      ++k;
    }
  }

  public void setFilename (String pFilename) {
    filename = pFilename;
  }

  public String getFilename () {
    return filename;
  }

  public List<Sprite> getSprites () { return sprites; }

  public Sprite getSprite (int index) throws IndexOutOfBoundsException {
    return sprites.get(index);
  }

  public void reset () {
    sprites.clear();
  }

  public void addSprite (Sprite s) {
    sprites.add(s);
  }

  public void sort () {
    sprites.sort(new Comparator<Sprite>() {
      @Override
      public int compare (Sprite a, Sprite b) {
        if (a.y < b.y) {
          return -1;
        } else {
          if (a.y == b.y) {
            if (a.x < b.x) {
              return -1;
            } else if (a.x == b.x) {
              return 0;
            }
          }
        }

        return 1;
      }
    });

  }

}
