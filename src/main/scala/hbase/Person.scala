package hbase

object Person{
  def apply(id: String, name: String): Person = new Person(id,name)
}

class Person(val id: String, val name:String){

    override def toString(): String = s"$id   $name"

}
