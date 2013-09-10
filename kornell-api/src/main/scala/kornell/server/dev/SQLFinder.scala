package kornell.server.dev

import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitResult._
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

import scala.collection.mutable.MutableList

class SQLFinder extends SimpleFileVisitor[Path] {
  val kornellApiSrc = System.getProperty("user.dir")
  val scriptsPath = Paths.get(kornellApiSrc, "src", "main", "ddl")

  val matcher = FileSystems.getDefault.getPathMatcher("glob:**.sql");
  //TODO: Change to scala version of SortedSet
  var result: MutableList[Path] = _

  override def visitFile(file: Path, attr: BasicFileAttributes): FileVisitResult = {
    if (matcher.matches(file))
      result += file
    CONTINUE
  }

  def files = {
    result = new MutableList[Path]
    Files.walkFileTree(scriptsPath, this);
    result.sorted
  }
}