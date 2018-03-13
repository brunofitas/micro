package io.micro.userprofile.rest


import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info
import com.typesafe.config.{Config, ConfigFactory}
import io.swagger.models.auth.BasicAuthDefinition

object SwaggerService extends SwaggerHttpService {

  val config: Config = ConfigFactory.load()
  override val info = Info(version = "1.0")
  override val host = config.getString("app.swagger.api")
  override val unwantedDefinitions = Seq("Function1", "Function1RequestContextFutureRouteResult")
  override val securitySchemeDefinitions = Map("basicAuth" -> new BasicAuthDefinition())
  override val apiClasses = Set(
    classOf[UserProfileService]
  )
}
