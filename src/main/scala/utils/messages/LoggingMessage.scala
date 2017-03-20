package utils.messages

import java.util.Date
import java.util.logging.Level

/**
  * Created by mischcon on 3/20/17.
  */

trait LoggingMessage

case class LogEntry(msg : String, level : Level, timestamp : Date = new Date()) extends LoggingMessage
