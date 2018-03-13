package io.micro.userprofile.dal

import java.util.UUID

import io.micro.lib.con.DBComponent
import io.micro.lib.sys
import io.micro.lib.sys.{Error, Success}
import io.micro.userprofile.dao.UserProfile
import io.micro.userprofile.db.PGDBComponent
import io.micro.userprofile.dbio.UserProfileDBIO
import io.micro.userprofile.{dto => DTO}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future


private[userprofile] trait UserProfileRepository extends UserProfileDBIO {
  this: DBComponent =>

  def findAll: Future[Either[sys.Error, List[UserProfile]]] =
    (for {
      dao <- db.run(UserProfileDBIO.findAll)
    } yield Right(dao))
    .recover {
      case _ => Left(Error(500))
    }


  def findAllWithMembers: Future[Either[Error, List[DTO.UserProfile]]] =
    (for {
      dao <- db.run(UserProfileDBIO.findAllWithMembers)
      dto <- Future(dao.map(d => DTO.UserProfile(d)))
    } yield Right(dto) )
    .recover{
      case _ => Left(Error(500))
    }



  def findById(id:Option[UUID]): Future[Either[Error,UserProfile]] =
    (for {
      dao <- db.run(UserProfileDBIO.getById(id))
    } yield Right(dao))
    .recover {
      case Error(404, _, _ )    => Left(Error(404, "error", Some("User profile not found")))
      case _                    => Left(Error(500))
    }


  def findByIdWithMembers(id:UUID): Future[Either[Error,DTO.UserProfile]]  =
    (for {
      dao <- db.run(UserProfileDBIO.getByIdWithMembers(Some(id)))
      dto <- Future(DTO.UserProfile(dao))
    } yield Right(dto))
    .recover {
      case Error(404, _, _ )    => Left(Error(404, "error", Some("User profile not found")))
      case _                    => Left(Error(500))
    }


  def insert(profile:DTO.UserProfile):Future[Either[Error,DTO.UserProfile]] =
    (for {
      (p,a) <- Future(DTO.UserProfile.toDAO(profile))
      dao   <- db.run(UserProfileDBIO.insert(p, a))
      dto   <- Future(DTO.UserProfile(dao))
    } yield Right(dto))
    .recover {
      case _:NoSuchElementException => Left(Error(404))
      case e:Error                  => Left(e)
      case _                        => Left(Error(409))
    }


  def update(profile:DTO.UserProfile):Future[Either[Error,DTO.UserProfile]] =
    (for {
      (p,a) <- Future(DTO.UserProfile.toDAO(profile))
      dao   <- db.run(UserProfileDBIO.update(p,a))
      dto   <- Future(DTO.UserProfile(dao))
    } yield Right(dto))
    .recover {
      case _:NoSuchElementException                      => Left(Error(404,"error", Some("User profile not found")))
      case Error(404, _, Some("Organisation not found")) => Left(Error(409,"error", Some("Invalid organisation")))
      case e:Error                                       => Left(e)
      case _                                             => Left(Error(500))
    }


  def delete(id:Option[UUID]): Future[Either[Error,Success]] =
    (for {
      del <- db.run(UserProfileDBIO.delete(id))
      res <- { Future{
        if(del == 1)
          Right(Success(200,"success",Some("User profile was removed")))
        else
          Left(Error(404, "error", Some("User profile not found")))
        }
      }
    } yield res)
      .recover {
        case _  => Left(Error(500))
      }

}

object PGUserProfileRepository extends UserProfileRepository with PGDBComponent




