import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {
  
  val appName = "BetLiMS"
  
  val appVersion = "1.0-SNAPSHOT"
  
  val appDependencies = Seq(
    "org.mindrot" % "jbcrypt" % "0.3m"
  )
  

  lazy val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "Sonatype Snapshots"  at "https://oss.sonatype.org/content/repositories/snapshots"
  )
}