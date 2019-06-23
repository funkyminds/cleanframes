package cleanframes

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.apache.spark.sql.functions
import org.scalatest.{FlatSpec, Matchers}

/**
  * This test demonstrates how to use cleanframes to clean data with nested case classes.
  */
class NestedCaseClassesTest
  extends FlatSpec
    with Matchers
    with DataFrameSuiteBase {

  "Cleaner" should "compile and use a custom transformer for a custom type" in {
    import cleanframes.syntax._ // to use `.clean`
    import spark.implicits._

    // define test data for a dataframe
    val input = Seq(
      // @formatter:off
      ("1",           "1",          "1",           "1",           null),
      (null,          "2",          null,          "2",           "corrupted"),
      ("corrupted",   null,         "corrupted",   null,          "true"),
      ("4",           "corrupted",  "4",           "4",           "false"),
      ("5",           "5",          "5",           "corrupted",   "false"),
      ("6",           "6",          "6",           "6",           "true")
      // @formatter:on
    )
      // give column names that are known to you
      .toDF("col1", "col2", "col3", "col4", "col5")

    // import standard functions for conversions shipped with the library
    import cleanframes.instances.all._

    // !important: you need to give a new structure to allow to access sub elements
    val renamed = input.select(
      functions.struct(
        input.col("col1") as "a_col_1",
        input.col("col2") as "a_col_2"
      ) as "a",
      functions.struct(
        input.col("col3") as "b_col_1",
        input.col("col4") as "b_col_2"
      ) as "b",
      input.col("col5") as "c"
    )

    val result = renamed.clean[AB]
      .as[AB]
      .collect

    result should {
      contain theSameElementsAs Seq(
        // @formatter:off
        AB( A(Some(1), Some(1)),  B(Some(1),  Some(1.0)), Some(false)),
        AB( A(None,    Some(2)),  B(None,     Some(2.0)), Some(false)),
        AB( A(None,    None),     B(None,     None),      Some(true)),
        AB( A(Some(4), None),     B(Some(4),  Some(4.0)), Some(false)),
        AB( A(Some(5), Some(5)),  B(Some(5),  None),      Some(false)),
        AB( A(Some(6), Some(6)),  B(Some(6),  Some(6.0)), Some(true))
        // @formatter:on
      )
    }
  }

}

case class A(a_col_1: Option[Int], a_col_2: Option[Float])

case class B(b_col_1: Option[Float], b_col_2: Option[Double])

case class AB(a: A, b: B, c: Option[Boolean])