package com.urdnot.api

import akka.Done
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Flow, Source}
import akka.util.ByteString
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.nio.file.{Files, Path}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import scala.util.Success

class StreamsSuite01 extends AnyWordSpec with Matchers with ApiHelloRoutes {
  private val file: Path = Files.createTempFile("test", ".tmp")
  val appendFlowTest: Flow[ByteString, ByteString, _] = appendFlow
  val appendFlowTestData: ByteString = ByteString("this is a test string")
  val appendFlowTestRun: Future[IOResult] = Source.single(appendFlowTestData).runWith(FileIO.toPath(f = file))

  "The appendFlow should" {
    "return an appended string" in {
      Await.result(appendFlowTestRun, 3.seconds) shouldBe IOResult(21,Success(Done))
    }
    1
  }
}
