package proxy.dao

import java.sql.Timestamp

import proxy.dto.Request

object RequestDAO{
  def apply(sql: String,tableName:String,phoenixDAO: PhoenixDAO): RequestDAO = new RequestDAO(sql,tableName,phoenixDAO)
}

class RequestDAO(sql:String,tableName:String,phoenixDAO: PhoenixDAO) {
   def createTable: Unit ={
     phoenixDAO.createTable(sql)
   }

  def addRequest(requst:Request): Unit ={
    val statement = phoenixDAO.connection.createStatement()
    val value = s"upsert into $tableName values(${requst.ip},'${requst.time}','${requst.request.get}','${requst.browser.get}','${requst.operationSystem.get}')"
//    println(value)
    statement.executeUpdate(
      value)
    phoenixDAO.connection.commit()
    phoenixDAO.connection.close()
  }
  def getRequest(request: Request):Request={
    val prepareStatement = phoenixDAO.connection.prepareStatement(
      s"select * from $tableName where ip=${request.ip}"
    )
    val resultSet = prepareStatement.executeQuery()
    var result:Request=null
    val dto= Request
    while (resultSet.next()){
      val ip=resultSet.getInt(1)
      val time=resultSet.getTimestamp(2)
      val request=Option(resultSet.getString(3))
      val browser=Option(resultSet.getString(4))
      val operationSytem=Option(resultSet.getString(5))
      result= dto.apply(ip,time,request,browser,operationSytem)
    }
    result
  }

}
