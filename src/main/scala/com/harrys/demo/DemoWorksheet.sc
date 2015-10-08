/**
 * Test demo integration
 */
import java.io.File
import java.util
import java.util.{Date, UUID}
import com.harrys.demo.{DemoDataFetcher, DemoTaskCreator}
import com.typesafe.config.ConfigFactory
import io.ingestion.source.api.data.RawDataCollector
import io.ingestion.source.api.model.{DataIngestionTask, DataIngestionJob, IngestionSource}
import io.ingestion.source.api.task.{FetchRawData, CreateTasksForJob}
import scala.collection.JavaConversions._
object DemoWorksheet {
  val name = "com.harrys.demo.DemoIntegration"
  val sourceConfig = ConfigFactory.parseString(
    """{
      "firstValue": 1,
      "lastValue": 20,
      "chunkSize": "3",
      "jdbcUrl": "empty"
    }""".stripMargin)
  val ingestionSource = new IngestionSource(name, sourceConfig)
  val id = UUID.randomUUID()
  val parameters = ConfigFactory.empty()
  val startedAt = new Date()
  val ingestionJob = new DataIngestionJob(ingestionSource, id, parameters, startedAt)
  val createTasks = new CreateTasksForJob(ingestionJob)
  val taskCreator = new DemoTaskCreator()
  taskCreator.createTasks(createTasks)
  val builder = createTasks.getTaskBuilder
  val tasks = builder.build()

  //

  val directory = new File("/tmp/demo_integration_test")
  val accumulator = new RawDataCollector(directory)

  val t = tasks.get(0)
  val task = new DataIngestionTask(ingestionJob, t.getTaskNumber, t.getTaskArguments)
  // tasks.foreach(task => {
  val ddf = new DemoDataFetcher()
  val fetcher = new FetchRawData(task, accumulator)
  val t = fetcher.getTask
  val job = fetcher.getJob

  ddf.fetchRawData(fetcher)
  // })

}