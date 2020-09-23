package s3.upload;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Params {
  public final List<Path> paths;
  public final String bucketName;

  public Params(List<Path> paths, String bucketName) {
    this.paths = paths;
    this.bucketName = bucketName;
  }

  public static Params parse(String[] args) {
    if (args.length < 2) {
      throw new IllegalArgumentException("Expecting at least 2 args: path bucketname");
    }

    List<Path> paths = new ArrayList<>();
    for (int i = 0; i < args.length - 1; i++) {
      String arg = args[i];
      if (arg.startsWith("glob:")) {
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(arg);
        Path basedir = Paths.get(arg.substring(5)).getParent();
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(basedir, pathMatcher::matches)) {
          dirStream.forEach(paths::add);
        } catch (IOException e) {
          throw new IllegalStateException(e);
        }
      } else {
        Path path = Paths.get(args[i]);
        if (!path.toFile().isFile()) {
          throw new IllegalArgumentException("File does not exist: " + path);
        }
        paths.add(path);
      }
    }

    String bucketName = args[args.length - 1];
    return new Params(paths, bucketName);
  }
}
