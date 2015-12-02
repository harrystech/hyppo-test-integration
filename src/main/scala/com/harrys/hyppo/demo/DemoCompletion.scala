package com.harrys.hyppo.demo

import com.harrys.hyppo.source.api.task.HandleJobCompleted
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

object DemoCompletion {

  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  def onJobCompleted(details: HandleJobCompleted): Unit = {

    val sourceConfiguration = new DemoSourceConfiguration(details.getSource.getConfiguration)
    val jobUUID = details.getJob.getId.toString

    log.info(s"Job ${jobUUID} completed with its counting")
  }

}
