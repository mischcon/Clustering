package Exceptions

import worker.messages.Task

/**
  * Created by mischcon on 26.03.2017.
  */
case class TestFailException(task : Task, result: Throwable) extends Throwable
case class TestSuccessException(task : Task, result : Object) extends Throwable
