package cleanframes

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.scalatest.{FlatSpec, Matchers}

/**
  * This test demonstrates how to define and use a function for own types.
  *
  * Refer to code below the test class to have a full understanding of the test case.
  */
class CustomTypesTest
  extends FlatSpec
    with Matchers
    with DataFrameSuiteBase {

  "Cleaner" should "compile and use a custom transformer for a custom type" in {
    import spark.implicits._ // to use `.toDF` and `.as`
    import cleanframes.syntax._ // to use `.clean`

    // define test data for a dataframe
    val input = Seq(
      // @formatter:off
      ("0",           "1",          null),
      (null,          "2",          "true"),
      ("corrupted",   null,         "true"),
      ("0",           "corrupted",  "false")
      // @formatter:on
    )
      // important! dataframe's column names must match parameter names of the case class passed to `.clean` method
      .toDF("col1", "col2", "col3")

    // import standard functions for conversions shipped with the library
    import cleanframes.instances.all._

    // import function defined by you for the custom type
    import CustomTypes._

    val result = input
      // call cleanframes API
      .clean[CustomTypes]
      // make Dataset
      .as[CustomTypes]
      .collect

    import cleanframes.{CustomTypes => Model} // just for readability sake

    result should {
      contain theSameElementsAs Seq(
        // @formatter:off
        Model(Some(CustomType()),   Some(1),  Some(false)),
        Model(None,                 Some(2),  Some(true)),
        Model(None,                 None,     Some(true)),
        Model(Some(CustomType()),   None,     Some(false))
        // @formatter:on
      )
    }.and(have size 4)
  }
}

/**
  * Define some type (case class) that you use in other type (case class).
  */
case class CustomType()

/**
  * Define a final type (case class) where you use type defined above.
  */
case class CustomTypes(col1: Option[CustomType],
                       col2: Option[Int],
                       col3: Option[Boolean])

/**
  * Define a function that want to use for conversion from a source string value to the first type defined above.
  * This a udf approach.
  *
  * This logic doesn't matter, it is just for educational purposes.
  */
object CustomTypes {
  implicit val func: String => Option[CustomType] = in => if (in == "0") Some(CustomType()) else None
}