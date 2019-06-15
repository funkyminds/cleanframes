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
      ("0",           "1",          "0",           "1",           null),
      (null,          "2",          null,          "2",           "true"),
      ("corrupted",   null,         "corrupted",   null,          "true"),
      ("0",           "corrupted",  "0",           "corrupted",   "false")
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
    val result = renamed
      .clean[AB]
      .as[AB]
      .collect

    result should {
      contain theSameElementsAs Seq(
        // @formatter:off
        AB( A(Some(0), Some(1.0f)), B(Some(0.0f), Some(1.0)), Some(false)),
        AB( A(None,    Some(2.0f)), B(None,       Some(2.0)), Some(true)),
        AB( A(None,    None),       B(None,       None),      Some(true)),
        AB( A(Some(0), None),       B(Some(0.0f), None),      Some(false))
        // @formatter:on
      )
    }
  }

}

case class A(a_col_1: Option[Int], a_col_2: Option[Float])

case class B(b_col_1: Option[Float], b_col_2: Option[Double])

case class AB(a: A, b: B, c: Option[Boolean])