package com.harrys.hyppo.demo

import com.typesafe.config.Config

final class DemoJobParameters(config: Config) {

  val value: Option[Int] = {
    if (config.hasPath("value")) {
      Some(config.getInt("value"))
    } else {
      None
    }
  }
}
