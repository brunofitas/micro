package io.micro.userprofile.dto

import java.util.UUID
import io.micro.userprofile.dao.ProfileType.ProfileType
import io.micro.userprofile.dao.SalutationType.SalutationType
import io.micro.userprofile.{dao => DAO}
import io.swagger.annotations.ApiModelProperty

import scala.annotation.meta.field


final case class UserProfile
(
  id:Option[UUID],
  @(ApiModelProperty @field)(dataType = "io.micro.userprofile.dao.SalutationType$")
  salutation:SalutationType,
  firstName:String,
  lastName:String,
  email:String,
  telephone:String,
  @(ApiModelProperty @field)(dataType = "io.micro.userprofile.dao.ProfileType$")
  `type`:ProfileType,
  organisation:Organisation,
  address: Address
)

object UserProfile{

  def apply(p:(DAO.UserProfile, DAO.Address, (DAO.Organisation, DAO.Address))) : UserProfile = {

    UserProfile(

      id= p._1.id,
      firstName = p._1.firstName,
      lastName = p._1.lastName,
      email = p._1.email,
      salutation = p._1.salutation,
      telephone = p._1.telephone,
      `type` = p._1.`type`,

      address = Address(
        line1 = p._2.line1,
        line2 = p._2.line2,
        postCode = p._2.postCode,
        city = p._2.city,
        country = p._2.country
      ),

      organisation = Organisation(
        id = p._3._1.id.getOrElse(p._1.organisation),
        name = Some(p._3._1.name),
        email = Some(p._3._1.email),
        `type` = Some(p._3._1.`type`),

        address = Some( Address(
          line1 = p._3._2.line1,
          line2 = p._3._2.line2,
          postCode = p._3._2.postCode,
          city = p._3._2.city,
          country = p._3._2.country
        ))
      )
    )
  }


  def toDAO(p:UserProfile):(DAO.UserProfile, DAO.Address) = {
    (
      DAO.UserProfile(
        id= p.id,
        firstName = p.firstName,
        lastName = p.lastName,
        email = p.email,
        salutation = p.salutation,
        telephone = p.telephone,
        `type` = p.`type`,
        organisation = p.organisation.id
      ),
      DAO.Address(
        id= p.id,
        line1 = p.address.line1,
        line2 = p.address.line2,
        postCode = p.address.postCode,
        city = p.address.city,
        country = p.address.country
      )
    )
  }
}

