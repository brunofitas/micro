package io.micro.lib.sys


case class Success(id:Int, `type`:String = "success", message:Option[String] = None)
case class Error(id:Int, `type`:String = "error", message:Option[String] = None) extends Throwable


