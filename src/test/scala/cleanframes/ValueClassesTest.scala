package cleanframes

import org.scalatest.{FlatSpec, Matchers}
import com.holdenkarau.spark.testing.DataFrameSuiteBase

/**
  * This test demonstrates how to deal with case classes where you have fields of same types (for example two integer fields)
  * but you want to process them differently.
  *
  * Such case can be resolved with scala feature called 'value classes'.
  * Reference: https://docs.scala-lang.org/overviews/core/value-classes.html
  *
  * Refer to code below the test class to have a full understanding of the test case.
  */
class ValueClassesTest
  extends FlatSpec
    with Matchers
    with DataFrameSuiteBase {

  "Cleaner" should "use transformers for value types and clean the dataset" in {
    import spark.implicits._ // to use `.toDF` and `.as`
    import cleanframes.syntax._ // to use `.clean`

    // define test data for a dataframe
    val input = Seq(
      // @formatter:off
      ("1",         "1",          "1",          "1",          "true",       "true"),
      ("2",         "2",          "2",          "2",          "false",      "false"),
      ("corrupted", "corrupted",  "corrupted",  "corrupted",  "corrupted",  "corrupted")
      // @formatter:on
    )
      // important! dataframe's column names must match parameter names of the case class passed to `.clean` method
      .toDF("col1", "col2", "col3", "col4", "col5", "col6")

    // import standard functions for conversions shipped with the library
    import cleanframes.instances.all._

    // import `all._` can be replaced with these two below
    // import cleanframes.instances.tryToOption._
    // import cleanframes.instances.higher._

    // import custom transformers
    import Transformers._

    val result = input
      // call cleanframes API
      .clean[TestModel]
      // make Dataset
      .as[TestModel]
      .collect

    result should {
      contain theSameElementsAs Seq(
        // @formatter:off
        TestModel(Some(Col1(101)),  Some(Col2(201)),  Some(Col3(301)),  Some(Col4(401)),  Some(Col5(true)), Some(Col6(false))),
        TestModel(Some(Col1(102)),  Some(Col2(202)),  Some(Col3(302)),  Some(Col4(402)),  Some(Col5(true)), Some(Col6(false))),
        TestModel(None,             None,             None,             None,             Some(Col5(true)), Some(Col6(false)))
        // @formatter:on
      )
    }.and(have size 3)
  }
}

/**
  * Define model that uses same types via value classes defined below.
  */
case class TestModel(col1: Option[Col1],
                     col2: Option[Col2],
                     col3: Option[Col3],
                     col4: Option[Col4],
                     col5: Option[Col5],
                     col6: Option[Col6])

/**
  * Define first value class that represents Int value
  */
case class Col1(not_relevant: Int) extends AnyVal

/**
  * Define second value class that represents Int value
  */
case class Col2(not_relevant: Int) extends AnyVal

/**
  * Define first value class that represents Double value
  */
case class Col3(not_relevant: Double) extends AnyVal

/**
  * Define second value class that represents Double value
  */
case class Col4(not_relevant: Double) extends AnyVal

/**
  * Define first value class that represents Boolean value
  */
case class Col5(not_relevant: Boolean) extends AnyVal

/**
  * Define second value class that represents Boolean value
  */
case class Col6(not_relevant: Boolean) extends AnyVal


/**
  * Define custom transformations for all defined value classes above.
  * This a udf approach.
  *
  * Logic doesn't matter, it is just for educational purposes.
  */
object Transformers {

  import java.lang.Integer.parseInt
  import java.lang.Double.parseDouble

  implicit lazy val col1Transformer: String => Col1 = a => Col1(parseInt(a) + 100)
  implicit lazy val col2Transformer: String => Col2 = a => Col2(parseInt(a) + 200)
  implicit lazy val col3transformer: String => Col3 = a => Col3(parseDouble(a) + 300)
  implicit lazy val col4transformer: String => Col4 = a => Col4(parseDouble(a) + 400)
  implicit lazy val col5transformer: String => Col5 = _ => Col5(true)
  implicit lazy val col6transformer: String => Col6 = _ => Col6(false)

}