import sbt.{Resolver, _}

object Library {

  val resolvers = Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )

  val version = Map(
    "scala"           -> "2.12.4",
    "akka"            -> "2.4.20",
    "akkaHttp"        -> "10.0.11",
    "shapeless"       -> "2.3.3",
    "scalaTest"       -> "3.0.3",
    "swaggerAkkaHttp" -> "0.13.0",
    "swaggerJaxrs"    -> "1.5.18",
    "akkaHttpCors"    -> "0.2.2",
    "slf4j"           -> "1.7.25",
    "circe"           -> "0.9.1",
    "akkaHttpCirce"   -> "1.19.0",
    "slick"           -> "3.2.1",
    "postgresql"      -> "42.2.1",
    "h2"              -> "1.4.196"
  )

  val akkaActor         = "com.typesafe.akka" %% "akka-actor"   % version("akka")
  val akkaTestKit       = "com.typesafe.akka" %% "akka-testkit" % version("akka") % Test
  val akkaStream        = "com.typesafe.akka" %% "akka-stream"  % version("akka")
  val akkaSlf4j         = "com.typesafe.akka" %% "akka-slf4j"   % version("akka")
  val akkaHttp          = "com.typesafe.akka" %% "akka-http"    % version("akkaHttp")
  val akkaHttpTestKit   = "com.typesafe.akka" %% "akka-http-testkit" % version("akkaHttp") % Test
  val akkaHttpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % version("akkaHttp")
  val shapeless         = "com.chuusai" %% "shapeless" % version("shapeless")
  val scalaTest         = "org.scalatest"     %% "scalatest"    % version("scalaTest") % Test
  val scalaMock         = "org.scalamock" %% "scalamock" % "4.1.0" % Test
  val swaggerAkkaHttp   = "com.github.swagger-akka-http" %% "swagger-akka-http" % version("swaggerAkkaHttp")
  val swaggerJaxrs      = "io.swagger" % "swagger-jaxrs" % version("swaggerJaxrs")
  val akkaHttpCors      = "ch.megard" %% "akka-http-cors" % version("akkaHttpCors")
  val slf4j             = "org.slf4j" % "slf4j-simple" % version("slf4j")
  val circeCore         = "io.circe"  % "circe-core_2.12" % version("circe")
  val circeGeneric      = "io.circe"  % "circe-generic_2.12" % version("circe")
  val circeParser       = "io.circe"  % "circe-parser_2.12" % version("circe")
  val akkaHttpCirce     = "de.heikoseeberger" % "akka-http-circe_2.12" % version("akkaHttpCirce")
  val slick             = "com.typesafe.slick" %% "slick" % version("slick")
  val slickHikariCP     = "com.typesafe.slick" %% "slick-hikaricp" % version("slick")
  val postgresql        = "org.postgresql" % "postgresql" % version("postgresql")
  val h2                = "com.h2database" % "h2" % version("h2")
  val slickRepo         = "com.byteslounge" %% "slick-repo" % "1.4.3"
  val flyway            = "org.flywaydb" % "flyway-core" % "5.0.7"

}
