import java.sql.{Connection, DriverManager}

import com.typesafe.config.ConfigFactory

object DBConnectionTest extends App {

  var connection : Connection = _

  val config = ConfigFactory.load()
  println(config.getString("db.url"))

  try {
    Class.forName(config.getString("db.driver"))

    connection = DriverManager.getConnection(
      config.getString("db.url"),
      config.getString("db.username"),
      config.getString("db.password"))

    val statement = connection.createStatement
    val rs = statement.executeQuery("SELECT * FROM sds.settings")

    while (rs.next) {
      val key = rs.getString("settings_key")
      val value = rs.getString("settings_value")
      // println("settings_key = %s, settings_value = %s".format(key, value))
    }
  }
  catch {
    case e: Exception => e.getMessage
  }
  connection.close()
}
