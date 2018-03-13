

lazy val micro =
  Project(id = "micro", base = file("."))
    .settings(Settings.moduleSettings: _*)
    .aggregate(libDefault, msUserProfile)


lazy val libDefault =
  Project(id = "lib-default", base = file("lib-default"))
    .settings(Settings.moduleSettings: _*)
    .settings(Settings.repSettings: _*)


lazy val msUserProfile =
  Project(id = "ms-user-profile", base = file("ms-user-profile"))
    .settings(Settings.moduleSettings: _*)
    .settings(Settings.apiSettings: _*)
    .settings(Settings.repSettings: _*)
    .dependsOn(libDefault)





