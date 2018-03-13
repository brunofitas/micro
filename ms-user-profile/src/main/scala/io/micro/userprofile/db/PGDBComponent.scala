package io.micro.userprofile.db

import com.typesafe.config.{Config, ConfigFactory}
import io.micro.lib.con.DBComponent


private[userprofile] trait PGDBComponent extends DBComponent{

  val config:Config = ConfigFactory.load()

  val profile = slick.jdbc.PostgresProfile

  val db = profile.api.Database.forConfig("database")

}
