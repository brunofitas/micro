package io.micro.userprofile.dto


final case class Address
(
  line1:String,
  line2:Option[String],
  postCode:String,
  city:String,
  country:String
)

