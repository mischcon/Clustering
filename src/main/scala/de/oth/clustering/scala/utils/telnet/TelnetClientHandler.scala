package de.oth.clustering.scala.utils.telnet

import java.util.logging.Logger

import org.jboss.netty.channel._

/**
 * Handles a client-side channel.
 */
class TelnetClientHandler extends SimpleChannelUpstreamHandler {

  private val logger = Logger.getLogger(getClass.getName)

  override def handleUpstream(ctx: ChannelHandlerContext, e: ChannelEvent) {
    e match {
      case c: ChannelStateEvent ⇒ logger.info(e.toString)
      case _                    ⇒ None
    }
    super.handleUpstream(ctx, e)
  }

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
    System.err.println(e.getMessage)
  }

  override def exceptionCaught(context: ChannelHandlerContext, e: ExceptionEvent) {
    // Close the connection when an exception is raised.
    logger.warning("Unexpected exception from downstream." + e.getCause)
    e.getChannel.close()
  }
}
