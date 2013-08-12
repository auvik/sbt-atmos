/**
 *  Copyright (C) 2013 Typesafe, Inc <http://typesafe.com>
 */
package com.typesafe.sbt
package atmos

import sbt._
import sbt.Keys._
import sbt.PlayKeys.playRunHooks
import play.Project.{ createPlayRunCommand, playReloaderClasspath, playReloaderClassLoader }

object AtmosPlaySpecific {
	import SbtAtmosPlay.AtmosPlay
  import SbtAtmosPlay.AtmosPlayKeys.weavingClassLoader

  def atmosPlaySpecificSettings(): Seq[Setting[_]] = Seq(
    commands += createPlayRunCommand("atmos-run", playRunHooks in AtmosPlay, externalDependencyClasspath in AtmosPlay, weavingClassLoader in AtmosPlay, playReloaderClasspath, playReloaderClassLoader)
  ) ++ SbtAtmos.traceAkka("2.1.4")
}
