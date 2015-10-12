package com.harrys.hyppo.demo

import com.typesafe.config.Config

final class DemoJobParameters(config: Config) {

  /**
   * Expects config for job parameters that looks similar to this:
   * { }
   * which will use the first value and last value from the source configuration
   * Or like this:
   * { "value": 1 }
   * which will override the source configuration to use firstValue = lastValue = value
   */

  val value: Option[Int] = {
    if (config.hasPath("value")) {
      Some(config.getInt("value"))
    } else {
      None
    }
  }
}
