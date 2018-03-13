package io.micro.userprofile.util

import java.util.UUID

import io.micro.userprofile.dao._
import io.micro.userprofile.{dto => DTO}

object Examples{

  val randomID:UUID =
    UUID.randomUUID()

  val add: Address=
    Address(None, "line1", Some("line2"), "postCode", "city1", "country")

  val org: Organisation =
    Organisation( id = None, name = "Organisation 1", email = "mail@org1.com", `type`= OrgType.CHAMBERS)

  val existingProfile: UserProfile =
    UserProfile( Some(UUID.fromString("885e9ef6-2155-11e8-b467-0ed5f89f718b")), "fname1", "lname1",
      "user1@mail",SalutationType.MR, "0790000001", ProfileType.BARRISTER, UUID.fromString("9fd3da6d-c409-4e8f-aee6-f80f9c0db9cf"))

  val existingAddress: Address=
    Address(Some(UUID.fromString("885e9ef6-2155-11e8-b467-0ed5f89f718b")),"addline3.1", Some("addline3.2"),
      "postCode3","city3","country3" )

  val existingOrg: Organisation =
    Organisation(Some(UUID.fromString("9fd3da6d-c409-4e8f-aee6-f80f9c0db9cf")), "org1", "org1@mail", OrgType.CHAMBERS)


  val pro: UserProfile =
    UserProfile(None, "fname", "lname", "email", SalutationType.MR, "0000", ProfileType.BARRISTER, existingProfile.organisation)



  val userProfileDTO: DTO.UserProfile =
    DTO.UserProfile((pro, add, (existingOrg,add)))


}
