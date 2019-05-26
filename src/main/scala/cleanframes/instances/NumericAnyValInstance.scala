package cleanframes.instances

import cleanframes.Cleaner
import org.apache.spark.sql.functions._

trait NumericAnyValInstance {
  implicit def instance[T](implicit sdt: SparkDataType[T]): Cleaner[Option[T]] = {
    Cleaner.materialize { (frame, name, alias) =>
      List(
        when(
          not(
            frame.col(name.get).isNaN
          ),
          frame.col(name.get)
        ) cast sdt.getDataType as alias.get
      )
    }
  }
}