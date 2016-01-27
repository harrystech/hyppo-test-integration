package com.harrys.hyppo.demo

import com.harrys.hyppo.demo.avro.DemoAvroRecord
import com.harrys.hyppo.source.api.PersistingSemantics
import com.harrys.hyppo.source.api.task.{PersistProcessedData, ProcessedDataPersister}
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions

final class DemoPersister() extends ProcessedDataPersister[DemoAvroRecord] {

  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  /**
    * @return Indicates that this integration supports idempotent persistence and is therefore safe to retry if task
    *         execution fails
    */
  override def semantics = PersistingSemantics.Idempotent

  override def persistProcessedData(persist: PersistProcessedData[DemoAvroRecord]): Unit = {

    val iterator = JavaConversions.asScalaIterator(persist.openReader())

    iterator.foreach(persistSingleRecord)
  }

  private def persistSingleRecord(record: DemoAvroRecord): Unit = {
    val jobUUID = record.get("jobUUID")
    val taskNumber = record.get("taskNumber")
    val value = record.get("value")

    // Normally, you'd store the row now in a database using the DSN from the configuration.
    log.info(s"Persisting in log: jobUUID = $jobUUID, taskNumber = $taskNumber, value = $value")
  }

}
