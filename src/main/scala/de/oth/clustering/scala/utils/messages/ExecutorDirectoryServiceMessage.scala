package de.oth.clustering.scala.utils.messages

import akka.actor.Address

trait ExecutorDirectoryServiceMessage

/* REQUEST */
case object GetExecutorAddress extends ExecutorDirectoryServiceMessage

/* RESPONSE */
case class ExecutorAddress(address: Address) extends ExecutorDirectoryServiceMessage