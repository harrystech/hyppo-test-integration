package com.harrys.demo

import com.typesafe.config.ConfigException
import io.ingestion.source.api.ValidationResult
import io.ingestion.source.api.model.{DataIngestionJob, DataIngestionTask, IngestionSource}

object DemoValidations {

  def validateSourceConfiguration(source: IngestionSource): ValidationResult = {
    val errors = ValidationResult.valid()

    util.Try(new DemoSourceConfiguration(source.getConfiguration)) match {
      // Showing ConfigException$WrongType or ConfigException$Missing may be helpful:
      case util.Failure(cause: ConfigException) => errors.addErrorMessage(s"Bad configuration for source configuration (${cause.getClass.toString}): ${cause.getMessage}")
      case util.Failure(cause: Throwable) => errors.addErrorMessage(s"Failed to load source configuration: ${cause.getMessage}")
      case util.Success(config) =>
        if (config.firstValue < 1) {
          errors.addErrorMessage("firstValue must be > 0")
        }
        if (config.lastValue < config.firstValue) {
          errors.addErrorMessage("lastValue must be at least equal to firstValue")
        }
        if (config.chunkSize < 1) {
          errors.addErrorMessage("Chunk size (number of values per task) must be a positive integer")
        }
        if (config.dsn.length == 0) {
          errors.addErrorMessage("DSN is empty")
        }
    }
    errors
  }

  def validateJobParameters(job: DataIngestionJob): ValidationResult = {
    val errors = ValidationResult.valid()

    util.Try(new DemoJobParameters(job.getParameters)) match {
      case util.Failure(cause: ConfigException) => errors.addErrorMessage(s"Bad configuration for job parameters (${cause.getClass.toString}): ${cause.getMessage}")
      case util.Failure(cause: Throwable) => errors.addErrorMessage(s"Failed to load job parameters: ${cause.getMessage}")
      case util.Success(parameters) =>
        parameters.value.foreach(value => if (value < 1) errors.addErrorMessage("value (if used) must be > 0"))
    }
    errors
  }

  def validateTaskArguments(task: DataIngestionTask): ValidationResult = {
    val errors = ValidationResult.valid()

    util.Try(new DemoTaskArguments(task.getTaskArguments)) match {
      case util.Failure(cause: ConfigException) => errors.addErrorMessage(s"Bad configuration for task arguments (${cause.getClass.toString}): ${cause.getMessage}")
      case util.Failure(cause: Throwable) => errors.addErrorMessage(s"Failed to load task arguments: ${cause.getMessage}")
      case util.Success(arguments) =>
        if (arguments.valueList.isEmpty) {
          errors.addErrorMessage("List of values may not be empty")
        }
    }
    errors
  }

}
