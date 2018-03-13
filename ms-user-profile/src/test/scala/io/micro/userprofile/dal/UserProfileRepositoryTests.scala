package io.micro.userprofile.dal

import java.util.UUID

import io.micro.lib.sys.{Error, Success}
import io.micro.userprofile.dao._
import io.micro.userprofile.{dto => DTO}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import io.micro.userprofile.util.{Examples, H2TestDBComponent}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}


class UserProfileRepositoryTests extends WordSpecLike with ScalaFutures with BeforeAndAfterAll with Matchers
  with UserProfileRepository with H2TestDBComponent {

  implicit val patience:PatienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))



  "findAll" must {
    "list all profiles" in {
      whenReady(findAll){
        case Left(_)  => fail()
        case Right(s) => assert(s.asInstanceOf[List[UserProfile]].lengthCompare(2) == 0)
      }
    }
  }

  "findAllWithMembers" must {
    "list all profiles with organisation and address" in {
      whenReady(findAllWithMembers){
        case Left(_)  => fail()
        case Right(s) =>
          assert(s.head.address.line1 == "addline3.1")
      }
    }
  }

  "findById" must {
    "return an existing user-profile" in {
      whenReady(findById(Examples.existingProfile.id)) {
        case Left(_)  => fail()
        case Right(r) => assert(r.firstName == "fname1")
      }
    }
    "return an error when user-profile does not exists" in {
      whenReady(findById(Some(UUID.randomUUID()))) {
        case Left(f)  => assert(f == Error(404, msg = Some("User profile not found")))
        case Right(_) => fail()
      }
    }
  }

  "findByIdWithMembers" must {
    "return an existing user-profile with address and org" in {
      whenReady(findByIdWithMembers(Examples.existingProfile.id.get)) {
        case Left(_)  => fail()
        case Right(r) =>
          assert(r.address.line1 == "addline3.1")
          assert(r.organisation.address.get.line1 == "addline1.1")
      }
    }
    "return an error when user-profile does not exists" in {
      whenReady(findByIdWithMembers(Examples.randomID)) {
        case Left(f)  => assert(f == Error(404, msg = Some("User profile not found")))
        case Right(_) => fail()
      }
    }
  }


  "create" must {
    "return a new profile with a generated UUID for id and address" in {
      whenReady(insert(Examples.userProfileDTO)) {
        case Left(_)  =>fail()
        case Right(r) => assert(r.firstName == Examples.userProfileDTO.firstName)
      }
    }

    "return an error when Id exists" in {
      whenReady(insert(Examples.userProfileDTO.copy(id = Examples.existingProfile.id))) {
        case Left(e)  => assert(e == Error(409))
        case Right(_) => fail()
      }
    }
    "return an error when organisation does not exists" in {
      whenReady(insert(Examples.userProfileDTO.copy(organisation = DTO.Organisation(id = Examples.randomID, None, None, None, None)))) {
        case Left(e)  => assert(e == Error(404, msg = Some("Organisation not found")))
        case Right(_) => fail()
      }
    }


  }


  "update" must {

    "return an updated profile with a valid id and org" in {
      whenReady(update(Examples.userProfileDTO.copy(id = Examples.existingProfile.id, firstName = "Updated"))) {
        case Left(_)  => fail()
        case Right(r) => assert(r.firstName == "Updated")
      }
    }
    "return an error when user-profile does not exists" in {
      whenReady(update(Examples.userProfileDTO.copy(id = Some(Examples.randomID), firstName = "Updated")))  {
        case Left(e)  => assert(e == Error(404, msg = Some("User profile not found")))
        case Right(_) => fail()
      }
    }
    "return an error when organisation does not exists" in {
      val invalidOrg = DTO.Organisation(id = Examples.randomID, None, None, None, None)
      whenReady(update(Examples.userProfileDTO.copy(id = Examples.existingProfile.id, organisation = invalidOrg))) {
        case Left(e)  => assert(e == Error(409, msg = Some("Invalid organisation")))
        case Right(_) => fail()
      }
    }

  }


  "delete" must {

    "delete an existing profile" in {
      whenReady(delete(Examples.existingProfile.id)){
        case Left(_)  => fail()
        case Right(r) => assert(r == Success(200, msg = Some("User profile was removed")))
      }
    }

    "return an error when user-profile does not exists" in {
      whenReady(delete(Some(UUID.randomUUID()))){
        case Left(e)  => assert(e == Error(404, msg = Some("User profile not found")))
        case Right(_) => fail()
      }
    }


  }


}
