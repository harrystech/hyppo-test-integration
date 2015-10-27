package com.harrys.demo

import java.util.{Date, UUID}

import com.typesafe.config.ConfigFactory
import com.harrys.hyppo.source.api.model.{DataIngestionJob, DataIngestionTask, IngestionSource}
import org.scalatest.concurrent.TimeLimitedTests
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{Matchers, WordSpecLike}

class DemoValidationsTest extends WordSpecLike with Matchers with TimeLimitedTests {

  override val timeLimit = Span(20, Seconds)

  val name = "com.harrys.demo.DemoIntegration"
  val defaultSourceConfig = ConfigFactory.parseString(
    """{
      "firstValue": 1,
      "lastValue": 2,
      "chunkSize": 1,
      "jdbcUrl": "jdbc:postgresql://host:port/database"
    }""".stripMargin)
  val defaultJobParameters = ConfigFactory.empty
  val defaultTaskArguments = ConfigFactory.parseString( """{"valueList": [1, 2]}""")

  "The validation of a source configuration" must {

    "approve a good configuration" in {
      val ingestionSource = new IngestionSource(name, defaultSourceConfig)
      val errors = DemoValidations.validateSourceConfiguration(ingestionSource)
      errors.isValid shouldBe true
    }

    "reject gracefully an empty configuration" in {
      val sourceConfig = ConfigFactory.empty
      val ingestionSource = new IngestionSource(name, sourceConfig)
      val errors = DemoValidations.validateSourceConfiguration(ingestionSource)
      errors.hasErrors shouldBe true
    }

    "reject a first value that is too small" in {
      val sourceConfig = ConfigFactory.parseString(
        """{
          "firstValue": 0,
          "lastValue": 2,
          "chunkSize": 1,
          "jdbcUrl": "jdbc:postgresql://host:port/database"
        }""".stripMargin)
      val ingestionSource = new IngestionSource(name, sourceConfig)
      val errors = DemoValidations.validateSourceConfiguration(ingestionSource)
      errors.hasErrors shouldBe true
    }

    "reject a bad order of first and last value" in {
      val sourceConfig = ConfigFactory.parseString(
        """{
          "firstValue": 20,
          "lastValue": 10,
          "chunkSize": 1,
          "jdbcUrl": "jdbc:postgresql://host:port/database"
        }""".stripMargin)
      val ingestionSource = new IngestionSource(name, sourceConfig)
      val errors = DemoValidations.validateSourceConfiguration(ingestionSource)
      errors.hasErrors shouldBe true
    }

    "reject a chunk size which is not a number" in {
      val sourceConfig = ConfigFactory.parseString( """{"chunkSize": "one", "jdbcUrl": "jdbc:postgresql://host:port/database"}""")
      val ingestionSource = new IngestionSource(name, sourceConfig)
      val errors = DemoValidations.validateSourceConfiguration(ingestionSource)
      errors.hasErrors shouldBe true
    }

    "reject a bad chunk size" in {
      val sourceConfig = ConfigFactory.parseString( """{"chunkSize": 0, "jdbcUrl": "jdbc:postgresql://host:port/database"}""")
      val ingestionSource = new IngestionSource(name, sourceConfig)
      val errors = DemoValidations.validateSourceConfiguration(ingestionSource)
      errors.hasErrors shouldBe true
    }

    "reject an empty jdbcUrl" in {
      val sourceConfig = ConfigFactory.parseString( """{"chunkSize": 1, "jdbcUrl": ""}""")
      val ingestionSource = new IngestionSource(name, sourceConfig)
      val errors = DemoValidations.validateSourceConfiguration(ingestionSource)
      errors.hasErrors shouldBe true
    }
  }

  "The validation of a job configuration" must {

    val ingestionSource = new IngestionSource(name, defaultSourceConfig)
    val id = UUID.randomUUID()
    val startedAt = new Date()

    "approve a good parameter set" in {
      val job = new DataIngestionJob(ingestionSource, id, defaultJobParameters, startedAt)
      val errors = DemoValidations.validateJobParameters(job)
      errors.isValid shouldBe true
    }

    "allow overriding the source configuration value range" in {
      val jobParameters = ConfigFactory.parseString("""{"value": 99}""")
      val job = new DataIngestionJob(ingestionSource, id, jobParameters, startedAt)
      val errors = DemoValidations.validateJobParameters(job)
      errors.isValid shouldBe true
    }

  }

  "The validation of a task configuration" must {

    val ingestionSource = new IngestionSource(name, defaultSourceConfig)
    val id = UUID.randomUUID()
    val startedAt = new Date()
    val ingestionJob = new DataIngestionJob(ingestionSource, id, defaultJobParameters, startedAt)
    val taskNumber = 1

    "approve a good argument set" in {
      val task = new DataIngestionTask(ingestionJob, taskNumber, defaultTaskArguments)
      val errors = DemoValidations.validateTaskArguments(task)
      errors.isValid shouldBe true
    }

    "reject gracefully an empty configuration" in {
      val taskArguments = ConfigFactory.empty()
      val task = new DataIngestionTask(ingestionJob, taskNumber, taskArguments)
      val errors = DemoValidations.validateTaskArguments(task)
      errors.hasErrors shouldBe true
    }

    "reject an empty value list" in {
      val taskArguments = ConfigFactory.parseString( """{"valueList": []}""")
      val task = new DataIngestionTask(ingestionJob, taskNumber, taskArguments)

      val errors = DemoValidations.validateTaskArguments(task)
      errors.hasErrors shouldBe true
    }
  }

}
