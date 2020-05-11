package com.urdnot.api

import akka.actor.{Actor, ActorLogging, Props}
import com.urdnot.api.ApiHelloActor.HelloRequest

// add all your case classes/objects here, anything that will be coming
// in from the listener

object ApiHelloActor {
  case class HelloRequest(userName: String, message: String)
  def props(): Props.type = Props
}

class ApiHelloActor extends Actor with ActorLogging with ApiHelloDataObjects {
  // build a case for each inbound message type

  def receive: Receive = {
    case HelloRequest(userName, _) => sender() ! HelloRequest(s"$userName", "Hello there!")
    case _       => log.info("Invalid message")
  }
}
