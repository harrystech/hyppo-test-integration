package com.harrys.hyppo.demo

import java.nio.file.Files
import java.util.{Date, UUID}

import com.harrys.hyppo.source.api.data.RawDataCollector
import com.harrys.hyppo.source.api.model.{DataIngestionJob, DataIngestionTask, IngestionSource}
import com.harrys.hyppo.source.api.task.{CreateTasksForJob, FetchRawData}
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.TimeLimitedTests
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{Matchers, WordSpecLike}

import scala.collection.JavaConversions._

class DemoDataFetcherTest extends WordSpecLike with Matchers with TimeLimitedTests {

  override val timeLimit = Span(20, Seconds)

  "The data fetcher" must {

    "lead to temp files being created" in {
      val name = "com.harrys.hyppo.demo.DemoIntegration"
      val defaultSourceConfig = ConfigFactory.parseString(
        s"""{
            "firstValue": 1,
            "lastValue": 3,
            "chunkSize": 1,
            "jdbcUrl": "jdbc:postgresql://host:port/database"
        }""".stripMargin)
      val ingestionSource = new IngestionSource(name, defaultSourceConfig)

      val id = UUID.randomUUID()
      val defaultJobParameters = ConfigFactory.empty
      val startedAt = new Date()
      val ingestionJob = new DataIngestionJob(ingestionSource, id, defaultJobParameters, startedAt)

      val createTasks = new CreateTasksForJob(ingestionJob)
      val taskCreator = new DemoTaskCreator()
      taskCreator.createTasks(createTasks)
      val builder = createTasks.getTaskBuilder
      val tasks = builder.build()

      val tmpDir = Files.createTempDirectory("test")
      tmpDir.toFile.deleteOnExit()
      val dataCollector = new RawDataCollector(tmpDir.toFile)

      tasks.foreach(deserializedTask => {
        val task = new DataIngestionTask(ingestionJob, deserializedTask.getTaskNumber, deserializedTask.getTaskArguments)
        val fetcher = new FetchRawData(task, dataCollector)
        val dataFetcher = new DemoDataFetcher()
        dataFetcher.fetchRawData(fetcher)
      })

      dataCollector.hasRawDataFiles shouldBe true
      dataCollector.getRawFiles.length shouldEqual tasks.length

      // val file0 = dataCollector.getRawFiles.get(0)
      // val records = new AvroRecordAppender(DemoAvroRecord, null)
      // val processor = new ProcessRawData(task, file0, records)
    }
  }

}
