package cleanframes

import org.scalatest.{FlatSpec, Matchers}
import com.holdenkarau.spark.testing.DataFrameSuiteBase

/**
  * This test demonstrates usage of concrete type import instead of putting in all available.
  */
class SingleImportInsteadAllTest
  extends FlatSpec
    with Matchers
    with DataFrameSuiteBase {

  "Cleaner" should "transform data by using concrete import" in {
    import spark.implicits._ // to use `.toDF` and `.as`
    import cleanframes.syntax._ // to use `.clean`

    // define test data for a dataframe
    val input = Seq(
      ("1"),
      ("corrupted"),
      ("3"),
      ("4"),
      ("5"),
      (null),
      ("null"),
      ("     x   "),
      ("     6 2  "),
      ("6"),
      ("7"),
      ("8")
    )
      // important! dataframe's column names must match parameter names of the case class passed to `.clean` method
      .toDF("col1")

    // import standard functions for conversions shipped with the library
    import cleanframes.instances.all._

    val result = input
      // call cleanframes API
      .clean[SingleIntModel]
      // make Dataset
      .as[SingleIntModel]
      .collect

    result should {
      contain theSameElementsAs Seq(
        SingleIntModel(Some(1)),
        SingleIntModel(None),
        SingleIntModel(Some(3)),
        SingleIntModel(Some(4)),
        SingleIntModel(Some(5)),
        SingleIntModel(None),
        SingleIntModel(None),
        SingleIntModel(None),
        SingleIntModel(None),
        SingleIntModel(Some(6)),
        SingleIntModel(Some(7)),
        SingleIntModel(Some(8))
      )
    }
  }

}

case class SingleIntModel(col1: Option[Int])
