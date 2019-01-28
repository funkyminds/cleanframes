package cleanframes

import org.scalatest.{FlatSpec, Matchers}
import com.holdenkarau.spark.testing.DataFrameSuiteBase

import cleanframes.instances.all._
import cleanframes.syntax._

class ValueClassesTest extends FlatSpec with Matchers with DataFrameSuiteBase {

  "Cleaner" should "use transformers for value types and clean the dataset" in {
    import spark.implicits._
    import Transformers._

    val input = Seq(
      ("1", "1", "1", "1", "true", "true"),
      ("2", "2", "2", "2", "false", "false"),
      ("corrupted", "corrupted", "corrupted", "corrupted", "corrupted", "corrupted")
    ).toDF("col1", "col2", "col3", "col4", "col5", "col6")

    val result = input
      .clean[TestModel]
      .as[TestModel]
      .collect

    result should {
      contain theSameElementsAs Seq(
        TestModel(Some(Col1(101)), Some(Col2(201)), Some(Col3(301)), Some(Col4(401)), Some(Col5(true)), Some(Col6(false))),
        TestModel(Some(Col1(102)), Some(Col2(202)), Some(Col3(302)), Some(Col4(402)), Some(Col5(true)), Some(Col6(false))),
        TestModel(None, None, None, None, Some(Col5(true)), Some(Col6(false)))
      )
    }.and(have size 3)
  }
}

case class TestModel(col1: Option[Col1],
                     col2: Option[Col2],
                     col3: Option[Col3],
                     col4: Option[Col4],
                     col5: Option[Col5],
                     col6: Option[Col6])

case class Col1(not_relevant: Int) extends AnyVal

case class Col2(not_relevant: Int) extends AnyVal

case class Col3(not_relevant: Double) extends AnyVal

case class Col4(not_relevant: Double) extends AnyVal

case class Col5(not_relevant: Boolean) extends AnyVal

case class Col6(not_relevant: Boolean) extends AnyVal

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