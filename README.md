sbt-atmos
=========

[sbt] plugin for running [Typesafe Console][console] in development.


Add plugin
----------

This plugin requires sbt 0.12.

Add plugin to `project/plugins.sbt`. For example:

```scala
addSbtPlugin("com.typesafe.sbt" % "sbt-atmos" % "0.1.0-SNAPSHOT")
```

Add the sbt-atmos settings to the project. For a `.sbt` build, add a line with:

```scala
atmosSettings
```

For a full `.scala` build, add these settings to your project settings:

```scala
com.typesafe.sbt.SbtAtmos.atmosSettings
```

For example:

```scala
import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtAtmos.atmosSettings

object SampleBuild extends Build {
  lazy val sample = Project(
    id = "sample",
    base = file("."),
    settings = Defaults.defaultSettings ++ atmosSettings ++ Seq(
      name := "Sample",
      scalaVersion := "2.10.2",
      libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.2.0"
    )
  )
}
```

A simple [sample Akka project][sample] configured with the sbt-atmos plugin is
included in this repository.


Run with Typesafe Console
-------------------------

To run your application with Typesafe Console there are extra versions of the
`run` and `run-main` tasks. These use the same underlying settings for the
regular `run` tasks, and also add the configuration needed to instrument your
application, and start and stop Typesafe Console.

To run the default or discovered main class use:

    atmos:run

To run a specific main class:

    atmos:run-main org.something.MainClass


Trace configuration
-------------------

It's possible to configure which actors in your application are traced, and at
what sampling rates.

The underlying configuration uses the [Typesafe Config][config] library. A
configuration file is automatically created by the sbt-atmos plugin.

There are sbt settings to adjust the tracing and sampling of actors. Trace
configuration is based on actor paths. For example:

```scala
import com.typesafe.sbt.SbtAtmos.Atmos
import com.typesafe.sbt.SbtAtmos.AtmosKeys.{ traceable, sampling }

traceable in Atmos := Seq(
  "/user/someActor" -> true,  // trace this actor
  "/user/actors/*"  -> true,  // trace all actors in this subtree
  "*"               -> false  // other actors are not traced
)

sampling in Atmos := Seq(
  "/user/someActor" -> 1,     // sample every trace for this actor
  "/user/actors/*"  -> 100    // sample every 100th trace in this subtree
)
```

**Note**: The default settings are to collect all traces for all actors.
For applications with heavier loads you should select specific parts of the
application to trace.


Mailing list
------------

Please use the [Typesafe Console mailing list][email].


License
-------

[Typesafe Console][console] is licensed under the [Typesafe Subscription Agreement][license]
and is made available through the sbt-atmos plugin for development use only.

The code for the sbt-atmos plugin is open source software licensed under the
[Apache 2.0 License][apache].

For more information see [Typesafe licenses][licenses].


Contribution policy
-------------------

Contributions via GitHub pull requests are gladly accepted from their original
author. Before we can accept pull requests, you will need to agree to the
[Typesafe Contributor License Agreement][cla] online, using your GitHub account.


[sbt]: https://github.com/sbt/sbt
[console]: http://typesafe.com/platform/runtime/console
[sample]: https://github.com/typesafehub/sbt-atmos/tree/master/sample/abc
[config]: https://github.com/typesafehub/config
[email]: http://groups.google.com/group/typesafe-console
[license]: http://github.com/typesafehub/sbt-atmos/blob/master/TypesafeSubscriptionAgreement.md
[apache]: http://www.apache.org/licenses/LICENSE-2.0.html
[licenses]: http://typesafe.com/legal/licenses
[cla]: http://www.typesafe.com/contribute/cla
