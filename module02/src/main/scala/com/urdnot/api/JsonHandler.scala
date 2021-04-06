package com.urdnot.api

import akka.actor.{Actor, Props}

object JsonHandler {
  case class routeMessage(route: String, entity: String)
  def props: Props = Props(new JsonHandler)
}

class JsonHandler extends Actor {
    def receive = {
      case _       => sender() ! "huh?"
    }
}