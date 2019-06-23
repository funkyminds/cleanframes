package cleanframes

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.scalatest.{FlatSpec, Matchers}
import shapeless.tag
import shapeless.tag.@@

/**
  * This test demonstrates how to deal with case classes where you have fields of same types (for example two integer fields)
  * but you want to process them differently.
  */
class MultipleSameTypesExample
  extends FlatSpec
    with Matchers
    with DataFrameSuiteBase {

  "Cleaner" should "use transformers for tagged types and clean the dataset" in {
    import cleanframes.syntax._
    import spark.implicits._ // to use `.clean`

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

    // import `all._` can be replaced with these two below
     import cleanframes.instances.tryToOption._
     import cleanframes.instances.higher._

    // import custom transformers
    import Transformers._

    val result = input
      // call cleanframes API
      .clean[TestModel]
      .as[TestModel]
      .collect

    result should {
      contain theSameElementsAs Seq(
        // @formatter:off
        TestModel(Some(tag[Col1][Int](101)),  Some(tag[Col2][Int](201)),  Some(tag[Col3][Double](301)),  Some(tag[Col4][Double](401)),  Some(tag[Col5][Boolean](true)), Some(tag[Col6][Boolean](false))),
        TestModel(Some(tag[Col1][Int](102)),  Some(tag[Col2][Int](202)),  Some(tag[Col3][Double](302)),  Some(tag[Col4][Double](402)),  Some(tag[Col5][Boolean](true)), Some(tag[Col6][Boolean](false))),
        TestModel(None,                       None,                       None,                          None,                          Some(tag[Col5][Boolean](true)), Some(tag[Col6][Boolean](false)))
        // @formatter:on
      )
    }.and(have size 3)
  }
}

/**
  * Define additional traits used for type tagging.
  */
trait Col1
trait Col2
trait Col3
trait Col4
trait Col5
trait Col6

/**
  * Define model that tags each type.
  */
case class TestModel(col1: Option[Int @@ Col1],
                     col2: Option[Int @@ Col2],
                     col3: Option[Double @@ Col3],
                     col4: Option[Double @@ Col4],
                     col5: Option[Boolean @@ Col5],
                     col6: Option[Boolean @@ Col6])


/**
  * Define custom transformations for all defined tagged types.
  * This a udf approach.
  *
  * Logic doesn't matter, it is just for educational purposes.
  */
object Transformers {

  import java.lang.Double.parseDouble
  import java.lang.Integer.parseInt

  implicit lazy val col1Transformer: String => Int @@ Col1     = a => tag[Col1][Int](parseInt(a) + 100)
  implicit lazy val col2Transformer: String => Int @@ Col2     = a => tag[Col2][Int](parseInt(a) + 200)
  implicit lazy val col3transformer: String => Double @@ Col3  = a => tag[Col3][Double](parseDouble(a) + 300)
  implicit lazy val col4transformer: String => Double @@ Col4  = a => tag[Col4][Double](parseDouble(a) + 400)
  implicit lazy val col5transformer: String => Boolean @@ Col5 = _ => tag[Col5][Boolean](true)
  implicit lazy val col6transformer: String => Boolean @@ Col6 = _ => tag[Col6][Boolean](false)

}