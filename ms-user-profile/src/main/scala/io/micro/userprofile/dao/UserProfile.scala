package io.micro.userprofile.dao

import java.util.UUID
import io.circe.{Decoder, Encoder}
import io.micro.lib.con.DBComponent
import io.micro.userprofile.dao.ProfileType.ProfileType
import io.micro.userprofile.dao.SalutationType.SalutationType
import io.swagger.annotations.ApiModel


final case class UserProfile
(
  id:Option[UUID],
  firstName:String,
  lastName:String,
  email:String,
  salutation:SalutationType,
  telephone:String,
  `type`:ProfileType,
  organisation:UUID
)


private[userprofile] trait UserProfileTable extends OrganisationTable with AddressTable {
  this: DBComponent =>

  import SalutationType._
  import profile.api._

  implicit def salutationTypeMapper: BaseColumnType[ProfileType] =
    MappedColumnType.base[ProfileType, String]({ e => e.toString }, { s => ProfileType.withName(s)})

  implicit def profileTypeMapper: BaseColumnType[SalutationType] =
    MappedColumnType.base[SalutationType, String]({ e => e.toString }, { s => SalutationType.withName(s)})


  protected  class UserProfileTable(tag: Tag) extends Table[UserProfile](tag, "userprofile"){
    val id            = column[UUID]("id", O.PrimaryKey)
    val firstName     = column[String]("first_name")
    val lastName      = column[String]("last_name")
    val email         = column[String]("email")
    val salutation    = column[SalutationType]("salutation")
    val telephone     = column[String]("telephone")
    val _type         = column[ProfileType]("type")
    val orgId         = column[UUID]("organisation_id")
    def address       = foreignKey("profile_address_fk", id, addresses)(_.id, onDelete=ForeignKeyAction.Cascade)
    def organisation  = foreignKey("profile_organisation_fk", orgId, organisations)(_.id)
    def * = (id.?, firstName, lastName, email, salutation, telephone, _type, orgId).shaped <>
      (UserProfile.tupled, UserProfile.unapply)
  }

  protected implicit class UserProfileExtensions(query:Query[UserProfileTable, UserProfile, Seq]){
    def getById(id:Rep[Option[UUID]])   = query.filter(_.id === id)
    def withAddress                     = query.join(addresses).on(_.id === _.id)
    def withOrganisation                = query.join(organisations).on(_.orgId === _.id)
    def withMembers = for {
      ((pro,padd),(org,oadd))  <- query.withAddress.join(organisations.withAddress).on(_._1.orgId === _._1.id )
    } yield (pro,padd,(org,oadd))
  }

  def profiles = TableQuery[UserProfileTable]

}



@ApiModel(description = "")
case object SalutationType extends Enumeration{
  type SalutationType = Value
  val MR:Value  = Value("Mr")
  val MRS:Value = Value("Mrs")
  val MS:Value  = Value("Ms")

}

trait SalutationTypeCodecs{
  implicit val salutationTypeDecoder: Decoder[SalutationType.Value] = Decoder.enumDecoder(SalutationType)
  implicit val salutationTypeEncoder: Encoder[SalutationType.Value] = Encoder.enumEncoder(SalutationType)
}


@ApiModel(description = "")
case object ProfileType extends Enumeration{
  type ProfileType = Value
  val BARRISTER:Value  = Value("barrister")
  val JUDGE:Value  = Value("judge")

}

trait ProfileTypeCodecs{
  implicit val barristerTypeDecoder: Decoder[ProfileType.Value] = Decoder.enumDecoder(ProfileType)
  implicit val barristerTypeEncoder: Encoder[ProfileType.Value] = Encoder.enumEncoder(ProfileType)
}

