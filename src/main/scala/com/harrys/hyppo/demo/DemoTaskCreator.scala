package com.harrys.hyppo.demo

import java.util

import com.harrys.hyppo.source.api.task.{CreateTasksForJob, TaskCreator}

import scala.collection.JavaConversions._
import scala.math.min

final class DemoTaskCreator extends TaskCreator {

  override def createTasks(creator: CreateTasksForJob): Unit = {

    val sourceConfiguration = new DemoSourceConfiguration(creator.getSource.getConfiguration)
    val jobParameters = new DemoJobParameters(creator.getJob.getParameters)

    // Override the source configuration of firstValue and lastValue by job value if that is specified
    val jobValue = jobParameters.value
    val firstValue = jobValue.getOrElse(sourceConfiguration.firstValue)
    val lastValue = jobValue.getOrElse(sourceConfiguration.lastValue)

    val chunkSize = sourceConfiguration.chunkSize
    val jobRange = firstValue to lastValue by chunkSize

    jobRange.foreach(startValue => {
      val taskRange = startValue until min(startValue + chunkSize, lastValue + 1)
      val taskList = new util.ArrayList[Int]()
      taskList.addAll(taskRange)
      creator.createTaskWithArgs(Map[String, AnyRef]("valueList" -> taskList))
    })
  }

}
