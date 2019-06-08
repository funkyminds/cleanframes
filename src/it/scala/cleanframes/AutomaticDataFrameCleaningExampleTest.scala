package cleanframes

import java.nio.file.Paths

import cleanframes.model.Example
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.scalatest.{FlatSpec, Matchers}

/**
  * This test demonstrates automatic data frame cleaning with the library.
  */
class AutomaticDataFrameCleaningExampleTest
  extends FlatSpec
    with Matchers
    with DataFrameSuiteBase {

  it should "clean dataframe values without entire-row discards automatically with cleanframes API" in {
    val csvFilePath = Paths
      .get("src", "it", "resources", "readme", "example.csv")
      .toFile
      .getAbsolutePath

    val frame = spark
      .read
      .option("header", "false")
      .csv(csvFilePath)
      .toDF("col1", "col2", "col3")

    import cleanframes.syntax._
    import cleanframes.instances.all._
    import spark.implicits._

    val cleaned = frame.clean[Example]

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
