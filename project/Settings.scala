/**
 * Application settings
 */
object Settings {
  object Definitions {
    /** The name of the application */
    val name = "ocs3"

    /** Top level version */
    val version = "2016001.1.1"

    /** Options for the scala compiler */
    val scalacOptions = Seq(
      "-Xlint",
      "-unchecked",
      "-deprecation",
      "-feature"
    )
  }

  /** global dependency versions */
  object LibraryVersions {
    val scala = "2.11.8"
    val scalaDom = "0.9.0"
    val scalajsReact = "0.10.4"
    val scalaCSS = "0.4.0"

    val ocsVersion = "2016001.1.1"
  }
}
