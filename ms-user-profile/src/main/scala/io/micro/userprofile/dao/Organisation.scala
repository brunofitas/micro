package io.micro.userprofile.dao

import java.util.UUID

import io.circe.{Decoder, Encoder}
import io.micro.lib.con.DBComponent
import io.micro.userprofile.dao.OrgType.OrgType
import io.swagger.annotations.ApiModel


final case class Organisation
(
  id: Option[UUID],
  name: String,
  email: String,
  `type`: OrgType
)


private[userprofile] trait OrganisationTable extends AddressTable {
  this: DBComponent =>

  import profile.api._

  implicit def orgType =
    MappedColumnType.base[OrgType, String]({ e => e.toString }, { s => OrgType.withName(s)})

  protected class OrganisationTable(tag: Tag) extends Table[Organisation](tag, "organisation"){
    val id          = column[UUID]("id", O.PrimaryKey)
    val name        = column[String]("name")
    val email       = column[String]("email")
    val _type       = column[OrgType]("type")
    def addressK    = foreignKey("organisation_address_fk", id, addresses)(_.id, onDelete=ForeignKeyAction.Cascade)
    def * = (id.?, name, email, _type).shaped <> (Organisation.tupled, Organisation.unapply)
  }

  protected implicit class OrganisationExtensions(query:Query[OrganisationTable, Organisation, Seq]){
    def getById(id:Option[UUID])  = query.filter(_.id === id)
    def withAddress               = query.join(addresses).on(_.id === _.id)
    def address                   = query.withAddress.map(_._2)
  }

  def organisations = TableQuery[OrganisationTable]

}



@ApiModel(description = "")
case object OrgType extends Enumeration{
  type OrgType = Value
  val CHAMBERS: Value = Value("chambers")
}

trait OrgTypeCodecs{
  implicit val orgTypeDecoder: Decoder[OrgType.Value] = Decoder.enumDecoder(OrgType)
  implicit val orgTypeEncoder: Encoder[OrgType.Value] = Encoder.enumEncoder(OrgType)
}


