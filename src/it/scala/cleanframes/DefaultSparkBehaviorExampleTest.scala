package cleanframes

import java.nio.file.Paths

import cleanframes.model.Example
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.apache.spark.sql.Encoders
import org.scalatest.{FlatSpec, Matchers}

class DefaultSparkBehaviorExampleTest extends FlatSpec
  with Matchers
  with DataFrameSuiteBase {

  it should "clean dataframe values with entire-row discards by loading and collecting data" in {
    val csvFilePath = Paths
      .get("src", "it", "resources", "readme", "example.csv")
      .toFile
      .getAbsolutePath

    import spark.implicits._

    val frame = spark
      .read
      .option("header", "false")
      .schema(Encoders.product[Example].schema)
      .csv(csvFilePath)

    val result = frame
      .as[Example]
      .collect()

    result should {
      contain theSameElementsAs Seq(
        // @formatter:off
        Example(Some(1),  Some(true),   Some(1.0f)),
        Example(None,     None,         None),
        Example(Some(3),  Some(false),  Some(3.0f)),
        Example(None,     None,         None),
        Example(Some(5),  Some(true),   Some(5.0f))
        // @formatter:on
      )
    }
  }

}