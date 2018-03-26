name := """play-scala-seed"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(guice, ehcache,
 "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
 "com.typesafe.play" % "play-slick_2.12" % "3.0.0",
 "com.typesafe.play" % "play-slick-evolutions_2.12" % "3.0.0",
 "mysql" % "mysql-connector-java" % "6.0.6",
 "com.h2database" % "h2" % "1.4.196",
 specs2 % Test,
 evolutions)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
