package com.urdnot.api

import akka.actor.{Actor, ActorLogging, Props}
import com.urdnot.api.ApiHelloActor.HelloRequest

object ApiHelloActor {
  case class HelloRequest(userName: String, message: String)

  def props(): Props.type = Props
}
class ApiHelloActor extends Actor with ActorLogging with ApiHelloDataObjects {

  def receive: Receive = {
    case HelloRequest(userName, _) => sender() ! HelloRequest(s"$userName", "Hello there!")
    case _       => log.info("Invalid message")
  }
}
