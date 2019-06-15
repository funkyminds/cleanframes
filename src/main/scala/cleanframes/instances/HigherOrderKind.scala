package cleanframes.instances

import cleanframes.Cleaner
import org.apache.spark.sql.functions.udf

import scala.reflect.runtime.universe.TypeTag

trait HigherOrderKind {

  implicit def stringHigherOrder[A, B[_]](implicit
                                          aTag: TypeTag[A],
                                          bTag: TypeTag[B[A]],
                                          func: String => B[A]): Cleaner[B[A]] =
    Cleaner.materialize { (frame, name, alias) =>
      List(
        udf(func).apply(frame.col(name.get)) as alias.get
      )
    }
}
