package de.oth.clustering.scala.worker.traits

import de.oth.clustering.java.utils.DeployInfoInterface
import de.oth.clustering.java.vm.vagrant.configuration.VagrantEnvironmentConfig

/**
  * Created by mischcon on 11.05.17.
  */
trait VMDeployWorkerTrait {

  /**
    * Response of a {@link de.oth.de.oth.clustering.java.clustering.scala.worker.messages#GetDeployInfo GetDeployInfo} message.
    * Used to pass the deployInfo to the target.
    *
    * See wiki for more details about the workflow.
    * @param deployInfo
    */
  def handlerDeployInfo[T >: DeployInfoInterface](deployInfo : T)

  /**
    * Response of a {@link de.oth.de.oth.clustering.java.clustering.scala.worker.messages#GetDeployInfo GetDeployInfo} message.
    * Used to inform the target about the fact that there are currently no tasks and therefore
    * also no deploy info.
    *
    * See wiki for more details about the workflow.
    */
  def handlerNoDeployInfo()

}
