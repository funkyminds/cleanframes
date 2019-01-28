# Clean Frames

**Clean Frames** is a library for Apache Spark SQL module. It provides a type class for data cleansing.


## Getting Clean Frames

The current stable version is 0.1.0, which is built against Scala 2.12.x.

If you're using SBT, add the following line to your build file:

```scala
libraryDependencies += "io.funkyminds" %% "cleanframes" % "0.1.0-SNAPSHOT"
```

## Quick Start

```scala
import cleanframes.instances.all._
import cleanframes.syntax._
import cleanframes.instances.TryToOption._

case class CustomModel(col1: Option[Float], col2: Option[Int], col3: Option[Boolean])

val input: DataFrame = ???

val clean: Dataset[CustomModel] = input
                                    .toDF("col1", "col2", "col3")
                                    .clean[CustomModel]
                                    .as[CustomModel]
```

### Clean Frames with Maven

Clean Frames is also available for projects using the Maven build tool via the following dependency,

```xml
<dependency>
  <groupId>io.funkyminds</groupId>
  <artifactId>cleanframes_2.12</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## Contributors

+ Dawid Rutowicz <dawid.rutowicz@gmail.com> [@dawrutowicz](https://twitter.com/dawrutowicz)