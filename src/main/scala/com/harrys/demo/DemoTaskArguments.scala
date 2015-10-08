package com.harrys.demo

import com.typesafe.config.Config

import scala.collection.JavaConversions._

final class DemoTaskArguments(config: Config) {

  /**
   * Expects config for task arguments that looks similar to this:
   * { "valueList": [1, 2] }
   */

  val valueList = asScalaBuffer(config.getIntList("valueList"))

}
