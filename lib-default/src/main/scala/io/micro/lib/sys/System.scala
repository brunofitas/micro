package io.micro.lib.sys


case class Success(id:Int, `type`:Option[String] = Some("success"), msg:Option[String] = None)
case class Error(id:Int, `type`:Option[String] = Some("error"), msg:Option[String] = None) extends Throwable


