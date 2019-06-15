package cleanframes

import org.scalatest.{FlatSpec, Matchers}
import com.holdenkarau.spark.testing.DataFrameSuiteBase

/**
  * This test demonstrates how to use cleanframes to clean data by using only standard functions delivered with the library.
  */
class OptionalPrimitivesTest
  extends FlatSpec
    with Matchers
    with DataFrameSuiteBase {

  "Cleaner" should "transform data to concrete types if possible" in {
    import spark.implicits._ // to use `.toDF` and `.as`
    import cleanframes.syntax._ // to use `.clean`

    // define test data for a dataframe
    val input = Seq(
      // @formatter:off
      ("1",         "1",          "1",          "1",          "1",          "1",          "true"),
      ("corrupted", "2",          "2",          "2",          "2",          "2",          "false"),
      ("3",         "corrupted",  "3",          "3",          "3",          "3",          null),
      ("4",         "4",          "corrupted",  "4",          "4",          "4",          "true"),
      ("5",         "5",          "5",          "corrupted",  "5",          "5",          "false"),
      ("6",         "6",          "6",          "6",          "corrupted",  "6",          null),
      ("7",         "7",          "7",          "7",          "7",          "corrupted",  "true"),
      ("8",         "8",          "8",          "8",          "8",          "8",          "corrupted")
      // @formatter:on
    )
      // important! dataframe's column names must match parameter names of the case class passed to `.clean` method
      .toDF("col1", "col2", "col3", "col4", "col5", "col6", "col7")

    // import standard functions for conversions shipped with the library
    import cleanframes.instances.all._

    val result = input
      // call cleanframes API
      .clean[AnyValsExample]
      // make Dataset
      .as[AnyValsExample]
      .collect

    import cleanframes.{AnyValsExample => Model} // just for readability sake

    result should {
      contain theSameElementsAs Seq(
        // @formatter:off
        Model(Some(1),   Some(1),  Some(1),  Some(1),  Some(1),  Some(1),  Some(true)),
        Model(None,      Some(2),  Some(2),  Some(2),  Some(2),  Some(2),  Some(false)),
        Model(Some(3),   None,     Some(3),  Some(3),  Some(3),  Some(3),  Some(false)),
        Model(Some(4),   Some(4),  None,     Some(4),  Some(4),  Some(4),  Some(true)),
        Model(Some(5),   Some(5),  Some(5),  None,     Some(5),  Some(5),  Some(false)),
        Model(Some(6),   Some(6),  Some(6),  Some(6),  None,     Some(6),  Some(false)),
        Model(Some(7),   Some(7),  Some(7),  Some(7),  Some(7),  None,     Some(true)),
        Model(Some(8),   Some(8),  Some(8),  Some(8),  Some(8),  Some(8),  Some(false))
        // @formatter:on
      )
    }.and(have size 8)

  }

}

case class AnyValsExample(col1: Option[Int],
                          col2: Option[Byte],
                          col3: Option[Short],
                          col4: Option[Long],
                          col5: Option[Float],
                          col6: Option[Double],
                          col7: Option[Boolean])