package com.harrys.demo

import com.typesafe.config.Config

final class DemoSourceConfiguration(config: Config) {

  /**
   * Expects a configuration for an ingestion source that looks similar to this:
   * {
   * "firstValue": 10,
   * "lastValue": 20,
   * "chunkSize": 2,
   * "jdbcUrl": "DSN to connect to your database"
   * }
   */

  // Value range to be created within tasks via a job
  val firstValue = config.getInt("firstValue")
  val lastValue = config.getInt("lastValue")

  // Limits number of values that can be grabbed at once.
  val chunkSize = config.getInt("chunkSize")

  // DSN for persistence phase
  val dsn = config.getString("jdbcUrl")

}
