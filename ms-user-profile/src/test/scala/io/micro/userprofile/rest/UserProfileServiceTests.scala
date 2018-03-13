package io.micro.userprofile.rest

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import io.micro.userprofile.dto.UserProfile
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpecLike}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.micro.userprofile.util.{Examples, H2UserProfileRepository}
import scala.concurrent.duration._
import io.micro.userprofile.{dto => DTO}


class UserProfileServiceTests extends UserProfileService(H2UserProfileRepository) with WordSpecLike with Matchers
  with ScalaFutures with ScalatestRouteTest{

  implicit val timeout : Timeout =
    Timeout(3 seconds)


  "GET /profiles" must {
    "return a list with 2 profiles" in {
      val request = Get(uri="/profiles")
      request ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[List[UserProfile]].lengthCompare(2) shouldBe 0
      }
    }
  }

  "POST /profiles" must {
    "create a new profile" in {
      val request = Post(uri="/profiles", Examples.userProfileDTO )
      request ~> routes ~> check {
        status shouldBe StatusCodes.Created
      }
    }
    "return an error when profile exists" in {
      val request = Post(uri="/profiles", Examples.userProfileDTO.copy(id = Examples.existingProfile.id) )
      request ~> routes ~> check {
        status shouldBe StatusCodes.Conflict
      }
    }
    "return an error when organisation doesn't exist" in {
      val invalidOrg = DTO.Organisation(id = Examples.randomID, None, None, None, None)
      val request = Post(uri="/profiles", Examples.userProfileDTO.copy(organisation =  invalidOrg) )
      request ~> routes ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }
  }

  "GET /profiles/{ID}" must {
    val id = Examples.existingProfile.id.get
    "return an existing profile" in {
      val request = Get(uri="/profiles/" + id)
      request ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[UserProfile].firstName shouldBe "fname1"
      }
    }
    "return 404 when profile doesn't exist" in{
      val request = Get(uri="/profiles/" + Examples.randomID)
      request ~> routes ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }
  }

  "PUT /profiles/{ID}" must {
    val id = Examples.existingProfile.id.get

    "update an existing profile" in {
      val request = Put(uri="/profiles/" + id, Examples.userProfileDTO.copy(id = Some(id), firstName = "UPDATED") )
      request ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[UserProfile].firstName shouldBe "UPDATED"
      }
    }
    "return an error when profile doesn't exist" in {
      val request = Put(uri="/profiles/" + Examples.randomID , Examples.userProfileDTO )
      request ~> routes ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }
    "return an error when organisation doesn't exist" in {
      val invalidOrg = DTO.Organisation(id = Examples.randomID, None, None, None, None)
      val request = Put(uri="/profiles/" + id, Examples.userProfileDTO.copy(id = Some(id), organisation =  invalidOrg) )
      request ~> routes ~> check {
        status shouldBe StatusCodes.Conflict
      }
    }
  }

  "DELETE /profiles/{ID}" must {

    "delete an existing profile" in {
      val request = Delete(uri="/profiles/" + Examples.existingProfile.id.get  )
      request ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
    }
    "return an error when profiles doesn't exist" in {
      val request = Delete(uri="/profiles/" + Examples.randomID  )
      request ~> routes ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }
  }
}
