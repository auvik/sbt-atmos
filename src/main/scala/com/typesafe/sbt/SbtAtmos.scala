/**
 *  Copyright (C) 2013 Typesafe, Inc <http://typesafe.com>
 */
package com.typesafe.sbt

import sbt._
import sbt.Keys._
import java.net.URI

object SbtAtmos extends Plugin {
  import atmos.AtmosRunner._

  val AtmosVersion = "1.2.0-M6"

  val Atmos = config("atmos") hide

  object AtmosKeys {
    val atmosVersion = SettingKey[String]("atmos-version")
    val aspectjVersion = SettingKey[String]("aspectj-version")

    val atmosPort = TaskKey[Int]("atmos-port")
    val consolePort = TaskKey[Int]("console-port")
    val tracePort = TaskKey[Int]("trace-port")

    val atmosOptions = TaskKey[Seq[String]]("atmos-options")
    val consoleOptions = TaskKey[Seq[String]]("console-options")

    val atmosClasspath = TaskKey[Classpath]("atmos-classpath")
    val consoleClasspath = TaskKey[Classpath]("console-classpath")
    val traceClasspath = TaskKey[Classpath]("trace-classpath")
    val aspectjWeaver = TaskKey[Option[File]]("aspectj-weaver")

    val atmosDirectory = SettingKey[File]("atmos-directory")
    val atmosConfigDirectory = SettingKey[File]("atmos-config-directory")
    val atmosLogDirectory = SettingKey[File]("atmos-log-directory")
    val atmosConfigString = TaskKey[String]("atmos-config-string")
    val atmosLogbackString = SettingKey[String]("atmos-logback-string")
    val atmosConfig = TaskKey[File]("atmos-config")
    val consoleConfigString = TaskKey[String]("console-config-string")
    val consoleLogbackString = SettingKey[String]("console-logback-string")
    val consoleConfig = TaskKey[File]("console-config")
    val traceable = SettingKey[Seq[(String, Boolean)]]("traceable")
    val traceableConfigString = SettingKey[String]("traceable-config-string")
    val sampling = SettingKey[Seq[(String, Int)]]("sampling")
    val samplingConfigString = SettingKey[String]("sampling-config-string")
    val traceConfigString = TaskKey[String]("trace-config-string")
    val traceLogbackString = SettingKey[String]("trace-logback-string")
    val traceConfig = TaskKey[File]("trace-config")

    val sigarLibs = TaskKey[Option[File]]("sigar-libs")
    val atmosRunListeners = TaskKey[Seq[URI => Unit]]("atmos-run-listeners")
    val atmosInputs = TaskKey[AtmosInputs]("atmos-inputs")
  }

  import AtmosKeys._

  lazy val atmosSettings: Seq[Setting[_]] = inConfig(Atmos)(atmosScopedSettings) ++ atmosUnscopedSettings

  def atmosScopedSettings: Seq[Setting[_]] = Seq(
    atmosVersion := "1.2.0-M6",
    aspectjVersion := "1.7.2",

    atmosPort := selectPort(8660),
    consolePort := selectPort(9900),
    tracePort := selectPort(28660),

    atmosOptions := Seq("-Xms512m", "-Xmx512m"),
    consoleOptions := Seq("-Xms512m", "-Xmx512m"),

    atmosClasspath <<= managedClasspath(Atmos),
    consoleClasspath <<= managedClasspath(AtmosConsole),
    traceClasspath <<= managedClasspath(AtmosTrace),
    aspectjWeaver <<= findAspectjWeaver,

    atmosDirectory <<= target / "atmos",
    atmosConfigDirectory <<= atmosDirectory / "conf",
    atmosLogDirectory <<= atmosDirectory / "log",
    atmosConfigString <<= tracePort map defaultAtmosConfig,
    atmosLogbackString <<= defaultLogbackConfig("atmos"),
    atmosConfig <<= writeConfig("atmos", atmosConfigString, atmosLogbackString),
    consoleConfigString <<= (name, atmosPort) map defaultConsoleConfig,
    consoleLogbackString <<= defaultLogbackConfig("console"),
    consoleConfig <<= writeConfig("console", consoleConfigString, consoleLogbackString),
    traceable := Seq("*" -> true),
    traceableConfigString <<= traceable apply { s => seqToConfig(s, indent = 6, quote = true) },
    sampling := Seq("*" -> 1),
    samplingConfigString <<= sampling apply { s => seqToConfig(s, indent = 6, quote = true) },
    traceConfigString <<= (normalizedName, traceableConfigString, samplingConfigString, tracePort) map defaultTraceConfig,
    traceLogbackString := "",
    traceConfig <<= writeConfig("trace", traceConfigString, traceLogbackString),

    sigarLibs <<= unpackSigar,

    atmosRunListeners := Seq.empty,
    atmosRunListeners <+= streams map { s => logConsoleUri(s.log)(_) },

    atmosInputs <<= (
      atmosPort, consolePort, atmosOptions, consoleOptions,
      atmosClasspath, consoleClasspath, traceClasspath, aspectjWeaver,
      atmosDirectory, atmosConfig, consoleConfig, traceConfig, sigarLibs,
      atmosRunListeners
    ) map AtmosInputs,

    inScope(Scope(This, Select(Compile), Select(run.key), This))(Seq(runner in run in Atmos <<= atmosRunner)).head,
    run <<= Defaults.runTask(fullClasspath in Runtime, mainClass in run in Compile, runner in run),
    runMain <<= Defaults.runMainTask(fullClasspath in Runtime, runner in run)
  )

  def atmosUnscopedSettings: Seq[Setting[_]] = Seq(
    ivyConfigurations ++= Seq(Atmos, AtmosConsole, AtmosTrace, AtmosWeave, AtmosSigar),

    resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",

    libraryDependencies <++= (atmosVersion in Atmos)(atmosDependencies),
    libraryDependencies <++= (atmosVersion in Atmos)(consoleDependencies),
    libraryDependencies <++= (aspectjVersion in Atmos)(weaveDependencies),
    libraryDependencies <++= (atmosVersion in Atmos)(sigarDependencies),
    autoTraceDependencies
  )

  def autoTraceDependencies = {
    libraryDependencies <++= (libraryDependencies, atmosVersion in Atmos, scalaVersion)(traceDependencies)
  }
}
