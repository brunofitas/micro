package io.micro.userprofile.rest

import javax.ws.rs.Path

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.micro.lib.con.DBComponent
import io.micro.lib.sys.Success
import io.micro.userprofile.dal.{PGUserProfileRepository, UserProfileRepository}
import io.micro.userprofile.dao.{OrgTypeCodecs, ProfileTypeCodecs, SalutationTypeCodecs}
import io.micro.userprofile.dto.UserProfile
import io.swagger.annotations._


@Api(value = "/profiles", produces = "application/json")
@Path("/profiles")
class UserProfileService(repository: UserProfileRepository with DBComponent) extends Directives
  with OrgTypeCodecs with ProfileTypeCodecs with SalutationTypeCodecs{


  val routes : Route =
    listCollection ~ createEntity ~ collectionOptions ~
    retrieveEntity ~ updateEntity ~ deleteEntity ~ entityOptions



  /**
    * LIST
    */
  @ApiOperation(
    value = "List user profiles",
    nickname = "getProfiles",
    httpMethod = "GET",
    response = classOf[List[UserProfile]])
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def listCollection : Route =
    (get & path("profiles") ) {
      onSuccess(repository.findAllWithMembers) {
        case Left(e) => complete(e.id -> e)
        case Right(r) => complete(200 -> r)
      }
    }


  /**
    * CREATE
    */
  @ApiOperation(
    value = "Create Profile", nickname = "createProfile", httpMethod = "POST", response = classOf[UserProfile], code = 201)
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name = "body",
      value = "Create Profile",
      required = true,
      dataTypeClass = classOf[UserProfile], paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal server error"),
    new ApiResponse(code = 400, message = "Bad Request")
  ))
  def createEntity : Route =
    (post & path("profiles") )  {
      entity(as[UserProfile]) { content =>
        onSuccess(repository.insert(content)){
          case Left(error)  => complete(error.id -> error)
          case Right(res)   => complete(201 -> res)
        }
      }
    }


  /**
    * RETRIEVE
    */
  @Path("{profileId}")
  @ApiOperation(
    value = "Get user profile",
    nickname = "getProfile",
    httpMethod = "GET",
    response = classOf[UserProfile])
  @ApiImplicitParams(Array(new ApiImplicitParam(
    name= "profileId",
    value= "User profile Id",
    required= true,
    dataType= "string",
    format= "uuid",
    paramType= "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal server error"),
    new ApiResponse(code = 404, message = "Profile not found")
  ))
  def retrieveEntity : Route = {
    (get & path("profiles" / JavaUUID) ){ id =>
      onSuccess(repository.findByIdWithMembers(id)){
        case Left(e)  => complete(e.id -> e)
        case Right(r) => complete(200 -> r)
      }
    }
  }


  /**
    * UPDATE
    */
  @Path("{profileId}")
  @ApiOperation(value = "Update user profile", httpMethod = "PUT", response = classOf[UserProfile])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name= "profileId",
      value= "User profile Id",
      required= true,
      dataType= "string",
      format= "uuid",
      paramType= "path"
    ),
    new ApiImplicitParam(
      name = "body",
      value = "Update user profile",
      required = true,
      dataTypeClass = classOf[UserProfile],
      paramType = "body"
    )
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Profile not found"),
    new ApiResponse(code = 400, message = "Bad Request")
  ))
  def updateEntity : Route =
    (put & path("profiles" / JavaUUID) ){ id =>
      entity(as[UserProfile]) { content =>
        onSuccess(repository.update(content.copy(id = Some(id)))){
          case Left(error)  => complete(error.id -> error)
          case Right(res)   => complete(200 -> res)
        }
      }
    }



  /**
    * DELETE
    */
  @Path("{profileId}")
  @ApiOperation(value = "Delete user profile", httpMethod = "DELETE", response = classOf[Success])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name= "profileId",
      value= "User profile Id",
      required= true,
      dataType= "string",
      format= "uuid",
      paramType= "path"
    )
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Profile not found")
  ))
  def deleteEntity : Route =
    (delete & path("profiles" / JavaUUID) ){ id =>
      onSuccess(repository.delete(Some(id))){
        case Left(error)  => complete(error.id -> error)
        case Right(res)   => complete(200 -> res)
      }

    }



  /**
    * OPTIONS
    */
  @ApiOperation(value="Options for user profiles", response = classOf[Void], httpMethod = "OPTIONS", hidden = false)
  @ApiResponses(Array(new ApiResponse(code = 200, message = "OK")))
  def collectionOptions: Route = {
    (options & path("profiles")) {
      complete(HttpResponse(StatusCodes.OK, entity = HttpEntity.empty(ContentTypes.`application/json`)))
    }
  }

  @Path("{profileId}")
  @ApiOperation(value="Options for a user profile", response = classOf[Void], httpMethod = "OPTIONS", hidden = false)
  @ApiImplicitParams(Array(new ApiImplicitParam(
    name= "profileId",
    value= "User profile Id",
    required= true,
    dataType= "string",
    format= "uuid",
    paramType= "path")
  ))
  @ApiResponses(Array(new ApiResponse(code = 200, message = "OK")))
  def entityOptions: Route = {
    (options & path("profiles" / JavaUUID) ){ _ =>
      complete(HttpResponse(StatusCodes.OK, entity = HttpEntity.empty(ContentTypes.`application/json`)))
    }
  }


}


object UserProfileService extends UserProfileService(PGUserProfileRepository)








