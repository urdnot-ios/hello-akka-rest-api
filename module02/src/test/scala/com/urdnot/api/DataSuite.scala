package com.urdnot.api

import akka.http.scaladsl.model.StatusCodes

trait DataSuite {
  val validJson: Array[Byte] =
    """{
  "foo": "bar",
  "baz": 123,
  "list of stuff": [ 4, 5, 6 ]}""".getBytes

  val invalidJson: Array[Byte] =
    """NOPE""".getBytes

  val username = "jsewell"
  val message = "hello"
  val simpleHelloReply = """{"hello" : "jsewell"}"""

  val badJsonExtractor = """{"id": "c730433b-082c-4984-9d66-855c243266f0","name": "Foo", "counts": [1, 2, 3], "values": {"bar": true, "baz": 100.001, "qux": ["a", "b"]}"""
  val badJsonReply: StatusCodes.ServerError = StatusCodes.InternalServerError
  val validJsonExtractor = """{"id": "c730433b-082c-4984-9d66-855c243266f0","name": "Foo", "counts": [1, 2, 3], "values": {"bar": true, "baz": 100.001, "qux": ["a", "b"]}}"""
  val validJsonExtractorResult = 100.001
}
