import java.util.Properties

organization := "com.harrys"

name := "hyppo-test-integration"

version := "0.6.1"

scalaVersion := "2.11.7"

//  Import the SBT avro settings
sbtavro.SbtAvro.avroSettings

version in avroConfig := "1.7.7"

(sourceDirectory in avroConfig) := (resourceDirectory in Compile).value / "com" / "harrys" / "hyppo" / "demo" / "avro"

(javaSource in avroConfig) := (javaSource in Compile).value

managedSourceDirectories in Compile += (javaSource in avroConfig).value

resolvers ++= Seq(Resolver.sonatypeRepo("public"), Resolver.sonatypeRepo("snapshots"))

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "commons-io" % "commons-io" % "2.4",
  "org.json4s" %% "json4s-core" % "3.2.11",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.json4s" %% "json4s-ext" % "3.2.11",
  "com.harrys.hyppo" % "source-api" % "0.6.0"
)

lazy val integrationUtils  = RootProject(uri("https://github.com/harrystech/ingestion-utils.git#v0.0.2"))

//  Export jars instead of exporting the classpath location
//exportJars := true

//  Override default assembly name
assemblyJarName := { name.value + "-assembly-" + version.value + ".jar" }

//  Setup for using ScalaTest
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest"  % "2.2.4" % "test"
)

testOptions += Tests.Argument(TestFrameworks.ScalaTest)

testOptions in Test += Tests.Setup(() => {
  //  Set the classpath so we can fork a new JVM
  System.setProperty("testing.classpath", (fullClasspath in Test).value.files.map(_.getAbsolutePath).mkString(":"))
  //  Loads hyppo-test-integration/.env file into environment for pulling values from
  val envFile = baseDirectory.value / ".env"
  if (envFile.isFile){
    IO.load(System.getProperties, envFile)
  }
})
