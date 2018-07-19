package spritepack.library;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class ImageLibrary {

  private final ArrayList<ImageFile> sprites = new ArrayList<>();

  public ImageLibrary (String pPath)
  {
    ImageFinder finder = new ImageFinder();
    try {
      Files.walkFileTree(Paths.get(pPath), finder);
    } catch (IOException e) {
      System.err.println("Error finding image files in: " + pPath);
    }
  }

  public List<ImageFile> getSprites () { return sprites; }

  public ImageFile getSprite (int index) throws IndexOutOfBoundsException {
    return sprites.get(index);
  }

  private class ImageFinder extends SimpleFileVisitor<Path> {
    private PathMatcher matcher =
        FileSystems.getDefault().getPathMatcher("glob:*.png");

    void find (Path file) {
      Path name = file.getFileName();
      if (null != name && matcher.matches(name)) {
        sprites.add(new ImageFile(file.toAbsolutePath()));
      }
    }

    // Invoke the pattern matching
    // method on each file.
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
      find(file);
      return FileVisitResult.CONTINUE;
    }

    // Invoke the pattern matching
    // method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
      find(dir);
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file,
                                           IOException exc) {
      exc.printStackTrace();
      return FileVisitResult.CONTINUE;
    }
  }
}
