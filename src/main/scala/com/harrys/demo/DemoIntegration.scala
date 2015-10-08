package com.harrys.demo

import com.harrys.demo.avro.DemoAvroRecord
import io.ingestion.source.api.data.AvroRecordType
import io.ingestion.source.api.model.{DataIngestionJob, DataIngestionTask, IngestionSource}
import io.ingestion.source.api.task._
import io.ingestion.source.api.{RawDataIntegration, ValidationResult}

class DemoIntegration extends RawDataIntegration[DemoAvroRecord] {

  // Implement DataIntegration interface

  override val avroType = AvroRecordType.forClass(classOf[DemoAvroRecord])

  override def validateSourceConfiguration(source: IngestionSource): ValidationResult = DemoValidations.validateSourceConfiguration(source)

  override def validateJobParameters(job: DataIngestionJob): ValidationResult = DemoValidations.validateJobParameters(job)

  override def validateTaskArguments(task: DataIngestionTask): ValidationResult = DemoValidations.validateTaskArguments(task)

  override def newIngestionTaskCreator(): TaskCreator = new DemoTaskCreator()

  override def newDataPersister(): ProcessedDataPersister[DemoAvroRecord] = new DemoPersister()

  // RawDataIntegration interface

  override def newRawDataFetcher(): RawDataFetcher = new DemoDataFetcher()

  override def newRawDataProcessor(): RawDataProcessor[DemoAvroRecord] = new DemoDataProcessor()


}
