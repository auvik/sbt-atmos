/**
 *  Copyright (C) 2013 Typesafe, Inc <http://typesafe.com>
 */
package com.typesafe.sbt

import sbt._
import sbt.Keys._
import play.Project.{ ClassLoaderCreator, playVersion }

object SbtAtmosPlay extends Plugin {
  import SbtAtmos._
  import atmos.AtmosPlayRun._
  import atmos.AtmosRunner.AtmosTraceCompile

  val AtmosPlay = config("atmos-play").extend(Atmos)

  object AtmosPlayKeys {
    val weavingClassLoader = TaskKey[ClassLoaderCreator]("weaving-class-loader")
  }

  import AtmosKeys._
  import AtmosPlayKeys._

  lazy val atmosPlaySettings: Seq[Setting[_]] =
    atmosSettings ++
    inConfig(AtmosPlay)(atmosConfigurationSettings(Compile, AtmosTraceCompile)) ++
    atmosPlayRunSettings ++
    tracePlay

  def tracePlay(): Seq[Setting[_]] = Seq(
    libraryDependencies <++= (playVersion, atmosVersion in AtmosPlay)(tracePlayDependencies)
  )
}
