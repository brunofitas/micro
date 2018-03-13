import sbt.Keys._



object Settings {



  val moduleSettings = Seq(

    organization := "io.brunofitas",

    version := "1.0",

    scalaVersion := Library.version("scala"),

    scalacOptions ++= Seq(
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
      "-unchecked",
      "-deprecation",
      "-feature",
      "-language:postfixOps",
      "-Xlint"
    ),

    javacOptions ++= Seq(
      "-source", "1.8",
      "-target", "1.8",
      "Xlint"
    ),


    libraryDependencies ++= Seq(
      Library.shapeless,
      Library.scalaTest,
      Library.scalaMock,
      Library.slf4j,
      Library.akkaActor,
      Library.akkaTestKit,
      Library.akkaStream,
      Library.akkaSlf4j
    ),

    resolvers ++= Library.resolvers,

    fork in run := true


  )



  val apiSettings = Seq(

    libraryDependencies ++= Seq(
      Library.akkaHttpTestKit,
      Library.akkaHttpSprayJson,
      Library.swaggerJaxrs,
      Library.swaggerAkkaHttp,
      Library.akkaHttpCors,
      Library.circeCore,
      Library.circeGeneric,
      Library.circeParser,
      Library.akkaHttpCirce
    )

  )


  val repSettings = Seq(

    libraryDependencies ++= Seq(
      Library.slick,
      Library.slickHikariCP,
      Library.postgresql,
      Library.h2
    )

  )


}
