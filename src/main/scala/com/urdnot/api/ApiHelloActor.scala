package com.urdnot.api

import akka.actor.{Actor, ActorLogging, Props}
import com.urdnot.api.ApiHelloActor.{HelloMessage, HelloReplyMessage}

// add all your case classes/objects here, anything that will be coming
// in from the listener

object ApiHelloActor {
  case class HelloMessage(userName: String, message: String)
  case class HelloReplyMessage(message: String)
  def props(): Props.type = Props
}

class ApiHelloActor extends Actor with ActorLogging  {
  // build a case for each inbound message type

  def receive: Receive = {
    case HelloMessage(userName, message) => sender() ! HelloReplyMessage(s"Hello there, $userName!")
    case _       => log.info("Invalid message")
  }
}
