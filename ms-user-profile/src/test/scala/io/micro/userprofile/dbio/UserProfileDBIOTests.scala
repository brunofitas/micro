package io.micro.userprofile.dbio

import java.util.UUID
import io.micro.userprofile.dao.{Address, Organisation, UserProfile}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.concurrent.Await
import scala.concurrent.duration._
import io.micro.lib.sys.Error
import io.micro.userprofile.util.{Examples, H2TestDBComponent}


class UserProfileDBIOTests extends WordSpecLike with ScalaFutures with BeforeAndAfterAll with Matchers
 with UserProfileDBIO with H2TestDBComponent {


  "findAll" must {
    "list all profiles" in {
      val res = Await.result(db.run(UserProfileDBIO.findAll), 5 seconds)
      res shouldBe a [List[_]]
      assert(res.asInstanceOf[List[UserProfile]].lengthCompare(2) == 0)
    }
  }

  "findAllWithMembers" must {
    "list all profiles with members" in {
      val res = Await.result(db.run(UserProfileDBIO.findAllWithMembers), 5 seconds)
      res shouldBe a [List[_]]
      val profiles = res.asInstanceOf[List[(UserProfile, Address, (Organisation, Address))]]
      assert(profiles.head._1.firstName == "fname1")
    }
  }

  "getById" must {
    "return an existing profile" in {
      val res = Await.result(db.run(UserProfileDBIO.getById(Examples.existingProfile.id)), 5 seconds)
      assert(res.firstName == "fname1")
    }

    "return 404 Error when profile does not exists" in {
      val res = intercept[Error] {
        Await.result(db.run(UserProfileDBIO.getById(Some(Examples.randomID))), 5 seconds)
      }
      assert(res.id == 404)
    }
  }


  "getByIdWithMembers" must {
    "return an existing profile with address and organisation" in {
      val res = Await.result(db.run(UserProfileDBIO.getByIdWithMembers(Examples.existingProfile.id)), 5 seconds)
      assert(res._1.firstName == "fname1")
      assert(res._2.id == res._1.id)
      assert(res._3._1.id.get == res._1.organisation)
      assert(res._3._1.id == res._3._2.id)
    }

    "return 404 Error when profile does not exists" in {
      val res = intercept[Error] {
        Await.result(db.run(UserProfileDBIO.getByIdWithMembers(Some(Examples.randomID))), 5 seconds)
      }
      assert(res.id == 404)
    }
  }

  "getOrg" must {
    "return an existing org" in {
      val res = Await.result(db.run(UserProfileDBIO.getOrg(Examples.existingOrg.id)), 5 seconds)
      assert(res == Examples.existingOrg)
    }

    "return 404 Error when profile does not exists" in {
      val res = intercept[Error] {
        Await.result(db.run(UserProfileDBIO.getOrg(Some(Examples.randomID))), 5 seconds)
      }
      assert(res.id == 404)
    }
  }

  "getOrgWithAddress" must {
    "return an existing org with Address" in {
      val res = Await.result(db.run(UserProfileDBIO.getOrgWithAddress(Examples.existingOrg.id)), 5 seconds)
      assert(res._1.name == Examples.existingOrg.name)
      assert(res._1.id == res._2.id)
    }

    "return 404 Error when profile does not exists" in {
      val res = intercept[Error] {
        Await.result(db.run(UserProfileDBIO.getOrg(Some(Examples.randomID))), 5 seconds)
      }
      assert(res.id == 404)
    }
  }

  "getAddress" must {
    "return an existing address" in {
      val res = Await.result(db.run(UserProfileDBIO.getAddress(Examples.existingAddress.id)), 5 seconds)
      assert(res == Examples.existingAddress)
    }

    "return 404 Error when profile does not exists" in {
      val res = intercept[Error] {
        Await.result(db.run(UserProfileDBIO.getAddress(Some(Examples.randomID))), 5 seconds)
      }
      assert(res.id == 404)
    }
  }

  "insert" must {
    "insert a new user profile and return all members in" in {
      val res = Await.result(db.run(UserProfileDBIO.insert(Examples.pro, Examples.add)), 5 seconds)
      assert(res._1.id.isDefined)
      assert(res._1.firstName == Examples.pro.firstName)
      assert(res._2.line1 == Examples.add.line1)
    }
    "return an  error when organisation does not exists" in {
      val pro = Examples.pro.copy(organisation = UUID.randomUUID())
      val res = intercept[Error] {
        Await.result(db.run(UserProfileDBIO.insert(pro, Examples.add)), 5 seconds)
      }
      assert(res.id == 404)
    }
  }

  "update" must {
    "update an existing user profile" in {
      val pro = Examples.existingProfile.copy(firstName = "UPDATED")
      val add = Examples.existingAddress.copy(line1 = "UPDATED")
      val res = Await.result(db.run(UserProfileDBIO.update(pro, add)), 5 seconds)
      assert(res._1.firstName == "UPDATED")
      assert(res._2.line1 == "UPDATED")
    }
    "return an error when organisation does not exists" in {
      val pro = Examples.existingProfile.copy(organisation = UUID.randomUUID())
      val res = intercept[Error] {
        Await.result(db.run(UserProfileDBIO.insert(pro, Examples.existingAddress)), 5 seconds)
      }
      assert(res.id == 404)
    }
  }

  "delete" must {
    "delete an existing user profile" in {
      val res = Await.result(db.run(UserProfileDBIO.delete(Examples.existingProfile.id)), 5 seconds)
      assert(res == 1)
    }
    "return 0 when profile does not exists" in {
      val res = Await.result(db.run(UserProfileDBIO.delete(Some(Examples.randomID))), 5 seconds)
      assert(res == 0)
    }
  }

}
