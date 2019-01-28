package cleanframes

import org.scalatest.{FlatSpec, Matchers}
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import cleanframes.instances.all._
import cleanframes.syntax._

class CustomTypesTest extends FlatSpec with Matchers with DataFrameSuiteBase {

  "Cleaner" should "compile and use a custom transformer for a custom type" in {
    import spark.implicits._

    val input = Seq(
      ("0", "1", null),
      (null, "2", "true"),
      ("corrupted", null, "true"),
      ("0", "corrupted", "false")
    ).toDF("col1", "col2", "col3")
      .coalesce(1)

    implicit val func: String => Option[CustomType] = in => if (in == "0") Some(CustomType()) else None

    val result = input
      .clean[CustomTypes]
      .as[CustomTypes]
      .collect

    result should {
      contain theSameElementsAs Array(
        CustomTypes(Some(CustomType()), Some(1), Some(false)),
        CustomTypes(None, Some(2), Some(true)),
        CustomTypes(None, None, Some(true)),
        CustomTypes(Some(CustomType()), None, Some(false))
      )
    }.and(have size 4)
  }

  // TODO: should not compile without a transformer
}

case class CustomType()

case class CustomTypes(col1: Option[CustomType],
                       col2: Option[Int],
                       col3: Option[Boolean])
