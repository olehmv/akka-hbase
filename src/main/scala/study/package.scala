import scala.reflect.runtime.universe._
package object study{
  def getClassAnnotaion[T:TypeTag]=ReflectHelper.classAnnotations[T]
  def getMethodAnnotaion[T:TypeTag]=ReflectHelper.methodAnnotations[TypeTag[T]]
}