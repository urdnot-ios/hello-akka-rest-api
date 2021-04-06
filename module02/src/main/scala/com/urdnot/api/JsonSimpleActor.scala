package com.urdnot.api

import akka.actor.Actor
import io.circe.parser._
import io.circe.{Json, ParsingFailure}


class JsonSimpleActor extends Actor {
    def receive = {
      case x:Array[Byte] => sender() ! basicJsonParser(x)
      case _       => sender() ! "huh?"
    }
  def basicJsonParser(inData: Array[Byte]): Either[ParsingFailure, Json] = {
    parse(inData.mkString)
  }
  def basicJsonOptionParser(inData: Array[Byte]): Json = {
    parse(inData.mkString).getOrElse(Json.Null)
  }
}