package cleanframes

import org.scalatest.{FlatSpec, Matchers}
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import cleanframes.instances.all._
import cleanframes.syntax._

class OptionalPrimitiveTest extends FlatSpec with Matchers with DataFrameSuiteBase {

  "Cleaner" should "transform data to concrete types if possible" in {
    import spark.implicits._

    val input = Seq(
      ("1", "1", "1", "1", "1", "1", "true"),
      ("corrupted", "2", "2", "2", "2", "2", "false"),
      ("3", "corrupted", "3", "3", "3", "3", null),
      ("4", "4", "corrupted", "4", "4", "4", "true"),
      ("5", "5", "5", "corrupted", "5", "5", "false"),
      ("6", "6", "6", "6", "corrupted", "6", null),
      ("7", "7", "7", "7", "7", "corrupted", "true"),
      ("8", "8", "8", "8", "8", "8", "corrupted")
    ).toDF("col1", "col2", "col3", "col4", "col5", "col6", "col7")
      .coalesce(1)

    val result = input
      .clean[AnyValsExample]
      .as[AnyValsExample]
      .collect

    result should {
      contain theSameElementsAs Seq(
        AnyValsExample(Some(1), Some(1), Some(1), Some(1), Some(1), Some(1), Some(true)),
        AnyValsExample(None, Some(2), Some(2), Some(2), Some(2), Some(2), Some(false)),
        AnyValsExample(Some(3), None, Some(3), Some(3), Some(3), Some(3), Some(false)),
        AnyValsExample(Some(4), Some(4), None, Some(4), Some(4), Some(4), Some(true)),
        AnyValsExample(Some(5), Some(5), Some(5), None, Some(5), Some(5), Some(false)),
        AnyValsExample(Some(6), Some(6), Some(6), Some(6), None, Some(6), Some(false)),
        AnyValsExample(Some(7), Some(7), Some(7), Some(7), Some(7), None, Some(true)),
        AnyValsExample(Some(8), Some(8), Some(8), Some(8), Some(8), Some(8), Some(false))
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