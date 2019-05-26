package cleanframes

import org.scalatest.{FlatSpec, Matchers}
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.apache.spark.sql.types.IntegerType

/**
  * This test demonstrates how to use a custom defined Cleaner instance along with ones imported from the library.
  *
  * There might be a case where you need a different logic then one in already imported instances.
  *
  * This can be resolved by rules of implicits precedence.
  * For more, refer: http://eed3si9n.com/implicit-parameter-precedence-again
  */
class OverrideSpecificConversionTest
  extends FlatSpec
    with Matchers
    with DataFrameSuiteBase {

  "Cleaner" should "transform data to concrete types by using overridden Cleaner[Int] instance" in {
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

    // define your Cleaner instance - in this case for int
    // you override one imported along with above `all._`
    implicit val redefinedInt: Cleaner[Option[Int]] = {
      import org.apache.spark.sql.functions
      Cleaner.materialize { (frame, name, alias) =>
        List(
          functions.when(
            functions.not(
              frame.col(name.get).isNaN
            ),
            frame.col(name.get) * 1000
          ) cast IntegerType as alias.get
        )
      }
    }

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
        Model(Some(1000),   Some(1),  Some(1),  Some(1),  Some(1),  Some(1),  Some(true)),
        Model(None,         Some(2),  Some(2),  Some(2),  Some(2),  Some(2),  Some(false)),
        Model(Some(3000),   None,     Some(3),  Some(3),  Some(3),  Some(3),  Some(false)),
        Model(Some(4000),   Some(4),  None,     Some(4),  Some(4),  Some(4),  Some(true)),
        Model(Some(5000),   Some(5),  Some(5),  None,     Some(5),  Some(5),  Some(false)),
        Model(Some(6000),   Some(6),  Some(6),  Some(6),  None,     Some(6),  Some(false)),
        Model(Some(7000),   Some(7),  Some(7),  Some(7),  Some(7),  None,     Some(true)),
        Model(Some(8000),   Some(8),  Some(8),  Some(8),  Some(8),  Some(8),  Some(false))
        // @formatter:on
      )
    }.and(have size 8)

  }
}
