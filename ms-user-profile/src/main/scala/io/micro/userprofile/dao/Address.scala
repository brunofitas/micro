package io.micro.userprofile.dao

import java.util.UUID
import io.micro.lib.con.DBComponent


final case class Address
(
  id: Option[UUID],
  line1: String,
  line2: Option[String],
  postCode: String,
  city: String,
  country: String
)


private[userprofile] trait AddressTable {
  this: DBComponent =>

  import profile.api._

  protected class AddressTable(tag: Tag) extends Table[Address](tag, "address"){
    val id        = column[UUID]("id", O.PrimaryKey)
    val line1     = column[String]("line1")
    val line2     = column[Option[String]]("line2")
    val postCode  = column[String]("post_code")
    val city      = column[String]("city")
    val country   = column[String]("country")
    def * = (id.?, line1, line2, postCode, city, country) <> (Address.tupled, Address.unapply)
  }

  protected def addresses = TableQuery[AddressTable]

  protected implicit class AddressExtensions(query:Query[AddressTable, Address, Seq]){
    def getById(id:Rep[Option[UUID]])     = query.filter(_.id === id)
    def insert(a:Address)                 = addresses += a
  }

}
