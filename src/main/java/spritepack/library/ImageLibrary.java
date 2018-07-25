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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageLibrary {
  private static final Logger logger = Logger.getLogger("ImageLibrary");

  private final ArrayList<ImageFile> sprites = new ArrayList<>(1024);

  /**
   * Create an ImageLibrary by walking the directory tree starting at `pPath'
   * and selecting any image files found.
   *
   * @param pPath Root directory for library.
   */
  public ImageLibrary (String pPath)
  {
    ImageFinder finder = new ImageFinder();
    try {
      Files.walkFileTree(Paths.get(pPath), finder);
    } catch (IOException e) {
      logger.log(Level.WARNING, "Error finding images under: " + pPath, e);
    }
  }

  public List<ImageFile> getSprites () { return sprites; }

  public ImageFile getSprite (int index) throws IndexOutOfBoundsException {
    return sprites.get(index);
  }

  /**
   * File visitor to find files matching "*.png"
   * TODO [smh] Match a bunch of image extensions. For now I only need png.
   */
  private class ImageFinder extends SimpleFileVisitor<Path> {
    private final PathMatcher matcher =
        FileSystems.getDefault().getPathMatcher("glob:*.png");

    void find (Path file) {
      Path name = file.getFileName();
      if (null != name && matcher.matches(name)) {
        sprites.add(new ImageFile(file.toAbsolutePath()));
      }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
      find(file);
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
      find(dir);
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException e) {

      logger.log(Level.WARNING, "File visit failed: " + file, e);

      return FileVisitResult.CONTINUE;
    }
  }
}
