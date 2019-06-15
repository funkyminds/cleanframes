package cleanframes.instances

import cleanframes.Cleaner
import org.apache.spark.sql.functions.{lower, trim, when, lit}
import org.apache.spark.sql.types._

trait AnyValInstances
  extends IntInstances
    with ByteInstances
    with CharInstances
    with ShortInstances
    with LongInstances
    with FloatInstances
    with DoubleInstances
    with BooleanInstances
    with NumericAnyValInstance

trait IntInstances {
  implicit lazy val integerType: SparkDataType[Int] = new SparkDataType[Int] {
    override def getDataType: DataType = IntegerType
  }
}

trait ByteInstances {
  implicit lazy val byteType: SparkDataType[Byte] = new SparkDataType[Byte] {
    override def getDataType: DataType = ByteType
  }
}

trait CharInstances {
  implicit val stdStringToChar: String => Char = _.charAt(0)
}

trait ShortInstances {
  implicit lazy val shortType: SparkDataType[Short] = new SparkDataType[Short] {
    override def getDataType: DataType = ShortType
  }
}

trait LongInstances {
  implicit lazy val longType: SparkDataType[Long] = new SparkDataType[Long] {
    override def getDataType: DataType = LongType
  }
}

trait FloatInstances {
  implicit lazy val floatType: SparkDataType[Float] = new SparkDataType[Float] {
    override def getDataType: DataType = FloatType
  }
}

trait DoubleInstances {
  implicit lazy val doubleType: SparkDataType[Double] = new SparkDataType[Double] {
    override def getDataType: DataType = DoubleType
  }
}

trait BooleanInstances {
  implicit lazy val booleanCleaner: Cleaner[Option[Boolean]] = {
    Cleaner.materialize { (frame, name, alias) =>
      List(
        when(
          trim(lower(frame.col(name.get))) === "true",
          lit(true) cast BooleanType
        ).otherwise(false) as alias.get
      )
    }
  }
}
