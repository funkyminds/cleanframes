package cleanframes

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.scalatest.{FlatSpec, Matchers}

/**
  * This example demonstrates how to use cleanframes with spark UDF approach.
  *
  * Apache Spark SQL delivers functions package with methods that Tungsten friendly and highly recommended.
  *
  * Another approach is to use UDFs () which allow to use scala code instead. While code is nicer to write and retain types, it is not best in optimization.
  */
class UdfApproachTest
  extends FlatSpec
    with Matchers
    with DataFrameSuiteBase {

  "Cleaner" should "use custom udf functions" in {
    import spark.implicits._ // to use `.toDF` and `.as`
    import cleanframes.syntax._ // to use `.clean`

    val input = Seq(
      // @formatter:off
      ("1",   "2",          "3"),
      ("1",   "corrupted",  "3")
      // @formatter:on
    )
      // important! dataframe's column names must match parameter names of the case class passed to `.clean` method
      .toDF("col1", "col2", "col3")

    import cleanframes.instances.tryToOption._ // to use defined functions by executing them in scala.util.Try within udfs
    import cleanframes.instances.higher._ // to allow usage of transformation line above (tryToOption)

    import UdfModel._

    val result = input
      // call cleanframes API
      .clean[UdfModel]
      // make Dataset
      .as[UdfModel]
      .collect

    result should {
      contain theSameElementsAs Seq(
        UdfModel(Some(2), Some(6.0), Some(103.0f)),
        UdfModel(Some(2), None, Some(103.0f))
      )
    }
  }

}

case class UdfModel(col1: Option[Int],
                    col2: Option[Double],
                    col3: Option[Float])

/**
  * Define scala functions. They will be resolved automatically to concrete Cleaner instances.
  */
object UdfModel {

  import java.lang.Double.parseDouble
  import java.lang.Float.parseFloat
  import java.lang.Integer.parseInt

  implicit lazy val col1Transformer: String => Int = a => parseInt(a) * 2
  implicit lazy val col2Transformer: String => Double = (a: String) => parseDouble(a) * 3
  implicit lazy val col3transformer: String => Float = a => {
    parseFloat(a) + 100
  }
}