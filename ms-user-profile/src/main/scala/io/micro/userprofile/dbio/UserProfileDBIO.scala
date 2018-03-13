package io.micro.userprofile.dbio

import java.util.UUID

import io.micro.lib.con.DBComponent
import io.micro.lib.sys.Error
import io.micro.userprofile.dao.{Address, Organisation, UserProfile, UserProfileTable}

import scala.concurrent.ExecutionContext.Implicits._


private[userprofile] trait UserProfileDBIO extends UserProfileTable {
  this: DBComponent =>

  protected object UserProfileDBIO {

    import profile.api._

    def findAll: DBIO[List[UserProfile]] =
      profiles.to[List].result

    def findAllWithMembers: DBIO[List[(UserProfile, Address, (Organisation, Address))]] =
      profiles.withMembers.to[List].result


    def getById(id:Option[UUID]): DBIO[UserProfile] =
      profiles.getById(id).result.headOption.flatMap {
        case Some(pro) => DBIO.successful(pro)
        case None      => DBIO.failed(Error(404, msg = Some("Profile not found")))
      }

    def getByIdWithMembers(id:Option[UUID]): DBIO[(UserProfile, Address, (Organisation, Address))] =
      profiles.getById(id).withMembers.result.headOption.flatMap {
        case Some((pro, add ,org))  => DBIO.successful((pro,add,org))
        case None                   => DBIO.failed(Error(404, msg = Some("Profile not found")))
      }

    def getOrg(id:Option[UUID]): DBIO[Organisation] =
      organisations.getById(id).result.headOption.flatMap {
        case Some(org) => DBIO.successful(org)
        case None      => DBIO.failed(Error(404, msg = Some("Organisation not found")))
      }

    def getOrgWithAddress(id:Option[UUID]): DBIO[(Organisation,Address)] =
      organisations.getById(id).withAddress.result.headOption.flatMap {
        case Some((org,add))  => DBIO.successful((org,add))
        case None             => DBIO.failed(Error(404, msg =  Some("Organisation not found")))
      }

    def getAddress(id:Option[UUID]): DBIO[Address] =
      addresses.getById(id).result.headOption.flatMap {
        case Some(add) => DBIO.successful(add)
        case None      => DBIO.failed(Error(404, msg = Some("Address not found")))
      }

    def insert(profile:UserProfile, address:Address): DBIO[(UserProfile,Address,(Organisation,Address))] = {
      val pro = profile.copy(id = Some(profile.id.getOrElse(UUID.randomUUID())))
      for {
        (org, oad)  <- getOrgWithAddress(Some(pro.organisation))
        add         <- addresses += address.copy(id = pro.id) if add == 1
        _           <- profiles += pro
      } yield (pro, address, (org, oad))
    }.transactionally

    def update(profile:UserProfile, address: Address): DBIO[(UserProfile,Address,(Organisation,Address))] = {
      for {
        pro   <- profiles.filter(_.id === profile.id).result.headOption if pro.isDefined
        org   <- getOrgWithAddress(Some(profile.organisation))
        add   <- addresses.getById(profile.id).update(address)
        _     <- profiles.getById(profile.id).update(profile)
      } yield (profile, address.copy(id = profile.id), org)
    }.transactionally

    def delete(id:Option[UUID]): DBIO[Int] = {
      for {
        _   <- addresses.getById(id).delete
        res <- profiles.getById(id).delete
      } yield res
    }.transactionally


  }

}

