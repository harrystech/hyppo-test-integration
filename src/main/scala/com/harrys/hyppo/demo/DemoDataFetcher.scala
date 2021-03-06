package com.harrys.hyppo.demo

import java.io.ByteArrayInputStream

import com.harrys.hyppo.source.api.task.{FetchRawData, RawDataFetcher}
import com.typesafe.scalalogging.Logger
import org.apache.commons.io.Charsets
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.slf4j.LoggerFactory

final class DemoDataFetcher extends RawDataFetcher {

  val charset = Charsets.UTF_8

  override def fetchRawData(fetcher: FetchRawData): Unit = {

    val sourceConfiguration = new DemoSourceConfiguration(fetcher.getSource.getConfiguration)
    val jobUUID = fetcher.getJob.getId.toString
    val jobParameters = new DemoJobParameters(fetcher.getJob.getParameters)
    val taskNumber = fetcher.getTask.getTaskNumber
    val taskArguments = new DemoTaskArguments(fetcher.getTask.getTaskArguments)

    // Pretend to be fetching from an API by creating JSON
    taskArguments.valueList.foreach {
      singleValue =>
        val row =
          ("jobUUID" -> jobUUID) ~
            ("taskNumber" -> taskNumber) ~
            ("firstValue" -> sourceConfiguration.firstValue) ~
            ("lastValue" -> sourceConfiguration.lastValue) ~
            ("chunkSize" -> sourceConfiguration.chunkSize) ~
            ("jobValue" -> jobParameters.value.getOrElse(-1)) ~
            ("value" -> singleValue.intValue)

        val json = pretty(render(row))
        val stream = new ByteArrayInputStream(json.getBytes(charset))
        fetcher.addData(stream)
    }
  }

}
