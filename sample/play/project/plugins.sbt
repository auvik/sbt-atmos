// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.1.4-RC1")

addSbtPlugin("com.typesafe.sbt" % "sbt-atmos-play" % "0.3.0-SNAPSHOT")
