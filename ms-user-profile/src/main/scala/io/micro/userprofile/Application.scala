package io.micro.userprofile

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers.HttpOriginRange
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import com.typesafe.config.{Config, ConfigFactory}
import io.micro.userprofile.rest.{SwaggerService, UserProfileService}
import scala.collection.immutable.Seq
import scala.concurrent.ExecutionContext


object Application extends App with Directives {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val config: Config = ConfigFactory.load()

  private val corsSettings = CorsSettings.defaultSettings.copy(
    allowCredentials = false,
    allowedOrigins = HttpOriginRange.*,
    allowedMethods = Seq(GET, POST, PUT, HEAD, DELETE, OPTIONS),
  )

  private lazy val routes: Route =
    cors(corsSettings) {
      UserProfileService.routes ~ SwaggerService.routes
    }

  Http().bindAndHandle(routes, config.getString("app.server.host"), config.getInt("app.server.port"))

}
