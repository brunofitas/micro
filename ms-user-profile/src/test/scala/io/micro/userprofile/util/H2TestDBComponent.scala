package io.micro.userprofile.util

import java.util.UUID

import io.micro.lib.con.DBComponent


trait H2TestDBComponent extends DBComponent{

  val profile = slick.jdbc.H2Profile

  import profile.api._

  val dbName = "jdbc:h2:mem:test" + UUID.randomUUID().toString() + ";"

  val db: Database = {
    Database.forURL(
      url = dbName +
        "MODE=MySql;DATABASE_TO_UPPER=false;" +
        "INIT=runscript from 'ms-user-profile/src/test/resources/V1__schema.sql'" +
        "\\;runscript from 'ms-user-profile/src/test/resources/V2__data.sql'",
      driver = "org.h2.Driver")
  }


}
