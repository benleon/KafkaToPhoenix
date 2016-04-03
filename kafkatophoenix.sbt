name := "KafkaToPhoenix"

version := "1.0"
scalaVersion := "2.10.4"
val sparkVersion = "1.5.2.3.3.4.0-3485"
val kafkaVersion = "0.9.0.2.3.4.0-3485" 
val phoenixVersion = "4.4.0.2.3.4.0-3485"
val hbaseVersion = "1.1.2.2.3.4.0-3485"
ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }
//libraryDependencies += "org.apache.hbase" % "hbase" % hbaseVersion exclude("log4j", "log4j") exclude("org.slf4j","slf4j-log4j12") exclude("org.slf4j","slf4j-api")
libraryDependencies += "com.google.guava" % "guava" % "11.0.2" exclude("log4j", "log4j") exclude("org.slf4j","slf4j-log4j12") exclude("org.slf4j","slf4j-api")
libraryDependencies += "org.apache.spark" % "spark-streaming-kafka_2.10" % sparkVersion  exclude("log4j", "log4j") exclude("org.slf4j","slf4j-log4j12") exclude("org.slf4j","slf4j-api")
libraryDependencies += "org.apache.spark" % "spark-streaming_2.10" % sparkVersion % "provided" exclude("log4j", "log4j") exclude("org.slf4j","slf4j-log4j12") exclude("org.slf4j","slf4j-api") 
//libraryDependencies += "org.apache.phoenix" % "phoenix-assembly" % phoenixVersion  exclude("log4j", "log4j") exclude("org.slf4j","slf4j-log4j12") exclude("org.slf4j","slf4j-api") 
libraryDependencies +=   "org.apache.kafka" %% "kafka"  % kafkaVersion exclude("log4j", "log4j") exclude("org.slf4j","slf4j-log4j12") exclude("org.slf4j","slf4j-api")
libraryDependencies += "joda-time" % "joda-time" % "2.9.2" exclude("log4j", "log4j") exclude("org.slf4j","slf4j-log4j12") exclude("org.slf4j","slf4j-api")
libraryDependencies += "log4j" % "log4j" % "1.2.14"
libraryDependencies += "org.apache.commons" % "commons-pool2" % "2.3"

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
resolvers += "hdp-private" at "http://nexus-private.hortonworks.com/nexus/content/groups/public"
resolvers += "spring-releases" at "https://repo.spring.io/libs-release"
resolvers += Resolver.sonatypeRepo("public")
resolvers += DefaultMavenRepository


mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case x if x.startsWith("org/apache") => MergeStrategy.last
    case x if x.startsWith("edu") => MergeStrategy.last
    case x if x.startsWith("javax") => MergeStrategy.last
    case x if x.startsWith("org/apache/common") => MergeStrategy.last
    case x if x.startsWith("com") => MergeStrategy.last 
    case x if x.startsWith("jline") => MergeStrategy.last
    case x if x.startsWith("junit") => MergeStrategy.last
    case x if x.startsWith("org/jboss") => MergeStrategy.last
    case x if x.startsWith("org/joda/time") => MergeStrategy.last
    case x if x.startsWith("org/apache/log4j") => MergeStrategy.last
    case x if x.startsWith("META-INF/ECLIPSEF.RSA") => MergeStrategy.last
    case x if x.startsWith("META-INF/mailcap") => MergeStrategy.last
    case x if x.startsWith("plugin.properties") => MergeStrategy.last
    case x if x.startsWith("mrapp-generated-classpath") => MergeStrategy.last
    case x => old(x)
  }
}

assemblyOption in assembly ~= { _.copy(includeScala = false) }
