package cleanframes

import java.nio.file.Paths

import cleanframes.model.Example
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{lit, lower, trim, when, not => sqlNot}
import org.apache.spark.sql.types.{BooleanType, FloatType, IntegerType}
import org.scalatest.{FlatSpec, Matchers}

/**
  * This test demonstrates manual data frame cleaning with Spark-SQL API.
  */
class ManualDataFrameCleaningExampleTest
  extends FlatSpec
    with Matchers
    with DataFrameSuiteBase {

  it should "clean dataframe values without entire-row discards using Apache Spark SQL functions manually" in {
    val csvFilePath = Paths
      .get("src", "it", "resources", "readme", "example.csv")
      .toFile
      .getAbsolutePath

    import spark.implicits._

    val frame = spark
      .read
      .option("header", "false")
      .csv(csvFilePath)
      .toDF("col1", "col2", "col3")

    val cleaned: DataFrame = frame.withColumn(
      "col1",
      when(
        sqlNot(
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
        sqlNot(
          frame.col("col3").isNaN
        ),
        frame.col("col3")
      ) cast FloatType
    )

    val result = cleaned
      .as[Example]
      .collect()

    result should {
      contain theSameElementsAs Seq(
        // @formatter:off
        Example(Some(1),  Some(true),   Some(1.0f)),
        Example(None,     Some(true),   Some(2.0f)),
        Example(Some(3),  Some(false),  Some(3.0f)),
        Example(Some(4),  Some(true),   None),
        Example(Some(5),  Some(true),   Some(5.0f))
        // @formatter:on
      )
    }
  }

}
