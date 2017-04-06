package worker
import akka.actor.{ActorRef, Props, Terminated}
import worker.messages.{AddTask, GetTask, HasTask, NoMoreTasks}
import scala.concurrent.duration._

class DistributorActor extends WorkerTrait{

  var childrenTuple : List[(ActorRef, Class[_])] = List.empty

  override def preStart(): Unit = {
    super.preStart()
    log.debug("hello from distributor!")
  }

  override def postStop(): Unit = {
    super.postStop()
    log.debug("Goodbye from distributor!")
  }

  override def receive: Receive = {
    case p : AddTask => addTask(p)
    case p : GetTask => getTask(p)
    case t : Terminated => childrenTuple = childrenTuple.filter(a => a._1 != t.actor)
    case a => log.warning(s"received unexpected message: $a")
  }

  def addTask(msg : AddTask) = {
    val api = msg.group.head
    context.child(api) match {
      case Some(child) => child ! msg
      case None => {
        msg.task.singleInstance match {
          case false => createChild(classOf[GroupActor], msg.group.take(1), api) ! msg
          case true => createChild(classOf[SingleInstanceActor], msg.group.take(1), api) ! msg
        }
      }
    }
  }

  def createChild(classToCreate : Class[_], group : List[String], name : String) : ActorRef = {
    val ref : ActorRef = context.actorOf(Props(classToCreate, group), name)
    childrenTuple = (ref, classToCreate) :: childrenTuple
    context.watch(ref)
    ref
  }

  def getTask(msg : GetTask) = {
    if(context.children.isEmpty){
      log.debug("No children present")
      sender() ! NoMoreTasks
    }
    log.debug(s"received getTask - forwarding (have children: ${context.children.size})")
    context.actorOf(Props(classOf[TaskAggregator], childrenTuple.sortBy(a => a._2 == classOf[SingleInstanceActor]).map(a => a._1), 1.second), s"FROM_DISTRIBUTOR-${new java.util.Random().nextLong()}") forward msg
    //context.children.foreach(u => u forward msg)
  }
}
