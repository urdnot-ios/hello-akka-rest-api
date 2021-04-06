package com.urdnot.api

trait ApiHelloDataObjects {
  case class SimpleJson(
                         stringItem: String,
                         intItem: Int,
                         listItem: List[Int])
}
