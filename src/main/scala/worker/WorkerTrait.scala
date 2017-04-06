package worker

import akka.actor.{Actor, ActorLogging}
import akka.persistence.PersistentActor

trait WorkerTrait extends Actor with ActorLogging
