
# Clean Frames

[![Build Status](https://travis-ci.org/funkyminds/cleanframes.svg?branch=master)](https://travis-ci.org/funkyminds/cleanframes)

**Clean Frames** is a library for Apache Spark SQL module. It provides a type class for data cleansing.


## Getting Clean Frames

The current stable version is 0.3.0, which is cross built against Scala (2.10-2.12) and Apache Spark (1.6.0-2.4.3)

If you're using SBT, add the following line to your build file:

```scala
libraryDependencies += "io.funkyminds" %% "cleanframes" % "0.3.0-SNAPSHOT"
```

## Quick Start

```scala
import cleanframes.instances.all._
import cleanframes.syntax._

// define model
case class CustomModel(col1: Option[Float], col2: Option[Int], col3: Option[Boolean])

// load data
val input: DataFrame = ???

// clean it!
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
  <version>2.4.3_0.3.0-SNAPSHOT</version>
</dependency>
```

## Contributors

+ Dawid Rutowicz <dawid.rutowicz@gmail.com> [@dawrutowicz](https://twitter.com/dawrutowicz)