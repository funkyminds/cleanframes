
# Clean Frames

[![Build Status](https://travis-ci.org/funkyminds/cleanframes.svg?branch=master)](https://travis-ci.org/funkyminds/cleanframes)

**Clean Frames** is a library for Apache Spark SQL module. It provides a type class for data cleansing.


## Getting Clean Frames

The current stable version is 0.3.0, which is cross built against Scala (2.10-2.12) and Apache Spark (1.6.0-2.4.3).

If you're using SBT, add the following line to your build file:

```scala
libraryDependencies += "io.funkyminds" %% "cleanframes" % "2.4.3_0.3.0-SNAPSHOT"
```

## Quick Start

Assuming DataFrame is loaded from csv file of following content:
```csv
1,true,1.0
lmfao,true,2.0
3,false,3.0
4,true,yolo data
5,true,5.0
```

library clean data to:

```
Example(Some(1),  Some(true),   Some(1.0f)),
Example(None,     Some(true),   Some(2.0f)),
Example(Some(3),  Some(false),  Some(3.0f)),
Example(Some(4),  Some(true),   None),
Example(Some(5),  Some(true),   Some(5.0f))
```

with minimal code:

```scala
import cleanframes.instances.all._
import cleanframes.syntax._
  
frame
  .clean[Example]
  .as[Example]
```

See [full code example](src/it/scala/cleanframes/AutomaticDataFrameCleaningExampleTest.scala) for a complete reference.

### What is so different?

We would like to live in a world where data quality is superb but only unicorns are perfect.

Apache Spark by default discards entire row if it contains any invalid value.

Having called Spark for same data:

```scala
frame
  .as[Example]
```

would result in:

```
Example(Some(1),  Some(true),   Some(1.0f)),
Example(None,     None,         None),
Example(Some(3),  Some(false),  Some(3.0f)),
Example(None,     None,         None),
Example(Some(5),  Some(true),   Some(5.0f))
```

As noticed, data in second and forth rows are lost. Such behaviour might not be accepted in some domains.

See [full code example](src/it/scala/cleanframes/DefaultSparkBehaviorExampleTest.scala) for a complete reference.

### Pure Spark-SQL API

To discard only not valid cells without losing entire row, such Spark SQL API might be called: 

```scala
val cleaned = frame.withColumn(
  "col1",
  when(
    not(
      frame.col("col1").isNaN
    ),
    frame.col("col1")
  ) cast IntegerType
).withColumn(
  "col2",
  when(
    trim(lower(frame.col("col2"))) === "true",
    lit(true) cast BooleanType
  ).otherwise(false)
).withColumn(
  "col3",
  when(
    not(
      frame.col("col3").isNaN
    ),
    frame.col("col3")
  ) cast FloatType
)
```

### cleanframes

__cleanframes__ is a small library that does such boilerplate as above for you by calling:

```scala
frame.clean[CaseClass]
```

It resolves type-related transformations in a compile time using implicit resolutions in a type-safe way.

The library is shipped with common basic transformations and can be extended via custom ones.

Refer to documentation to see instructions with examples.  

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