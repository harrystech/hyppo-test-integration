package com.harrys.hyppo.demo

import java.util.{Date, UUID}

import com.harrys.hyppo.source.api.model.{DataIngestionJob, DataIngestionTask, IngestionSource}
import com.harrys.hyppo.source.api.task.CreateIngestionTasks
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.TimeLimitedTests
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{Matchers, WordSpecLike}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

class DemoTaskCreatorTest extends WordSpecLike with Matchers with TimeLimitedTests {

  override val timeLimit = Span(20, Seconds)

  val name = "com.harrys.hyppo.demo.DemoIntegration"
  val firstValue = 1
  val lastValue = 3
  val defaultSourceConfig = ConfigFactory.parseString(
    s"""{
      "firstValue": $firstValue,
      "lastValue": $lastValue,
      "chunkSize": 1,
      "jdbcUrl": "jdbc:postgresql://host:port/database"}
    """.stripMargin)
  val ingestionSource = new IngestionSource(name, defaultSourceConfig)

  val defaultJobParameters = ConfigFactory.empty
  val id = UUID.randomUUID()
  val startedAt = new Date()
  val ingestionJob = new DataIngestionJob(ingestionSource, id, defaultJobParameters, startedAt)

  "The task creator" must {

    "map value range to list of tasks" in {
      val createTasks = new CreateIngestionTasks(ingestionJob)
      val taskCreator = new DemoTaskCreator()

      taskCreator.createIngestionTasks(createTasks)

      val builder = createTasks.getTaskBuilder
      val tasks = builder.build()

      // Make sure we use a chunkSize of 1 for this test
      val chunkSize = new DemoSourceConfiguration(ingestionSource.getConfiguration).chunkSize
      assert(chunkSize == 1)

      // Now for the actual test, note that it's inclusive wrt last value
      val parameters = new DemoJobParameters(ingestionJob.getParameters)
      tasks.length shouldBe (lastValue - firstValue + 1)
    }

    "map single value to single task" in {
      val jobParameters = ConfigFactory.parseString("value: 99")
      val ingestionJob = new DataIngestionJob(ingestionSource, id, jobParameters, startedAt)

      val createTasks = new CreateIngestionTasks(ingestionJob)
      val taskCreator = new DemoTaskCreator()

      taskCreator.createIngestionTasks(createTasks)

      val builder = createTasks.getTaskBuilder
      val tasks = builder.build()

      val parameters = new DemoJobParameters(ingestionJob.getParameters)
      tasks.length shouldBe 1

      val taskValues = new DemoTaskArguments(tasks.get(0).getTaskArguments)
      taskValues.valueList shouldEqual List(99)
    }
  }

  "The task creator for a chunky source" must {

    def buildTasksFor(firstValue: Int, lastValue: Int, chunkSize: Int): java.util.List[DataIngestionTask] = {

      val sourceConfig = ConfigFactory.parseString(
        s"""{
          "firstValue": $firstValue,
          "lastValue": $lastValue,
          "chunkSize": $chunkSize,
          "jdbcUrl": "jdbc:postgresql://host:port/database"}
        """.stripMargin)
      val ingestionSource = new IngestionSource(name, sourceConfig)

      val ingestionJob = new DataIngestionJob(ingestionSource, id, defaultJobParameters, startedAt)
      val createTasks = new CreateIngestionTasks(ingestionJob)
      val taskCreator = new DemoTaskCreator()

      taskCreator.createIngestionTasks(createTasks)

      val builder = createTasks.getTaskBuilder
      builder.build()
    }

    "map value ranges within tasks" in {

      val expected = 2 to 20
      val tasks = buildTasksFor(expected.start, expected.end, 3)

      val actual = tasks.foldRight(ListBuffer.empty[Integer]) {
        (task, acc) =>
          val args = new DemoTaskArguments(task.getTaskArguments)
          val values = args.valueList
          acc.++=:(values)
      }

      actual shouldBe expected.toList
    }
  }

}
