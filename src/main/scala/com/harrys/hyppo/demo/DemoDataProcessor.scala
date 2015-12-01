package com.harrys.hyppo.demo

import com.harrys.hyppo.demo.avro.DemoAvroRecord
import com.harrys.hyppo.demo.json.DemoJson
import com.harrys.hyppo.source.api.task.{ProcessRawData, RawDataProcessor}
import com.typesafe.scalalogging.Logger
import org.apache.commons.io.IOUtils
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import org.slf4j.LoggerFactory

final class DemoDataProcessor extends RawDataProcessor[DemoAvroRecord] {

  override def processRawData(process: ProcessRawData[DemoAvroRecord]): Unit = {

    implicit val formats = DefaultFormats

    val stream = process.openInputStream()

    try {
      val json = parse(stream)
      val row = json.extract[DemoJson]
      val record = new DemoAvroRecord(row.jobUUID, row.taskNumber, row.firstValue, row.lastValue, row.chunkSize, row.jobValue, row.value)
      process.append(record)

    } finally {
      IOUtils.closeQuietly(stream)
    }
  }

}
