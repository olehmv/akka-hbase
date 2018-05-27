package study

import java.io.File

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._
object Main {

  import javax.imageio.ImageIO

  val photo1 = ImageIO.read(new File("photo.jpg"))

  def main(args: Array[String]): Unit = {
    val value: universe.Type = typeOf[Test]
    value.typeSymbol.asClass.annotations.foreach(println)
    typeOf[hbase.entity.Car].typeSymbol.asClass.annotations.foreach(println)
    typeOf[hbase.entity.Car].decls.collect {
      case f: Symbol =>
        f.annotations.foreach(a=>a.tree.children.foreach(println))
        f.annotations.foreach(println)
    }


  }
}
