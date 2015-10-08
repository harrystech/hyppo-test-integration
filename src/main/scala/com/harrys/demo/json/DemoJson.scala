package com.harrys.demo.json

import scala.beans.BeanProperty

final case class DemoJson
(
  @BeanProperty jobUUID: String,
  @BeanProperty taskNumber: Int,
  @BeanProperty firstValue: Int,
  @BeanProperty lastValue: Int,
  @BeanProperty chunkSize: Int,
  @BeanProperty jobValue: Int,
  @BeanProperty value: Int
  ) {

}
