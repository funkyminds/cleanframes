package cleanframes

import java.math.MathContext

import cleanframes.instances.SparkDataType
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.apache.spark.sql.types.{DataType, DecimalType}
import org.scalatest.{FlatSpec, Matchers}

/**
  * This test demonstrates how to add a Cleaner instance for DataType that is not present in the library.
  *
  * For example, DecimalType needs to parametrized by a precision and a scale.
  */
class AddNewSparkDataTypeSupportTest
  extends FlatSpec
  with Matchers
  with DataFrameSuiteBase {

  "Cleaner" should "resolve and use a Cleaner instance for BigDecimal" in {
    import spark.implicits._ // to use `.toDF` and `.as`
    import cleanframes.syntax._ // to use `.clean`

    // define test data for a dataframe
    val input = Seq(
      // @formatter:off
      ("1",         "10.110"),
      ("2",         "30.47"),
      (null,        "99.6"),
      ("4",         "corrupted"),
      ("5",         null),
      ("corrupted", "99.6")
      // @formatter:on
    )
      // important! dataframe's column names must match parameter names of the case class passed to `.clean` method
      .toDF("id", "amount")

    // import standard functions for conversions shipped with the library
    import cleanframes.instances.all._

    // define new spark type support
    // cleanframes automatically will create Cleaner[BigDecimal] instance
    implicit val decimalCleaner = new SparkDataType[BigDecimal] {
      override def getDataType: DataType = DecimalType(4, 2)
    }

    val result = input
      // call cleanframes API
      .clean[Model]
      // make Dataset
      .as[Model]
      .collect

    val mathCtx = new MathContext(4)

    result should {
      contain theSameElementsAs Seq(
        // @formatter:off
        Model(Some(1),  Some(BigDecimal(1011, 2, mathCtx))),
        Model(Some(2),  Some(BigDecimal(3047, 2, mathCtx))),
        Model(None,     Some(BigDecimal(9960, 2, mathCtx))),
        Model(Some(4),  None),
        Model(Some(5),  None),
        Model(None,     Some(BigDecimal(9960, 2, mathCtx)))
        // @formatter:on
      )
    }
  }
}

case class Model(id: Option[Int], amount: Option[BigDecimal])
