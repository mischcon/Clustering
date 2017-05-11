package worker.traits

import vm.vagrant.configuration.VagrantEnvironmentConfig
import worker.messages.DeployInfo

/**
  * Created by mischcon on 11.05.17.
  */
trait VMDeployWorkerTrait {

  /**
    * Response of a {@link worker.messages#GetDeployInfo GetDeployInfo} message.
    * Used to pass the deployInfo to the target.
    *
    * See wiki for more details about the workflow.
    * @param deployInfo
    */
  def handleDeployInfo(deployInfo : VagrantEnvironmentConfig)

  /**
    * Response of a {@link worker.messages#GetDeployInfo GetDeployInfo} message.
    * Used to inform the target about the fact that there are currently no tasks and therefore
    * also no deploy info.
    *
    * See wiki for more details about the workflow.
    */
  def handleNoDeployInfo()

}
