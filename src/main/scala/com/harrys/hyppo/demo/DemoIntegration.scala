package com.harrys.hyppo.demo

import com.harrys.hyppo.demo.avro.DemoAvroRecord
import com.harrys.hyppo.source.api.data.AvroRecordType
import com.harrys.hyppo.source.api.model.{DataIngestionJob, DataIngestionTask, IngestionSource}
import com.harrys.hyppo.source.api.task._
import com.harrys.hyppo.source.api.{RawDataIntegration, ValidationResult}

class DemoIntegration extends RawDataIntegration[DemoAvroRecord] {

  // Implement DataIntegration interface

  override val avroType = AvroRecordType.forClass(classOf[DemoAvroRecord])

  override def validateSourceConfiguration(source: IngestionSource): ValidationResult = DemoValidations.validateSourceConfiguration(source)

  override def validateJobParameters(job: DataIngestionJob): ValidationResult = DemoValidations.validateJobParameters(job)

  override def validateTaskArguments(task: DataIngestionTask): ValidationResult = DemoValidations.validateTaskArguments(task)

  override def newIngestionTaskCreator(): IngestionTaskCreator = new DemoTaskCreator()

  override def newProcessedDataPersister(): ProcessedDataPersister[DemoAvroRecord] = new DemoPersister()

  // RawDataIntegration interface

  override def newRawDataFetcher(): RawDataFetcher = new DemoDataFetcher()

  override def newRawDataProcessor(): RawDataProcessor[DemoAvroRecord] = new DemoDataProcessor()


}
