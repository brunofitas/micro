package io.micro.userprofile.dto

import java.util.UUID

import io.micro.userprofile.dao.OrgType.OrgType
import io.swagger.annotations.ApiModelProperty

import scala.annotation.meta.field


final case class Organisation
(
  id:UUID,
  name:Option[String],
  email:Option[String],
  @(ApiModelProperty @field)(dataType = "io.micro.userprofile.dao.OrgType$")
  `type`:Option[OrgType],
  address:Option[Address]
)
